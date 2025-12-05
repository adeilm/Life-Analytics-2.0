package com.dali.lifeanalytics.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /**
     * Find events within a date range (for calendar view)
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :start AND e.startTime <= :end ORDER BY e.startTime")
    List<CalendarEvent> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Find upcoming events (next N days)
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :now AND e.startTime <= :until AND e.completed = false ORDER BY e.startTime")
    List<CalendarEvent> findUpcoming(
        @Param("now") LocalDateTime now,
        @Param("until") LocalDateTime until
    );

    /**
     * Find events by category
     */
    List<CalendarEvent> findByCategoryOrderByStartTimeDesc(String category);

    /**
     * Find all incomplete events
     */
    List<CalendarEvent> findByCompletedFalseOrderByStartTime();

    /**
     * Find today's events
     */
    @Query("SELECT e FROM CalendarEvent e WHERE DATE(e.startTime) = CURRENT_DATE ORDER BY e.startTime")
    List<CalendarEvent> findToday();

    /**
     * Search events by title
     */
    List<CalendarEvent> findByTitleContainingIgnoreCaseOrderByStartTimeDesc(String keyword);
}
