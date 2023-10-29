package ru.skypro.homework;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.utilclass.JavaFileToMultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUserAndAuthController {
    @LocalServerPort
    private int port;
    @Autowired
    private UserController userController;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    String registerPath;
    String loginPath;
    String setPasswordPath;
    String infoAboutUserPath;
    String uploadAvatarPath;
    LoginDto testLoginDto;

    @BeforeEach
    void init() {
        registerPath = "http://localhost:" + port + "/register";
        loginPath = "http://localhost:" + port + "/login";
        setPasswordPath = "http://localhost:" + port + "/users/set_password";
        infoAboutUserPath = "http://localhost:" + port + "/users/me";
        uploadAvatarPath ="http://localhost:" + port + "/users/me/image";

        testLoginDto=new LoginDto("grand@gmail.com", "123123123");
    }

    @Test
    @Order(1)
    void contextLoad() {
        assertThat(userController).isNotNull();
    }

    @Test
    @Order(2)
    void registerFakeUser() {
        String testName = "grand@gmail.com";
        RegisterDto registerDtoUser = RegisterDto.builder().
                username(testName).
                firstName("Грант").
                lastName("Просто Грант").
                password("123123123").
                role("ADMIN").build();

        if (!usersRepository.existsByUsername(testName)) {
            ResponseEntity<?> response =
                    restTemplate.
                            postForEntity(registerPath, registerDtoUser, ResponseEntity.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Test
    void login() {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(loginPath, testLoginDto, ResponseEntity.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void uploadAvatarUser(){

        MultipartFile  file=  new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("avatarUser", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);


        ResponseEntity<Void> exchange = restTemplate.exchange
                (uploadAvatarPath, HttpMethod.PATCH, requestEntity, Void.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void setPassword() {
        SetPasswordDto setPasswordDto = new SetPasswordDto();
        setPasswordDto.setNewPassword("123123123");
        setPasswordDto.setCurrentPassword("123123123");

        SetPasswordDto responseSetDto =
                restTemplate.postForObject(setPasswordPath, getHttpWithAuthAndBody(setPasswordDto), SetPasswordDto.class);

        assertNotNull(responseSetDto.getNewPassword());
    }

    /**
     * Выводы:
     * Если spring security выбрасывает UNAUTHORIZED, то нельзя делать проверку на ошибку через assertThrow
     * Так как это не метод выбрасывает ее, а spring security. Поэтому нужно у сущности ответа проверять эту ошибку.
     *
     * Метод login() у меня не создавал сессию. Чтобы вызвать метод infoAboutUser(), мне нужно было подшить туда
     * авторизацию
     */
    @Test
    void infoAboutUser() {
        ResponseEntity<UserDto> exchange1 =
                restTemplate.exchange(infoAboutUserPath, HttpMethod.GET, getHttpEmpty(), UserDto.class);
        assertEquals(HttpStatus.UNAUTHORIZED,exchange1.getStatusCode());

        ResponseEntity<UserDto> exchange =
                restTemplate.exchange(infoAboutUserPath, HttpMethod.GET, getHttpWithAuthAndNotBody(), UserDto.class);
        assertNotNull(exchange.getBody().getId());
    }

    @Test
    void updateInfoUser() {
        UpdateUserDto updateUserDto=
                UpdateUserDto.builder().firstName("ChangeTest").lastName("ChangeTest").phone("1111").build();

        ResponseEntity<UpdateUserDto> exchange =
                restTemplate.exchange
                        (infoAboutUserPath, HttpMethod.PATCH, getHttpWithAuthAndBody(updateUserDto), UpdateUserDto.class);

        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        assertEquals("ChangeTest", Objects.requireNonNull(exchange.getBody()).getFirstName());
    }

    private <T>HttpEntity<?> getHttpWithAuthAndBody(T objectDto ){
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<T> httpEntity = new HttpEntity<>(objectDto,headers);
        return httpEntity;
    }

    private HttpEntity<?>  getHttpEmpty(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?>  getHttpWithAuthAndNotBody(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

}




