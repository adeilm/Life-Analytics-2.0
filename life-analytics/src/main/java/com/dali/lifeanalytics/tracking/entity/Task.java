package com.dali.lifeanalytics.tracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task Entity
 * ────────────
 * Represents a to-do item or task.
 *
 * Table: task
 * ┌────┬─────────┬─────────────┬────────┬──────────┬──────────┬────────────┐
 * │ id │ title   │ description │ status │ priority │ due_date │ created_at │
 * └────┴─────────┴─────────────┴────────┴──────────┴──────────┴────────────┘
 */
@Entity
@Table(name = "task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Task status: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    /**
     * Priority: LOW, MEDIUM, HIGH, URGENT
     */
    @Column(length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Optional category for grouping tasks.
     * Examples: STUDY, WORK, PERSONAL, HEALTH
     */
    @Column(length = 50)
    private String category;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if ("COMPLETED".equals(status) && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
