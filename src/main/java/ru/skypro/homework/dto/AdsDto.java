package ru.skypro.homework.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AdsDto {
    private Integer count;
    private ArrayList<AdDto> results;
}
