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
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Должны находиться в базе три тестовых пользователя
 * user@gmail.com - права USER
 * enemy@gmail.com- права USER
 * admin@gmail.com- права ADMIN
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
    CreateOrUpdateAdDto updateAdDto;


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

        updateAdDto = CreateOrUpdateAdDto.builder().
                title("Тестовый заголовок").
                description("Тестовое описание").
                price(999).build();
    }

    @Test
    @Order(1)
    void initController() {
        assertThat(adsController).isNotNull();
        assertThat(commentController).isNotNull();
    }


    @Test
    @Order(2)
    void masterOfAdOperations() {
        HttpHeaders headers = getHeaderUser();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("properties", updateAdDto);
        form.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestEntityWithDto = new HttpEntity<>(form, headers);

        HttpHeaders headerForUpdateImage = getHeaderUser();
        headerForUpdateImage.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> formForImage = new LinkedMultiValueMap<>();
        formForImage.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestEntityWithDto2 =
                new HttpEntity<>(formForImage, headerForUpdateImage);

        ResponseEntity<AdDto> exchangeAddAd =                   // Добавляем объявление
                restTemplate.exchange(
                        addAdPath,
                        HttpMethod.POST,
                        requestEntityWithDto,
                        AdDto.class);

        assertThat(exchangeAddAd.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertEquals(999, Objects.requireNonNull(exchangeAddAd.getBody()).getPrice());

        int idForDeleteAd = exchangeAddAd.getBody().getPk();     // Заносим id для дальнейших операций

        ResponseEntity<ExtendedAdDto> exchangeGetAdById =       // Находим объявление по id
                restTemplate.exchange(
                        getInfoAdPath,
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaderUser()),
                        ExtendedAdDto.class, idForDeleteAd);

        assertThat(exchangeGetAdById.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(Objects.requireNonNull(exchangeGetAdById.getBody()).getDescription());

        ResponseEntity<AdDto> exchangeUpdateAdUser =                 // Хозяин обновляет объявление
                restTemplate.exchange(
                        updateAdPath,
                        HttpMethod.PATCH,
                        new HttpEntity<CreateOrUpdateAdDto>(updateAdDto,getHeaderUser()),
                        AdDto.class, idForDeleteAd);
        assertThat(exchangeUpdateAdUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(Objects.requireNonNull(exchangeUpdateAdUser.getBody()).getTitle());

        ResponseEntity<byte[]> exchangeUpdateImageAdUser =                 // Хозяин обновляет картинку объявления
                restTemplate.exchange(
                        updateImageAdPath,
                        HttpMethod.PATCH,
                        requestEntityWithDto2,
                        byte[].class, idForDeleteAd);
        assertThat(exchangeUpdateImageAdUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(Objects.requireNonNull(exchangeUpdateImageAdUser.getBody()));

        ResponseEntity<Void> exchangeDeleteAd =                 // удаляет обявление хозяин USER
                restTemplate.exchange(
                        deleteAdPath,
                        HttpMethod.DELETE,
                        new HttpEntity<>(getHeaderUser()),
                        Void.class, idForDeleteAd);
        assertThat(exchangeDeleteAd.getStatusCode()).isEqualTo(HttpStatus.OK);

        exchangeGetAdById =                                     // Пытаемся найти удаленное объявление
                restTemplate.exchange(
                        getInfoAdPath,
                        HttpMethod.GET,
                        getHttpWithAuthAndNotBody(),
                        ExtendedAdDto.class, idForDeleteAd);
        assertThat(exchangeGetAdById.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Попытки другого пользователя удалить/редактировать/ чужие данные
     *
     */
    @Test
    void enemyOfAdOperations(){
        HttpHeaders headers = getHeaderUser();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("properties", updateAdDto);
        form.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);

        HttpHeaders headerForUpdateImage = getHeaderEnemy();
        headerForUpdateImage.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        MultiValueMap<String, Object> formForImage = new LinkedMultiValueMap<>();
        formForImage.add("image", getTestFile());
        HttpEntity<MultiValueMap<String, Object>> requestEntityWithDto2 =
                new HttpEntity<>(formForImage, headerForUpdateImage);

        ResponseEntity<AdDto> exchangeAddAd =                   // Добавляем объявление от лица хозяина
                restTemplate.exchange(
                        addAdPath,
                        HttpMethod.POST,
                        requestEntity,
                        AdDto.class);

        assertThat(exchangeAddAd.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertEquals(999, Objects.requireNonNull(exchangeAddAd.getBody()).getPrice());

        int idAd = exchangeAddAd.getBody().getPk();

        ResponseEntity<Void> exchangeDeleteAdEnemy =                 // Другой USER пытаеться удалить объявление
                restTemplate.exchange(
                        deleteAdPath,
                        HttpMethod.DELETE,
                        new HttpEntity<>(getHeaderEnemy()),
                        Void.class, idAd);
        assertThat(exchangeDeleteAdEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        ResponseEntity<AdDto> exchangeUpdateAdEnemy =                 // Другой USER пытается обновить объявление
                restTemplate.exchange(
                        updateAdPath,
                        HttpMethod.PATCH,
                        new HttpEntity<CreateOrUpdateAdDto>(updateAdDto,getHeaderEnemy()),
                        AdDto.class, idAd);
        assertThat(exchangeUpdateAdEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
// todo Мы же картинку ищем, а у нее другой номер
        ResponseEntity<byte[]> exchangeUpdateImageAdEnemy =                 // Другой обновляет чужую картинку объявления
                restTemplate.exchange(
                        updateImageAdPath,
                        HttpMethod.PATCH,
                        requestEntityWithDto2,
                        byte[].class, idAd);
        assertThat(exchangeUpdateImageAdEnemy.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(3)
    void getAllAds() {
        ResponseEntity<AdsDto> exchange = restTemplate.exchange(getAllAdPath, HttpMethod.GET, getHttpWithAuthAndNotBody(), AdsDto.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(Objects.requireNonNull(exchange.getBody()).getCount() > 0);
    }

//    @Test
//    @Order(4)
//    void getInfoAboutAd() {
//
//    }

//    @Test
//    void removeAd() {
//    }

    @Test
    void updateAds() {
    }

    /**
     * Находим все объявления авторизованного пользователя
     */
    @Test
    void getAdsMe() {
        ResponseEntity<AdsDto> exchangeSelfAllAds =
                restTemplate.exchange(
                        getSelfUserAllAdPath,
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaderUser()),
                        AdsDto.class);
        assertTrue(Objects.requireNonNull(exchangeSelfAllAds.getBody()).getCount() > 0);

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

    /**
     * Череда из трех методов для возврата заголовков аутентификации пользователя
     *
     * @return
     */
    private HttpHeaders getHeaderUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user@gmail.com", "123123123");
        return headers;

    }

    private HttpHeaders getHeaderEnemy() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        return headers;
    }

    private HttpHeaders getHeaderAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enemy@gmail.com", "123123123");
        return headers;
    }
}
