package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hexaend.auth_service.entity.RefreshToken;
import ru.hexaend.auth_service.entity.User;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteAllByUser(User user);

    void deleteAllByExpiryDateBefore(Instant expiryDateBefore);
}
