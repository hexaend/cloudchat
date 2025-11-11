package ru.hexaend.auth_service.exception;

public class OpaqueTokenNotFoundException extends BaseException {
    public OpaqueTokenNotFoundException(String message) {
        super(message);
    }
}

