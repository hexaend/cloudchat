package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.dto.request.AuthRequest;
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.entity.VerificationCode;
import ru.hexaend.auth_service.exception.InvalidPasswordException;
import ru.hexaend.auth_service.exception.LimitRefreshTokenException;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.repository.VerificationCodeRepository;
import ru.hexaend.auth_service.service.interfaces.AuthService;
import ru.hexaend.auth_service.service.interfaces.JwtService;
import ru.hexaend.auth_service.service.interfaces.OpaqueService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final OpaqueService opaqueService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = (User) userDetailsService.loadUserByUsername(request.username());
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }
        AuthResponse authResponse = generateTokens(user);
        userRepository.save(user);
        return authResponse;
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest refreshToken) {
        User user = opaqueService.getUserFromToken(refreshToken.token());
        if (user.getRefreshTokenCount() >= 5) {
            throw new LimitRefreshTokenException();
        }
        AuthResponse authResponse = generateTokens(user);
        userRepository.save(user);
        return authResponse;
    }

    @Override
    @Transactional
    public void verifyToken(String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code).orElseThrow(RuntimeException::new);
        User user = verificationCode.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = opaqueService.createOpaqueToken(user);
        user.setRefreshTokenCount(user.getRefreshTokenCount() + 1);
        return new AuthResponse("Bearer", accessToken, refreshToken);
    }
}
