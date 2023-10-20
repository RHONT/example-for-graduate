package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private String image;
}

//"id": 0,
//        "email": "string",
//        "firstName": "string",
//        "lastName": "string",
//        "phone": "string",
//        "role": "USER",
//        "image": "string"
