package ru.hexaend.dto.response;

import lombok.Value;

public record AuthResponse(
        String type,
        String accessToken,
        String refreshToken
) {
}
