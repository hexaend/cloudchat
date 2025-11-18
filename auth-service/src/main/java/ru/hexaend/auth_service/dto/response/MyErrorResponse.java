package ru.hexaend.auth_service.dto.response;

public record MyErrorResponse(
        String code,
        String message
) {
}
