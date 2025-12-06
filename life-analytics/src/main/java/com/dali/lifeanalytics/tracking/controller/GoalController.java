package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.Goal;
import com.dali.lifeanalytics.tracking.entity.GoalProgress;
import com.dali.lifeanalytics.tracking.service.GoalService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Goal Controller
 * ─────────────────
 * REST API for goal management.
 *
 * Base path: /api/goals
 *
 * Endpoints:
 *   GET    /api/goals                         – List all goals (optional filters)
 *   GET    /api/goals/{id}                    – Get a specific goal
 *   GET    /api/goals/active                  – Get active goals
 *   GET    /api/goals/overdue                 – Get overdue goals
 *   POST   /api/goals                         – Create a new goal
 *   PUT    /api/goals/{id}                    – Update an existing goal
 *   PATCH  /api/goals/{id}/status             – Update goal status
 *   DELETE /api/goals/{id}                    – Delete a goal
 *   
 * Progress endpoints:
 *   GET    /api/goals/{id}/progress           – Get progress entries for a goal
 *   POST   /api/goals/{id}/progress           – Add a progress entry
 *   DELETE /api/goals/{goalId}/progress/{id}  – Delete a progress entry
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/goals
    // GET /api/goals?domain=HEALTH
    // GET /api/goals?status=ACTIVE
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals(
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String status) {
        
        List<Goal> goals;
        
        if (domain != null && !domain.isBlank()) {
            goals = goalService.getGoalsByDomain(domain);
        } else if (status != null && !status.isBlank()) {
            goals = goalService.getGoalsByStatus(status);
        } else {
            goals = goalService.getAllGoals();
        }
        
        return ResponseEntity.ok(goals);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/goals/active
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/active")
    public ResponseEntity<List<Goal>> getActiveGoals() {
        return ResponseEntity.ok(goalService.getActiveGoals());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/goals/overdue
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/overdue")
    public ResponseEntity<List<Goal>> getOverdueGoals() {
        return ResponseEntity.ok(goalService.getOverdueGoals());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/goals/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long id) {
        return goalService.getGoalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/goals
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody Goal goal) {
        Goal createdGoal = goalService.createGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/goals/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id, @Valid @RequestBody Goal goal) {
        return goalService.updateGoal(id, goal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /api/goals/{id}/status
    // Body: { "status": "COMPLETED" }
    // ─────────────────────────────────────────────────────────────────────────
    @PatchMapping("/{id}/status")
    public ResponseEntity<Goal> updateGoalStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return goalService.updateGoalStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/goals/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        if (goalService.deleteGoal(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PROGRESS ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/goals/{id}/progress
    // GET /api/goals/{id}/progress?startDate=...&endDate=...
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/progress")
    public ResponseEntity<List<GoalProgress>> getGoalProgress(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Verify goal exists
        if (goalService.getGoalById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<GoalProgress> progress;
        if (startDate != null && endDate != null) {
            progress = goalService.getProgressBetween(id, startDate, endDate);
        } else {
            progress = goalService.getProgressByGoalId(id);
        }
        
        return ResponseEntity.ok(progress);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/goals/{id}/progress/latest
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/progress/latest")
    public ResponseEntity<GoalProgress> getLatestProgress(@PathVariable Long id) {
        return goalService.getLatestProgress(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/goals/{id}/progress
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/{id}/progress")
    public ResponseEntity<GoalProgress> addProgress(
            @PathVariable Long id,
            @Valid @RequestBody GoalProgress progress) {
        try {
            GoalProgress createdProgress = goalService.addProgress(id, progress);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProgress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/goals/{goalId}/progress/{progressId}
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{goalId}/progress/{progressId}")
    public ResponseEntity<Void> deleteProgress(
            @PathVariable Long goalId,
            @PathVariable Long progressId) {
        if (goalService.deleteProgress(progressId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
