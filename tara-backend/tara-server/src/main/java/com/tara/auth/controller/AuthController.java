package com.tara.auth.controller;

import com.tara.auth.dto.AuthResponse;
import com.tara.auth.dto.LoginRequest;
import com.tara.auth.dto.RefreshTokenRequest;
import com.tara.auth.dto.RegisterRequest;
import com.tara.auth.dto.UserDto;
import com.tara.auth.service.AuthService;
import com.tara.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Enterprise user authentication, registration, and session token management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new tenant organization and admin user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Registration successful. Workspace created.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and receive JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and retrieve a fresh JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve current authenticated user context and active workspace")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
