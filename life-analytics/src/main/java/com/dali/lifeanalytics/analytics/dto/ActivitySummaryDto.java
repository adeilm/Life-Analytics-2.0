package com.dali.lifeanalytics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Activity/time tracking summary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySummaryDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalMinutes;
    private Double totalHours;
    
    // Breakdown by category
    private List<CategoryBreakdown> byCategory;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private String category;
        private Integer minutes;
        private Double hours;
        private Double percentage; // of total
    }
}
