package ru.hexaend.chat_service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class ChatApplication {

//    private final KafkaTemplate<String, Event> kafkaTemplate;

    // TODO: kafka - last message, unread messages count
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

//    @Scheduled(cron = "*/5 * * * * *")
//    public void publishToKafka() {
//        Event event = Event.newBuilder()
//                .setUid(UUID.randomUUID().toString())
//                .setSubject("subject")
//                .setDescription("description")
//                .build();

//        kafkaTemplate.send(new ProducerRecord<>("test", event.getUid(), event));
//    }
}
