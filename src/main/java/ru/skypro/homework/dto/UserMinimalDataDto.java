package ru.skypro.homework.dto;

import lombok.Data;
import lombok.ToString;
import ru.skypro.homework.entities.Role;

import java.util.List;
import java.util.Set;

@Data
@ToString
public class UserMinimalDataDto {
    private String username;
    private String password;
    @ToString.Exclude
    private List<Role> roles;
    @ToString.Exclude
    private Set<Integer> ads;
    @ToString.Exclude
    private Set<Integer> comments;
}
