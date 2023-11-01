package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginDto {
    private String username;
    private String password;
}
