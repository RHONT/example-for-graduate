package ru.skypro.homework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AdNotDeletedException extends RuntimeException {
    public AdNotDeletedException() {
        super();
    }

    public AdNotDeletedException(String message) {
        super(message);
    }

    public AdNotDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdNotDeletedException(Throwable cause) {
        super(cause);
    }

    protected AdNotDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
