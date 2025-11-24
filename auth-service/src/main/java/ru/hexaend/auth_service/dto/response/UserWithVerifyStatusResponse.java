package ru.hexaend.auth_service.dto.response;

public record UserWithVerifyStatusResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean isVerified
) {
}
