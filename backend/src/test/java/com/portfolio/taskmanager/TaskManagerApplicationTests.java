package com.portfolio.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test — verifies the Spring context loads correctly.
 * Uses the "test" profile which swaps PostgreSQL for H2 in-memory.
 */
@SpringBootTest
@ActiveProfiles("test")
class TaskManagerApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test will fail
    }
}
