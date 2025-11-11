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

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpaqueServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

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

//    @DisplayName("Generate opaque token default length")
//    @Test
//    void generateOpaqueTokenDefaultLength() {
//        // when
//        String token = opaqueService.generateOpaqueToken();
//
//        // then
//        assertNotNull(token);
//        assertTrue(token.matches("[A-Za-z0-9_-]+"));
//        assertTrue(token.length() >= defaultLength);
//    }

//    @DisplayName("Generate opaque token custom length")
//    @Test
//    void generateOpaqueTokenCustomLength() {
//        // when
//        String token = opaqueService.generateOpaqueToken(customLength);
//
//        // then
//        assertNotNull(token);
//        assertTrue(token.matches("[A-Za-z0-9_-]+"));
//        assertTrue(token.length() >= customLength);
//    }

    @DisplayName("Create opaque token saves refresh token and returns token")
    @Test
    void createOpaqueTokenSavesAndReturnsToken() {
        // given
        User user = mock(User.class);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        String token = opaqueService.createOpaqueToken(user);

        // then
        assertNotNull(token);
        verify(refreshTokenRepository, times(1)).save(refreshTokenCaptor.capture());
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
}
