package com.dali.wellness.tracking.service;

import com.dali.wellness.tracking.entity.Habit;
import com.dali.wellness.tracking.entity.HabitLog;
import com.dali.wellness.tracking.repository.HabitLogRepository;
import com.dali.wellness.tracking.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * HabitLog Service
 * ─────────────────
 * Business logic for habit logging.
 */
@Service
@RequiredArgsConstructor
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;

    /**
     * Get all logs for a habit.
     */
    public List<HabitLog> getLogsByHabitId(Long habitId) {
        return habitLogRepository.findByHabitIdOrderByLogDateDesc(habitId);
    }

    /**
     * Get logs for a habit within a date range.
     */
    public List<HabitLog> getLogsByHabitIdAndDateRange(Long habitId, LocalDate from, LocalDate to) {
        return habitLogRepository.findByHabitIdAndLogDateBetweenOrderByLogDateDesc(habitId, from, to);
    }

    /**
     * Get a specific log by ID.
     */
    public Optional<HabitLog> getLogById(Long logId) {
        return habitLogRepository.findById(logId);
    }

    /**
     * Create a new log for a habit.
     * @param habitId The habit to log
     * @param log The log data (logDate, value, note)
     * @return The created log, or empty if habit not found
     */
    @Transactional
    public Optional<HabitLog> createLog(Long habitId, HabitLog log) {
        return habitRepository.findById(habitId)
                .map(habit -> {
                    log.setId(null);
                    log.setHabit(habit);
                    if (log.getLogDate() == null) {
                        log.setLogDate(LocalDate.now());
                    }
                    if (log.getValue() == null) {
                        log.setValue(1);
                    }
                    return habitLogRepository.save(log);
                });
    }

    /**
     * Quick log: Create a log for today with value=1.
     */
    @Transactional
    public Optional<HabitLog> quickLog(Long habitId) {
        return habitRepository.findById(habitId)
                .map(habit -> {
                    HabitLog log = HabitLog.builder()
                            .habit(habit)
                            .logDate(LocalDate.now())
                            .value(1)
                            .build();
                    return habitLogRepository.save(log);
                });
    }

    /**
     * Update a log.
     */
    @Transactional
    public Optional<HabitLog> updateLog(Long logId, HabitLog updated) {
        return habitLogRepository.findById(logId)
                .map(existing -> {
                    if (updated.getLogDate() != null) {
                        existing.setLogDate(updated.getLogDate());
                    }
                    if (updated.getValue() != null) {
                        existing.setValue(updated.getValue());
                    }
                    existing.setNote(updated.getNote());
                    return habitLogRepository.save(existing);
                });
    }

    /**
     * Delete a log.
     */
    @Transactional
    public boolean deleteLog(Long logId) {
        if (habitLogRepository.existsById(logId)) {
            habitLogRepository.deleteById(logId);
            return true;
        }
        return false;
    }

    /**
     * Check if habit was logged on a specific date.
     */
    public boolean isLoggedOnDate(Long habitId, LocalDate date) {
        return !habitLogRepository.findByHabitIdAndLogDate(habitId, date).isEmpty();
    }

    /**
     * Count completions in a date range.
     */
    public long countCompletions(Long habitId, LocalDate from, LocalDate to) {
        return habitLogRepository.countByHabitIdAndDateRange(habitId, from, to);
    }

    /**
     * Sum values in a date range.
     */
    public long sumValues(Long habitId, LocalDate from, LocalDate to) {
        return habitLogRepository.sumValueByHabitIdAndDateRange(habitId, from, to);
    }
}
