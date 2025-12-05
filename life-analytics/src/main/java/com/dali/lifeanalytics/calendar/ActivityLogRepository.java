package com.dali.lifeanalytics.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * Find activities for a specific date
     */
    List<ActivityLog> findByLogDateOrderByCreatedAtDesc(LocalDate logDate);

    /**
     * Find activities within a date range
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.logDate >= :start AND a.logDate <= :end ORDER BY a.logDate DESC, a.createdAt DESC")
    List<ActivityLog> findByDateRange(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    /**
     * Find activities by category
     */
    List<ActivityLog> findByCategoryOrderByLogDateDesc(String category);

    /**
     * Total minutes spent on a category within date range
     */
    @Query("SELECT COALESCE(SUM(a.durationMinutes), 0) FROM ActivityLog a WHERE a.category = :category AND a.logDate >= :start AND a.logDate <= :end")
    Integer sumMinutesByCategoryAndDateRange(
        @Param("category") String category,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    /**
     * Total minutes for all categories on a specific date
     */
    @Query("SELECT a.category, COALESCE(SUM(a.durationMinutes), 0) FROM ActivityLog a WHERE a.logDate = :date GROUP BY a.category")
    List<Object[]> sumMinutesByDateGroupedByCategory(@Param("date") LocalDate date);

    /**
     * Weekly breakdown by category
     */
    @Query("SELECT a.category, COALESCE(SUM(a.durationMinutes), 0) FROM ActivityLog a WHERE a.logDate >= :start AND a.logDate <= :end GROUP BY a.category ORDER BY SUM(a.durationMinutes) DESC")
    List<Object[]> weeklyBreakdownByCategory(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    /**
     * Find today's activities
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.logDate = CURRENT_DATE ORDER BY a.createdAt DESC")
    List<ActivityLog> findToday();
}
