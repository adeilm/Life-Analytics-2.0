package com.dali.lifeanalytics.analytics;

import com.dali.lifeanalytics.analytics.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Analytics Controller
 * ────────────────────
 * REST endpoints for analytics, trends, and dashboard.
 *
 * Endpoints:
 *   GET /api/analytics/habits/weekly     - Weekly habit completion report
 *   GET /api/analytics/health/trend      - Health metrics trend (default 7 days)
 *   GET /api/analytics/activities/summary - Activity time breakdown
 *   GET /api/analytics/dashboard         - Combined dashboard overview
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * GET /api/analytics/habits/weekly
     * Returns habit completion stats for the current week.
     */
    @GetMapping("/habits/weekly")
    public ResponseEntity<WeeklyHabitReportDto> getWeeklyHabitReport() {
        return ResponseEntity.ok(analyticsService.getWeeklyHabitReport());
    }

    /**
     * GET /api/analytics/health/trend?days=7
     * Returns health metrics trend.
     * @param days Number of days to analyze (default 7)
     */
    @GetMapping("/health/trend")
    public ResponseEntity<HealthTrendDto> getHealthTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(analyticsService.getHealthTrend(days));
    }

    /**
     * GET /api/analytics/activities/summary?days=7
     * Returns time spent per activity category.
     * @param days Number of days to analyze (default 7)
     */
    @GetMapping("/activities/summary")
    public ResponseEntity<ActivitySummaryDto> getActivitySummary(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(analyticsService.getActivitySummary(days));
    }

    /**
     * GET /api/analytics/dashboard
     * Returns combined dashboard with today's snapshot + weekly trends.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(analyticsService.getDashboard());
    }
}
