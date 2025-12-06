package com.dali.lifeanalytics.tracking.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HabitLog Entity
 * ────────────────
 * Records a single completion/progress entry for a habit.
 * 
 * Examples:
 *   - "Exercise" habit logged on 2025-12-04 with value=30 (minutes)
 *   - "Read" habit logged on 2025-12-04 with value=1 (completed)
 *   - "Water" habit logged with value=8 (glasses)
 *
 * Table: habit_log
 * ┌────┬──────────┬────────────┬───────┬──────┬────────────┐
 * │ id │ habit_id │ log_date   │ value │ note │ created_at │
 * └────┴──────────┴────────────┴───────┴──────┴────────────┘
 */
@Entity
@Table(name = "habit_log", indexes = {
    @Index(name = "idx_habit_log_habit_date", columnList = "habit_id, log_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The habit this log belongs to.
     * ManyToOne: Many logs can belong to one habit.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    @NotNull(message = "Habit is required")
    private Habit habit;

    /**
     * The date of this log entry.
     * Using LocalDate (not DateTime) because habits are tracked daily.
     */
    @NotNull(message = "Log date is required")
    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    /**
     * The value/count for this log.
     * Interpretation depends on the habit:
     *   - For countable habits: actual count (e.g., 8 glasses of water)
     *   - For duration habits: minutes (e.g., 30 min exercise)
     *   - For boolean habits: 1 = done, 0 = skipped
     */
    @Builder.Default
    private Integer value = 1;

    /**
     * Optional note for this log entry.
     * E.g., "Morning run in the park"
     */
    @Column(length = 500)
    private String note;

    /**
     * When this log was created.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.logDate == null) {
            this.logDate = LocalDate.now();
        }
    }
}
