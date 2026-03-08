package com.portfolio.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Payload for POST /api/auth/refresh */
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}
