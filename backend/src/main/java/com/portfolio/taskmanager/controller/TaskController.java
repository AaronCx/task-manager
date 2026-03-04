package com.portfolio.taskmanager.controller;

import com.portfolio.taskmanager.dto.request.TaskRequest;
import com.portfolio.taskmanager.dto.response.TaskResponse;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.enums.TaskStatus;
import com.portfolio.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task CRUD endpoints — all require a valid JWT.
 *
 * Users can only see and manage their own tasks.
 * The authenticated principal is injected via {@code @AuthenticationPrincipal}.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Create, read, update and delete tasks")
public class TaskController {

    private final TaskService taskService;

    // ── GET /api/tasks ────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all tasks for the authenticated user",
               description = "Optionally filter by status query param")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @Parameter(description = "Optional status filter: TODO | IN_PROGRESS | DONE")
            @RequestParam(required = false) TaskStatus status,
            @AuthenticationPrincipal User currentUser) {

        List<TaskResponse> tasks = (status != null)
                ? taskService.getTasksByStatus(currentUser, status)
                : taskService.getTasksForUser(currentUser);

        return ResponseEntity.ok(tasks);
    }

    // ── GET /api/tasks/{id} ───────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get a single task by ID")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(taskService.getTaskById(id, currentUser));
    }

    // ── POST /api/tasks ───────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, currentUser));
    }

    // ── PUT /api/tasks/{id} ───────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task (full replacement)")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(taskService.updateTask(id, request, currentUser));
    }

    // ── DELETE /api/tasks/{id} ────────────────────────────────────────

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
