package ru.hexaend.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class JwtServiceImplTest {

    private static JwtServiceImpl jwtService;

    @BeforeAll
    static void setUp() {
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
        jwtService = new JwtServiceImpl(passwordEncoder);
    }

    @DisplayName("Generate Access Token Test")
    @Test
    void generateAccessToken() {

        String token = jwtService.generateAccessToken("testuser");

        System.out.println(token);

        System.out.println("Decoded Token: " + jwtService.decodeToken(token));

    }

}