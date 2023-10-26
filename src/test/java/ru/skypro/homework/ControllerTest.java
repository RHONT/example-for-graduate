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
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.repository.*;
import org.assertj.core.api.Assertions;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CustomUserDetailsService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.utilclass.JavaFileToMultipartFile;

import javax.annotation.PostConstruct;
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

    UserDetails activeUser;
    private final String userNameUser = "user@gmail.com";
    UserDetails activeAdmin;
    private final String adminNameUser = "admin@gmail.com";
    UserDetails activeEnemy;
    private final String enemyNameUser = "enemy@gmail.com";

    RegisterDto registerDto;

    @BeforeEach
    @Transactional
    @Rollback(value = false)
    void init() {
        if (!usersRepository.existsByUsername(userNameUser) &&
                !usersRepository.existsByUsername(adminNameUser) &&
                !usersRepository.existsByUsername(enemyNameUser)) {

            RegisterDto   registerDtoUser = RegisterDto.builder().
                    username("user@gmail.com").
                    firstName("Евгений").
                    lastName("Белых").
                    password("123123123").
                    role("USER").build();

            RegisterDto registerDtoAdmin  = RegisterDto.builder().
                    username("admin@gmail.com").
                    firstName("Александр").
                    lastName("Лапутин").
                    password("123123123").
                    role("ADMIN").build();

            RegisterDto  registerDtoEnemy = RegisterDto.builder().
                    username("enemy@gmail.com").
                    firstName("Пал").
                    lastName("Патин").
                    password("123123123").
                    role("USER").build();

            authController.register(registerDtoUser);
            authController.register(registerDtoAdmin);
            authController.register(registerDtoEnemy);
        }

        authController.login(LoginDto.builder().username(userNameUser).password("123123123").build());
        authController.login(LoginDto.builder().username(adminNameUser).password("123123123").build());
        authController.login(LoginDto.builder().username(enemyNameUser).password("123123123").build());

        activeUser = customUserDetailsService.loadUserByUsername(userNameUser);
        activeAdmin = customUserDetailsService.loadUserByUsername(adminNameUser);
        activeEnemy = customUserDetailsService.loadUserByUsername(enemyNameUser);
    }

    @Test
    @Order(1)
    @Transactional
    void contextLoad() {
        Assertions.assertThat(authController).isNotNull();
        Assertions.assertThat(adsController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
    }

    @Test
    @Disabled
    void login() {
        LoginDto loginDto = LoginDto.builder().username(userNameUser).password("123123123").build();
        int statusCodeValue = authController.login(loginDto).getStatusCodeValue();
        assertEquals(HttpStatus.OK.value(), statusCodeValue);
    }

    //todo Не могу удалить user. Но тогда почему нет отката после окончания теста?
    @Test
    @Disabled
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
        LoginDto loginDto = LoginDto.builder().username(userNameUser).password("123123123").build();
        authController.login(loginDto);
        activeUser = customUserDetailsService.loadUserByUsername(userNameUser);
        UserDto userDto = userController.infoAboutAuthUser(activeUser);
        assertEquals(userDto.getFirstName(), "Евгений");
    }

    @Test
    @Order(3)
    void updateUserDto() {
        String testName="Думгай";
        String testLastName="Петрович";

        UpdateUserDto update = UpdateUserDto.builder().firstName(testName).lastName(testLastName).build();

        UpdateUserDto updatedUser = userController.updateUserDto(update, activeUser);

        assertEquals(updatedUser.getFirstName(), testName);

        update = UpdateUserDto.builder().firstName("Евгений").lastName("Белых").build();
        userController.updateUserDto(update, activeUser);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    @Order(2)
    void addAd() throws IOException {
        String testTitle = "Test_Title";
        int testPrice = 128;
        String testDesc = "Test_Description";

        CreateOrUpdateAdDto newAd = CreateOrUpdateAdDto.builder().
                title(testTitle).
                price(testPrice).
                description(testDesc).build();

        JavaFileToMultipartFile mf = new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        AdDto adDto = adsController.addAd(newAd, mf, activeUser);
        assertEquals(testTitle, adDto.getTitle());
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
        AdsDto adsDto = adsController.getAdsMe(activeUser);
        UserEntity user = usersRepository.findByUsername(activeUser.getUsername()).get();
        assertEquals(user.getAdEntityList().size(), adsDto.getCount());
    }

    //todo доделать
    @Test
    void updateImageAdd() {
    }

    @Test
    @Order(4)
    @Transactional
    @Rollback(value = false)
    void addComment() {
        String testComment="testComment";
        UserEntity user=usersRepository.findByUsername(userNameUser).get();
        AdEntity ad=user.getAdEntityList().get(0);

        CreateOrUpdateComment comment = new CreateOrUpdateComment();

        comment.setText(testComment);
        CommentDto commentDto = commentController.addComment(ad.getPk(), comment);
        assertEquals(commentDto.getText(), testComment);
    }


    @Test
    @Transactional
    void getComments() {
        UserEntity user=usersRepository.findByUsername(userNameUser).get();
        AdEntity ad=user.getAdEntityList().get(0);
        CommentsDto comments = commentController.getComments(ad.getPk());
        assertEquals(ad.getCommentEntityList().size(), comments.getCount());
    }


    @Test
    @Transactional
    void deleteComment() {
        UserEntity user=usersRepository.findByUsername(userNameUser).get();
        AdEntity ad=user.getAdEntityList().get(0);
        int numbAd=ad.getPk();
        CommentEntity comment=ad.getCommentEntityList().get(0);
        int numbComment=comment.getCommentId();

        commentController.deleteComment(numbAd, numbComment,activeUser);
        Optional<CommentEntity> commentFact = commentsRepository.findById(numbComment);
        assertTrue(commentFact.isEmpty());
    }

    @Test
    @Transactional
    void deleteCommentAdmin() {
        UserEntity user=usersRepository.findByUsername(userNameUser).get();
        AdEntity ad=user.getAdEntityList().get(0);
        int numbAd=ad.getPk();
        CommentEntity comment=ad.getCommentEntityList().get(0);
        int numbComment=comment.getCommentId();

        commentController.deleteComment(numbAd, numbComment,activeAdmin);
        Optional<CommentEntity> commentFact = commentsRepository.findById(numbComment);
        assertTrue(commentFact.isEmpty());
    }

    @Test
    @Transactional
    void deleteCommentEnemyUser() {
        UserEntity user=usersRepository.findByUsername(userNameUser).get();
        AdEntity ad=user.getAdEntityList().get(0);
        int numbAd=ad.getPk();
        CommentEntity comment=ad.getCommentEntityList().get(0);
        int numbComment=comment.getCommentId();

        assertThrows(UnauthorizedException.class,()->{
            commentController.deleteComment(numbAd, numbComment,activeEnemy);
        });
    }


    //todo почему-то не работает restTemplate
    @Test
    @Transactional
    void updateComment() {
        UserEntity user=usersRepository.findByUsername(userNameUser).get();
        AdEntity ad=user.getAdEntityList().get(0);
        int numbAd=ad.getPk();
        CommentEntity comment=ad.getCommentEntityList().get(0);
        int numbComment=comment.getCommentId();


        CreateOrUpdateComment commentDto = new CreateOrUpdateComment();
        String userComment="User_Comment";
        String adminComment="Admin_Comment";
        String enemyComment="Enemy_Comment";

        commentDto.setText(userComment);
        CommentDto commentUserDto = commentController.updateComment(numbAd, numbComment, commentDto, activeUser);
        assertEquals(userComment, commentUserDto.getText());

        commentDto.setText(adminComment);
        CommentDto commentAdminDto = commentController.updateComment(numbAd, numbComment, commentDto, activeAdmin);
        assertEquals(adminComment, commentAdminDto.getText());

        commentDto.setText(enemyComment);
        assertThrows(UnauthorizedException.class,()->{
            commentController.updateComment(numbAd, numbComment, commentDto, activeEnemy);
        });
//        CommentDto commentDto = restTemplate.patchForObject(startPath + "/ads/1/comments/1", "Тест другой", CommentDto.class);
    }


}
