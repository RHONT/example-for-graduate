package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.service.AdService;

import java.io.IOException;

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
        return adService.findAllAds();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public AdDto addAd(@RequestPart(name = "properties") CreateOrUpdateAdDto properties,
                       @RequestPart(name = "image") MultipartFile file,
                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return adService.adAd(properties, file, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}")
    public ExtendedAdDto getInfoAboutAd(@PathVariable Integer id) {
        log.info("Activated getAds method.");
        return adService.findInfoAboutAd(id);
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "{id}")
    public void removeAd(@PathVariable Integer id) {
        adService.deleteAdEntity(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/{id_ad}")
    public AdDto updateAds(@PathVariable Integer id_ad,
                           @RequestBody CreateOrUpdateAdDto updateAdDto) {
        log.info("Activated updateAds method.");
        return adService.updateAd(id_ad, updateAdDto);
    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping(path = "/me")
//    public AdsDto getAdsMe(Authentication authentication) {
//        log.info("Activated getAdsMe method.");
//        return adService.findMyAds(authentication);
//    }
//
//    @PatchMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<byte[]> updateImage(@PathVariable("id") Integer id,
//                                              @RequestParam("image") MultipartFile image) throws IOException {
//        log.info("Activated updateImage method.");
//        byte[] updatedImageBytes = image.getBytes();
//        //Здесь будет код
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(updatedImageBytes);
//    }

}