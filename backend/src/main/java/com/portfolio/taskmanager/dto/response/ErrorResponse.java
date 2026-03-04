package com.portfolio.taskmanager.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardised error payload returned by {@code GlobalExceptionHandler}.
 *
 * @param timestamp  when the error occurred
 * @param status     HTTP status code
 * @param error      short error name (e.g. "Not Found")
 * @param message    human-readable detail
 * @param path       request URI that triggered the error
 * @param fieldErrors per-field validation errors (nullable)
 */
public record ErrorResponse(
    LocalDateTime        timestamp,
    int                  status,
    String               error,
    String               message,
    String               path,
    Map<String, String>  fieldErrors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponse withFields(int status, String error, String message,
                                           String path, Map<String, String> fields) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, fields);
    }
}
