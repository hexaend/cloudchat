package ru.hexaend.service;

import ru.hexaend.dto.request.AuthRequest;
import ru.hexaend.dto.request.RegisterRequest;
import ru.hexaend.dto.response.AuthResponse;
import ru.hexaend.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    AuthResponse refreshAccessToken(String refreshToken);
//    void refreshToken(String refreshToken);
//    void logout(String refreshToken);
//    void changePassword(String username, String oldPassword, String newPassword);
//    void sendPasswordResetEmail(String email);
//    void emailVerification(String email); TODO: implement this method

}
