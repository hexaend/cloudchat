package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hexaend.auth_service.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
