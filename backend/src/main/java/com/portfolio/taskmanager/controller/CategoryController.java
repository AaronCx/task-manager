package com.portfolio.taskmanager.controller;

import com.portfolio.taskmanager.dto.request.CategoryRequest;
import com.portfolio.taskmanager.dto.response.CategoryResponse;
import com.portfolio.taskmanager.entity.Category;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.exception.ConflictException;
import com.portfolio.taskmanager.exception.ResourceNotFoundException;
import com.portfolio.taskmanager.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Task category management")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "List all categories for the authenticated user")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
            categoryRepository.findByOwnerOrderByNameAsc(currentUser).stream()
                .map(CategoryResponse::from)
                .toList()
        );
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User currentUser) {

        if (categoryRepository.existsByNameAndOwner(request.name(), currentUser)) {
            throw new ConflictException("Category '" + request.name() + "' already exists.");
        }

        Category category = Category.builder()
                .name(request.name())
                .color(request.color() != null ? request.color() : "#3b82f6")
                .owner(currentUser)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.from(categoryRepository.save(category)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User currentUser) {

        Category category = categoryRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        category.setName(request.name());
        if (request.color() != null) category.setColor(request.color());

        return ResponseEntity.ok(CategoryResponse.from(categoryRepository.save(category)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        Category category = categoryRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        categoryRepository.delete(category);
        return ResponseEntity.noContent().build();
    }
}
