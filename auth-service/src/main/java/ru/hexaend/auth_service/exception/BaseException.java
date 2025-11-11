package ru.hexaend.auth_service.exception;

public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, String... args) {
        super(String.format(message, (Object[]) args));
    }
}
