package ru.hexaend.auth_service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hexaend.auth_service.dto.request.ChangePasswordRequest;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.service.interfaces.AuthService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private AuthService authService;

    @DisplayName("GET /profile returns current user DTO")
    @Test
    void getProfileReturnsUserDto() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("alexey");
        when(userDetailsService.getCurrentUser()).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(new UserResponse(1L, "alexey", "email@test.io", "Alex", "Ivanov"));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("alexey")))
                .andExpect(jsonPath("$.email", is("email@test.io")));

        verify(userDetailsService).getCurrentUser();
        verify(userMapper).toDto(user);
    }

    @DisplayName("GET /profile/verify triggers verification email when not verified")
    @Test
    void verifyEmailSendsVerification() throws Exception {
        User user = new User();
        user.setUsername("alexey");
        user.setEmailVerified(false);
        when(userDetailsService.getCurrentUser()).thenReturn(user);
        when(userDetailsService.verifyEmail(user))
                .thenReturn(new VerifyStatusResponse("VERIFICATION_EMAIL_SENT", "sent"));

        mockMvc.perform(post("/profile/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("VERIFICATION_EMAIL_SENT")));

        verify(userDetailsService).getCurrentUser();
        verify(userDetailsService).verifyEmail(user);
    }

    // @DisplayName("PUT /profile/password delegates to service")
    // @Test
    // void changePasswordUpdatesPassword() throws Exception {
    // ChangePasswordRequest request = new ChangePasswordRequest("old", "new");
    //
    // mockMvc.perform(put("/profile/password")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(request)))
    // .andExpect(status().isOk());
    //
    // verify(userDetailsService).changePassword(eq(request));
    // }

}
