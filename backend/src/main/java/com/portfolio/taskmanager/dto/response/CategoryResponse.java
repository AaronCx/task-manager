package com.portfolio.taskmanager.dto.response;

import com.portfolio.taskmanager.entity.Category;

public record CategoryResponse(
    Long   id,
    String name,
    String color
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getColor());
    }
}
