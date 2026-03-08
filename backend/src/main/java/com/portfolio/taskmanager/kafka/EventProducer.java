package com.portfolio.taskmanager.kafka;

public interface EventProducer {
    void publish(String topic, TaskEvent event);
}
