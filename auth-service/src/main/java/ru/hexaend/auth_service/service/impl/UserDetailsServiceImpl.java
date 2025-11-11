package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.RegisterStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.entity.VerificationCode;
import ru.hexaend.auth_service.exception.EmailAlreadyInUseException;
import ru.hexaend.auth_service.exception.UsernameAlreadyInUseException;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.repository.VerificationCodeRepository;
import ru.hexaend.auth_service.service.EmailService;
import ru.hexaend.auth_service.service.UserDetailsService;
import ru.hexaend.auth_service.utils.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public User loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public RegisterStatusResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailAndEnabledIsTrue(request.email())) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }
        if (userRepository.existsByUsernameAndEnabledIsTrue(request.username())) {
            throw new UsernameAlreadyInUseException("Username is already in use");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String token = generateVerificationToken(user);
        emailService.sendVerificationEmail(user, token);
        return new RegisterStatusResponse("USER_REGISTERED_WAITING_FOR_VERIFICATION", "User registered successfully. Please verify your email.");
    }

    private String generateVerificationToken(User user) {
        String code = StringUtils.generateVerificationCode();

        VerificationCode verificationCode = VerificationCode.builder()
                .user(user)
                .code(code)
                .build();

        verificationCodeRepository.save(verificationCode);

        return code;
    }
}
