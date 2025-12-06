package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.HealthMetric;
import com.dali.lifeanalytics.tracking.service.HealthMetricService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * HealthMetric Controller
 * ────────────────────────
 * REST API for health metrics tracking.
 *
 * Base path: /api/health-metrics
 *
 * Endpoints:
 *   GET    /api/health-metrics              – List all metrics (optional date range)
 *   GET    /api/health-metrics/{id}         – Get a specific metric
 *   POST   /api/health-metrics              – Create a new metric
 *   PUT    /api/health-metrics/{id}         – Update a metric
 *   DELETE /api/health-metrics/{id}         – Delete a metric
 */
@RestController
@RequestMapping("/api/health-metrics")
@RequiredArgsConstructor
public class HealthMetricController {

    private final HealthMetricService healthMetricService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/health-metrics
    // GET /api/health-metrics?from=2025-01-01&to=2025-12-31
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * List all health metrics, optionally filtered by date range.
     *
     * @param from Start date (inclusive)
     * @param to   End date (inclusive)
     * @return List of health metrics (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<HealthMetric>> getAllMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        List<HealthMetric> metrics;
        if (from != null && to != null) {
            metrics = healthMetricService.getMetricsByDateRange(from, to);
        } else {
            metrics = healthMetricService.getAllMetrics();
        }
        return ResponseEntity.ok(metrics);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/health-metrics/{id}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Get a specific health metric by ID.
     *
     * @param id Path variable – the metric ID.
     * @return The metric (200 OK) or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthMetric> getMetricById(@PathVariable Long id) {
        return healthMetricService.getMetricById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/health-metrics
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Create a new health metric.
     *
     * Example body:
     * {
     *   "sleepHours": 7.5,
     *   "moodScore": 4,
     *   "stressLevel": 2,
     *   "energyLevel": 4,
     *   "note": "Feeling good today"
     * }
     *
     * If recordedAt is omitted, defaults to now.
     *
     * @param metric JSON body with metric data.
     * @return The created metric (201 Created).
     */
    @PostMapping
    public ResponseEntity<HealthMetric> createMetric(@Valid @RequestBody HealthMetric metric) {
        HealthMetric created = healthMetricService.createMetric(metric);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/health-metrics/{id}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Update an existing health metric.
     *
     * @param id     Path variable – the metric ID to update.
     * @param metric JSON body with updated data.
     * @return The updated metric (200 OK) or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthMetric> updateMetric(
            @PathVariable Long id,
            @Valid @RequestBody HealthMetric metric) {
        
        return healthMetricService.updateMetric(id, metric)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/health-metrics/{id}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Delete a health metric.
     *
     * @param id Path variable – the metric ID to delete.
     * @return 204 No Content if deleted, 404 Not Found if not exists.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetric(@PathVariable Long id) {
        if (healthMetricService.deleteMetric(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
