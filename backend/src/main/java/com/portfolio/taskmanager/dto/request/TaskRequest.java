package com.portfolio.taskmanager.dto.request;

import com.portfolio.taskmanager.enums.TaskPriority;
import com.portfolio.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Payload for POST /api/tasks and PUT /api/tasks/{id} */
public record TaskRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    String title,

    String description,

    TaskStatus status,

    TaskPriority priority,

    LocalDate dueDate,

    /** Optional: ID of the user to assign the task to. */
    Long assignedToId,

    /** Optional: ID of the category to tag the task with. */
    Long categoryId
) {}
