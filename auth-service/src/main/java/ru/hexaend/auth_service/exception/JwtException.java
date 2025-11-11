package ru.hexaend.auth_service.exception;

public class JwtException extends BaseException {
    public JwtException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    public JwtException(String message) {
        super(message);
    }
}

