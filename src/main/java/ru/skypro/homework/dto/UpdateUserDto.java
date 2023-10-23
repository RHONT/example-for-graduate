package ru.skypro.homework.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String phone;
}
