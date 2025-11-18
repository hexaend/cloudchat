package ru.hexaend.auth_service.rest.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hexaend.auth_service.dto.response.RoleResponse;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.mapper.RoleMapper;
import ru.hexaend.auth_service.rest.BaseWebMvcTest;
import ru.hexaend.auth_service.service.interfaces.RoleService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private RoleMapper roleMapper;

    @DisplayName("GET /admin/roles returns list of roles")
    @Test
    void getAllRolesReturnsList() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        when(roleService.getAllRoles(PageRequest.of(0, 20))).thenReturn(List.of(role));
        when(roleMapper.toResponse(role)).thenReturn(new RoleResponse(1L, "ADMIN"));

        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("ADMIN")));

        verify(roleService).getAllRoles(PageRequest.of(0, 20));
        verify(roleMapper).toResponse(role);
    }

    @DisplayName("POST /admin/roles creates role")
    @Test
    void createRoleCreatesRole() throws Exception {
        Role role = new Role();
        role.setId(5L);
        role.setName("MODERATOR");
        when(roleService.createRole("MODERATOR")).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(new RoleResponse(5L, "MODERATOR"));

        mockMvc.perform(post("/admin/roles")
                        .param("roleName", "MODERATOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("MODERATOR")))
                .andExpect(jsonPath("$.id", is(5)));

        verify(roleService).createRole("MODERATOR");
        verify(roleMapper).toResponse(role);
    }

    @DisplayName("PUT /admin/roles updates role")
    @Test
    void updateRoleUpdatesRole() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        when(roleService.updateRole(1L, "USER")).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(new RoleResponse(1L, "USER"));

        mockMvc.perform(put("/admin/roles")
                        .param("roleId", "1")
                        .param("roleName", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("USER")));

        verify(roleService).updateRole(1L, "USER");
        verify(roleMapper).toResponse(role);
    }

    @DisplayName("GET /admin/roles/{id} returns role")
    @Test
    void getRoleByIdReturnsRole() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        when(roleService.getRoleById(1L)).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(new RoleResponse(1L, "USER"));

        mockMvc.perform(get("/admin/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("USER")));

        verify(roleService).getRoleById(1L);
        verify(roleMapper).toResponse(role);
    }
}
