package com.dali.lifeanalytics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Health metrics trend over time
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthTrendDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalRecords;
    
    // Averages
    private Double avgSleepHours;
    private Double avgMoodScore;
    private Double avgStressLevel;
    private Double avgEnergyLevel;
    
    // Daily breakdown
    private List<DailyHealthDto> dailyData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHealthDto {
        private LocalDate date;
        private Double sleepHours;
        private Integer moodScore;
        private Integer stressLevel;
        private Integer energyLevel;
    }
}
