package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("ads")
public class AdsController {
    private final AdService adService;
    private final ImageService imageService;


    @Operation(
            summary = "Поиск объявлений по ID пользователя",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Объявления успешно найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdsDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Объявление"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public AdsDto getAllAds() {
        return adService.findAllAds();
    }

    @Operation(
            summary = "Создание нового объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Объявление успешно создано",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Объявление"
    )
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_USER")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public AdDto addAd(@RequestPart CreateOrUpdateAdDto properties,
                       @RequestPart(name = "image") MultipartFile file,
                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return adService.adAd(properties, file, userDetails);
    }

    @Operation(
            summary = "Получит информацию по объявления через ID ",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Объявление успешно найдено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExtendedAdDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Объявление"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}")
    public ExtendedAdDto getInfoAboutAd(@PathVariable Integer id) {
        log.info("Activated getAds method.");
        return adService.findInfoAboutAd(id);
    }

    @Operation(
            summary = "Удалить объявление через ID ",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Объявление успешно удалено"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Объявление"
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "{id}")
    public void removeAd(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        adService.deleteAdEntity(id, userDetails);
    }

    @Operation(
            summary = "Обновить объявление через ID ",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Объявление успешно изменено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Объявление"
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PatchMapping(path = "/{id_ad}")
    public AdDto updateAds(@PathVariable Integer id_ad,
                           @RequestBody CreateOrUpdateAdDto updateAdDto,
                           @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Activated updateAds method.");
        return adService.updateAd(id_ad, updateAdDto, userDetails);
    }


    @Operation(
            summary = "Найти все объявления по текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Объявления найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdsDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Объявление"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/me")
    public AdsDto getAdsMe(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Activated getAdsMe method.");
        return adService.findMyAds(userDetails);
    }

    @Operation(
            summary = "Обновление изображения объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Изображение успешно обновлено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Объявление"
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PatchMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImageAd(@PathVariable(name = "id") Integer id,
                                                @RequestParam("image") MultipartFile image,
                                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        log.info("Activated updateImage method.");
        byte[] dataForResponse = imageService.updateImageAd(id, image,userDetails);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(dataForResponse);
    }

}