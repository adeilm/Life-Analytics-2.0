package com.dali.lifeanalytics.tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * GoalProgress Entity
 * ────────────────────
 * Represents a progress entry for a goal.
 *
 * Table: goal_progress
 * ┌────┬─────────┬──────┬───────────────┬──────┬────────────┐
 * │ id │ goal_id │ date │ current_value │ note │ created_at │
 * └────┴─────────┴──────┴───────────────┴──────┴────────────┘
 */
@Entity
@Table(name = "goal_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many progress entries belong to one goal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    @JsonIgnore
    private Goal goal;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    /**
     * The value recorded on this date.
     */
    @NotNull(message = "Current value is required")
    @Column(name = "current_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentValue;

    /**
     * Optional note about this progress entry.
     */
    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Helper method to get the goal ID for DTOs
     */
    public Long getGoalId() {
        return goal != null ? goal.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
