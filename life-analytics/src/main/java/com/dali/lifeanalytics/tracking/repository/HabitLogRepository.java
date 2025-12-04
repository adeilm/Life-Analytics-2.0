package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * HabitLog Repository
 * ────────────────────
 * Data access for habit log entries.
 */
@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    /**
     * Find all logs for a specific habit.
     */
    List<HabitLog> findByHabitIdOrderByLogDateDesc(Long habitId);

    /**
     * Find logs for a habit within a date range.
     */
    List<HabitLog> findByHabitIdAndLogDateBetweenOrderByLogDateDesc(
            Long habitId, LocalDate startDate, LocalDate endDate);

    /**
     * Find a log for a specific habit on a specific date.
     * Useful to check if already logged today.
     */
    List<HabitLog> findByHabitIdAndLogDate(Long habitId, LocalDate logDate);

    /**
     * Count logs for a habit within a date range.
     * Useful for calculating completion rates.
     */
    @Query("SELECT COUNT(hl) FROM HabitLog hl WHERE hl.habit.id = :habitId " +
           "AND hl.logDate BETWEEN :startDate AND :endDate")
    long countByHabitIdAndDateRange(
            @Param("habitId") Long habitId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Sum values for a habit within a date range.
     * Useful for totaling minutes, counts, etc.
     */
    @Query("SELECT COALESCE(SUM(hl.value), 0) FROM HabitLog hl WHERE hl.habit.id = :habitId " +
           "AND hl.logDate BETWEEN :startDate AND :endDate")
    long sumValueByHabitIdAndDateRange(
            @Param("habitId") Long habitId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
