package ru.hexaend.chat_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.hexaend.chat_service.entity.Chat;
import ru.hexaend.chat_service.service.interfaces.KafkaService;
import ru.hexaend.kafka.events.chat.AddUserChatEvent;
import ru.hexaend.kafka.events.chat.CreateChatEvent;
import ru.hexaend.kafka.events.chat.RemoveUserChatEvent;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Override
    public void sendCreateChatEvent(Chat chat) {
        String eventId = generateUID();
        CreateChatEvent createChatEvent = CreateChatEvent.newBuilder()
                .setEventId(eventId)
                .setChatId(chat.getId())
                .setChatName(chat.getName())
                .setUserId(chat.getOwnerId())
                .setChatType(chat.getType().name())
                .build();

        kafkaTemplate.send("chat-topic", createChatEvent.getChatId(), createChatEvent);
    }

    @Override
    public void sendAddUserEvent(Long ownerId, Long chatId, Long userId) {
        String eventId = generateUID();
        AddUserChatEvent addUserChatEvent = AddUserChatEvent.newBuilder()
                .setEventId(eventId)
                .setChatId(chatId)
                .setUserId(ownerId)
                .setAddedUserId(userId)
                .build();

        kafkaTemplate.send("chat-topic", addUserChatEvent.getChatId(), addUserChatEvent);
    }

    @Override
    public void sendRemoveUserChat(Long ownerId, Long chatId, Long userId) {

        String eventId = generateUID();

        RemoveUserChatEvent removeUserChatEvent = RemoveUserChatEvent.newBuilder()
                .setEventId(eventId)
                .setChatId(chatId)
                .setUserId(ownerId)
                .setRemovedUserId(userId)
                .build();

        kafkaTemplate.send("chat-topic", removeUserChatEvent.getChatId(), removeUserChatEvent);
    }

    private String generateUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
