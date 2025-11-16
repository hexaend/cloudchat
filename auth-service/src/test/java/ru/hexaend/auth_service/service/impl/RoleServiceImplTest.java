package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.exception.EntityNotFoundException;
import ru.hexaend.auth_service.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @DisplayName("getAllRoles returns pageable content list")
    @Test
    void getAllRolesReturnsList() {
        // given
        Role r1 = new Role();
        Role r2 = new Role();

        when(roleRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(r1, r2)));

        // when
        List<Role> result = roleService.getAllRoles(Pageable.unpaged());

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleRepository).findAll(any(Pageable.class));
    }

    @DisplayName("createRole saves role with uppercase name")
    @Test
    void createRoleSavesUppercaseName() {
        // when
        Role created = roleService.createRole("admin");

        // then
        assertNotNull(created);
        assertEquals("ADMIN", created.getName());
        verify(roleRepository).save(created);
    }

    @DisplayName("updateRole updates existing role and saves")
    @Test
    void updateRoleUpdatesAndSaves() {
        // given
        Role existing = new Role();
        existing.setId(1L);
        existing.setName("OLD");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existing));

        // when
        Role updated = roleService.updateRole(1L, "newname");

        // then
        assertNotNull(updated);
        assertEquals("NEWNAME", updated.getName());
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(existing);
    }

    @DisplayName("updateRole throws when role not found")
    @Test
    void updateRoleThrowsWhenNotFound() {
        // when
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> roleService.updateRole(2L, "x"));
        verify(roleRepository).findById(2L);
        verify(roleRepository, never()).save(any());
    }

    @DisplayName("getRoleById returns role when found")
    @Test
    void getRoleByIdReturnsWhenFound() {
        // given
        Role r = new Role();
        when(roleRepository.findById(5L)).thenReturn(Optional.of(r));

        // when
        Role got = roleService.getRoleById(5L);

        // then
        assertEquals(r, got);
        verify(roleRepository).findById(5L);
    }

    @DisplayName("getRoleById throws when not found")
    @Test
    void getRoleByIdThrowsWhenNotFound() {
        when(roleRepository.findById(6L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.getRoleById(6L));
        verify(roleRepository).findById(6L);
    }
}

