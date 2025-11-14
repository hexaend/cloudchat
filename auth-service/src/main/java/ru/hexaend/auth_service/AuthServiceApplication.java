package ru.hexaend.auth_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import static java.awt.SystemColor.text;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class AuthServiceApplication  {

    // TODO: https://habr.com/ru/companies/owasp/articles/531716/
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }


}
