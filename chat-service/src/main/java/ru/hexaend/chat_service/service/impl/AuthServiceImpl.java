package ru.hexaend.chat_service.service.impl;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.hexaend.chat_service.service.interfaces.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public Long getCurrentUserId() {
        Jwt jwt = (Jwt) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return Long.parseLong((String) jwt.getClaims().get("jti"));
    }
}
