package ru.hexaend.chat_service.service.interfaces;

import ru.hexaend.chat_service.entity.Chat;

public interface KafkaService {

    void sendCreateChatEvent(Chat chat);

    void sendAddUserEvent(Long ownerId, Long chatId, Long userId);

    void sendRemoveUserChat(Long ownerId, Long chatId, Long userId);
}
