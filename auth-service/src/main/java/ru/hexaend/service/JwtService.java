package ru.hexaend.service;

public interface JwtService {

    String generateAccessToken(String username);

}
