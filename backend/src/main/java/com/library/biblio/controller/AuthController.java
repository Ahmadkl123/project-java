package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.auth.AuthResponse;
import com.library.biblio.dto.auth.LoginRequest;
import com.library.biblio.dto.auth.PasswordChangeRequest;
import com.library.biblio.dto.auth.RegisterRequest;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.service.AuthService;
import com.library.biblio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, registration, password management")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new student")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.ok(authService.register(req), "Registered");
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive a JWT")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.login(req), "Logged in");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current authenticated user")
    public ApiResponse<UserDto> me(@AuthenticationPrincipal Object principal) {
        String email = principal != null ? principal.toString() : null;
        return ApiResponse.ok(userService.getByEmail(email));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change current user password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal Object principal,
                                            @Valid @RequestBody PasswordChangeRequest req) {
        UserDto user = userService.getByEmail(principal.toString());
        authService.changePassword(user.getId(), req);
        return ApiResponse.ok(null, "Password updated");
    }
}
