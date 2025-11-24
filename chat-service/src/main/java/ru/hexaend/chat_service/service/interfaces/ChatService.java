package ru.hexaend.chat_service.service.interfaces;

import ru.hexaend.chat_service.entity.Chat;

import java.util.List;

public interface ChatService {
    List<Chat> getAllChats();

    Chat getChatById(Long id);

    Chat getOrCreateDialogue(Long userId);

    Chat createGroupChat(String name);

    void addParticipant(Long chatId, Long userId, String username);

    void removeParticipant(Long chatId, Long userId, String username);
}
