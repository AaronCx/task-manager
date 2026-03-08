package com.portfolio.taskmanager.service;

import com.portfolio.taskmanager.dto.request.LoginRequest;
import com.portfolio.taskmanager.dto.request.RefreshTokenRequest;
import com.portfolio.taskmanager.dto.request.RegisterRequest;
import com.portfolio.taskmanager.dto.response.AuthResponse;
import com.portfolio.taskmanager.entity.RefreshToken;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.exception.ConflictException;
import com.portfolio.taskmanager.repository.UserRepository;
import com.portfolio.taskmanager.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration, authentication, and token refresh.
 *
 * Keeps controllers thin — all business logic lives here.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtTokenProvider      jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService   refreshTokenService;

    /**
     * Register a new user account.
     *
     * @throws ConflictException if the email is already taken
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with email '" + request.email() + "' already exists.");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.of(token, refreshToken.getToken(), user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }

    /**
     * Authenticate an existing user and return a fresh JWT + refresh token.
     *
     * Spring Security's {@link AuthenticationManager} throws
     * {@link BadCredentialsException} on invalid credentials — caught by
     * {@code GlobalExceptionHandler}.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User vanished after authentication"));

        String token = jwtTokenProvider.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.of(token, refreshToken.getToken(), user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }

    /**
     * Exchange a valid refresh token for a new access token + refresh token pair.
     * The old refresh token is revoked (rotation).
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken existing = refreshTokenService.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (refreshTokenService.isExpired(existing)) {
            refreshTokenService.revokeToken(existing);
            throw new BadCredentialsException("Refresh token expired");
        }

        // Rotate: revoke old, issue new
        refreshTokenService.revokeToken(existing);
        User user = existing.getUser();

        String newAccessToken = jwtTokenProvider.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.of(newAccessToken, newRefreshToken.getToken(), user.getId(),
                user.getEmail(), user.getFirstName(), user.getLastName());
    }

    /**
     * Logout — revokes all refresh tokens for the given user.
     */
    @Transactional
    public void logout(User user) {
        refreshTokenService.revokeAllUserTokens(user);
    }
}
