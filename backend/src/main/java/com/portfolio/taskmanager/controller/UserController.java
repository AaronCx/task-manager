package com.portfolio.taskmanager.controller;

import com.portfolio.taskmanager.dto.response.UserSummary;
import com.portfolio.taskmanager.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User lookup endpoints")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "List all users (for task assignment dropdown)")
    public ResponseEntity<List<UserSummary>> getUsers() {
        List<UserSummary> users = userRepository.findAll().stream()
                .map(UserSummary::from)
                .toList();
        return ResponseEntity.ok(users);
    }
}
