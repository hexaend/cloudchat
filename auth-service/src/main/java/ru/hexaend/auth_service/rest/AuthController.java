package ru.hexaend.auth_service.rest;

import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse login = authService.login(request);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/register")
    public ResponseEntity<VerifyStatusResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userDetailsService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        AuthResponse newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        var response = userDetailsService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> resetPasswordToken(@RequestParam("code") String code, @RequestBody NewPasswordRequest request) {
        authService.resetPassword(code, request);
        return ResponseEntity.ok().build();
    }
    // TODO: user info + verify email + password reset + logout + delete account

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(@RequestParam("code") String code) {
        authService.verifyToken(code);
        // TODO: return to home page
        return ResponseEntity.ok().build();
    }

    // TODO: maybe later implement
//    @GetMapping("/logout")
//    public ResponseEntity<Void> logout() {
//        User user = userDetailsService.getCurrentUser();
//        authService.logout(user); // TODO: change to logout from current session only
//    }

    @GetMapping("/logout_all")
    public ResponseEntity<Void> logoutAllSessions() {
        User user = userDetailsService.getCurrentUser();
        userDetailsService.logoutAllSessions(user);
        return ResponseEntity.ok().build();
    }

}
