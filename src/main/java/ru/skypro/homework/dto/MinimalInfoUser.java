package ru.skypro.homework.dto;

public interface MinimalInfoUser {
    String getUsername();
    String getPassword();

    interface Roles{
        String getName();
    }
}
