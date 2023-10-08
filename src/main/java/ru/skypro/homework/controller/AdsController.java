package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("ads/")
public class AdsController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public AdsDto getAllAds (){
        log.info("Activated getAllAds method.");
        AdsDto adsDto=new AdsDto();
        ArrayList<AdDto> list=new ArrayList<>(List.of(new AdDto(),new AdDto(),new AdDto()));
        adsDto.setResults(list);

        return adsDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AdDto addAd (@RequestParam CreateOrUpdateAdDto createOrUpdateAdDto,
                        @RequestParam MultipartFile adWithImage){
        log.info("Activated addAd method.");
        return new AdDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}")
    public ExtendedAdDto getAds (@RequestParam Integer id){
        log.info("Activated getAds method.");
        return new ExtendedAdDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void removeAd (@RequestParam Integer id){
        log.info("Activated removeAd method.");
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping
    public AdDto updateAds (@RequestParam Integer id,
                            @RequestBody CreateOrUpdateAdDto updateAdDto){
        log.info("Activated updateAds method.");
        return new AdDto();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/me")
    public AdsDto getAdsMe (){
        log.info("Activated getAdsMe method.");
        return new AdsDto();
    }
    @PatchMapping(path = "/{id}/image")
    public ResponseEntity<byte[]> updateImage (@PathVariable("id") Integer id,
                                               @RequestParam("image") MultipartFile image){
        log.info("Activated updateImage method.");
        byte[] updatedImageBytes = null;
        //Здесь будет код
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(updatedImageBytes);
    }

}