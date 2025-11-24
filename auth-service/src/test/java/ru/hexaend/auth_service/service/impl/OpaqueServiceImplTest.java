package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hexaend.auth_service.entity.RefreshToken;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.OpaqueTokenNotFoundException;
import ru.hexaend.auth_service.repository.RefreshTokenRepository;
import ru.hexaend.auth_service.service.interfaces.CodeService;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpaqueServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private CodeService codeService;

    @InjectMocks
    private OpaqueServiceImpl opaqueService;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @BeforeEach
    void setUp() {
    }

    private final int defaultLength = 32;
    private final int customLength = 16;
    private final String sampleOpaqueToken = "opaque-token";

    @DisplayName("Create opaque token saves refresh token and returns token")
    @Test
    void createOpaqueTokenSavesAndReturnsToken() {
        // given
        User user = mock(User.class);

        // when
        String token = opaqueService.createOpaqueToken(user);

        // then
        assertNotNull(token);
        verify(codeService, times(1)).saveRefreshToken(refreshTokenCaptor.capture());
        RefreshToken saved = refreshTokenCaptor.getValue();
        assertEquals(token, saved.getToken());
        assertEquals(user, saved.getUser());
        assertTrue(saved.getExpiryDate().isAfter(Instant.now()));
    }

    @DisplayName("Get user from token returns user and deletes token")
    @Test
    void getUserFromTokenReturnsUserAndDeletes() {
        // given
        String token = sampleOpaqueToken;
        User user = mock(User.class);
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(rt));

        // when
        User result = opaqueService.getUserFromToken(token);

        // then
        assertEquals(user, result);
        verify(refreshTokenRepository, times(1)).deleteByToken(token);
    }

    @DisplayName("Get user from token for missing token throws exception")
    @Test
    void getUserFromTokenMissingThrows() {
        // given
        String token = "missing-token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // then
        assertThrows(OpaqueTokenNotFoundException.class, () -> opaqueService.getUserFromToken(token));
        verify(refreshTokenRepository, never()).deleteByToken(anyString());
    }

    @DisplayName("invalidateAllTokensForUser calls repository to delete all user's tokens")
    @Test
    void invalidateAllTokensForUserCallsRepository() {
        User user = new User();
        opaqueService.invalidateAllTokensForUser(user);
        verify(refreshTokenRepository).deleteAllByUser(user);
    }

    @DisplayName("deleteExpiredTokens calls repository with current time")
    @Test
    void deleteExpiredTokensCallsRepository() {
        opaqueService.deleteExpiredTokens();
        verify(refreshTokenRepository).deleteAllByExpiryDateBefore(any());
    }
}
