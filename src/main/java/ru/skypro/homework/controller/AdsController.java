package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.service.AdService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("ads/")
public class AdsController {
    private final AdService adService;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public AdsDto getAllAds() {
        log.info("Activated getAllAds method.");
        AdsDto adsDto = new AdsDto();
        ArrayList<AdDto> list = new ArrayList<>(List.of(new AdDto(), new AdDto(), new AdDto()));
        adsDto.setResults(list);

        return adsDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public AdDto addAdd(@RequestPart("UpdateAdDto") CreateOrUpdateAdDto createOrUpdateAdDto,
                        @RequestPart("urlImage") String urlImage) {
        return adService.createOrUpdateAd(createOrUpdateAdDto, urlImage);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AdDto addAd(@RequestBody CreateOrUpdateAdDto createOrUpdateAdDto,
                       @RequestParam("urlImage") String urlImage) {
        AdDto adDto = new AdDto();
        adDto.setTitle(createOrUpdateAdDto.getTitle());
        adDto.setPrice(createOrUpdateAdDto.getPrice());
        adDto.setImage(urlImage);
        log.info("Activated addAd method.");
        return adDto;
    }

    //Получение информации об объявлении
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}")
    public ExtendedAdDto getInfoAboutAd(@PathVariable Integer id) {
        log.info("Activated getAds method.");
        return adService.findInfoAboutAd(id);
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void removeAd(@RequestParam Integer id) {
        log.info("Activated removeAd method.");
        adService.deleteAdEntity (id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping
    public AdDto updateAds(@RequestParam Integer id,
                           @RequestBody CreateOrUpdateAdDto updateAdDto) {
        log.info("Activated updateAds method.");
        return adService.updateAd(id, updateAdDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/me")
    public AdsDto getAdsMe(Authentication authentication) {
        log.info("Activated getAdsMe method.");
        return adService.findMyAds(authentication);
    }

    @PatchMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(@PathVariable("id") Integer id,
                                              @RequestParam("image") MultipartFile image) throws IOException {
        log.info("Activated updateImage method.");
        byte[] updatedImageBytes = image.getBytes();
        //Здесь будет код
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(updatedImageBytes);
    }

}