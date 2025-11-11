package ru.hexaend.auth_service.dto.response;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean emailVerified) {
}
