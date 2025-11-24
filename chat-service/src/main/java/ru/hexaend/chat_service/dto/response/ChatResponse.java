package ru.hexaend.chat_service.dto.response;

import ru.hexaend.chat_service.entity.Chat;

import java.io.Serializable;
import java.util.Set;

public record ChatResponse(Long id, String name, Chat.ChatType type, Long ownerId,
                           Set<Long> participantIds) {
}