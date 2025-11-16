package ru.hexaend.auth_service.rest;

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
public class AuthController {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse login = authService.login(request);
        log.info("User '{}' logged in", request.username());
        return ResponseEntity.ok(login);
    }

    @PostMapping("/register")
    public ResponseEntity<VerifyStatusResponse> register(@RequestBody RegisterRequest request) {
        var response = userDetailsService.register(request);
        log.info("New user '{}' registered", request.username());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        AuthResponse newAccessToken = authService.refreshAccessToken(refreshToken);
        log.info("Access token refreshed using refresh");
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        var response = userDetailsService.resetPassword(request);
        log.info("Password reset requested");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> resetPasswordToken(@RequestParam("code") String code, @RequestBody NewPasswordRequest request) {
        authService.resetPassword(code, request);
        log.info("Password has been reset using code '{}'", code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(@RequestParam("code") String code) {
        authService.verifyToken(code);
        log.info("Email verified using code '{}'", code);
        // TODO: return to home page
        return ResponseEntity.ok().build();
    }

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
