package com.dali.lifeanalytics.tracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Entity
 * ──────────────
 * Represents an academic course.
 *
 * Table: course
 * ┌────┬──────┬──────┬─────────────┬────────────┐
 * │ id │ name │ code │ description │ created_at │
 * └────┴──────┴──────┴─────────────┴────────────┘
 */
@Entity
@Table(name = "course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Course code (e.g., "CS101", "MATH201")
     */
    @Column(length = 20, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Number of credits/ECTS
     */
    @Column
    private Integer credits;

    /**
     * Instructor name
     */
    @Column(length = 100)
    private String instructor;

    /**
     * Semester (e.g., "Fall 2025", "S1 2025")
     */
    @Column(length = 50)
    private String semester;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * One course can have many exams.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Exam> exams = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
