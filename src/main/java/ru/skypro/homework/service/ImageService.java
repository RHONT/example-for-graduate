package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.exceptions.ImageNotFoundException;
import ru.skypro.homework.mappers.ImageMapper;
import ru.skypro.homework.repository.ImageRepository;

import java.io.*;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final String source = "id-image/";

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

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
    public ImageEntity updateImageEntity(Integer id, MultipartFile file) throws IOException {
        Optional<ImageEntity> image = imageRepository.findById(id);
        if (image.isPresent()) {
            imageMapper.updateImageEntityFromFile(file, image.get());
            imageRepository.save(image.get());
            return image.get();
        } else {
            log.debug("Image with id {}, not found", id);
            throw new ImageNotFoundException("Image for ad with id" + id + " not found");
        }
    }
}
