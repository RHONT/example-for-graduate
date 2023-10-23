package ru.skypro.homework;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.controller.AuthController;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.LoginDto;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UsersRepository;
import org.assertj.core.api.Assertions;
import ru.skypro.homework.service.CustomUserDetailsService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private  ImageRepository imageRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private AuthController authController;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    private String userPath;

    RegisterDto registerDto= RegisterDto.builder().
            username("h@gmail.com").
            firstName("Ivan").
            password("123123123").
            role("USER").build();

   private static final LoginDto loginDto= LoginDto.builder().username("f@gmail.com").password("123123123").build();

    @BeforeEach
    void init() {
        userPath = "http://localhost:" + port + "/users/";
        restTemplate.postForEntity("http://localhost:"+port+"/login",loginDto,ResponseEntity.class);
    }

    @Test
    void login(){
        Assertions.assertThat(authController).isNotNull();

        LoginDto loginDto= LoginDto.builder().username("f@gmail.com").password("123123123").build();
        int statusCodeValue = authController.login(loginDto).getStatusCodeValue();
        assertEquals(HttpStatus.OK.value(),statusCodeValue);
    }


    @Test
    void infoAboutAuthUser(){
        UserDetails activeUser=customUserDetailsService.loadUserByUsername("f@gmail.com");
        UserDto userDto=userController.infoAboutAuthUser(activeUser);
        assertEquals(userDto.getFirstName(),"Генадий");
    }

    @Test
    void updateUserDto(){
        UserDetails activeUser=customUserDetailsService.loadUserByUsername("f@gmail.com");
        UpdateUserDto update= UpdateUserDto.builder().firstName("Новое Имя").lastName("Новая фамилия").build();
        UpdateUserDto updatedUser=userController.updateUserDto(update,activeUser);

        assertEquals(updatedUser.getFirstName(),"Новое Имя");

        update= UpdateUserDto.builder().firstName("Генадий").lastName("Владыкор").build();
        userController.updateUserDto(update,activeUser);
    }



}
