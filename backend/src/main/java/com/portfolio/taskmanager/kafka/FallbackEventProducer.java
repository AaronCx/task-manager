package com.portfolio.taskmanager.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Fallback event producer — active when Kafka is not configured.
 *
 * Instead of publishing to a Kafka topic, this producer writes notification
 * rows directly to the shared PostgreSQL database.
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false")
@RequiredArgsConstructor
@Slf4j
public class FallbackEventProducer implements EventProducer {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void publish(String topic, TaskEvent event) {
        String eventType = event.eventType();
        String message = buildMessage(event);

        jdbcTemplate.update(
                "INSERT INTO notifications (event_type, task_id, task_title, message, user_id, is_read, created_at) " +
                "VALUES (?, ?, ?, ?, ?, false, NOW())",
                eventType, event.taskId(), event.taskTitle(), message, event.ownerId()
        );

        log.info("Fallback: notification persisted — {} taskId={}", eventType, event.taskId());
    }

    private String buildMessage(TaskEvent event) {
        return switch (event.eventType()) {
            case "TASK_CREATED" -> String.format(
                    "Task '%s' was created with %s priority.",
                    event.taskTitle(), formatPriority(event.priority()));
            case "TASK_UPDATED" -> {
                boolean statusChanged = event.oldStatus() != null
                        && event.newStatus() != null
                        && !event.oldStatus().equals(event.newStatus());
                yield statusChanged
                        ? String.format("Task '%s' moved from %s to %s.",
                            event.taskTitle(), formatStatus(event.oldStatus()), formatStatus(event.newStatus()))
                        : String.format("Task '%s' was updated.", event.taskTitle());
            }
            case "TASK_DELETED" -> String.format(
                    "Task '%s' was deleted.", event.taskTitle());
            default -> String.format("Task '%s': %s", event.taskTitle(), event.eventType());
        };
    }

    private String formatStatus(String status) {
        if (status == null) return "Unknown";
        return switch (status) {
            case "TODO"        -> "To Do";
            case "IN_PROGRESS" -> "In Progress";
            case "DONE"        -> "Done";
            default            -> status;
        };
    }

    private String formatPriority(String priority) {
        if (priority == null) return "medium";
        return priority.charAt(0) + priority.substring(1).toLowerCase();
    }
}
