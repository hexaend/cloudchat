package ru.hexaend.auth_service.service.interfaces;

import ru.hexaend.auth_service.dto.request.AuthRequest;
import ru.hexaend.auth_service.dto.request.NewPasswordRequest;
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthRequest request);
    AuthResponse refreshAccessToken(RefreshTokenRequest refreshToken);
    void verifyToken(String token);
    void resetPassword(String code, NewPasswordRequest request);
}
