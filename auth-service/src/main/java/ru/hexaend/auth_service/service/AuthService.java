package ru.hexaend.auth_service.service;

import ru.hexaend.auth_service.dto.request.AuthRequest;
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.dto.response.UserResponse;

public interface AuthService {

    AuthResponse login(AuthRequest request);
    AuthResponse refreshAccessToken(RefreshTokenRequest refreshToken);
    void verifyToken(String token);
//    void refreshToken(String refreshToken);
//    void logout(String refreshToken);
//    void changePassword(String username, String oldPassword, String newPassword);
//    void sendPasswordResetEmail(String email);
//    void emailVerification(String email); TODO: implement this method

}
