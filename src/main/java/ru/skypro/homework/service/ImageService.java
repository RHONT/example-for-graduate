package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.exceptions.ImageNotFoundException;
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.mappers.ImageMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.ImageRepository;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final String source = "id-image/";

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final AdsRepository adsRepository;

    /**
     * Сохраняем картинку в базу. Двойное сохранение требуется для установления пути.
     * Так как оно привзяано к id картинки
     * Example: "id-image/5"
     * @param file
     * @return
     * @throws IOException
     */
    public ImageEntity createImageEntity(MultipartFile file) throws IOException {

        ImageEntity image = new ImageEntity();
        imageMapper.updateImageEntityFromFile(file,image);
        image.setFilePath(source);
        return image;
    }

    //todo Возможны ли ситуации, когда мы можем создать объявление без картинки?
    /**
     * Обновляем уже существующую картинку
     * @param id картинки
     * @param file на обновление
     * @return
     */
    public ImageEntity updateImageAdEntity(Integer id, MultipartFile file, UserDetails userDetails) throws IOException {
        Optional<ImageEntity> image = imageRepository.findById(id);
        if (image.isPresent()) {
            AdEntity ad=adsRepository.findByImageEntity(image.get()).get();
            checkAuthority(userDetails,ad);
            imageMapper.updateImageEntityFromFile(file, image.get());
            imageRepository.save(image.get());
            return image.get();
        }
         else {
            log.debug("Image with id {}, not found", id);
            throw new ImageNotFoundException("Image for ad with id" + id + " not found");
        }
    }

    /**
     * Проверка является ли комментарий личным
     */
    private boolean itISUserAd(UserDetails userDetails, AdEntity ad) {
        return Objects.equals(userDetails.getUsername(), ad.getAuthor().getUsername());
    }

    /**
     * Если авторизованный пользователь админ, то он имеет доступ на корректировку любого комментария
     * @param userDetails
     * @return
     */
    private boolean userIsAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Аккумулированный метод использующий userIsAdmin() и itISUserComment(), и если все плохо кидаем
     * исключение и пишем в лог событие
     * @param userDetails
     * @param ad
     */
    private void checkAuthority(UserDetails userDetails, AdEntity ad) {
        if (!itISUserAd(userDetails, ad) && !userIsAdmin(userDetails)) {
            log.debug("Attempted unauthorized access id ad={}", ad.getPk());
            throw new UnauthorizedException("Attempted unauthorized access");
        }
    }



}
