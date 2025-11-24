package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.hexaend.auth_service.entity.RefreshToken;
import ru.hexaend.auth_service.repository.RefreshTokenRepository;
import ru.hexaend.auth_service.service.interfaces.CodeService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Async
    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }


}
