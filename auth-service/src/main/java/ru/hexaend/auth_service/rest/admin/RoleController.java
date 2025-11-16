package ru.hexaend.auth_service.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hexaend.auth_service.dto.response.RoleResponse;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.mapper.RoleMapper;
import ru.hexaend.auth_service.service.interfaces.RoleService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        List<Role> roles = roleService.getAllRoles(pageable);
        List<RoleResponse> roleResponses = roles.stream().map(roleMapper::toResponse).toList();
        log.info("Retrieved {} roles", roleResponses.size());
        return ResponseEntity.ok(roleResponses);
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(String roleName) {
        Role role = roleService.createRole(roleName);
        RoleResponse roleResponse = roleMapper.toResponse(role);
        log.info("Created new role with name '{}'", roleName);
        return ResponseEntity.ok(roleResponse);
    }

    @PutMapping("/roles")
    public ResponseEntity<RoleResponse> updateRole(Long roleId, String roleName) {
        Role role = roleService.updateRole(roleId, roleName);
        RoleResponse roleResponse = roleMapper.toResponse(role);
        log.info("Updated role with id {} to new name '{}'", roleId, roleName);
        return ResponseEntity.ok(roleResponse);
    }

    @GetMapping("/roles/{roleId}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long roleId) {
        Role role = roleService.getRoleById(roleId);
        RoleResponse roleResponse = roleMapper.toResponse(role);
        log.info("Retrieved role with id {}", roleId);
        return ResponseEntity.ok(roleResponse);
    }

//    @DeleteMapping
//    public ResponseEntity<Void> deleteRole(Long roleId) {
//        roleService.deleteRole(roleId);
//        return ResponseEntity.noContent().build();
//    }

}
