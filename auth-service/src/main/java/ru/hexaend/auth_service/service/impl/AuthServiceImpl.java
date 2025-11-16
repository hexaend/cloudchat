package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.dto.request.AuthRequest;
import ru.hexaend.auth_service.dto.request.NewPasswordRequest;
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.entity.Code;
import ru.hexaend.auth_service.exception.InvalidPasswordException;
import ru.hexaend.auth_service.exception.LimitRefreshTokenException;
import ru.hexaend.auth_service.repository.CodeRepository;
import ru.hexaend.auth_service.service.interfaces.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final OpaqueService opaqueService;
    private final CodeRepository codeRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = (User) userDetailsService.loadUserByUsername(request.username());
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }
        return generateTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest refreshToken) {
        User user = opaqueService.getUserFromToken(refreshToken.token());
        return generateTokens(user);
    }

    @Override
    @Transactional
    public void verifyToken(String code) {
        Code verificationCode = codeRepository
                .findByCodeAndType(code, Code.VerificationCodeType.EMAIL_VERIFICATION)
                .orElseThrow(RuntimeException::new); // TODO: custom exception
        User user = verificationCode.getUser();
        userDetailsService.setEmailVerified(user);
        codeRepository.delete(verificationCode); // TODO: use a service method for this
    }

    @Override
    @Transactional
    public void resetPassword(String code, NewPasswordRequest request) {
        Code verificationCode = codeRepository
                .findByCodeAndType(code, Code.VerificationCodeType.PASSWORD_RESET)
                .orElseThrow(RuntimeException::new); // TODO: custom exception
        User user = verificationCode.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        codeRepository.delete(verificationCode); // TODO: use a service method for this
        emailService.sendPasswordResetConfirmationEmail(user);
        userDetailsService.logoutAllSessions(user);
    }



//    @Override
//    public void logout(User user) {
//
//    }


    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = opaqueService.createOpaqueToken(user);
        return new AuthResponse("Bearer", accessToken, refreshToken);
    }
}
