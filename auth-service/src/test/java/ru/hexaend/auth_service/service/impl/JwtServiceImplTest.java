package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hexaend.auth_service.config.RsaPropertiesConfig;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.JwtException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static JwtServiceImpl jwtService;

    @BeforeAll
    static void setUp() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RsaPropertiesConfig rsaPropertiesConfig = mock(RsaPropertiesConfig.class);
        when(rsaPropertiesConfig.getPrivateKey()).thenReturn(privateKey);

        jwtService = new JwtServiceImpl(rsaPropertiesConfig);
    }

    @DisplayName("Generate Access Token Test")
    @Test
    void generateAccessToken() {
        // given
        String username = "alexey";
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getId()).thenReturn(123L);
        when(user.getEmail()).thenReturn("alexey@example.com");
        when(user.getAuthorities()).thenReturn(new HashSet<>());

        // when
        String token = jwtService.generateAccessToken(user);

        // then
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        String subject = jwtService.getUsernameFromToken(token);
        assertEquals(username, subject);
    }

    @DisplayName("Get username from invalid token throws exception")
    @Test
    void getUsernameFromInvalidTokenThrowsException() {
        // given
        String invalidToken = "not-a-valid-jwt";

        // then
        assertThrows(JwtException.class, () -> jwtService.getUsernameFromToken(invalidToken));
    }

}