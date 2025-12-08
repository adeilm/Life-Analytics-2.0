package com.dali.wellness.tracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
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

    public HealthMetric() {
    }

    public HealthMetric(Long id, LocalDateTime recordedAt, Double sleepHours, Integer moodScore, Integer stressLevel, Integer energyLevel, String note) {
        this.id = id;
        this.recordedAt = recordedAt;
        this.sleepHours = sleepHours;
        this.moodScore = moodScore;
        this.stressLevel = stressLevel;
        this.energyLevel = energyLevel;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public Integer getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(Integer moodScore) {
        this.moodScore = moodScore;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
    }
}
