package ru.hexaend.auth_service.exception;

public class EmailAlreadyInUseException extends BaseException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}

