package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class ImageDto {
    private byte[] data;
    private String mediaType;
    private String fileSize;
    private String filePath;

}
