package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import ru.hexaend.auth_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        // set server.address and server.port on the service using reflection
        ReflectionTestUtils.setField(emailService, "address", "localhost");
        ReflectionTestUtils.setField(emailService, "port", "8081");
    }

    @DisplayName("sendVerificationEmail sends email with verification link")
    @Test
    void sendVerificationEmailSendsMessage() {
        // given
        User user = new User();
        user.setEmail("test@example.com");

        // when
        emailService.sendVerificationEmail(user, "code123");

        // then
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();
        assertArrayEquals(new String[]{"test@example.com"}, sent.getTo());
        assertTrue(sent.getText().contains("/auth/verify?code=code123"));
        assertEquals("Email Verification", sent.getSubject());
    }

    @DisplayName("sendResetPasswordEmail sends email with reset link")
    @Test
    void sendResetPasswordEmailSendsMessage() {
        User user = new User();
        user.setEmail("foo@example.com");

        emailService.sendResetPasswordEmail(user, "reset-abc");

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();
        assertArrayEquals(new String[]{"foo@example.com"}, sent.getTo());
        assertTrue(sent.getText().contains("/auth/reset?code=reset-abc"));
        assertEquals("Email Verification", sent.getSubject());
    }

    @DisplayName("sendPasswordResetConfirmationEmail sends confirmation message")
    @Test
    void sendPasswordResetConfirmationEmail() {
        User user = new User();
        user.setEmail("bar@example.com");

        emailService.sendPasswordResetConfirmationEmail(user);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();
        assertArrayEquals(new String[]{"bar@example.com"}, sent.getTo());
        assertEquals("Password Reset Confirmation", sent.getSubject());
        assertTrue(sent.getText().contains("successfully reset"));
    }

    @DisplayName("sendPasswordChangeEmail sends notification")
    @Test
    void sendPasswordChangeEmail() {
        User user = new User();
        user.setEmail("baz@example.com");

        emailService.sendPasswordChangeEmail(user);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();
        assertArrayEquals(new String[]{"baz@example.com"}, sent.getTo());
        assertEquals("Password Change Notification", sent.getSubject());
        assertTrue(sent.getText().contains("changed successfully"));
    }
}

