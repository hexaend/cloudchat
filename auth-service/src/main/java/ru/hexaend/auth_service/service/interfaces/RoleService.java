package ru.hexaend.auth_service.service.interfaces;

import org.springframework.data.domain.Pageable;
import ru.hexaend.auth_service.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles(Pageable pageable);

    Role createRole(String roleName);

    Role updateRole(Long roleId, String roleName);

    Role getRoleById(Long roleId);
}
