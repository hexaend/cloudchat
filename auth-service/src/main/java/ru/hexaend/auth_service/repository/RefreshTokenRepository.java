package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hexaend.auth_service.entity.RefreshToken;
import ru.hexaend.auth_service.entity.User;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("""
select rt from RefreshToken rt
where rt.token = :token
""")
    Optional<RefreshToken> findByToken(@Param("token") String token);

    @Modifying
    @Query("""
delete from RefreshToken rt
where rt.token = :token
""")
    void deleteByToken(@Param("token") String token);

    @Modifying
    @Query("""
delete from RefreshToken rt
where rt.user = :user
""")
    void deleteAllByUser(@Param("user") User user);

    @Modifying
    @Query("""
delete from RefreshToken rt
where rt.expiryDate < :expiryDateBefore
""")
    void deleteAllByExpiryDateBefore(@Param("expiryDateBefore") Instant expiryDateBefore);
}
