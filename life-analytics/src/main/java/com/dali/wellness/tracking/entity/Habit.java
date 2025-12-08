package com.dali.wellness.tracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Habit Entity
 * ────────────
 * Represents a trackable habit (e.g., "Exercise", "Read", "Meditate").
 *
 * Table: habit
 * ┌────┬──────────────┬──────────────┬─────────────────┬────────────┐
 * │ id │ name         │ category     │ target_per_week │ created_at │
 * └────┴──────────────┴──────────────┴─────────────────┴────────────┘
 *
 * Annotations explained:
 *   @Entity      – Marks this class as a JPA entity (mapped to a table)
 *   @Table       – Specifies the table name (optional if same as class)
 *   @Id          – Primary key
 *   @GeneratedValue – Auto-generate ID (IDENTITY = auto-increment in MySQL/H2)
 *   @Column      – Customize column properties (nullable, length, unique)
 *   @NotBlank    – Bean Validation: field cannot be null or empty
 */
@Entity
@Table(name = "habit")
@Data                   // Lombok: generates getters, setters, equals, hashCode, toString
@Builder                // Lombok: enables builder pattern: Habit.builder().name("X").build()
@NoArgsConstructor      // Lombok: required by JPA (no-arg constructor)
@AllArgsConstructor     // Lombok: constructor with all fields (used by builder)
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Habit name is required")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Category for grouping habits.
     * Examples: HEALTH, PRODUCTIVITY, STUDY, PERSONAL
     */
    @Column(length = 50)
    private String category;

    /**
     * How many times per week you aim to complete this habit.
     * 0 means no specific target (just tracking).
     */
    @PositiveOrZero(message = "Target must be zero or positive")
    @Column(name = "target_per_week")
    private Integer targetPerWeek = 0;

    /**
     * Timestamp when the habit was created.
     * Automatically set before persisting.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA lifecycle callback: set createdAt before INSERT.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
