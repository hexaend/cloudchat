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
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.InvalidPasswordException;
import ru.hexaend.auth_service.exception.LimitRefreshTokenException;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.service.JwtService;
import ru.hexaend.auth_service.service.OpaqueService;
import ru.hexaend.auth_service.service.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private OpaqueService opaqueService;

    @InjectMocks
    private AuthServiceImpl authService;

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
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
        when(user.getPasswordHash()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(jwtToken);
        when(opaqueService.createOpaqueToken(user)).thenReturn(opaqueToken);
        when(user.getRefreshTokenCount()).thenReturn(0);

        // then
        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("Bearer", response.type());
        assertEquals(jwtToken, response.accessToken());
        assertEquals(opaqueToken, response.refreshToken());

        verify(user).setRefreshTokenCount(1);
        verify(userRepository).save(user);
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
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

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
        when(user.getRefreshTokenCount()).thenReturn(1);

        // then
        AuthResponse response = authService.refreshAccessToken(request);
        assertNotNull(response);
        assertEquals("Bearer", response.type());
        assertEquals(jwtToken, response.accessToken());
        assertNull(response.refreshToken());

        verify(user).setRefreshTokenCount(2);
        verify(userRepository).save(user);
        verify(jwtService).generateAccessToken(user);
    }

    @Test
    @DisplayName("Refresh access token with invalid opaque token throws exception")
    void refreshAccessTokenWithInvalidOpaqueTokenThrowsException() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest(opaqueToken);

        // when
        User user = mock(User.class);
        when(user.getRefreshTokenCount()).thenReturn(10);
        when(opaqueService.getUserFromToken(opaqueToken)).thenReturn(user);

        // then
        LimitRefreshTokenException exception = assertThrows(LimitRefreshTokenException.class, () -> {
            authService.refreshAccessToken(request);
        });

        assertEquals("Limit of refresh tokens reached", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateAccessToken(any());
    }
}