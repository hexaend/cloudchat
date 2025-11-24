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
@Slf4j
@Tag(name = "Profile", description = "Endpoints for managing user profile")
public class ProfileController {

    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final AuthService authService;

    @Operation(summary = "Get user profile", description = "Retrieve the profile of the currently logged-in user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved profile", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @GetMapping
    public ResponseEntity<UserResponse> getProfile() {
        User user = userDetailsService.getCurrentUser();
        UserResponse userResponse = userMapper.toDto(user);
        log.info("Profile data retrieved for user '{}'", user.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Check email verification status", description = "Check if the current user's email is verified")
    @ApiResponse(responseCode = "200", description = "Successfully checked verification status")
    @GetMapping("/verified")
    public ResponseEntity<Boolean> isEmailVerified() {
        User user = userDetailsService.getCurrentUser();
        log.info("Email verification status checked for user '{}'", user.getUsername());
        return ResponseEntity.ok(user.isEmailVerified());
    }

    @Operation(summary = "Send email verification", description = "Send a verification email to the current user")
    @ApiResponse(responseCode = "200", description = "Verification email sent successfully", content = @Content(schema = @Schema(implementation = VerifyStatusResponse.class)))
    @PostMapping("/verify")
    public ResponseEntity<VerifyStatusResponse> verifyEmail() {
        User user = userDetailsService.getCurrentUser();
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified"); // TODO: custom exception
        }
        log.info("Sending email verification for user '{}'", user.getUsername());
        return ResponseEntity.ok(userDetailsService.verifyEmail(user));
    }

    @Operation(summary = "Change password", description = "Change the password for the current user")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userDetailsService.changePassword(request);
        log.info("Password changed for user '{}'", userDetailsService.getCurrentUser().getUsername());
        return ResponseEntity.ok().build();
    }

    // TODO: maybe later implement
//    @PatchMapping
//    public ResponseEntity<UserResponse> updateProfile() {
//
//    }

}
