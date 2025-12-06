package com.dali.lifeanalytics.intake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Daily Log DTO
 * ──────────────
 * Structure for receiving AI-generated daily logs.
 * The AI should produce JSON matching this structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLogDto {

    /**
     * Date of the log
     */
    private LocalDate date;

    /**
     * Health metrics for the day
     */
    private HealthEntry health;

    /**
     * Activities performed during the day
     */
    private List<ActivityEntry> activities;

    /**
     * Habits tracked for the day
     */
    private List<HabitEntry> habits;

    /**
     * Expenses recorded for the day
     */
    private List<ExpenseEntry> expenses;

    /**
     * Tasks created or updated
     */
    private List<TaskEntry> tasks;

    /**
     * Study sessions
     */
    private List<StudySessionEntry> studySessions;

    /**
     * General notes for the day
     */
    private String notes;

    // ═══════════════════════════════════════════════════════════════════════════
    // NESTED CLASSES
    // ═══════════════════════════════════════════════════════════════════════════

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthEntry {
        private Double sleepHours;
        private Integer moodScore;      // 1-10
        private Integer stressLevel;    // 1-10
        private Integer energyLevel;    // 1-10
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityEntry {
        private String type;            // STUDY, WORK, EXERCISE, LEISURE, etc.
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationMinutes;
        private List<String> tags;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabitEntry {
        private String habitName;       // Name of the habit (must match existing habit)
        private Double value;           // Value logged (e.g., 1 for completed, or specific count)
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseEntry {
        private BigDecimal amount;
        private String category;
        private String description;
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskEntry {
        private String title;
        private String description;
        private String priority;        // LOW, MEDIUM, HIGH, URGENT
        private LocalDate dueDate;
        private String category;
        private String status;          // PENDING, IN_PROGRESS, COMPLETED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudySessionEntry {
        private String courseCode;      // Course code to link to
        private String topic;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationMinutes;
        private String notes;
    }
}
