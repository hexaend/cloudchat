package ru.hexaend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hexaend.dto.request.AuthRequest;
import ru.hexaend.dto.request.RegisterRequest;
import ru.hexaend.dto.response.AuthResponse;
import ru.hexaend.dto.response.UserResponse;
import ru.hexaend.entity.User;
import ru.hexaend.mapper.UserMapper;
import ru.hexaend.repository.UserRepository;
import ru.hexaend.service.AuthService;
import ru.hexaend.service.JwtService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl clientDetailsServiceImpl;
    private final UserDetailsService userDetailsService;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailAndEnabledIsTrue(request.email())) {
            throw new RuntimeException("Email is already in use");
        }
        if (userRepository.existsByUsernameAndEnabledIsTrue(request.username())) {
            throw new RuntimeException("Username is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = (User) userDetailsService.loadUserByUsername(request.username());
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return generateTokens(user);
    }

    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtService.getUsernameFromToken(refreshToken);
        User user = (User) clientDetailsServiceImpl.loadUserByUsername(username);
        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse("Bearer", accessToken, refreshToken);
    }
}
