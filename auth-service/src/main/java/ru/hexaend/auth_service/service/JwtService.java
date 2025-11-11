package ru.hexaend.auth_service.service;

import ru.hexaend.auth_service.entity.User;

public interface JwtService {

    String generateAccessToken(User user);
    String getUsernameFromToken(String jwtToken);

}
