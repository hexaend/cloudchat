package ru.hexaend.auth_service.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Roles", description = "Endpoints for managing roles")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping("/roles")
    @Operation(
            summary = "Get all roles with pagination",
            description = "Retrieve a paginated list of all roles in the system."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of roles", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class)
    ))
    public ResponseEntity<List<RoleResponse>> getAllRoles(
            @Parameter(description = "Pagination information")
            @PageableDefault(size = 20) Pageable pageable
    ) {
        List<Role> roles = roleService.getAllRoles(pageable);
        List<RoleResponse> roleResponses = roles.stream().map(roleMapper::toResponse).toList();
        log.info("Retrieved {} roles", roleResponses.size());
        return ResponseEntity.ok(roleResponses);
    }

    @Operation(
            summary = "Create a new role",
            description = "Create a new role with the specified name."
    )
    @ApiResponse(responseCode = "200", description = "Successfully created new role", content
            = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class)))
    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Parameter(description = "Role name", example = "MODERATOR") String roleName) {
        Role role = roleService.createRole(roleName);
        RoleResponse roleResponse = roleMapper.toResponse(role);
        log.info("Created new role with name '{}'", roleName);
        return ResponseEntity.ok(roleResponse);
    }

    @Operation(
            summary = "Update a role",
            description = "Update an existing role's name."
    )
    @ApiResponse(responseCode = "200", description = "Successfully updated role", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class)
    ))
    @PutMapping("/roles")
    public ResponseEntity<RoleResponse> updateRole(
            @Parameter(description = "ID of the role to update") Long roleId,
            @Parameter(description = "New name for the role") String roleName
    ) {
        Role role = roleService.updateRole(roleId, roleName);
        RoleResponse roleResponse = roleMapper.toResponse(role);
        log.info("Updated role with id {} to new name '{}'", roleId, roleName);
        return ResponseEntity.ok(roleResponse);
    }

    @Operation(
            summary = "Get role by ID",
            description = "Retrieve a specific role by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved role", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class)
    ))
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<RoleResponse> getRoleById(
            @Parameter(description = "ID of the role to retrieve") @PathVariable Long roleId
    ) {
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
