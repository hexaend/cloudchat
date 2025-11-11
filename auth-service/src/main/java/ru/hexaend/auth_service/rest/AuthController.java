package ru.hexaend.auth_service.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hexaend.auth_service.dto.request.AuthRequest;
import ru.hexaend.auth_service.dto.request.RefreshTokenRequest;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.AuthResponse;
import ru.hexaend.auth_service.dto.response.RegisterStatusResponse;
import ru.hexaend.auth_service.service.AuthService;
import ru.hexaend.auth_service.service.UserDetailsService;

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
    public ResponseEntity<RegisterStatusResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userDetailsService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        AuthResponse newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    // TODO: user info + verify email + password reset + logout + delete account

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(@RequestParam("code") String code) {
        authService.verifyToken(code);
        // TODO: return to home page
        return ResponseEntity.ok().build();
    }
}
