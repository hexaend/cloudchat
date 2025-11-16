package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.entity.RefreshToken;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.OpaqueTokenNotFoundException;
import ru.hexaend.auth_service.repository.RefreshTokenRepository;
import ru.hexaend.auth_service.service.interfaces.OpaqueService;
import ru.hexaend.auth_service.utils.StringUtils;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OpaqueServiceImpl implements OpaqueService {


    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String createOpaqueToken(User user) {
        String opaqueToken = StringUtils.generateOpaqueToken();
        // TODO: get expiry date from config
        RefreshToken refreshToken = RefreshToken.builder()
                .token(opaqueToken).user(user).expiryDate(
                        Instant.ofEpochSecond(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
                ).build();

        refreshTokenRepository.save(refreshToken);

        return opaqueToken;
    }

    @Override
    @Transactional
    public User getUserFromToken(String token) {
        var user = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new OpaqueTokenNotFoundException("Refresh token not found"))
                .getUser();
        refreshTokenRepository.deleteByToken(token);
        return user;
    }

    @Override
    public void invalidateAllTokensForUser(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
