package ru.skypro.homework.exceptions;


public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String attemptedUnauthorizedAccess) {
    }
}
