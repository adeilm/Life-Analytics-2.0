package com.dali.lifeanalytics.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Conflict Detection Result DTO
 * ──────────────────────────────
 * Returned when checking for calendar conflicts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConflictCheckResultDto {

    /**
     * Whether a conflict was detected
     */
    private boolean hasConflict;

    /**
     * The requested time slot
     */
    private LocalDateTime requestedStart;
    private LocalDateTime requestedEnd;

    /**
     * List of conflicting events
     */
    private List<ConflictingEvent> conflicts;

    /**
     * Suggested alternative time slots
     */
    private List<TimeSlot> suggestedAlternatives;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConflictingEvent {
        private Long id;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String category;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String description;
    }
}
