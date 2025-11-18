package ru.hexaend.auth_service.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.hexaend.auth_service.entity.RefreshToken;
import ru.hexaend.auth_service.entity.User;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@mail.io");
        user.setPasswordHash("hash");
        user.setFirstName("Alex");
        user.setLastName("Ivanov");
        return userRepository.save(user);
    }

    private RefreshToken createToken(User user, String tokenValue, Instant expiry) {
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiryDate(expiry);
        return refreshTokenRepository.save(token);
    }

    @DisplayName("findByToken returns token when present")
    @Test
    void findByTokenReturnsToken() {
        User user = createUser("alexey");
        createToken(user, "token-1", Instant.now().plusSeconds(3600));

        Optional<RefreshToken> result = refreshTokenRepository.findByToken("token-1");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("alexey");
    }

    @DisplayName("deleteByToken removes token")
    @Test
    void deleteByTokenRemovesToken() {
        User user = createUser("alexey");
        createToken(user, "token-2", Instant.now().plusSeconds(3600));

        refreshTokenRepository.deleteByToken("token-2");

        assertThat(refreshTokenRepository.findByToken("token-2")).isEmpty();
    }

    @DisplayName("deleteAllByUser removes all tokens for user")
    @Test
    void deleteAllByUserRemovesAll() {
        User user = createUser("alexey");
        createToken(user, "token-3", Instant.now().plusSeconds(3600));
        createToken(user, "token-4", Instant.now().plusSeconds(3600));

        refreshTokenRepository.deleteAllByUser(user);

        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    @DisplayName("deleteAllByExpiryDateBefore removes expired tokens")
    @Test
    void deleteAllByExpiryDateBeforeRemovesExpired() {
        User user = createUser("alexey");
        createToken(user, "token-5", Instant.now().minusSeconds(10));
        createToken(user, "token-6", Instant.now().plusSeconds(3600));

        refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());

        assertThat(refreshTokenRepository.findByToken("token-5")).isEmpty();
        assertThat(refreshTokenRepository.findByToken("token-6")).isPresent();
    }
}

