package ru.skypro.homework;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.controller.CommentController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Должны находиться в базе два тестовых пользователя
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRestAdAndComment {
    @LocalServerPort
    int port;

    private final AdsController adsController;
    private final CommentController commentController;
    private final RestTemplate restTemplate;

    String addCommentPath;
    String deleteCommentPath;
    String updateCommentPath;
    String getAllCommentByAdPath;

    String addAdPath;
    String updateAdPath;
    String deleteAdPath;
    String getAllAdPath;
    String getInfoAdPath;
    String getSelfUserAllAdPath;
    String updateImageAdPath;


    public TestRestAdAndComment(AdsController adsController, CommentController commentController, RestTemplate restTemplate) {
        this.adsController = adsController;
        this.commentController = commentController;
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void initVariable() {
        addCommentPath = "http://localhost:" + port + "/ads/{id}/comments";
        deleteCommentPath = "http://localhost:" + port + "/ads/{adId}/comments/{commentId}";
        updateCommentPath = "http://localhost:" + port + "/ads/{adId}/comments/{commentId}";
        getAllCommentByAdPath = "http://localhost:" + port + "/ads/{id}/comments";

        addAdPath = "http://localhost:" + port + "/ads";
        updateAdPath = "http://localhost:" + port + "/ads/{id}";
        deleteAdPath = "http://localhost:" + port + "/ads/{id}";
        getAllAdPath = "http://localhost:" + port + "/ads";
        getInfoAdPath = "http://localhost:" + port + "/ads/{id}";
        getSelfUserAllAdPath = "http://localhost:" + port + "/ads/me";
        updateImageAdPath = "http://localhost:" + port + "/ads/{id}/image";
    }

    @Test
    @Order(1)
    void initController(){
            assertThat(adsController).isNotNull();
            assertThat(commentController).isNotNull();
    }

    @Test
    void getAllAds() {
    }

    @Test
    void addAd() {
    }

    @Test
    void getInfoAboutAd() {
    }

    @Test
    void removeAd() {
    }

    @Test
    void updateAds() {
    }

    @Test
    void getAdsMe() {
    }

    @Test
    void updateImageAd() {
    }

    @Test
    void getComments() {
    }

    @Test
    void addComment() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void updateComment() {
    }
}
