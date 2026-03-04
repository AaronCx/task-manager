package com.portfolio.taskmanager.dto.response;

/** Returned after a successful login or registration. */
public record AuthResponse(
    String token,
    String tokenType,
    Long   userId,
    String email,
    String firstName,
    String lastName
) {
    /** Convenience factory — token type is always "Bearer". */
    public static AuthResponse of(String token, Long userId, String email,
                                  String firstName, String lastName) {
        return new AuthResponse(token, "Bearer", userId, email, firstName, lastName);
    }
}
