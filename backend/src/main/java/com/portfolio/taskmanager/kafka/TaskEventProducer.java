package com.portfolio.taskmanager.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka-backed event producer — active when {@code app.kafka.enabled=true}.
 *
 * Error handling strategy:
 *   - Kafka failures are caught and logged as WARN — they never bubble up to
 *     the caller, so task operations succeed even when Kafka is unavailable.
 *   - The send is non-blocking (fire-and-forget with callback logging).
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer implements EventProducer {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    /**
     * Publishes an event to the given topic asynchronously.
     *
     * @param topic Kafka topic (use {@link TaskEvent} constants)
     * @param event Event payload
     */
    public void publish(String topic, TaskEvent event) {
        try {
            kafkaTemplate.send(topic, String.valueOf(event.taskId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Kafka publish failed — topic={}, taskId={}: {}",
                                    topic, event.taskId(), ex.getMessage());
                        } else {
                            log.debug("Kafka event published — topic={}, taskId={}, offset={}",
                                    topic, event.taskId(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception ex) {
            // KafkaTemplate itself may throw if the broker is unreachable at config time
            log.warn("Kafka unavailable — event not published. topic={}, taskId={}: {}",
                    topic, event.taskId(), ex.getMessage());
        }
    }
}
