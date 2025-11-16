package ru.hexaend.auth_service.service.impl;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.service.interfaces.EmailService;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${server.port}")
    private String port;

    @Value("${server.address}")
    private String address;

    @Override
    @Observed(name = "email.sendVerification", contextualName = "send-verification-email")
    public void sendVerificationEmail(User user, String verificationCode) {
        // TODO: customize email content from configuration/template
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Email Verification");
        String url = "http://" + address + ":" + port + "/auth/verify?code=" + verificationCode;
        msg.setText("Please verify your email by clicking the following link: " + url);
        mailSender.send(msg);
    }

    @Override
    public void sendResetPasswordEmail(User user, String resetPasswordCode) {
        // TODO: customize email content from configuration/template
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Email Verification");
        String url = "http://" + address + ":" + port + "/auth/reset?code=" + resetPasswordCode;
        msg.setText("Please reset your password by clicking the following link: " + url);
        mailSender.send(msg);
    }

    @Override
    public void sendPasswordResetConfirmationEmail(User user) {
        // TODO: customize email content from configuration/template
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Password Reset Confirmation");
        msg.setText("Your password has been successfully reset.");
        mailSender.send(msg);
    }

    @Override
    public void sendPasswordChangeEmail(User user) {
        // TODO: customize email content from configuration/template
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Password Change Notification");
        msg.setText("Your password has been changed successfully. If you did not perform this action, please contact support immediately.");
        mailSender.send(msg);
    }


}
