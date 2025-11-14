package ru.hexaend.auth_service.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hexaend.auth_service.dto.request.ChangePasswordRequest;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.service.interfaces.AuthService;
import ru.hexaend.auth_service.service.interfaces.UserDetailsService;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<UserResponse> getProfile() {
        User user = userDetailsService.getCurrentUser();
        UserResponse userResponse = userMapper.toDto(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<VerifyStatusResponse> verifyEmail() {
        User user = userDetailsService.getCurrentUser();
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified"); // TODO: custom exception
        }

        return ResponseEntity.ok(userDetailsService.verifyEmail(user));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userDetailsService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    // TODO: maybe later implement
//    @PatchMapping
//    public ResponseEntity<UserResponse> updateProfile() {
//
//    }

}
