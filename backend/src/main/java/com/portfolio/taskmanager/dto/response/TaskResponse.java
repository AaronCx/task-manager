package com.portfolio.taskmanager.dto.response;

import com.portfolio.taskmanager.entity.Task;
import com.portfolio.taskmanager.enums.TaskPriority;
import com.portfolio.taskmanager.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Read-only representation of a {@link Task} sent to the client. */
public record TaskResponse(
    Long          id,
    String        title,
    String        description,
    TaskStatus    status,
    TaskPriority  priority,
    LocalDate     dueDate,
    Long          ownerId,
    String        ownerName,
    Long          assignedToId,
    String        assignedToName,
    Long          categoryId,
    String        categoryName,
    String        categoryColor,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /** Map a JPA entity to its DTO. */
    public static TaskResponse from(Task task) {
        String assignedName = task.getAssignedTo() != null
                ? task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName()
                : null;
        Long assignedId = task.getAssignedTo() != null ? task.getAssignedTo().getId() : null;

        Long catId = task.getCategory() != null ? task.getCategory().getId() : null;
        String catName = task.getCategory() != null ? task.getCategory().getName() : null;
        String catColor = task.getCategory() != null ? task.getCategory().getColor() : null;

        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate(),
            task.getOwner().getId(),
            task.getOwner().getFirstName() + " " + task.getOwner().getLastName(),
            assignedId,
            assignedName,
            catId,
            catName,
            catColor,
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
