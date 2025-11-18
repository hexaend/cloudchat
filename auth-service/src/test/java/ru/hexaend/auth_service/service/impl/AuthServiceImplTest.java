package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.hexaend.auth_service.dto.request.AuthRequest;
import ru.hexaend.auth_service.dto.request.NewPasswordRequest;
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.InvalidPasswordException;
import ru.hexaend.auth_service.exception.LimitRefreshTokenException;
import ru.hexaend.auth_service.repository.CodeRepository;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.service.interfaces.EmailService;
import ru.hexaend.auth_service.service.interfaces.JwtService;
import ru.hexaend.auth_service.service.interfaces.OpaqueService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private OpaqueService opaqueService;

    @Mock
    private EmailService emailService;

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
    }

    private final String username = "alexey";
    private final String rawPassword = "securePassword!";
    private final String encodedPassword = "$encodedPasswordHash$";
    private final String jwtToken = "jwt-token-string";
    private final String opaqueToken = "opaque-token-string";


    @DisplayName("Login success generates tokens and saves user")
    @Test
    void loginSucessGeneratesTokensAndSaveUser() {
        // given
        AuthRequest request = new AuthRequest(username, rawPassword);

        // when
        User user = mock(User.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
        when(user.getPasswordHash()).thenReturn(encodedPassword);
        when(jwtService.generateAccessToken(user)).thenReturn(jwtToken);
        when(opaqueService.createOpaqueToken(user)).thenReturn(opaqueToken);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // then
        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("Bearer", response.type());
        assertEquals(jwtToken, response.accessToken());
        assertEquals(opaqueToken, response.refreshToken());

        verify(userDetailsService).loadUserByUsername(request.username());
        verify(jwtService).generateAccessToken(user);
        verify(opaqueService).createOpaqueToken(user);
    }

    @Test
    @DisplayName("Login with invalid password throws exception")
    void loginWithInvalidPasswordThrowsException() {
        // given
        AuthRequest request = new AuthRequest(username, rawPassword);

        // when
        User user = mock(User.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
        when(user.getPasswordHash()).thenReturn(encodedPassword);

        // then
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateAccessToken(any());
        verify(opaqueService, never()).createOpaqueToken(any());
    }

    @Test
    @DisplayName("Refresh access token generates new token and updates user")
    void refreshAccessTokenGeneratesNewTokenAndUpdatesUser() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest(opaqueToken);

        // when
        User user = mock(User.class);
        when(opaqueService.getUserFromToken(opaqueToken)).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn(jwtToken);

        // then
        AuthResponse response = authService.refreshAccessToken(request);
        assertNotNull(response);
        assertEquals("Bearer", response.type());
        assertEquals(jwtToken, response.accessToken());
        assertNull(response.refreshToken());

        verify(opaqueService).getUserFromToken(request.token());
        verify(jwtService).generateAccessToken(user);
        verify(opaqueService).createOpaqueToken(user);
    }

    @DisplayName("verifyToken sets email verified and deletes code")
    @Test
    void verifyTokenSetsEmailVerifiedAndDeletesCode() {
        // given
        String code = "abc123";
        User user = mock(User.class);
        var verificationCode = mock(ru.hexaend.auth_service.entity.Code.class);
        when(verificationCode.getUser()).thenReturn(user);
        when(codeRepository.findByCodeAndType(eq(code), any())).thenReturn(java.util.Optional.of(verificationCode));

        // when
        authService.verifyToken(code);

        // then
        verify(codeRepository).findByCodeAndType(eq(code), any());
        verify(userDetailsService).setEmailVerified(user);
        verify(codeRepository).delete(verificationCode);
    }

    @DisplayName("verifyToken throws when code not found")
    @Test
    void verifyTokenThrowsWhenCodeNotFound() {
        when(codeRepository.findByCodeAndType(anyString(), any())).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.verifyToken("missing"));
    }

    @DisplayName("resetPassword applies new password and cleans up code")
    @Test
    void resetPasswordAppliesNewPasswordAndCleansUp() {
        // given
        String code = "reset-code";
        User user = mock(User.class);
        var verificationCode = mock(ru.hexaend.auth_service.entity.Code.class);
        when(verificationCode.getUser()).thenReturn(user);
        when(codeRepository.findByCodeAndType(eq(code), any())).thenReturn(java.util.Optional.of(verificationCode));
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");
        NewPasswordRequest req = new NewPasswordRequest("new-pass");


        // when
        authService.resetPassword(code, req);

        // then
        verify(codeRepository).findByCodeAndType(eq(code), any());
        verify(codeRepository).delete(verificationCode);
        verify(emailService).sendPasswordResetConfirmationEmail(user);
        verify(userDetailsService).logoutAllSessions(user);
    }

    @DisplayName("resetPassword throws when code not found")
    @Test
    void resetPasswordThrowsWhenCodeNotFound() {
        when(codeRepository.findByCodeAndType(anyString(), any())).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.resetPassword("missing", new NewPasswordRequest("x")));
    }
}