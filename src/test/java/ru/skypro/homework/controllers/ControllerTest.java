package ru.skypro.homework.controllers;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.controller.AuthController;
import ru.skypro.homework.controller.CommentController;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.ForbiddenException;
import ru.skypro.homework.repository.*;
import org.assertj.core.api.Assertions;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.secutity.CustomUserDetailsService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.utilclass.JavaFileToMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // подробнее изучить.
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTest {
    @Value("${path.avito.image.folder.test}")
    private String pathToTestImage;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UserController userController;
    @Autowired
    private AuthController authController;
    @Autowired
    AdsController adsController;

    @Autowired
    private AdsRepository adsRepository;

    @Autowired
    private CommentController commentController;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private String startPath;

    private UserDetails activeUser;
    private UserDetails activeAdmin;
    private UserDetails activeEnemy;

    private final String userNameUser = "user@gmail.com";
    private final String adminNameUser = "admin@gmail.com";
    private final String enemyNameUser = "enemy@gmail.com";

    RegisterDto registerDto;

    @BeforeEach
    @Transactional
    @Rollback(value = false)
    void init() {
        activeUser = customUserDetailsService.loadUserByUsername(userNameUser);
        activeAdmin = customUserDetailsService.loadUserByUsername(adminNameUser);
        activeEnemy = customUserDetailsService.loadUserByUsername(enemyNameUser);
    }

    @Test
    @Order(1)
    void contextLoad() {
        Assertions.assertThat(authController).isNotNull();
        Assertions.assertThat(adsController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
    }

    @Test
    void login() {
        LoginDto loginDto = LoginDto.builder().username(userNameUser).password("123123123").build();
        int statusCodeValue = authController.login(loginDto).getStatusCodeValue();
        assertEquals(HttpStatus.OK.value(), statusCodeValue);
    }

    @Test
    @Transactional
    void register() {
        String userName = "TestPerson@gmail.com";
        RegisterDto registerDto=RegisterDto.builder().username(userName).password("222222222222").role("USER").build();
        authController.register(registerDto);

    }


    @Test
    void infoAboutAuthUser() {
        UserDto userDto = userController.infoAboutAuthUser(activeUser);
        assertEquals(userDto.getFirstName(), "Евгений");
    }

    @Test
    void updateUserDto() {
        authController.login(LoginDto.builder().username(userNameUser).password("123123123").build());
        String testName = "Думгай";
        String testLastName = "Петрович";

        UpdateUserDto update = UpdateUserDto.builder().firstName(testName).lastName(testLastName).build();
        UpdateUserDto updatedUser = userController.updateUserDto(update, activeUser);
        assertEquals(updatedUser.getFirstName(), testName);

        update = UpdateUserDto.builder().firstName("Евгений").lastName("Белых").build();
        userController.updateUserDto(update, activeUser);
    }

    @Test
    void addAd() throws IOException {
        authController.login(LoginDto.builder().username(userNameUser).password("123123123").build());
        String testTitle = "Test_Title";
        int testPrice = 128;
        String testDesc = "Test_Description";

        CreateOrUpdateAdDto newAd = CreateOrUpdateAdDto.builder().
                title(testTitle).
                price(testPrice).
                description(testDesc).build();

        JavaFileToMultipartFile myMultiPartFile =
                new JavaFileToMultipartFile(new File(pathToTestImage));
        AdDto adDto = adsController.addAd(newAd, myMultiPartFile, activeUser);
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

    @Test
    void updateImageAdd() {
    }

    @Test
    void addComment() {
        authController.login(LoginDto.builder().username(userNameUser).password("123123123").build());
        String testComment = "Change";
        UserEntity user = usersRepository.findByUsername(userNameUser).get();
        CreateOrUpdateComment comment = new CreateOrUpdateComment();
        AdEntity ad = AdEntity.builder().author(user).price(111).description("Test").title("testTitle").build();
        ad = adsRepository.save(ad);
        comment.setText(testComment);

        CommentDto commentDto = commentController.addComment(ad.getPk(), comment, activeUser);
        assertEquals(commentDto.getText(), testComment);
    }

    @Test
    @Transactional
    void getComments() {
        UserEntity user = usersRepository.findByUsername(userNameUser).get();
        AdEntity ad = user.getAdEntityList().get(0);
        CommentsDto comments = commentController.getComments(ad.getPk());
        assertEquals(ad.getCommentEntityList().size(), comments.getCount());
    }


    @Test
    void deleteComment() {
        authController.login(LoginDto.builder().username(userNameUser).password("123123123").build());
        UserEntity user = usersRepository.findByUsername(userNameUser).get();

        AdEntity ad = AdEntity.builder().author(user).price(111).description("Test").title("testTitle").build();
        CommentEntity commentEntity= CommentEntity.builder().text("Set").adEntity(ad).userEntity(user).build();
        ad = adsRepository.save(ad);
        commentEntity=commentsRepository.save(commentEntity);

        int numbAd = ad.getPk();
        int numbComment = commentEntity.getCommentId();

        commentController.deleteComment(numbAd, numbComment, activeUser);
        Optional<CommentEntity> commentFact = commentsRepository.findById(numbComment);
        assertTrue(commentFact.isEmpty());
    }

    @Test
    void deleteCommentAdmin() {
        authController.login(LoginDto.builder().username(adminNameUser).password("123123123").build());
        UserEntity user = usersRepository.findByUsername(userNameUser).get();

        AdEntity ad = AdEntity.builder().author(user).price(111).description("Test").title("testTitle").build();
        CommentEntity commentEntity= CommentEntity.builder().text("Set").adEntity(ad).userEntity(user).build();
        ad = adsRepository.save(ad);
        commentEntity=commentsRepository.save(commentEntity);

        int numbAd = ad.getPk();
        int numbComment = commentEntity.getCommentId();

        commentController.deleteComment(numbAd, numbComment, activeAdmin);
        Optional<CommentEntity> commentFact = commentsRepository.findById(numbComment);
        assertTrue(commentFact.isEmpty());
    }

    @Test
    void deleteCommentEnemyUser() {
        authController.login(LoginDto.builder().username(enemyNameUser).password("123123123").build());
        UserEntity user = usersRepository.findByUsername(userNameUser).get();

        AdEntity ad = AdEntity.builder().author(user).price(111).description("Test").title("testTitle").build();
        CommentEntity commentEntity= CommentEntity.builder().text("Set").adEntity(ad).userEntity(user).build();
        ad = adsRepository.save(ad);
        commentEntity=commentsRepository.save(commentEntity);

        int numbAd = ad.getPk();
        int numbComment = commentEntity.getCommentId();

        assertThrows(ForbiddenException.class, () -> {
            commentController.deleteComment(numbAd, numbComment, activeEnemy);
        });
    }


    @Test
    void updateComment() {
        authController.login(LoginDto.builder().username(userNameUser).password("123123123").build());
        UserEntity user = usersRepository.findByUsername(userNameUser).get();

        AdEntity ad = AdEntity.builder().author(user).price(111).description("Test").title("testTitle").build();
        CommentEntity commentEntity= CommentEntity.builder().text("Set").adEntity(ad).userEntity(user).build();
        ad = adsRepository.save(ad);
        commentEntity=commentsRepository.save(commentEntity);

        int numbAd = ad.getPk();
        int numbComment = commentEntity.getCommentId();

        CreateOrUpdateComment commentDto = new CreateOrUpdateComment();
        String userComment = "User_Comment";
        String adminComment = "Admin_Comment";
        String enemyComment = "Enemy_Comment";

        commentDto.setText(userComment);
        CommentDto commentUserDto = commentController.updateComment(numbAd, numbComment, commentDto, activeUser);
        assertEquals(userComment, commentUserDto.getText());

        authController.login(LoginDto.builder().username(adminNameUser).password("123123123").build());
        commentDto.setText(adminComment);
        CommentDto commentAdminDto = commentController.updateComment(numbAd, numbComment, commentDto, activeAdmin);
        assertEquals(adminComment, commentAdminDto.getText());

        commentDto.setText(enemyComment);
        assertThrows(ForbiddenException.class, () -> {
            commentController.updateComment(numbAd, numbComment, commentDto, activeEnemy);
        });
    }


}
