package com.portfolio.taskmanager.dto.response;

import com.portfolio.taskmanager.entity.User;

/** Lightweight user representation for dropdowns / assignment. */
public record UserSummary(
    Long   id,
    String firstName,
    String lastName,
    String email
) {
    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
