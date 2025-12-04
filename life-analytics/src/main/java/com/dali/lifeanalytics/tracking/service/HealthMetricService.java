package com.dali.lifeanalytics.tracking.service;

import com.dali.lifeanalytics.tracking.entity.HealthMetric;
import com.dali.lifeanalytics.tracking.repository.HealthMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * HealthMetric Service
 * ─────────────────────
 * Business logic for health metrics tracking.
 */
@Service
@RequiredArgsConstructor
public class HealthMetricService {

    private final HealthMetricRepository healthMetricRepository;

    /**
     * Get all health metrics (most recent first).
     */
    public List<HealthMetric> getAllMetrics() {
        return healthMetricRepository.findAllByOrderByRecordedAtDesc();
    }

    /**
     * Get a metric by ID.
     */
    public Optional<HealthMetric> getMetricById(Long id) {
        return healthMetricRepository.findById(id);
    }

    /**
     * Get metrics within a date range.
     * @param from Start date (inclusive, from 00:00)
     * @param to End date (inclusive, until 23:59:59)
     */
    public List<HealthMetric> getMetricsByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);
        return healthMetricRepository.findByRecordedAtBetweenOrderByRecordedAtDesc(start, end);
    }

    /**
     * Create a new health metric.
     */
    @Transactional
    public HealthMetric createMetric(HealthMetric metric) {
        metric.setId(null);
        if (metric.getRecordedAt() == null) {
            metric.setRecordedAt(LocalDateTime.now());
        }
        return healthMetricRepository.save(metric);
    }

    /**
     * Update a health metric.
     */
    @Transactional
    public Optional<HealthMetric> updateMetric(Long id, HealthMetric updated) {
        return healthMetricRepository.findById(id)
                .map(existing -> {
                    if (updated.getRecordedAt() != null) {
                        existing.setRecordedAt(updated.getRecordedAt());
                    }
                    if (updated.getSleepHours() != null) {
                        existing.setSleepHours(updated.getSleepHours());
                    }
                    if (updated.getMoodScore() != null) {
                        existing.setMoodScore(updated.getMoodScore());
                    }
                    if (updated.getStressLevel() != null) {
                        existing.setStressLevel(updated.getStressLevel());
                    }
                    if (updated.getEnergyLevel() != null) {
                        existing.setEnergyLevel(updated.getEnergyLevel());
                    }
                    existing.setNote(updated.getNote());
                    return healthMetricRepository.save(existing);
                });
    }

    /**
     * Delete a health metric.
     */
    @Transactional
    public boolean deleteMetric(Long id) {
        if (healthMetricRepository.existsById(id)) {
            healthMetricRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get average mood for a date range.
     */
    public Double getAverageMood(LocalDate from, LocalDate to) {
        return healthMetricRepository.avgMoodScoreBetween(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    /**
     * Get average stress for a date range.
     */
    public Double getAverageStress(LocalDate from, LocalDate to) {
        return healthMetricRepository.avgStressLevelBetween(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    /**
     * Get average energy for a date range.
     */
    public Double getAverageEnergy(LocalDate from, LocalDate to) {
        return healthMetricRepository.avgEnergyLevelBetween(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    /**
     * Get average sleep for a date range.
     */
    public Double getAverageSleep(LocalDate from, LocalDate to) {
        return healthMetricRepository.avgSleepHoursBetween(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }
}
