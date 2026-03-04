package com.portfolio.taskmanager.seeder;

import com.portfolio.taskmanager.entity.Task;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.enums.TaskPriority;
import com.portfolio.taskmanager.enums.TaskStatus;
import com.portfolio.taskmanager.repository.TaskRepository;
import com.portfolio.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds the database with demo users and sample tasks on first run.
 *
 * Guard: checks if users already exist before inserting anything,
 * so re-deployments don't create duplicate data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Only seed once
        if (userRepository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("Seeding database with sample data...");

        // ── Create demo users ─────────────────────────────────────────
        User alice = userRepository.save(User.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice@demo.com")
                .password(passwordEncoder.encode("password123"))
                .build());

        User bob = userRepository.save(User.builder()
                .firstName("Bob")
                .lastName("Smith")
                .email("bob@demo.com")
                .password(passwordEncoder.encode("password123"))
                .build());

        // ── Create sample tasks for Alice ─────────────────────────────
        taskRepository.saveAll(List.of(

            Task.builder()
                .title("Set up CI/CD pipeline")
                .description("Configure GitHub Actions for automated build, test, and deploy.")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().minusDays(5))
                .owner(alice)
                .assignedTo(alice)
                .build(),

            Task.builder()
                .title("Design REST API endpoints")
                .description("Define resource paths, request/response bodies, and error codes.")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().minusDays(10))
                .owner(alice)
                .assignedTo(alice)
                .build(),

            Task.builder()
                .title("Implement JWT authentication")
                .description("Add Spring Security + JJWT. Protect all routes except /auth/**")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(2))
                .owner(alice)
                .assignedTo(alice)
                .build(),

            Task.builder()
                .title("Build React dashboard")
                .description("Create task list, filtering, and status update UI with Tailwind CSS.")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(5))
                .owner(alice)
                .assignedTo(bob)
                .build(),

            Task.builder()
                .title("Write unit tests")
                .description("Cover service layer with JUnit 5 + Mockito. Target 80%+ coverage.")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(7))
                .owner(alice)
                .assignedTo(alice)
                .build(),

            Task.builder()
                .title("Add OpenAPI / Swagger documentation")
                .description("Annotate all endpoints and configure Swagger UI at /swagger-ui.html")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now().plusDays(10))
                .owner(alice)
                .assignedTo(null)
                .build(),

            Task.builder()
                .title("Deploy to cloud (AWS / Render)")
                .description("Containerise with Docker and push to a free-tier cloud host.")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(14))
                .owner(alice)
                .assignedTo(bob)
                .build(),

            Task.builder()
                .title("Performance testing with k6")
                .description("Run load tests against the API and document the results.")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now().plusDays(21))
                .owner(alice)
                .assignedTo(null)
                .build()
        ));

        // ── Create sample tasks for Bob ───────────────────────────────
        taskRepository.saveAll(List.of(

            Task.builder()
                .title("Review pull requests")
                .description("Review and merge open PRs from the frontend team.")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(1))
                .owner(bob)
                .assignedTo(bob)
                .build(),

            Task.builder()
                .title("Update project README")
                .description("Add architecture diagram, screenshots, and local setup guide.")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now().plusDays(3))
                .owner(bob)
                .assignedTo(bob)
                .build()
        ));

        log.info("Database seeded: 2 users, 10 tasks.");
    }
}
