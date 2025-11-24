package ru.hexaend.auth_service.service.impl;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.repository.CodeRepository;
import ru.hexaend.auth_service.repository.RoleRepository;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.service.interfaces.EmailService;
import ru.hexaend.auth_service.service.interfaces.OpaqueService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;
import ru.hexaend.auth_service.utils.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CodeRepository codeRepository;
    private final OpaqueService opaqueService;
    private final RoleRepository roleRepository;
    // private final AuthService authService;

    @Override
    @Observed(name = "auth.load_user_by_username", contextualName = "load-user-by-username", lowCardinalityKeyValues = {
            "operation", "load_user",
            "service", "auth-service",
            "method", "LOAD_USER_BY_USERNAME"
    })
    public User loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    @Observed(name = "auth.register", contextualName = "register-user", lowCardinalityKeyValues = {
            "operation", "registration",
            "service", "auth-service",
            "method", "REGISTER"
    })
    public VerifyStatusResponse register(RegisterRequest request) {
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return verifyEmail(user);
    }

    @Transactional
    @Override
    @Observed(name = "auth.verify_email", contextualName = "send-verification-email", lowCardinalityKeyValues = {
            "operation", "send_verification_email",
            "service", "auth-service",
            "method", "VERIFY_EMAIL"
    })
    public VerifyStatusResponse verifyEmail(User user) {
        String token = generateVerificationToken(user);
        // TODO: use async email sending
        // TODO: move to separate function
        emailService.sendVerificationEmail(user, token);
        return new VerifyStatusResponse("VERIFICATION_EMAIL_SENT",
                "Verification email sent to " + user.getEmail());
    }

    @Override
    @Observed(name = "auth.get_current_user", contextualName = "get-current-user", lowCardinalityKeyValues = {
            "operation", "get_current_user",
            "service", "auth-service",
            "method", "GET_CURRENT_USER"
    })
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return getUserByUsername(username);
    }

    @Override
    @Observed(name = "auth.get_user_by_username", contextualName = "get-user-by-username", lowCardinalityKeyValues = {
            "operation", "get_user_by_username",
            "service", "auth-service",
            "method", "GET_USER_BY_USERNAME"
    })
    public User getUserByUsername(String username) {
        // TODO: custom exception
        return userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
    }

    @Override
    @Observed(name = "auth.set_email_verified", contextualName = "set-email-verified", lowCardinalityKeyValues = {
            "operation", "set_email_verified",
            "service", "auth-service",
            "method", "SET_EMAIL_VERIFIED"
    })
    public void setEmailVerified(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    @Observed(name = "auth.reset_password_request", contextualName = "reset-password-request", lowCardinalityKeyValues = {
            "operation", "reset_password_request",
            "service", "auth-service",
            "method", "RESET_PASSWORD_REQUEST"
    })
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.getByEmail(request.email()).orElseThrow(); // TODO: custom exception
        String code = generateResetPasswordToken(user);
        emailService.sendResetPasswordEmail(user, code);

        return new ResetPasswordResponse("RESET_EMAIL_SENT", "Reset password email sent to " + user.getEmail());
    }

    @Override
    @Transactional
    @Observed(name = "auth.change_password", contextualName = "change-password", lowCardinalityKeyValues = {
            "operation", "change_password",
            "service", "auth-service",
            "method", "CHANGE_PASSWORD"
    })
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        if (passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
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
    @Observed(name = "auth.logout_all_sessions", contextualName = "logout-all-sessions", lowCardinalityKeyValues = {
            "operation", "logout_all_sessions",
            "service", "auth-service",
            "method", "LOGOUT_ALL_SESSIONS"
    })
    public void logoutAllSessions(User user) {
        opaqueService.invalidateAllTokensForUser(user);
        // TODO: logout from this session by cookie/other
    }

    @Override
    public Role getDefaultUserRole() {
        log.info(roleRepository.findByName("USER").toString());
        return roleRepository.findByName("USER").orElse(null);
    }

    @Override
    public User getUserByIdOrUsername(Long userId, String username) {
        return userRepository.findByIdOrUsername(userId, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
