package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.dto.request.ChangePasswordRequest;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.request.ResetPasswordRequest;
import ru.hexaend.auth_service.dto.response.ResetPasswordResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.Code;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.EmailAlreadyInUseException;
import ru.hexaend.auth_service.exception.UsernameAlreadyInUseException;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.repository.CodeRepository;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.service.interfaces.EmailService;
import ru.hexaend.auth_service.service.interfaces.OpaqueService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;
import ru.hexaend.auth_service.utils.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CodeRepository codeRepository;
    private final OpaqueService opaqueService;
//    private final AuthService authService;

    @Override
    public User loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public VerifyStatusResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailAndEnabledIsTrue(request.email())) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }
        if (userRepository.existsByUsernameAndEnabledIsTrue(request.username())) {
            throw new UsernameAlreadyInUseException("Username is already in use");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return verifyEmail(user);
    }

    @Transactional
    @Override
    public VerifyStatusResponse verifyEmail(User user) {
        String token = generateVerificationToken(user);
        // TODO: use async email sending
        // TODO: move to separate function
        emailService.sendVerificationEmail(user, token);
        return new VerifyStatusResponse("VERIFICATION_EMAIL_SENT", "Verification email sent to " + user.getEmail());
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return getUserByUsername(username);
    }

    @Override
    public User getUserByUsername(String username) {
        // TODO: custom exception
        return userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
    }

    @Override
    public void setEmailVerified(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }


    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.getByEmail(request.email()).orElseThrow(); // TODO: custom exception
        String code = generateResetPasswordToken(user);
        emailService.sendResetPasswordEmail(user, code);

        return new ResetPasswordResponse("RESET_EMAIL_SENT", "Reset password email sent to " + user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        if (passwordEncoder.matches(user.getPassword(), request.oldPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Old password is incorrect"); // TODO: custom exception
        }

        emailService.sendPasswordChangeEmail(user);
        logoutAllSessions(user);
    }

    @Transactional
    @Override
    public void logoutAllSessions(User user) {
        user.setRefreshTokenCount(0);
        opaqueService.invalidateAllTokensForUser(user);
        // TODO: logout from this session by cookie/other
    }

    private String generateResetPasswordToken(User user) {
        String code = StringUtils.generateSecretString();

        Code resetPasswordCode = Code.builder()
                .user(user)
                .type(Code.VerificationCodeType.PASSWORD_RESET)
                .code(code)
                .build();

        codeRepository.save(resetPasswordCode);

        return code;
    }

    private String generateVerificationToken(User user) {
        String code = StringUtils.generateSecretString();

        Code verificationCode = Code.builder()
                .user(user)
                .type(Code.VerificationCodeType.EMAIL_VERIFICATION)
                .code(code)
                .build();

        codeRepository.save(verificationCode);

        return code;
    }
}
