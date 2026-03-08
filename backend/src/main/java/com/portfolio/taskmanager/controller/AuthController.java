package com.portfolio.taskmanager.controller;

import com.portfolio.taskmanager.dto.request.LoginRequest;
import com.portfolio.taskmanager.dto.request.RefreshTokenRequest;
import com.portfolio.taskmanager.dto.request.RegisterRequest;
import com.portfolio.taskmanager.dto.response.AuthResponse;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints.
 *
 * POST /api/auth/register → create account + return JWT + refresh token
 * POST /api/auth/login    → verify credentials + return JWT + refresh token
 * POST /api/auth/refresh  → exchange refresh token for new token pair
 * POST /api/auth/logout   → revoke all refresh tokens (requires JWT)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, refresh, and logout endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive a JWT + refresh token")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access + refresh token pair")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke all refresh tokens for the authenticated user")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User currentUser) {
        authService.logout(currentUser);
        return ResponseEntity.noContent().build();
    }
}
