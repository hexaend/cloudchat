package ru.hexaend.chat_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hexaend.chat_service.entity.Chat;
import ru.hexaend.chat_service.repository.ChatRepository;
import ru.hexaend.chat_service.service.interfaces.AuthService;
import ru.hexaend.chat_service.service.interfaces.ChatService;
import ru.hexaend.chat_service.service.interfaces.KafkaService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


// TODO: kafka transactions
// TODO: maybe change kafka events from services to capture with Debezium 
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final AuthService authService;
    private final ChatRepository chatRepository;
    private final KafkaService kafkaService;

    @Override
    public List<Chat> getAllChats() {
        Long userId = authService.getCurrentUserId();
        return chatRepository.findAllByUserId(userId);
    }

    @Override
    public Chat getChatById(Long id) {
        Chat chat = chatRepository.findById(id).orElseThrow(RuntimeException::new); // TODO: Custom exception
        Long userId = authService.getCurrentUserId();
        if (chat.getOwnerId().equals(userId) || chat.getParticipantIds().contains(userId)) {
            return chat;
        } else {
            throw new RuntimeException(); // TODO: Custom exception
        }
    }

    @Override
    public Chat getOrCreateDialogue(Long userId) {
        Long ownerId = authService.getCurrentUserId();

        if (ownerId.equals(userId)) {
            throw new RuntimeException(); // TODO: Custom exception
        }

        Optional<Chat> chat = chatRepository.findPrivateChatBetweenUsers(ownerId, userId);

        if (chat.isPresent()) {
            return chat.get();
        }

        // TODO: check user is friend

        Chat newChat = Chat.builder()
                .name("Chat between " + ownerId + " and " + userId)
                .ownerId(ownerId)
                .type(Chat.ChatType.PRIVATE)
                .participantIds(new HashSet<>(Set.of(userId, ownerId)))
                .build();


        chatRepository.save(newChat);

        kafkaService.sendCreateChatEvent(newChat);

        return newChat;
    }

    @Override
    public Chat createGroupChat(String name) {
        Long ownerId = authService.getCurrentUserId();
        Chat chat = Chat.builder()
                .name(name)
                .ownerId(ownerId)
                .type(Chat.ChatType.GROUP)
                .participantIds(new HashSet<>(Set.of(ownerId)))
                .build();

        chatRepository.save(chat);

        kafkaService.sendCreateChatEvent(chat);
        return chat;
    }

    @Override
    public void addParticipant(Long chatId, Long userId, String username) {
        Chat chat = getChatById(chatId);

        if (chat.getType() != Chat.ChatType.GROUP) {
            throw new RuntimeException(); // TODO: Custom exception
        }

        Long ownerId = authService.getCurrentUserId();
        if (!chat.getOwnerId().equals(ownerId)) {
            throw new RuntimeException(); // TODO: Custom exception
        }

        // TODO: check user existence by userId or username

        // TODO: get userId by username if userId is null
        chat.getParticipantIds().add(userId);

        chatRepository.save(chat);

        kafkaService.sendAddUserEvent(ownerId, chatId, userId);
    }

    @Override
    public void removeParticipant(Long chatId, Long userId, String username) {
        Chat chat = getChatById(chatId);

        if (chat.getType() != Chat.ChatType.GROUP) {
            throw new RuntimeException(); // TODO: Custom exception
        }

        Long ownerId = authService.getCurrentUserId();
        if (!chat.getOwnerId().equals(ownerId)) {
            throw new RuntimeException(); // TODO: Custom exception
        }

        if (chat.getParticipantIds().contains(userId)) {
            chat.getParticipantIds().remove(userId);
            chatRepository.save(chat);
            kafkaService.sendRemoveUserChat(ownerId, chatId, userId);
        } else {
            throw new RuntimeException(); // TODO: Custom exception
        }

    }
}
