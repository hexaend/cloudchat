package ru.hexaend.auth_service.exception;

public class UsernameAlreadyInUseException extends BaseException {
    public UsernameAlreadyInUseException(String message) {
        super(message);
    }
}

