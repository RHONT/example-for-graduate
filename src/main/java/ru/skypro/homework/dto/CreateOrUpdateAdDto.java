package ru.skypro.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrUpdateAdDto {
    private String title;
    private Integer price;
    private String description;
}
