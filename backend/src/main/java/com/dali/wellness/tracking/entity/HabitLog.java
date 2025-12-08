package com.dali.wellness.tracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    public HabitLog() {
    }

    public HabitLog(Long id, Habit habit, LocalDate logDate, Integer value, String note, LocalDateTime createdAt) {
        this.id = id;
        this.habit = habit;
        this.logDate = logDate;
        this.value = value;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.logDate == null) {
            this.logDate = LocalDate.now();
        }
    }
}
