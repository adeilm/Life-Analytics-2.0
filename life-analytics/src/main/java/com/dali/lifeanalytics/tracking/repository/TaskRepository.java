package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Task Repository
 * ─────────────────
 * Spring Data JPA repository for {@link Task} entities.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks by status.
     */
    List<Task> findByStatus(String status);

    /**
     * Find all tasks by category.
     */
    List<Task> findByCategory(String category);

    /**
     * Find all tasks by priority.
     */
    List<Task> findByPriority(String priority);

    /**
     * Find tasks due before a specific date.
     */
    List<Task> findByDueDateBefore(LocalDate date);

    /**
     * Find tasks due on a specific date.
     */
    List<Task> findByDueDate(LocalDate date);

    /**
     * Find tasks due between two dates.
     */
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find overdue tasks (pending tasks with due date in the past).
     */
    @Query("SELECT t FROM Task t WHERE t.status != 'COMPLETED' AND t.status != 'CANCELLED' AND t.dueDate < :today")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    /**
     * Find pending tasks ordered by due date.
     */
    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' OR t.status = 'IN_PROGRESS' ORDER BY t.dueDate ASC NULLS LAST")
    List<Task> findActiveTasks();

    /**
     * Count tasks by status.
     */
    long countByStatus(String status);
}
