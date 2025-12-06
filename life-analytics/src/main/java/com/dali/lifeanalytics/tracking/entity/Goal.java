package com.dali.lifeanalytics.tracking.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal Entity
 * ────────────
 * Represents a personal goal with a target value and deadline.
 *
 * Table: goal
 * ┌────┬───────┬────────┬──────────────┬─────────────┬────────────┐
 * │ id │ title │ domain │ target_value │ target_date │ created_at │
 * └────┴───────┴────────┴──────────────┴─────────────┴────────────┘
 */
@Entity
@Table(name = "goal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Domain of the goal.
     * Examples: HEALTH, STUDY, CAREER, FINANCE, PERSONAL, FITNESS
     */
    @Column(nullable = false, length = 50)
    private String domain;

    /**
     * Target value to achieve (e.g., 100 for "Read 100 books")
     */
    @NotNull(message = "Target value is required")
    @Column(name = "target_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetValue;

    /**
     * Unit of measurement (e.g., "books", "kg", "hours", "EUR")
     */
    @Column(length = 30)
    private String unit;

    /**
     * Current value (cached for quick access)
     */
    @Column(name = "current_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal currentValue = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    /**
     * Status: ACTIVE, COMPLETED, ABANDONED
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * One goal can have many progress entries.
     */
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GoalProgress> progressEntries = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "ACTIVE";
        }
        if (currentValue == null) {
            currentValue = BigDecimal.ZERO;
        }
    }

    /**
     * Calculate progress percentage
     */
    public double getProgressPercentage() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentValue.divide(targetValue, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
