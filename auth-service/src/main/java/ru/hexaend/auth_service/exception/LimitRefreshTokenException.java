package ru.hexaend.auth_service.exception;

public class LimitRefreshTokenException extends BaseException {
    public LimitRefreshTokenException(String message) {
        super(message);
    }

    public LimitRefreshTokenException() {
        super("Limit of refresh tokens reached");
    }
}
