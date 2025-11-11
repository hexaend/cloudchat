package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hexaend.auth_service.entity.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByCode(String code);
}