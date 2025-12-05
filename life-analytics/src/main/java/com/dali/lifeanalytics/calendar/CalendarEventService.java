package com.dali.lifeanalytics.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarEventService {

    private final CalendarEventRepository repository;

    public CalendarEvent create(CalendarEvent event) {
        return repository.save(event);
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public CalendarEvent findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    public CalendarEvent update(Long id, CalendarEvent updated) {
        CalendarEvent existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setLocation(updated.getLocation());
        existing.setAllDay(updated.getAllDay());
        existing.setNotes(updated.getNotes());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Mark event as completed
     */
    public CalendarEvent complete(Long id) {
        CalendarEvent event = findById(id);
        event.setCompleted(true);
        return repository.save(event);
    }

    /**
     * Get events for a specific date range (calendar view)
     */
    @Transactional(readOnly = true)
    public List<CalendarEvent> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByDateRange(start, end);
    }

    /**
     * Get upcoming events for the next N days
     */
    @Transactional(readOnly = true)
    public List<CalendarEvent> findUpcoming(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(days);
        return repository.findUpcoming(now, until);
    }

    /**
     * Get today's events
     */
    @Transactional(readOnly = true)
    public List<CalendarEvent> findToday() {
        return repository.findToday();
    }

    /**
     * Find events by category
     */
    @Transactional(readOnly = true)
    public List<CalendarEvent> findByCategory(String category) {
        return repository.findByCategoryOrderByStartTimeDesc(category);
    }

    /**
     * Search events by keyword
     */
    @Transactional(readOnly = true)
    public List<CalendarEvent> search(String keyword) {
        return repository.findByTitleContainingIgnoreCaseOrderByStartTimeDesc(keyword);
    }
}
