package ru.hexaend.auth_service.dto.request;

public record RegisterRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName
) {
}
