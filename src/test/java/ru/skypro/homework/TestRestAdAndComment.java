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
import ru.skypro.homework.dto.*;

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
 * admin@gmail.com- права ADMIN. (тесты пока не добавлены, ибо верую, что работает)
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

    private final AdCommentIdRepo idRepo = new AdCommentIdRepo();

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

    CreateOrUpdateAdDto updateAdDto;
    CreateOrUpdateComment commentDto;

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

        updateAdDto = CreateOrUpdateAdDto.builder().
                title("Тестовый заголовок").
                description("Тестовое описание").
                price(999).build();

        commentDto = new CreateOrUpdateComment();
        commentDto.setText("Тестовый комментарий");
    }

    @Test
    @Order(1)
    void initController() {
        assertThat(adsController).isNotNull();
        assertThat(commentController).isNotNull();
    }
    /**
     * Действия хозяина над собственным объявлением
     */
    @Test
    void masterOfAdOperations() {
        HttpHeaders headerForUpdateImage = getHeaderUser();
        headerForUpdateImage.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> formForImage = new LinkedMultiValueMap<>();
        formForImage.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestEntityWithDto2 =
                new HttpEntity<>(formForImage, headerForUpdateImage);

        refreshDataUser();
        int idAd = idRepo.getIdAd();

        ResponseEntity<ExtendedAdDto> exGetAdById =       // Находим объявление по id
                restTemplate.exchange(
                        getInfoAdPath,
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaderUser()),
                        ExtendedAdDto.class, idAd);

        assertThat(exGetAdById.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(Objects.requireNonNull(exGetAdById.getBody()).getDescription());

        ResponseEntity<AdDto> exUpdateAdMaster =                 // Хозяин обновляет объявление
                restTemplate.exchange(
                        updateAdPath,
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateAdDto, getHeaderUser()),
                        AdDto.class, idAd);
        assertThat(exUpdateAdMaster.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(Objects.requireNonNull(exUpdateAdMaster.getBody()).getTitle());

        ResponseEntity<byte[]> exUpdateImageAdMaster =                 // Хозяин обновляет картинку объявления
                restTemplate.exchange(
                        updateImageAdPath,
                        HttpMethod.PATCH,
                        requestEntityWithDto2,
                        byte[].class, idAd);
        assertThat(exUpdateImageAdMaster.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(Objects.requireNonNull(exUpdateImageAdMaster.getBody()));

        ResponseEntity<Void> exDeleteAdMaster =                 // удаляет обявление хозяин USER
                restTemplate.exchange(
                        deleteAdPath,
                        HttpMethod.DELETE,
                        new HttpEntity<>(getHeaderUser()),
                        Void.class, idAd);
        assertThat(exDeleteAdMaster.getStatusCode()).isEqualTo(HttpStatus.OK);

        exGetAdById =                                     // Пытаемся найти удаленное объявление
                restTemplate.exchange(
                        getInfoAdPath,
                        HttpMethod.GET,
                        getHttpWithAuthAndNotBody(),
                        ExtendedAdDto.class, idAd);
        assertThat(exGetAdById.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Хозяин комментарий совершает действия
     */
    @Test
    void masterOfCommentOperations() {
        refreshDataUser();
        int idAd = idRepo.getIdAd();
        int idComment = idRepo.getIdComment();

        ResponseEntity<CommentDto> exUpdateCommentMaster =           // Хозяин обновляет свой комментарий
                restTemplate.exchange(
                        deleteCommentPath,
                        HttpMethod.PATCH,
                        new HttpEntity<>(commentDto, getHeaderUser()),
                        CommentDto.class, idAd, idComment);
        assertThat(exUpdateCommentMaster.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> exDeleteCommentMaster =                 //  Хозяин удаляет свой комментарий
                restTemplate.exchange(
                        deleteCommentPath,
                        HttpMethod.DELETE,
                        new HttpEntity<>(getHeaderUser()),
                        Void.class, idAd, idComment);
        assertThat(exDeleteCommentMaster.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Действия пользователя над чужими объявлениями
     * удалить/редактировать/
     */
    @Test
    void enemyOfAdOperations() {
        HttpHeaders headerForUpdateImage = getHeaderEnemy();
        headerForUpdateImage.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> formForImage = new LinkedMultiValueMap<>();
        formForImage.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestEntityWithDto2 =
                new HttpEntity<>(formForImage, headerForUpdateImage);

        refreshDataUser();
        int idAd = idRepo.getIdAd();

        ResponseEntity<Void> exDeleteAdEnemy =                 // Другой USER пытаеться удалить объявление
                restTemplate.exchange(
                        deleteAdPath,
                        HttpMethod.DELETE,
                        new HttpEntity<>(getHeaderEnemy()),
                        Void.class, idAd);
        assertThat(exDeleteAdEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<AdDto> exUpdateAdEnemy =                 // Другой USER пытается обновить объявление
                restTemplate.exchange(
                        updateAdPath,
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateAdDto, getHeaderEnemy()),
                        AdDto.class, idAd);
        assertThat(exUpdateAdEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<byte[]> exUpdateImageAdEnemy =                 // Другой обновляет чужую картинку объявления
                restTemplate.exchange(
                        updateImageAdPath,
                        HttpMethod.PATCH,
                        requestEntityWithDto2,
                        byte[].class, idAd);
        assertThat(exUpdateImageAdEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Операции враждебного пользователя над чужими комментариями
     */
    @Test
    void enemyOfCommentOperations() {
        refreshDataUser();
        int idAd = idRepo.getIdAd();
        int idComment = idRepo.getIdComment();

        ResponseEntity<Void> exDeleteCommentEnemy =                 // Другой USER пытаеться удалить комментарий
                restTemplate.exchange(
                        deleteCommentPath,
                        HttpMethod.DELETE,
                        new HttpEntity<>(getHeaderEnemy()),
                        Void.class, idAd, idComment);
        assertThat(exDeleteCommentEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<CommentDto> exUpdateCommentEnemy =                 // Другой USER пытается обновить комментарий
                restTemplate.exchange(
                        deleteCommentPath, HttpMethod.PATCH,
                        new HttpEntity<>(commentDto, getHeaderEnemy()),
                        CommentDto.class, idAd, idComment);
        assertThat(exUpdateCommentEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    /**
     * Находим все обьявления по базе
     */
    @Test
    void getAllAds() {
        ResponseEntity<AdsDto> exchange =
                restTemplate.exchange(
                        getAllAdPath,
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaderUser()),
                        AdsDto.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(Objects.requireNonNull(exchange.getBody()).getCount() > 0);
    }

    /**
     * Находим все комментарии по id объявления
     */
    @Test
    void getComments() {
        refreshDataUser();
        int idAd = idRepo.getIdAd();
        ResponseEntity<CommentsDto> exGetAllCommentsById = restTemplate.exchange(
                getAllCommentByAdPath,
                HttpMethod.GET,
                new HttpEntity<>(getHeaderUser()),
                CommentsDto.class, idAd);
        assertTrue(Objects.requireNonNull(exGetAllCommentsById.getBody()).getCount() > 0);
    }

    /**
     * Находим все объявления авторизованного пользователя user@gmail.com
     */
    @Test
    void getAdsMe() {
        ResponseEntity<AdsDto> exchangeSelfAllAds = restTemplate.exchange(
                getSelfUserAllAdPath,
                HttpMethod.GET,
                new HttpEntity<>(getHeaderUser()),
                AdsDto.class);
        assertTrue(Objects.requireNonNull(exchangeSelfAllAds.getBody()).getCount() > 0);
    }

    /**
     * Возвращаем сущность http с авторизацией от user@gmail.com
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
     */
    private FileSystemResource getTestFile() {
        Path testFile = Paths.get("src/main/resources/image/test.jpg");
        return new FileSystemResource(testFile);
    }

    /**
     * Возвращает заголовок пользователя
     */
    private HttpHeaders getHeaderUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user@gmail.com", "123123123");
        return headers;
    }

    /**
     * Возвращает заголовок пользователя "враждебного"
     */
    private HttpHeaders getHeaderEnemy() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        return headers;
    }
    /**
     * Возвращает заголовок пользователя админа
     */
    private HttpHeaders getHeaderAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        return headers;
    }
    /**
     * idRepo - обертка для Map
     * Метод создает от user@gmail.com одно объявление и один комментарий к нему
     * Хранит в себе id объявления и комментария. Чтбы другие тесты могли всегда получать актульные id
     */
    private void refreshDataUser() {
        if (idRepo.getIdAd() != null || idRepo.getIdComment() != null) {
            idRepo.clear();
        }
        HttpHeaders headers = getHeaderUser();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("properties", updateAdDto);
        form.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestWithDto = new HttpEntity<>(form, headers);

        ResponseEntity<AdDto> exAddAd =
                restTemplate.exchange(addAdPath, HttpMethod.POST, requestWithDto, AdDto.class);
        int idAd = exAddAd.getBody().getPk();

        ResponseEntity<CommentDto> exComment = restTemplate.exchange(addCommentPath, HttpMethod.POST,
                new HttpEntity<>(commentDto, getHeaderUser()), CommentDto.class, idAd);
        assertNotNull(Objects.requireNonNull(exComment.getBody()).getAuthor());

        idRepo.setIdAd(exAddAd.getBody().getPk());
        idRepo.setIdComment(exComment.getBody().getPk());
    }
}
