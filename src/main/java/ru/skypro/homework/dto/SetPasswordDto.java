package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class SetPasswordDto {
    String currentPassword;
    String newPassword;

}
