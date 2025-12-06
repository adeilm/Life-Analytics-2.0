package com.dali.lifeanalytics.tracking.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense Entity
 * ───────────────
 * Represents a financial expense entry.
 *
 * Table: expense
 * ┌────┬────────┬──────────┬──────┬──────┬────────────┐
 * │ id │ amount │ category │ date │ note │ created_at │
 * └────┴────────┴──────────┴──────┴──────┴────────────┘
 */
@Entity
@Table(name = "expense")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Category of expense.
     * Examples: FOOD, TRANSPORT, EDUCATION, ENTERTAINMENT, HEALTH, UTILITIES, OTHER
     */
    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate date = LocalDate.now();

    @Column(columnDefinition = "TEXT")
    private String note;

    /**
     * Optional: link to a specific description (e.g., "Coffee at Starbucks")
     */
    @Column(length = 200)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

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
