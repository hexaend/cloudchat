package ru.hexaend.auth_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.repository.RoleRepository;

import java.util.List;

import static java.awt.SystemColor.text;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AuthServiceApplication implements CommandLineRunner  {

    private final RoleRepository roleRepository;

    public AuthServiceApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // TODO: https://habr.com/ru/companies/owasp/articles/531716/
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }


    // TODO: remove to flyway migration
    @Override
    public void run(String... args) throws Exception {

        Role adminRole = Role.builder()
                .name("ADMIN")
                .description("Administrator with full access")
                .build();

        Role userRole = Role.builder()
                .name("USER")
                .description("Regular user with limited access")
                .build();

//        roleRepository.saveAll(List.of(userRole, adminRole));
        log.info("Auth Service started");
    }
}
