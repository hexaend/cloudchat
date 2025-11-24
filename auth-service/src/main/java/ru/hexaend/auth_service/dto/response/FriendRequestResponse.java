package ru.hexaend.auth_service.dto.response;

public record FriendRequestResponse(
        Long id,
        UserResponse sender,
        UserResponse receiver,
        String status
) {
}
