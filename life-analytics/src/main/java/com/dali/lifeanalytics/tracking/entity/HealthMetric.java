package com.dali.lifeanalytics.tracking.entity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * HealthMetric Entity
 * ────────────────────
 * Records a snapshot of health/wellness indicators at a point in time.
 * Designed for daily check-ins but can be logged multiple times per day.
 *
 * Table: health_metric
 * ┌────┬─────────────┬─────────────┬─────────────┬──────────────┬─────────────┬──────┐
 * │ id │ recorded_at │ sleep_hours │ mood_score  │ stress_level │ energy_level│ note │
 * └────┴─────────────┴─────────────┴─────────────┴──────────────┴─────────────┴──────┘
 *
 * Score scales (1-5):
 *   1 = Very Low/Bad
 *   2 = Low/Poor  
 *   3 = Moderate/Okay
 *   4 = Good/High
 *   5 = Excellent/Very High
 */
@Entity
@Table(name = "health_metric", indexes = {
    @Index(name = "idx_health_metric_recorded", columnList = "recorded_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * When this metric was recorded.
     * Defaults to now if not specified.
     */
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    /**
     * Hours of sleep (e.g., 7.5).
     * Nullable – not always tracked.
     */
    @PositiveOrZero(message = "Sleep hours must be zero or positive")
    @Column(name = "sleep_hours")
    private Double sleepHours;

    /**
     * Mood score (1-5).
     * 1 = Very bad, 5 = Excellent
     */
    @Min(value = 1, message = "Mood score must be between 1 and 5")
    @Max(value = 5, message = "Mood score must be between 1 and 5")
    @Column(name = "mood_score")
    private Integer moodScore;

    /**
     * Stress level (1-5).
     * 1 = Very low stress, 5 = Very high stress
     */
    @Min(value = 1, message = "Stress level must be between 1 and 5")
    @Max(value = 5, message = "Stress level must be between 1 and 5")
    @Column(name = "stress_level")
    private Integer stressLevel;

    /**
     * Energy level (1-5).
     * 1 = Exhausted, 5 = Very energetic
     */
    @Min(value = 1, message = "Energy level must be between 1 and 5")
    @Max(value = 5, message = "Energy level must be between 1 and 5")
    @Column(name = "energy_level")
    private Integer energyLevel;

    /**
     * Optional note about how you're feeling.
     * E.g., "Feeling tired after late night coding"
     */
    @Column(length = 1000)
    private String note;

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
    }
}
