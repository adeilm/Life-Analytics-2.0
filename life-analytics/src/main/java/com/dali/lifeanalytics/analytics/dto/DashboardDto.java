package com.dali.lifeanalytics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Wellness Tracker Dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private LocalDate asOf;
    
    // Today's snapshot
    private TodaySnapshot today;
    
    // Weekly summaries
    private WeeklyHabitReportDto habitReport;
    private HealthTrendDto healthTrend;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodaySnapshot {
        private Integer habitsCompletedToday;
        private Integer habitsTotal;
        private Double sleepLastNight;
        private Integer currentMood;
    }
}
