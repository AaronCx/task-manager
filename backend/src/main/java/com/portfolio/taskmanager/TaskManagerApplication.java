package com.portfolio.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Task Manager API.
 *
 * Portfolio project demonstrating:
 *   - Spring Boot 3 + Java 17
 *   - JWT-based stateless authentication
 *   - Spring Security method/route protection
 *   - Hibernate/JPA with PostgreSQL
 *   - OpenAPI/Swagger documentation
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
