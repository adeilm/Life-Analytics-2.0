package com.dali.wellness.tracking.controller;

import com.dali.wellness.tracking.entity.Habit;
import com.dali.wellness.tracking.entity.HabitLog;
import com.dali.wellness.tracking.service.HabitService;
import com.dali.wellness.tracking.service.HabitLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Habit Controller
 * ─────────────────
 * REST API for habit management.
 *
 * Base path: /api/habits
 *
 * Endpoints:
 *   GET    /api/habits            – List all habits (optional ?category=X)
 *   GET    /api/habits/{id}       – Get a specific habit
 *   POST   /api/habits            – Create a new habit
 *   PUT    /api/habits/{id}       – Update an existing habit
 *   DELETE /api/habits/{id}       – Delete a habit
 *
 * Annotations explained:
 *   @RestController    – Combines @Controller + @ResponseBody (returns JSON)
 *   @RequestMapping    – Base path for all endpoints in this controller
 *   @GetMapping, etc.  – HTTP method + path mapping
 *   @PathVariable      – Extracts value from URL path (e.g., /habits/5 → id=5)
 *   @RequestParam      – Extracts query parameter (e.g., ?category=HEALTH)
 *   @RequestBody       – Deserializes JSON request body into Java object
 *   @Valid             – Triggers bean validation on the request body
 *   ResponseEntity     – Allows setting HTTP status code and headers
 */
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;
    private final HabitLogService habitLogService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/habits
    // GET /api/habits?category=HEALTH
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * List all habits, optionally filtered by category.
     *
     * @param category Optional query param to filter by category.
     * @return List of habits (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits(
            @RequestParam(required = false) String category) {
        
        List<Habit> habits;
        if (category != null && !category.isBlank()) {
            habits = habitService.getHabitsByCategory(category);
        } else {
            habits = habitService.getAllHabits();
        }
        return ResponseEntity.ok(habits);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/habits/{id}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Get a specific habit by ID.
     *
     * @param id Path variable – the habit ID.
     * @return The habit (200 OK) or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabitById(@PathVariable Long id) {
        return habitService.getHabitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/habits
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Create a new habit.
     *
     * @param habit JSON body with habit data (name required).
     * @return The created habit (201 Created).
     */
    @PostMapping
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody Habit habit) {
        Habit created = habitService.createHabit(habit);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/habits/{id}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Update an existing habit.
     *
     * @param id    Path variable – the habit ID to update.
     * @param habit JSON body with updated data.
     * @return The updated habit (200 OK) or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody Habit habit) {
        
        return habitService.updateHabit(id, habit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/habits/{id}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Delete a habit.
     *
     * @param id Path variable – the habit ID to delete.
     * @return 204 No Content if deleted, 404 Not Found if not exists.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        if (habitService.deleteHabit(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HABIT LOGS
    // ═══════════════════════════════════════════════════════════════════════════

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/habits/{id}/logs
    // GET /api/habits/{id}/logs?from=2025-01-01&to=2025-12-31
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Get all logs for a habit, optionally filtered by date range.
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<HabitLog>> getHabitLogs(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        // Verify habit exists
        if (habitService.getHabitById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<HabitLog> logs;
        if (from != null && to != null) {
            logs = habitLogService.getLogsByHabitIdAndDateRange(id, from, to);
        } else {
            logs = habitLogService.getLogsByHabitId(id);
        }
        return ResponseEntity.ok(logs);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/habits/{id}/logs
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Create a new log for a habit.
     * Body: { "logDate": "2025-12-04", "value": 30, "note": "Morning run" }
     * If logDate is omitted, defaults to today.
     * If value is omitted, defaults to 1.
     */
    @PostMapping("/{id}/logs")
    public ResponseEntity<HabitLog> createHabitLog(
            @PathVariable Long id,
            @RequestBody HabitLog log) {
        
        return habitLogService.createLog(id, log)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/habits/{id}/logs/quick
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Quick log: Mark habit as done for today (value=1).
     * No body required.
     */
    @PostMapping("/{id}/logs/quick")
    public ResponseEntity<HabitLog> quickLogHabit(@PathVariable Long id) {
        return habitLogService.quickLog(id)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/habits/{habitId}/logs/{logId}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Update a specific log entry.
     */
    @PutMapping("/{habitId}/logs/{logId}")
    public ResponseEntity<HabitLog> updateHabitLog(
            @PathVariable Long habitId,
            @PathVariable Long logId,
            @RequestBody HabitLog log) {
        
        return habitLogService.updateLog(logId, log)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/habits/{habitId}/logs/{logId}
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Delete a specific log entry.
     */
    @DeleteMapping("/{habitId}/logs/{logId}")
    public ResponseEntity<Void> deleteHabitLog(
            @PathVariable Long habitId,
            @PathVariable Long logId) {
        
        if (habitLogService.deleteLog(logId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
