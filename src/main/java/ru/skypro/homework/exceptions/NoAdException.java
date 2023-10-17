package ru.skypro.homework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoAdException extends RuntimeException {
    public NoAdException(String s) {
    }
}
