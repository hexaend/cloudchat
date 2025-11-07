package ru.hexaend.dto.response;

public record LoginResponse(
        String type,
        String accessToken,
        String refreshToken
) {
}
