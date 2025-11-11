package ru.hexaend.auth_service.dto.response;

public record VerifyStatusRequest(
        String status,
        String message
) {
}
