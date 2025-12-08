package com.dali.wellness.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Analytics Controller - Wellness Tracker
 * ────────────────────────────────────────
 * REST endpoints for habit and health analytics.
 *
 * Endpoints:
 *   GET /api/analytics/habits/weekly  - Weekly habit completion report
 *   GET /api/analytics/health/trend   - Health metrics trend
 *   GET /api/analytics/dashboard      - Combined dashboard overview
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
    public ResponseEntity<Map<String, Object>> getWeeklyHabitReport() {
        return ResponseEntity.ok(analyticsService.getWeeklyHabitReport());
    }

    /**
     * GET /api/analytics/health/trend?days=7
     * Returns health metrics trend (sleep, mood, stress, energy).
     */
    @GetMapping("/health/trend")
    public ResponseEntity<Map<String, Object>> getHealthTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(analyticsService.getHealthTrend(days));
    }

    /**
     * GET /api/analytics/dashboard
     * Returns combined dashboard with today's snapshot + weekly trends.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(analyticsService.getDashboard());
    }
}
