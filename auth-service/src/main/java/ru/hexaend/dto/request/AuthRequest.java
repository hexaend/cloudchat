package ru.hexaend.dto.request;

public record AuthRequest(
        String username,
        String password
) {
}
