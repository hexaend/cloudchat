package ru.hexaend.auth_service.dto.response;

public record VerifyStatusResponse(
        String status,
        String message
) {
}
