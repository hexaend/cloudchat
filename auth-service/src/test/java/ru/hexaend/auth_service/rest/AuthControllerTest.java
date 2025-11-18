package ru.hexaend.auth_service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hexaend.auth_service.dto.request.*;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.dto.response.ResetPasswordResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.service.interfaces.AuthService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest extends BaseWebMvcTest {

    private final String username = "alexey";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @DisplayName("POST /auth/login returns token pair")
    @Test
    void loginReturnsAccessAndRefreshTokens() throws Exception {
        AuthRequest request = new AuthRequest(username, "securePassword!");
        AuthResponse response = new AuthResponse("Bearer", "access-token", "refresh-token");
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.accessToken", is("access-token")))
                .andExpect(jsonPath("$.refreshToken", is("refresh-token")));

        verify(authService).login(eq(request));
    }

    @DisplayName("POST /auth/register returns verification status")
    @Test
    void registerReturnsVerificationStatus() throws Exception {
        RegisterRequest request = new RegisterRequest(username, "password", "email@test.io", "Alex", "Ivanov");
        VerifyStatusResponse response = new VerifyStatusResponse("VERIFICATION_EMAIL_SENT", "sent");
        when(userDetailsService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("VERIFICATION_EMAIL_SENT")));

        verify(userDetailsService).register(eq(request));
    }

    @DisplayName("POST /auth/refresh issues new access token")
    @Test
    void refreshTokenReturnsNewAccessToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("opaque-token");
        AuthResponse response = new AuthResponse("Bearer", "new-access", null);
        when(authService.refreshAccessToken(any())).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("new-access")))
                .andExpect(jsonPath("$.refreshToken", is(nullValue())));

        verify(authService).refreshAccessToken(eq(request));
    }

    @DisplayName("POST /auth/reset-password sends reset email")
    @Test
    void resetPasswordRequestSendsEmail() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("user@mail.io");
        ResetPasswordResponse response = new ResetPasswordResponse("RESET_EMAIL_SENT", "sent");
        when(userDetailsService.resetPassword(any())).thenReturn(response);

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESET_EMAIL_SENT")));

        verify(userDetailsService).resetPassword(eq(request));
    }

    @DisplayName("GET /auth/reset applies new password via code")
    @Test
    void resetPasswordTokenChangesPassword() throws Exception {
        NewPasswordRequest request = new NewPasswordRequest("newPass123");

        mockMvc.perform(get("/auth/reset")
                        .param("code", "abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authService).resetPassword("abc", request);
    }

    @DisplayName("GET /auth/verify confirms email")
    @Test
    void verifyTokenConfirmsEmail() throws Exception {
        mockMvc.perform(get("/auth/verify").param("code", "verify-code"))
                .andExpect(status().isOk());

        verify(authService).verifyToken("verify-code");
    }

    @DisplayName("GET /auth/logout_all logs out current user everywhere")
    @Test
    void logoutAllSessionsUsesCurrentUser() throws Exception {
        User user = new User();
        user.setUsername(username);
        when(userDetailsService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/auth/logout_all"))
                .andExpect(status().isOk());

        verify(userDetailsService).getCurrentUser();
        verify(userDetailsService).logoutAllSessions(user);
    }


}
