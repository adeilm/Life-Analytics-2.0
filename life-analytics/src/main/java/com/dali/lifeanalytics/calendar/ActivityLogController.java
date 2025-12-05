package com.dali.lifeanalytics.calendar;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService service;

    /**
     * Create a new activity log
     * POST /api/activities
     */
    @PostMapping
    public ResponseEntity<ActivityLog> create(@Valid @RequestBody ActivityLog log) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(log));
    }

    /**
     * Quick log: minimal fields
     * POST /api/activities/quick
     * Body: { "activity": "Reading", "category": "LEARNING", "durationMinutes": 60 }
     */
    @PostMapping("/quick")
    public ResponseEntity<ActivityLog> quickLog(@RequestBody QuickLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.quickLog(request.activity(), request.category(), request.durationMinutes()));
    }

    /**
     * Get all activity logs
     * GET /api/activities
     */
    @GetMapping
    public ResponseEntity<List<ActivityLog>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Get activity log by ID
     * GET /api/activities/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Update an activity log
     * PUT /api/activities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActivityLog> update(
            @PathVariable Long id,
            @Valid @RequestBody ActivityLog log) {
        return ResponseEntity.ok(service.update(id, log));
    }

    /**
     * Delete an activity log
     * DELETE /api/activities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get today's activities
     * GET /api/activities/today
     */
    @GetMapping("/today")
    public ResponseEntity<List<ActivityLog>> findToday() {
        return ResponseEntity.ok(service.findToday());
    }

    /**
     * Get activities for a specific date
     * GET /api/activities/date/2025-12-04
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ActivityLog>> findByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.findByDate(date));
    }

    /**
     * Get activities within a date range
     * GET /api/activities/range?start=2025-12-01&end=2025-12-07
     */
    @GetMapping("/range")
    public ResponseEntity<List<ActivityLog>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.findByDateRange(start, end));
    }

    /**
     * Get this week's breakdown by category (hours)
     * GET /api/activities/weekly-breakdown
     */
    @GetMapping("/weekly-breakdown")
    public ResponseEntity<Map<String, Double>> getWeeklyBreakdown() {
        return ResponseEntity.ok(service.getWeeklyBreakdown());
    }

    /**
     * Get total hours logged today
     * GET /api/activities/today/total
     */
    @GetMapping("/today/total")
    public ResponseEntity<Map<String, Object>> getTodayTotal() {
        double hours = service.getTodayTotalHours();
        return ResponseEntity.ok(Map.of(
            "date", LocalDate.now(),
            "totalHours", hours,
            "totalMinutes", (int) (hours * 60)
        ));
    }

    /**
     * DTO for quick logging
     */
    record QuickLogRequest(String activity, String category, int durationMinutes) {}
}
