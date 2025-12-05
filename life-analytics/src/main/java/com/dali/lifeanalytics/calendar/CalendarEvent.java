package com.dali.lifeanalytics.calendar;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a calendar event (appointment, meeting, reminder, etc.)
 * 
 * Use cases:
 * - Doctor appointments
 * - Meetings
 * - Deadlines
 * - Reminders
 * - Social events
 */
@Entity
@Table(name = "calendar_event", indexes = {
    @Index(name = "idx_event_start", columnList = "start_time"),
    @Index(name = "idx_event_category", columnList = "category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    /**
     * Category of the event: WORK, HEALTH, SOCIAL, PERSONAL, FAMILY, OTHER
     */
    @Size(max = 50)
    @Column(length = 50)
    private String category;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * End time is optional - some events (like reminders) don't have duration
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * Location can be physical address or virtual link
     */
    @Size(max = 300)
    @Column(length = 300)
    private String location;

    /**
     * Is this an all-day event?
     */
    @Column(name = "all_day")
    @Builder.Default
    private Boolean allDay = false;

    /**
     * Has this event been completed/attended?
     */
    @Builder.Default
    private Boolean completed = false;

    /**
     * Optional notes after the event
     */
    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
