package ru.hexaend.message_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.hexaend.kafka.events.chat.AddUserChatEvent;
import ru.hexaend.kafka.events.chat.CreateChatEvent;
import ru.hexaend.kafka.events.chat.RemoveUserChatEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConsumer {

    @KafkaListener(topics = "chat-topic", groupId = "message-service")
    public void consume(ConsumerRecord<Long, Object> record) {
        Object event = record.value();

        log.info("Received event: {} with key: {}", event.getClass().getSimpleName(), record.key());

        switch (event) {
            case CreateChatEvent e -> log.info("CreateChatEvent received for chatId: {}", e.getChatId());
            case AddUserChatEvent e -> log.info("AddUserChatEvent received for chatId: {}, addedUserId: {}", e.getChatId(), e.getAddedUserId());
            case RemoveUserChatEvent e -> log.info("");
            default -> log.warn("Unknown event type: {}", event.getClass());
        }
    }
}