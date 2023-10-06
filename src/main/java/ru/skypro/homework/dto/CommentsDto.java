package ru.skypro.homework.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CommentsDto {
    private Integer count;
    private ArrayList<CommentDto> results;
}
