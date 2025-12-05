package com.dali.lifeanalytics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for individual habit statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitStatsDto {
    private Long habitId;
    private String habitName;
    private String category;
    private Integer targetPerWeek;
    private Integer completedThisWeek;
    private Double completionRate; // 0.0 - 1.0
    private Integer currentStreak; // consecutive days
}
