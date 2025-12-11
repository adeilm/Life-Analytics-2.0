package com.dali.wellness.integration.google;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dali.wellness.tracking.entity.Task;
import com.dali.wellness.tracking.service.TaskService;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final GoogleCalendarService googleCalendarService;
    private final TaskService taskService;

    public CalendarController(GoogleCalendarService googleCalendarService, TaskService taskService) {
        this.googleCalendarService = googleCalendarService;
        this.taskService = taskService;
    }

    @PostMapping("/sync-task/{taskId}")
    public String syncTaskToCalendar(@PathVariable Long taskId) {
        // Fetch task
        Task task = taskService.getAllTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getDeadline() == null) {
            return "Task has no deadline";
        }

        // Create event (1 hour duration by default for tasks)
        LocalDateTime start = task.getDeadline().minusHours(1);
        LocalDateTime end = task.getDeadline();

        return googleCalendarService.createEvent(
                task.getTitle(),
                task.getDescription(),
                start,
                end
        );
    }
    
    @GetMapping("/test-auth")
    public String testAuth() {
        try {
            // Just try to create a dummy event to trigger auth flow
            return googleCalendarService.createEvent("Test Event", "Testing Auth", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        } catch (Exception e) {
            return "Auth failed: " + e.getMessage();
        }
    }
}
