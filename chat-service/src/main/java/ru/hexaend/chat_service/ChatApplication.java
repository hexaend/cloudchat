package ru.hexaend.chat_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
    // TODO: kafka - last message, unread messages count
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
