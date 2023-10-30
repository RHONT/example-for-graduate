package ru.skypro.homework;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.controller.CommentController;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Должны находиться в базе три тестовых пользователя
 * user@gmail.com - права USER
 * enemy@gmail.com- права USER
 * admin@gmail.com- права ADMIN
 * Должно существовать объявление с id = 1
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRestAdAndComment {
    @LocalServerPort
    int port;

    @Autowired
    private AdsController adsController;
    @Autowired
    private CommentController commentController;
    @Autowired
    private TestRestTemplate restTemplate;

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

    Integer testIdAd;


//    public TestRestAdAndComment(AdsController adsController, CommentController commentController, RestTemplate restTemplate) {
//        this.adsController = adsController;
//        this.commentController = commentController;
//        this.restTemplate = restTemplate;
//    }

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
    void initController() {
        assertThat(adsController).isNotNull();
        assertThat(commentController).isNotNull();
    }


    @Test
    @Order(2)
    void addAd() {
        CreateOrUpdateAdDto updateAdDto =
                CreateOrUpdateAdDto.builder().
                        title("Тестовый заголовок").
                        description("Тестовое описание").
                        price(999).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user@gmail.com", "123123123");
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("properties", updateAdDto);
        form.add("image", getTestFile());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);

        ResponseEntity<AdDto> exchange = restTemplate.exchange(addAdPath, HttpMethod.POST, requestEntity, AdDto.class);

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertEquals(999, Objects.requireNonNull(exchange.getBody()).getPrice());

    }

    @Test
    @Order(3)
    void getAllAds() {
        ResponseEntity<AdsDto> exchange = restTemplate.exchange(getAllAdPath, HttpMethod.GET, getHttpWithAuthAndNotBody(), AdsDto.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(Objects.requireNonNull(exchange.getBody()).getCount() > 0);
    }

    @Test
    @Order(4)
    void getInfoAboutAd() {
        ResponseEntity<ExtendedAdDto> exchange =
                restTemplate.exchange(
                        getInfoAdPath,
                        HttpMethod.GET,
                        getHttpWithAuthAndNotBody(),
                        ExtendedAdDto.class, 1);

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(exchange.getBody().getDescription());
    }

    @Test
    void removeAd() {
        CreateOrUpdateAdDto updateAdDto =
                CreateOrUpdateAdDto.builder().
                        title("Тестовый заголовок").
                        description("Тестовое описание").
                        price(999).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user@gmail.com", "123123123");
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("properties", updateAdDto);
        form.add("image", getTestFile());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);

        ResponseEntity<AdDto> exchange = restTemplate.exchange(addAdPath, HttpMethod.POST, requestEntity, AdDto.class);

        int idDeleted= exchange.getBody().getPk();


        ResponseEntity<Void> exchange2 =
                restTemplate.exchange(
                        deleteAdPath,
                        HttpMethod.DELETE,
                        getHttpWithAuthAndNotBody(),
                        Void.class, idDeleted);
        assertThat(exchange2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ExtendedAdDto> exchange3 =
                restTemplate.exchange(
                        getInfoAdPath,
                        HttpMethod.GET,
                        getHttpWithAuthAndNotBody(),
                        ExtendedAdDto.class, idDeleted);
        assertThat(exchange3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

    /**
     * Возвращаем сущность http с авторизацией, имитация живого пользователя
     *
     * @return
     */
    private HttpEntity<?> getHttpWithAuthAndNotBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user@gmail.com", "123123123");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    /**
     * Из файла делаем экземпляр FileSystemResource, ибо обычный файл не запихнуть в клиентский запрос
     *
     * @return
     */
    private FileSystemResource getTestFile() {
        Path testFile = Paths.get("src/main/resources/image/test.jpg");
        return new FileSystemResource(testFile);
    }
}
