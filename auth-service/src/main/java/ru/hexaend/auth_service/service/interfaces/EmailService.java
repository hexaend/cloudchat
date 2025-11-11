package ru.hexaend.auth_service.service.interfaces;

import ru.hexaend.auth_service.entity.User;

public interface EmailService {

    void sendVerificationEmail(User user, String verificationCode);

}
