package com.portfolio.taskmanager.repository;

import com.portfolio.taskmanager.entity.Task;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** All tasks owned by a specific user (the dashboard view). */
    List<Task> findByOwnerOrderByCreatedAtDesc(User owner);

    /** Filter by status within a user's tasks. */
    List<Task> findByOwnerAndStatusOrderByCreatedAtDesc(User owner, TaskStatus status);

    /** Check ownership before allowing update/delete. */
    Optional<Task> findByIdAndOwner(Long id, User owner);
}
