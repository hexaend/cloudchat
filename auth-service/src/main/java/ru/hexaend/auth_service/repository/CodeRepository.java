package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hexaend.auth_service.entity.Code;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {
    Optional<Code> findByCode(String code);

    Optional<Code> findByCodeAndType(String code, Code.VerificationCodeType type);
}