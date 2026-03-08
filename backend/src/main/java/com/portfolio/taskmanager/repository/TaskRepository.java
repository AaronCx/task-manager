package com.portfolio.taskmanager.repository;

import com.portfolio.taskmanager.entity.Task;
import com.portfolio.taskmanager.entity.User;
import com.portfolio.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /** Search tasks by title or description (case-insensitive). */
    @Query("SELECT t FROM Task t WHERE t.owner = :owner " +
           "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Task> searchByOwner(@Param("owner") User owner, @Param("query") String query);

    /** Search tasks by title/description with status filter. */
    @Query("SELECT t FROM Task t WHERE t.owner = :owner AND t.status = :status " +
           "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Task> searchByOwnerAndStatus(@Param("owner") User owner,
                                       @Param("status") TaskStatus status,
                                       @Param("query") String query);
}
