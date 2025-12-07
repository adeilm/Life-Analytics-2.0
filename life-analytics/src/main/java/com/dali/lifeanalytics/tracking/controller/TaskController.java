package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.Task;
import com.dali.lifeanalytics.tracking.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Task Controller
 * ─────────────────
 * REST API for task management.
 *
 * Base path: /api/tasks
 *
 * Endpoints:
 *   GET    /api/tasks                – List all tasks (optional filters)
 *   GET    /api/tasks/{id}           – Get a specific task
 *   GET    /api/tasks/active         – Get active (pending/in-progress) tasks
 *   GET    /api/tasks/overdue        – Get overdue tasks
 *   POST   /api/tasks                – Create a new task
 *   PUT    /api/tasks/{id}           – Update an existing task
 *   PATCH  /api/tasks/{id}/status    – Update task status
 *   DELETE /api/tasks/{id}           – Delete a task
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/tasks
    // GET /api/tasks?status=PENDING
    // GET /api/tasks?category=STUDY
    // GET /api/tasks?priority=HIGH
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Task> tasks;
        
        if (status != null && !status.isBlank()) {
            tasks = taskService.getTasksByStatus(status);
        } else if (category != null && !category.isBlank()) {
            tasks = taskService.getTasksByCategory(category);
        } else if (priority != null && !priority.isBlank()) {
            tasks = taskService.getTasksByPriority(priority);
        } else if (dueDate != null) {
            tasks = taskService.getTasksDueOn(dueDate);
        } else if (startDate != null && endDate != null) {
            tasks = taskService.getTasksDueBetween(startDate, endDate);
        } else {
            tasks = taskService.getAllTasks();
        }
        
        return ResponseEntity.ok(tasks);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/tasks/active
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/active")
    public ResponseEntity<List<Task>> getActiveTasks() {
        return ResponseEntity.ok(taskService.getActiveTasks());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/tasks/overdue
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/tasks/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/tasks
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/tasks/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.updateTask(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /api/tasks/{id}/status
    // Body: { "status": "COMPLETED" }
    // ─────────────────────────────────────────────────────────────────────────
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return taskService.updateTaskStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/tasks/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
