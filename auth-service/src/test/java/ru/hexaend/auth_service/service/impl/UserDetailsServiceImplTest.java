package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.hexaend.auth_service.dto.request.ChangePasswordRequest;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.request.ResetPasswordRequest;
import ru.hexaend.auth_service.dto.response.ResetPasswordResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.EmailAlreadyInUseException;
import ru.hexaend.auth_service.exception.UsernameAlreadyInUseException;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.repository.CodeRepository;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.service.interfaces.EmailService;
import ru.hexaend.auth_service.service.interfaces.OpaqueService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    private final String username = "alexey";
    private final String password = "securePassword!";
    private final String email = "alexey@example.com";
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private CodeRepository codeRepository;
    @Mock
    private OpaqueService opaqueService;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @DisplayName("loadUserByUsername returns user when found and enabled")
    @Test
    void loadUserByUsernameReturnsUserWhenFoundAndEnabled() {
        // given
        User user = mock(User.class);

        // when
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // then
        User result = userDetailsService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByUsername(username);
    }

    @DisplayName("loadUserByUsername throws when user not found")
    @Test
    void loadUserByUsernameThrowsWhenNotFound() {
        // when
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // then
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        verify(userRepository).findByUsername(username);
    }

//    @DisplayName("register creates user when username and email are free")
//    @Test
//    void registerCreatesUserWhenFree() {
//        // given
//        RegisterRequest request = new RegisterRequest(username, password, email, "Alex", "Ivanov");
//        User mappedUser = new User();
//
//        // when
//        when(userRepository.existsByEmailAndEnabledIsTrue(email)).thenReturn(false);
//        when(userRepository.existsByUsernameAndEnabledIsTrue(username)).thenReturn(false);
//        when(userMapper.toEntity(request)).thenReturn(mappedUser);
//        when(passwordEncoder.encode(password)).thenReturn("encoded");
//        // verification email will be sent; CodeRepository is mocked so no NPE
//
//        // then
//        VerifyStatusResponse response = userDetailsService.register(request);
//
//        assertNotNull(response);
//        assertEquals("VERIFICATION_EMAIL_SENT", response.status());
//        assertTrue(response.message().contains("Verification email sent to"));
//
//        verify(userRepository).existsByEmailAndEnabledIsTrue(email);
//        verify(userRepository).existsByUsernameAndEnabledIsTrue(username);
//        verify(userMapper).toEntity(request);
//        verify(passwordEncoder).encode(password);
//        verify(userRepository).save(mappedUser);
//        verify(emailService).sendVerificationEmail(eq(mappedUser), anyString());
//    }

//    @DisplayName("register throws when email already in use")
//    @Test
//    void registerThrowsWhenEmailInUse() {
//        // given
//        RegisterRequest request = new RegisterRequest(username, password, email, "Alex", "Ivanov");
//
//        // when
//        when(userRepository.existsByEmailAndEnabledIsTrue(email)).thenReturn(true);
//
//        // then
//        EmailAlreadyInUseException ex = assertThrows(EmailAlreadyInUseException.class, () -> userDetailsService.register(request));
//        assertEquals("Email is already in use", ex.getMessage());
//
//        verify(userRepository).existsByEmailAndEnabledIsTrue(email);
//        verify(userRepository, never()).save(any());
//    }

//    @DisplayName("register throws when username already in use")
//    @Test
//    void registerThrowsWhenUsernameInUse() {
//        // given
//        RegisterRequest request = new RegisterRequest(username, password, email, "Alex", "Ivanov");
//
//        // when
//        when(userRepository.existsByEmailAndEnabledIsTrue(email)).thenReturn(false);
//        when(userRepository.existsByUsernameAndEnabledIsTrue(username)).thenReturn(true);
//
//        // then
//        UsernameAlreadyInUseException ex = assertThrows(UsernameAlreadyInUseException.class, () -> userDetailsService.register(request));
//        assertEquals("Username is already in use", ex.getMessage());
//
//        verify(userRepository).existsByEmailAndEnabledIsTrue(email);
//        verify(userRepository).existsByUsernameAndEnabledIsTrue(username);
//        verify(userRepository, never()).save(any());
//    }

    @DisplayName("getCurrentUser returns user from security context")
    @Test
    void getCurrentUserReturnsUser() {
        // given
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        User user = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        User result = userDetailsService.getCurrentUser();

        // then
        assertEquals(user, result);
        verify(userRepository).findByUsername(username);
    }

    @DisplayName("getUserByUsername throws when not found")
    @Test
    void getUserByUsernameThrowsWhenNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userDetailsService.getUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @DisplayName("setEmailVerified sets flag and saves user")
    @Test
    void setEmailVerifiedSetsFlag() {
        User user = new User();
        user.setEmailVerified(false);

        userDetailsService.setEmailVerified(user);

        assertTrue(user.isEmailVerified());
        verify(userRepository).save(user);
    }

    @DisplayName("resetPassword sends reset email and returns response")
    @Test
    void resetPasswordSendsEmail() {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest(email);
        User user = new User();
        user.setEmail(email);

        // when
        when(userRepository.getByEmail(email)).thenReturn(Optional.of(user));
        when(codeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // then
        ResetPasswordResponse response = userDetailsService.resetPassword(request);

        assertNotNull(response);
        assertEquals("RESET_EMAIL_SENT", response.status());
        assertTrue(response.message().contains("Reset password email sent to"));

        verify(userRepository).getByEmail(email);
        verify(emailService).sendResetPasswordEmail(eq(user), anyString());
        verify(codeRepository).save(any());
    }

    @DisplayName("resetPassword throws when user not found")
    @Test
    void resetPasswordThrowsWhenUserNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest(email);
        when(userRepository.getByEmail(email)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> userDetailsService.resetPassword(request));
        verify(userRepository).getByEmail(email);
        verify(emailService, never()).sendResetPasswordEmail(any(), anyString());
    }

    @DisplayName("changePassword updates password when old password matches and logs out sessions")
    @Test
    void changePasswordUpdatesAndLogsOut() {
        // given
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        User user = mock(User.class);

        // setup security context
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.newPassword())).thenReturn("encodedNew");

        // when
        userDetailsService.changePassword(request);

        // then
        verify(userRepository).save(user);
        verify(emailService).sendPasswordChangeEmail(user);
        verify(opaqueService).invalidateAllTokensForUser(user);
    }

    @DisplayName("changePassword throws when old password incorrect")
    @Test
    void changePasswordThrowsWhenOldPasswordIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongOld", "newPass");
        User user = mock(User.class);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userDetailsService.changePassword(request));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendPasswordChangeEmail(any());
    }

    @DisplayName("logoutAllSessions calls opaque service to invalidate tokens")
    @Test
    void logoutAllSessionsCallsOpaque() {
        User user = new User();
        userDetailsService.logoutAllSessions(user);
        verify(opaqueService).invalidateAllTokensForUser(user);
    }
}
