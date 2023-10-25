package ru.skypro.homework;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.controller.AuthController;
import ru.skypro.homework.controller.CommentController;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repository.*;
import org.assertj.core.api.Assertions;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CustomUserDetailsService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.utilclass.JavaFileToMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private AuthController authController;

    @Autowired
    AdsController adsController;

    @Autowired
    AdsRepository adsRepository;

    @Autowired
    CommentController commentController;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ImageService imageService;

    @Autowired
    AdService adService;

    String startPath;


    RegisterDto registerDto;

    private static final LoginDto loginDto = LoginDto.builder().username("f@gmail.com").password("123123123").build();

    @BeforeEach
    void init() {
        restTemplate.postForEntity("http://localhost:" + port + "/login", loginDto, ResponseEntity.class);
        startPath = "http://localhost:" + port + "/";

        registerDto = RegisterDto.builder().
                username("h@gmail.com").
                firstName("Ivan").
                password("123123123").
                role("USER").build();
    }

    @Test
    void contextLoad() {
        Assertions.assertThat(authController).isNotNull();
        Assertions.assertThat(adsController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
    }

    @Test
    void login() {
        LoginDto loginDto = LoginDto.builder().username("f@gmail.com").password("123123123").build();
        int statusCodeValue = authController.login(loginDto).getStatusCodeValue();
        assertEquals(HttpStatus.OK.value(), statusCodeValue);
    }

    //todo Не могу удалить user
    @Test
    @Transactional
    void register() {
        String userName = "TestPerson@gmail.com";
        registerDto.setUsername(userName);
        ResponseEntity<?> response =
                restTemplate.
                        postForEntity(startPath + "register", registerDto, ResponseEntity.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
    }


    @Test
    void infoAboutAuthUser() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        UserDto userDto = userController.infoAboutAuthUser(activeUser);
        assertEquals(userDto.getFirstName(), "Генадий");
    }

    @Test
    void updateUserDto() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        UpdateUserDto update = UpdateUserDto.builder().firstName("Новое Имя").lastName("Новая фамилия").build();
        UpdateUserDto updatedUser = userController.updateUserDto(update, activeUser);

        assertEquals(updatedUser.getFirstName(), "Новое Имя");

        update = UpdateUserDto.builder().firstName("Генадий").lastName("Владыкор").build();
        userController.updateUserDto(update, activeUser);
    }

    @Test
    void addAd() throws IOException {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        CreateOrUpdateAdDto newAd = CreateOrUpdateAdDto.builder().
                title("Тест2").
                price(100).
                description("Тест описание2").build();

        JavaFileToMultipartFile mf = new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        AdDto adDto = adsController.addAd(newAd, mf, activeUser);
        assertEquals("Тест2", adDto.getTitle());
    }

    @Test
    void getAllAds() {
        AdsDto adsDto = adsController.getAllAds();
        int sum = adsRepository.findAll().size();
        assertEquals(sum, adsDto.getCount());
    }

    @Test
    @Transactional
    void getAdsMe() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        AdsDto adsDto = adsController.getAdsMe(activeUser);
        UserEntity user = usersRepository.findByUsername(activeUser.getUsername()).get();
        assertEquals(user.getAdEntityList().size(), adsDto.getCount());
    }

    //todo доделать
    @Test
    void updateImageAdd() {
    }

    @Test
    void addComment() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        CreateOrUpdateComment comment = new CreateOrUpdateComment();
        comment.setText("Тест комментарий");
        CommentDto commentDto = commentController.addComment(1, comment);
        assertEquals(commentDto.getText(),"Тест комментарий");
    }


    @Test
    @Transactional
    void getComments() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        AdEntity ad=adsRepository.findById(1).get();
        CommentsDto comments = commentController.getComments(ad.getPk());
        assertEquals(ad.getCommentEntityList().size(),comments.getCount());
    }


    //todo авторизация
    @Test
    @Transactional
    void deleteComment() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        AdEntity ad=adsRepository.findById(1).get();
        int count=ad.getCommentEntityList().size();
        commentController.deleteComment(1,1);
        Optional<CommentEntity> comment = commentsRepository.findById(1);
        assertTrue(comment.isEmpty());
    }


    //todo почему то не работает restTamplate
    @Test
    void updateComment() {
        UserDetails activeUser = customUserDetailsService.loadUserByUsername("f@gmail.com");
        CreateOrUpdateComment comment = new CreateOrUpdateComment();
        comment.setText("Тест другой");

//        CommentDto commentDto = restTemplate.patchForObject(startPath + "/ads/1/comments/1", "Тест другой", CommentDto.class);
        CommentDto commentDto =commentController.updateComment(1,1,"Тест другой",activeUser);
        assertEquals("Тест другой",commentDto.getText());


    }


}
