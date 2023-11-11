package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.skypro.homework.entities.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMinimalDataDto {
    private String username;
    private String password;
    @ToString.Exclude
    private List<Role> roles;
}
