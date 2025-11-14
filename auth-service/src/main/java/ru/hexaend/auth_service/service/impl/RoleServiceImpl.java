package ru.hexaend.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.exception.EntityNotFoundException;
import ru.hexaend.auth_service.repository.RoleRepository;
import ru.hexaend.auth_service.service.interfaces.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public List<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional
    public Role createRole(String roleName) {
        Role role = Role.builder().name(roleName.toUpperCase()).build();
        roleRepository.save(role);
        return role;
    }

    @Override
    @Transactional
    public Role updateRole(Long roleId, String roleName) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        role.setName(roleName.toUpperCase());
        roleRepository.save(role);
        return role;
    }

    @Override
    @Transactional
    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }

}
