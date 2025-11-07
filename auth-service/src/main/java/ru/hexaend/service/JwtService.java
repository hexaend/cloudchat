package ru.hexaend.service;

import ru.hexaend.entity.User;

import java.text.ParseException;

public interface JwtService {

    String generateAccessToken(User user);
    String generateRefreshToken(User user);

    boolean validateToken(String jwtToken);

    String getUsernameFromToken(String jwtToken);
//    String extractUsername(String token);
//    Jwt<?, ?> decodeToken(String token);
//    boolean isTokenValid(String token, String username);

}
