package ru.hexaend.auth_service.dto.response;

public record LoginResponse(
        String type,
        String accessToken,
        String refreshToken
) {
}
