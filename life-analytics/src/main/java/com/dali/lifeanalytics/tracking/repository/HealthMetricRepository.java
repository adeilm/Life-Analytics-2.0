package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * HealthMetric Repository
 * ────────────────────────
 * Data access for health metrics.
 */
@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {

    /**
     * Find all metrics ordered by most recent first.
     */
    List<HealthMetric> findAllByOrderByRecordedAtDesc();

    /**
     * Find metrics within a date/time range.
     */
    List<HealthMetric> findByRecordedAtBetweenOrderByRecordedAtDesc(
            LocalDateTime start, LocalDateTime end);

    /**
     * Get average mood score for a period.
     */
    @Query("SELECT AVG(hm.moodScore) FROM HealthMetric hm " +
           "WHERE hm.recordedAt BETWEEN :start AND :end AND hm.moodScore IS NOT NULL")
    Double avgMoodScoreBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Get average stress level for a period.
     */
    @Query("SELECT AVG(hm.stressLevel) FROM HealthMetric hm " +
           "WHERE hm.recordedAt BETWEEN :start AND :end AND hm.stressLevel IS NOT NULL")
    Double avgStressLevelBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Get average energy level for a period.
     */
    @Query("SELECT AVG(hm.energyLevel) FROM HealthMetric hm " +
           "WHERE hm.recordedAt BETWEEN :start AND :end AND hm.energyLevel IS NOT NULL")
    Double avgEnergyLevelBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Get average sleep hours for a period.
     */
    @Query("SELECT AVG(hm.sleepHours) FROM HealthMetric hm " +
           "WHERE hm.recordedAt BETWEEN :start AND :end AND hm.sleepHours IS NOT NULL")
    Double avgSleepHoursBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
