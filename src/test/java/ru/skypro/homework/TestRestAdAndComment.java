package ru.skypro.homework;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.controller.CommentController;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TestRestAdAndComment {
    @LocalServerPort
    int port;

    private final AdsController adsController;
    private final CommentController commentController;

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


    public TestRestAdAndComment(AdsController adsController, CommentController commentController) {
        this.adsController = adsController;
        this.commentController = commentController;
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
}
