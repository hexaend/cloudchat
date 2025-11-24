package ru.hexaend.auth_service.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hexaend.auth_service.dto.request.*;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.service.interfaces.AuthService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;

    @Operation(summary = "User login", description = "Authenticate user and return access and refresh tokens")
    @ApiResponse(responseCode = "200", description = "Successfully logged in", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse login = authService.login(request);
        log.info("User '{}' logged in", request.username());
        return ResponseEntity.ok(login);
    }

    @Operation(summary = "User registration", description = "Register a new user")
    @ApiResponse(responseCode = "200", description = "Successfully registered", content = @Content(schema = @Schema(implementation = VerifyStatusResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<VerifyStatusResponse> register(@RequestBody RegisterRequest request) {
        var response = userDetailsService.register(request);
        log.info("New user '{}' registered", request.username());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh access token", description = "Get a new access token using a refresh token")
    @ApiResponse(responseCode = "200", description = "Successfully refreshed token", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        AuthResponse newAccessToken = authService.refreshAccessToken(refreshToken);
        log.info("Access token refreshed using refresh");
        return ResponseEntity.ok(newAccessToken);
    }

    @Operation(summary = "Request password reset", description = "Initiate password reset process")
    @ApiResponse(responseCode = "200", description = "Password reset requested")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        var response = userDetailsService.resetPassword(request);
        log.info("Password reset requested");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password with token", description = "Complete password reset using the token received via email")
    @ApiResponse(responseCode = "200", description = "Password successfully reset")
    @GetMapping("/reset")
    public ResponseEntity<Void> resetPasswordToken(
            @Parameter(description = "Reset token") @RequestParam("code") String code,
            @RequestBody NewPasswordRequest request) {
        authService.resetPassword(code, request);
        log.info("Password has been reset using code '{}'", code);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Verify email token", description = "Verify email address using the token")
    @ApiResponse(responseCode = "200", description = "Email successfully verified")
    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(@Parameter(description = "Verification token") @RequestParam("code") String code) {
        authService.verifyToken(code);
        log.info("Email verified using code '{}'", code);
        // TODO: return to home page
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Logout from all sessions", description = "Invalidate all sessions for the current user")
    @ApiResponse(responseCode = "200", description = "Successfully logged out from all sessions")
    @GetMapping("/logout_all")
    public ResponseEntity<Void> logoutAllSessions() {
        User user = userDetailsService.getCurrentUser();
        userDetailsService.logoutAllSessions(user);
        log.info("User '{}' logged out from all sessions", user.getUsername());
        return ResponseEntity.ok().build();
    }

    // TODO: user info + verify email + password reset + logout + delete account
    // TODO: maybe later implement
//    @GetMapping("/logout")
//    public ResponseEntity<Void> logout() {
//        User user = userDetailsService.getCurrentUser();
//        authService.logout(user); // TODO: change to logout from current session only
//    }
}
