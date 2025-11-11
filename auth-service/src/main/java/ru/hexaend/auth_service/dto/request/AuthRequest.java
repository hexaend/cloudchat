package ru.hexaend.auth_service.dto.request;

public record AuthRequest(
        String username,
        String password
) {
}
