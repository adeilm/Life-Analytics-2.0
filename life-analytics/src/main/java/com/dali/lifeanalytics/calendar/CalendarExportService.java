package com.dali.lifeanalytics.calendar;

import com.dali.lifeanalytics.calendar.dto.ConflictCheckResultDto;
import com.dali.lifeanalytics.tracking.entity.Exam;
import com.dali.lifeanalytics.tracking.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Calendar Export Service
 * ────────────────────────
 * Generates .ics (iCalendar) files and handles conflict detection.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarExportService {

    private final CalendarEventRepository calendarEventRepository;
    private final ExamRepository examRepository;

    private static final DateTimeFormatter ICS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    /**
     * Generate an ICS file content for events in a date range.
     */
    public String generateIcsFile(LocalDateTime startDate, LocalDateTime endDate, boolean includeExams) {
        StringBuilder ics = new StringBuilder();
        
        // ICS Header
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//Life Analytics 2.0//EN\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");
        ics.append("METHOD:PUBLISH\r\n");
        ics.append("X-WR-CALNAME:Life Analytics Events\r\n");

        // Add calendar events
        List<CalendarEvent> events = calendarEventRepository.findByDateRange(startDate, endDate);
        for (CalendarEvent event : events) {
            ics.append(convertEventToVEvent(event));
        }

        // Optionally include exams
        if (includeExams) {
            List<Exam> exams = examRepository.findByDateTimeBetween(startDate, endDate);
            for (Exam exam : exams) {
                ics.append(convertExamToVEvent(exam));
            }
        }

        // ICS Footer
        ics.append("END:VCALENDAR\r\n");

        return ics.toString();
    }

    /**
     * Check for conflicts with existing events.
     */
    public ConflictCheckResultDto checkConflicts(LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        ConflictCheckResultDto.ConflictCheckResultDtoBuilder result = ConflictCheckResultDto.builder()
                .requestedStart(requestedStart)
                .requestedEnd(requestedEnd)
                .conflicts(new ArrayList<>())
                .suggestedAlternatives(new ArrayList<>());

        // Find overlapping events
        List<CalendarEvent> overlapping = findOverlappingEvents(requestedStart, requestedEnd);
        
        if (overlapping.isEmpty()) {
            result.hasConflict(false);
        } else {
            result.hasConflict(true);
            
            // Convert to conflict DTOs
            List<ConflictCheckResultDto.ConflictingEvent> conflicts = new ArrayList<>();
            for (CalendarEvent event : overlapping) {
                conflicts.add(ConflictCheckResultDto.ConflictingEvent.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .category(event.getCategory())
                        .build());
            }
            result.conflicts(conflicts);
            
            // Generate alternative suggestions
            result.suggestedAlternatives(findAlternativeSlots(requestedStart, requestedEnd, overlapping));
        }

        return result.build();
    }

    /**
     * Find events that overlap with the given time range.
     */
    private List<CalendarEvent> findOverlappingEvents(LocalDateTime start, LocalDateTime end) {
        // An event overlaps if:
        // (event.start < requested.end) AND (event.end > requested.start)
        List<CalendarEvent> allEvents = calendarEventRepository.findByDateRange(
                start.minusHours(12), end.plusHours(12));
        
        List<CalendarEvent> overlapping = new ArrayList<>();
        for (CalendarEvent event : allEvents) {
            LocalDateTime eventEnd = event.getEndTime() != null 
                    ? event.getEndTime() 
                    : event.getStartTime().plusHours(1);
            
            if (event.getStartTime().isBefore(end) && eventEnd.isAfter(start)) {
                overlapping.add(event);
            }
        }
        return overlapping;
    }

    /**
     * Find alternative time slots near the requested time.
     */
    private List<ConflictCheckResultDto.TimeSlot> findAlternativeSlots(
            LocalDateTime requestedStart, 
            LocalDateTime requestedEnd,
            List<CalendarEvent> conflicts) {
        
        List<ConflictCheckResultDto.TimeSlot> alternatives = new ArrayList<>();
        long durationMinutes = java.time.Duration.between(requestedStart, requestedEnd).toMinutes();

        // Try slots before the first conflict
        LocalDateTime earliestConflict = conflicts.stream()
                .map(CalendarEvent::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(requestedStart);
        
        LocalDateTime beforeSlotEnd = earliestConflict.minusMinutes(15);
        LocalDateTime beforeSlotStart = beforeSlotEnd.minusMinutes(durationMinutes);
        
        if (beforeSlotStart.isAfter(LocalDateTime.now())) {
            alternatives.add(ConflictCheckResultDto.TimeSlot.builder()
                    .startTime(beforeSlotStart)
                    .endTime(beforeSlotEnd)
                    .description("Before conflicting event(s)")
                    .build());
        }

        // Try slots after the last conflict
        LocalDateTime latestConflictEnd = conflicts.stream()
                .map(e -> e.getEndTime() != null ? e.getEndTime() : e.getStartTime().plusHours(1))
                .max(LocalDateTime::compareTo)
                .orElse(requestedEnd);
        
        LocalDateTime afterSlotStart = latestConflictEnd.plusMinutes(15);
        LocalDateTime afterSlotEnd = afterSlotStart.plusMinutes(durationMinutes);
        
        alternatives.add(ConflictCheckResultDto.TimeSlot.builder()
                .startTime(afterSlotStart)
                .endTime(afterSlotEnd)
                .description("After conflicting event(s)")
                .build());

        // Try same time next day
        LocalDateTime nextDayStart = requestedStart.plusDays(1);
        LocalDateTime nextDayEnd = requestedEnd.plusDays(1);
        List<CalendarEvent> nextDayConflicts = findOverlappingEvents(nextDayStart, nextDayEnd);
        
        if (nextDayConflicts.isEmpty()) {
            alternatives.add(ConflictCheckResultDto.TimeSlot.builder()
                    .startTime(nextDayStart)
                    .endTime(nextDayEnd)
                    .description("Same time tomorrow (no conflicts)")
                    .build());
        }

        return alternatives;
    }

    /**
     * Convert a CalendarEvent to VEVENT format.
     */
    private String convertEventToVEvent(CalendarEvent event) {
        StringBuilder vevent = new StringBuilder();
        vevent.append("BEGIN:VEVENT\r\n");
        vevent.append("UID:").append(generateUid(event.getId(), "event")).append("\r\n");
        vevent.append("DTSTAMP:").append(formatIcsDate(LocalDateTime.now())).append("\r\n");
        vevent.append("DTSTART:").append(formatIcsDate(event.getStartTime())).append("\r\n");
        
        if (event.getEndTime() != null) {
            vevent.append("DTEND:").append(formatIcsDate(event.getEndTime())).append("\r\n");
        }
        
        vevent.append("SUMMARY:").append(escapeIcsText(event.getTitle())).append("\r\n");
        
        if (event.getDescription() != null) {
            vevent.append("DESCRIPTION:").append(escapeIcsText(event.getDescription())).append("\r\n");
        }
        
        if (event.getLocation() != null) {
            vevent.append("LOCATION:").append(escapeIcsText(event.getLocation())).append("\r\n");
        }
        
        if (event.getCategory() != null) {
            vevent.append("CATEGORIES:").append(event.getCategory()).append("\r\n");
        }
        
        vevent.append("END:VEVENT\r\n");
        return vevent.toString();
    }

    /**
     * Convert an Exam to VEVENT format.
     */
    private String convertExamToVEvent(Exam exam) {
        StringBuilder vevent = new StringBuilder();
        vevent.append("BEGIN:VEVENT\r\n");
        vevent.append("UID:").append(generateUid(exam.getId(), "exam")).append("\r\n");
        vevent.append("DTSTAMP:").append(formatIcsDate(LocalDateTime.now())).append("\r\n");
        vevent.append("DTSTART:").append(formatIcsDate(exam.getDateTime())).append("\r\n");
        
        if (exam.getDurationMinutes() != null) {
            LocalDateTime endTime = exam.getDateTime().plusMinutes(exam.getDurationMinutes());
            vevent.append("DTEND:").append(formatIcsDate(endTime)).append("\r\n");
        }
        
        String summary = "EXAM: " + exam.getTitle();
        if (exam.getCourseName() != null) {
            summary += " (" + exam.getCourseName() + ")";
        }
        vevent.append("SUMMARY:").append(escapeIcsText(summary)).append("\r\n");
        
        if (exam.getDescription() != null) {
            vevent.append("DESCRIPTION:").append(escapeIcsText(exam.getDescription())).append("\r\n");
        }
        
        if (exam.getLocation() != null) {
            vevent.append("LOCATION:").append(escapeIcsText(exam.getLocation())).append("\r\n");
        }
        
        vevent.append("CATEGORIES:EXAM\r\n");
        vevent.append("END:VEVENT\r\n");
        return vevent.toString();
    }

    private String formatIcsDate(LocalDateTime dateTime) {
        return dateTime.format(ICS_DATE_FORMAT);
    }

    private String escapeIcsText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace(",", "\\,")
                   .replace(";", "\\;")
                   .replace("\n", "\\n");
    }

    private String generateUid(Long id, String type) {
        return type + "-" + id + "@lifeanalytics.local";
    }
}
