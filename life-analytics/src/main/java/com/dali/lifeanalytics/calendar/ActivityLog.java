package com.dali.lifeanalytics.calendar;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tracks time spent on activities for time analytics.
 * 
 * Use cases:
 * - "Spent 2h on Deep Work"
 * - "30 min exercise"
 * - "1.5h reading"
 * - "3h gaming" (leisure tracking)
 * 
 * Categories: WORK, EXERCISE, LEARNING, LEISURE, SOCIAL, SLEEP, OTHER
 */
@Entity
@Table(name = "activity_log", indexes = {
    @Index(name = "idx_activity_date", columnList = "log_date"),
    @Index(name = "idx_activity_category", columnList = "category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Activity name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String activity;

    /**
     * Category: WORK, DEEP_WORK, EXERCISE, LEARNING, LEISURE, SOCIAL, CHORES, OTHER
     */
    @Size(max = 50)
    @Column(length = 50)
    private String category;

    @NotNull(message = "Log date is required")
    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    /**
     * Duration in minutes
     */
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /**
     * Optional: specific start time
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * Optional: specific end time
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * Quality/productivity rating (1-5)
     * e.g., "Was this deep work session productive?"
     */
    @Column(name = "quality_rating")
    private Integer qualityRating;

    @Size(max = 500)
    @Column(length = 500)
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (logDate == null) {
            logDate = LocalDate.now();
        }
    }

    /**
     * Helper to get duration in hours (for display)
     */
    public double getDurationHours() {
        return durationMinutes / 60.0;
    }
}
