package com.portfolio.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must be at most 50 characters")
    String name,

    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color must be a valid hex code (e.g. #3b82f6)")
    String color
) {}
