package com.dali.lifeanalytics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Weekly habit completion report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyHabitReportDto {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Integer totalHabits;
    private Double overallCompletionRate;
    private List<HabitStatsDto> habits;
}
