package com.portfolio.taskmanager.service;

import com.portfolio.taskmanager.dto.request.LoginRequest;
import com.portfolio.taskmanager.dto.request.RegisterRequest;
import com.portfolio.taskmanager.dto.response.AuthResponse;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.exception.ConflictException;
import com.portfolio.taskmanager.repository.UserRepository;
import com.portfolio.taskmanager.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and authentication.
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
        return AuthResponse.of(token, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }

    /**
     * Authenticate an existing user and return a fresh JWT.
     *
     * Spring Security's {@link AuthenticationManager} throws
     * {@link org.springframework.security.authentication.BadCredentialsException}
     * on invalid credentials — caught by {@code GlobalExceptionHandler}.
     */
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User vanished after authentication"));

        String token = jwtTokenProvider.generateToken(user);
        return AuthResponse.of(token, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }
}
