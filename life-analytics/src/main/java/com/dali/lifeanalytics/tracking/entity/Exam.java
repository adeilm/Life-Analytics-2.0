package com.dali.lifeanalytics.tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Exam Entity
 * ────────────
 * Represents an exam or test for a course.
 *
 * Table: exam
 * ┌────┬───────────┬───────────┬──────────┬────────┬──────────────┐
 * │ id │ course_id │ date_time │ location │ weight │ description  │
 * └────┴───────────┴───────────┴──────────┴────────┴──────────────┘
 */
@Entity
@Table(name = "exam")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many exams belong to one course.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    /**
     * Exam title (e.g., "Midterm", "Final", "Quiz 1")
     */
    @Column(nullable = false, length = 100)
    private String title;

    @NotNull(message = "Exam date/time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(length = 100)
    private String location;

    /**
     * Weight of the exam in the final grade (e.g., 0.30 for 30%)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    /**
     * Additional notes about the exam (topics covered, format, etc.)
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Duration in minutes
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Helper method to get the course ID for DTOs
     */
    public Long getCourseId() {
        return course != null ? course.getId() : null;
    }

    /**
     * Helper method to get the course name for DTOs
     */
    public String getCourseName() {
        return course != null ? course.getName() : null;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
