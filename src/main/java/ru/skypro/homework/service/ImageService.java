package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.repository.ImageRepository;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageEntity goImageToBD(MultipartFile file) throws IOException {
        String source="id-image/";
        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));

        ImageEntity image = new ImageEntity();
        image.setData(file.getBytes());
        image.setMediaType(file.getContentType());
        image.setFileSize(file.getSize());
        image.setFilePath(source);

        return image;
    }

    private String getExtension(String fileName) {
        String substring = fileName.substring(fileName.lastIndexOf(".") + 1);
        return substring;
    }
}
