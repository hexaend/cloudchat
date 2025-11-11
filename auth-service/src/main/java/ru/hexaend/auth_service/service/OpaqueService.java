package ru.hexaend.auth_service.service;

import ru.hexaend.auth_service.entity.User;

public interface OpaqueService {

    String createOpaqueToken(User user);

    User getUserFromToken(String token);
}
