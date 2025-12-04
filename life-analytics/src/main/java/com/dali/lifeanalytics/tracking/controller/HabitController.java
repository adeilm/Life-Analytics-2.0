package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.Habit;
import com.dali.lifeanalytics.tracking.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
