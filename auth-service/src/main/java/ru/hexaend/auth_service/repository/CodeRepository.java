package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hexaend.auth_service.entity.Code;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    @Query("""
SELECT c FROM Code c
WHERE c.code = :code AND c.type=:type
""")
    Optional<Code> findByCodeAndType(@Param("code") String code, @Param("type") Code.VerificationCodeType type);
}