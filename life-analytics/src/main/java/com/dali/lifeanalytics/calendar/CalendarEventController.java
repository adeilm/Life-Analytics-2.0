package com.dali.lifeanalytics.calendar;

import com.dali.lifeanalytics.calendar.dto.ConflictCheckResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class CalendarEventController {

    private final CalendarEventService service;
    private final CalendarExportService exportService;

    /**
     * Create a new event
     * POST /api/events
     */
    @PostMapping
    public ResponseEntity<CalendarEvent> create(@Valid @RequestBody CalendarEvent event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(event));
    }

    /**
     * Get all events
     * GET /api/events
     */
    @GetMapping
    public ResponseEntity<List<CalendarEvent>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Get event by ID
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalendarEvent> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Update an event
     * PUT /api/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CalendarEvent> update(
            @PathVariable Long id,
            @Valid @RequestBody CalendarEvent event) {
        return ResponseEntity.ok(service.update(id, event));
    }

    /**
     * Delete an event
     * DELETE /api/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark event as completed
     * POST /api/events/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<CalendarEvent> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.complete(id));
    }

    /**
     * Get today's events
     * GET /api/events/today
     */
    @GetMapping("/today")
    public ResponseEntity<List<CalendarEvent>> findToday() {
        return ResponseEntity.ok(service.findToday());
    }

    /**
     * Get upcoming events (next N days, default 7)
     * GET /api/events/upcoming?days=7
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<CalendarEvent>> findUpcoming(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(service.findUpcoming(days));
    }

    /**
     * Get events by date range
     * GET /api/events/range?start=2025-12-01T00:00:00&end=2025-12-31T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<List<CalendarEvent>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(service.findByDateRange(start, end));
    }

    /**
     * Get events by category
     * GET /api/events/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<CalendarEvent>> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.findByCategory(category));
    }

    /**
     * Search events by keyword
     * GET /api/events/search?q=meeting
     */
    @GetMapping("/search")
    public ResponseEntity<List<CalendarEvent>> search(@RequestParam String q) {
        return ResponseEntity.ok(service.search(q));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CALENDAR EXPORT & CONFLICT DETECTION
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Export events as ICS file
     * GET /api/events/export?start=2025-12-01T00:00:00&end=2025-12-31T23:59:59&includeExams=true
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportIcs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "true") boolean includeExams) {
        
        String icsContent = exportService.generateIcsFile(start, end, includeExams);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "life-analytics-events.ics");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(icsContent);
    }

    /**
     * Check for conflicts before creating an event
     * POST /api/events/check-conflicts
     * Body: { "startTime": "2025-12-05T14:00:00", "endTime": "2025-12-05T16:00:00" }
     */
    @PostMapping("/check-conflicts")
    public ResponseEntity<ConflictCheckResultDto> checkConflicts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        ConflictCheckResultDto result = exportService.checkConflicts(startTime, endTime);
        
        if (result.isHasConflict()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Create event with conflict checking
     * POST /api/events/safe
     * Returns CONFLICT status if there are overlapping events
     */
    @PostMapping("/safe")
    public ResponseEntity<?> createWithConflictCheck(@Valid @RequestBody CalendarEvent event) {
        LocalDateTime endTime = event.getEndTime() != null 
                ? event.getEndTime() 
                : event.getStartTime().plusHours(1);
        
        ConflictCheckResultDto conflicts = exportService.checkConflicts(event.getStartTime(), endTime);
        
        if (conflicts.isHasConflict()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflicts);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(event));
    }
}
