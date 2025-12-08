package com.dali.wellness.analytics;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dali.wellness.tracking.entity.Habit;
import com.dali.wellness.tracking.entity.HealthMetric;
import com.dali.wellness.tracking.repository.HabitLogRepository;
import com.dali.wellness.tracking.repository.HabitRepository;
import com.dali.wellness.tracking.repository.HealthMetricRepository;

/**
 * Analytics Service - Wellness Tracker
 * ─────────────────────────────────────
 * Correlates sleep, mood, and habits.
 */
@Service
public class AnalyticsService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final HealthMetricRepository healthMetricRepository;

    public AnalyticsService(HabitRepository habitRepository, HabitLogRepository habitLogRepository, HealthMetricRepository healthMetricRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.healthMetricRepository = healthMetricRepository;
    }

    /**
     * Weekly habit completion report.
     */
    public Map<String, Object> getWeeklyHabitReport() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Habit> habits = habitRepository.findAll();
        List<Map<String, Object>> habitStats = new ArrayList<>();

        double totalCompletion = 0;
        int habitsWithTarget = 0;

        for (Habit habit : habits) {
            long completed = habitLogRepository.countByHabitIdAndDateRange(
                    habit.getId(), weekStart, weekEnd);

            Integer targetVal = habit.getTargetPerWeek();
            int target = targetVal != null ? targetVal : 7;
            double rate = target > 0 ? Math.min(1.0, (double) completed / target) : 1.0;

            int streak = calculateStreak(habit.getId());

            Map<String, Object> stats = new HashMap<>();
            stats.put("habitId", habit.getId());
            stats.put("habitName", habit.getName());
            stats.put("category", habit.getCategory());
            stats.put("targetPerWeek", target);
            stats.put("completedThisWeek", (int) completed);
            stats.put("completionRate", round2(rate));
            stats.put("currentStreak", streak);

            habitStats.add(stats);

            if (habit.getTargetPerWeek() != null && habit.getTargetPerWeek() > 0) {
                totalCompletion += rate;
                habitsWithTarget++;
            }
        }

        double overallRate = habitsWithTarget > 0 ? totalCompletion / habitsWithTarget : 0;

        Map<String, Object> report = new HashMap<>();
        report.put("weekStart", weekStart);
        report.put("weekEnd", weekEnd);
        report.put("totalHabits", habits.size());
        report.put("overallCompletionRate", round2(overallRate));
        report.put("habits", habitStats);

        return report;
    }

    /**
     * Calculate streak for a habit.
     */
    private int calculateStreak(Long habitId) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today;

        for (int i = 0; i < 365; i++) {
            List<?> logs = habitLogRepository.findByHabitIdAndLogDate(habitId, checkDate);
            if (!logs.isEmpty()) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    /**
     * Health trends for the last N days.
     */
    public Map<String, Object> getHealthTrend(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<HealthMetric> metrics = healthMetricRepository
                .findByRecordedAtBetweenOrderByRecordedAtDesc(startDateTime, endDateTime);

        Double avgSleep = metrics.stream()
                .filter(m -> m.getSleepHours() != null)
                .mapToDouble(HealthMetric::getSleepHours)
                .average().orElse(0.0);

        Double avgMood = metrics.stream()
                .filter(m -> m.getMoodScore() != null)
                .mapToInt(HealthMetric::getMoodScore)
                .average().orElse(0.0);

        Double avgStress = metrics.stream()
                .filter(m -> m.getStressLevel() != null)
                .mapToInt(HealthMetric::getStressLevel)
                .average().orElse(0.0);

        Double avgEnergy = metrics.stream()
                .filter(m -> m.getEnergyLevel() != null)
                .mapToInt(HealthMetric::getEnergyLevel)
                .average().orElse(0.0);

        List<Map<String, Object>> dailyData = metrics.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getRecordedAt().toLocalDate(),
                        Collectors.reducing((a, b) ->
                                a.getRecordedAt().isAfter(b.getRecordedAt()) ? a : b)))
                .values().stream()
                .filter(opt -> opt.isPresent())
                .map(opt -> {
                    HealthMetric m = opt.get();
                    Map<String, Object> day = new HashMap<>();
                    day.put("date", m.getRecordedAt().toLocalDate());
                    day.put("sleepHours", m.getSleepHours());
                    day.put("moodScore", m.getMoodScore());
                    day.put("stressLevel", m.getStressLevel());
                    day.put("energyLevel", m.getEnergyLevel());
                    return day;
                })
                .sorted((a, b) -> ((LocalDate) b.get("date")).compareTo((LocalDate) a.get("date")))
                .collect(Collectors.toList());

        Map<String, Object> trend = new HashMap<>();
        trend.put("startDate", startDate);
        trend.put("endDate", endDate);
        trend.put("totalRecords", metrics.size());
        trend.put("avgSleepHours", round2(avgSleep));
        trend.put("avgMoodScore", round2(avgMood));
        trend.put("avgStressLevel", round2(avgStress));
        trend.put("avgEnergyLevel", round2(avgEnergy));
        trend.put("dailyData", dailyData);

        return trend;
    }

    /**
     * Dashboard: today's snapshot + trends.
     */
    public Map<String, Object> getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        // Today's habits completed
        List<Habit> allHabits = habitRepository.findAll();
        int habitsCompletedToday = 0;
        for (Habit h : allHabits) {
            if (!habitLogRepository.findByHabitIdAndLogDate(h.getId(), today).isEmpty()) {
                habitsCompletedToday++;
            }
        }

        // Latest health metric
        List<HealthMetric> todayMetrics = healthMetricRepository
                .findByRecordedAtBetweenOrderByRecordedAtDesc(todayStart, todayEnd);
        HealthMetric latestHealth = todayMetrics.isEmpty() ? null : todayMetrics.get(0);

        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("habitsCompletedToday", habitsCompletedToday);
        snapshot.put("habitsTotal", allHabits.size());
        snapshot.put("sleepLastNight", latestHealth != null ? latestHealth.getSleepHours() : null);
        snapshot.put("currentMood", latestHealth != null ? latestHealth.getMoodScore() : null);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("asOf", today);
        dashboard.put("today", snapshot);
        dashboard.put("habitReport", getWeeklyHabitReport());
        dashboard.put("healthTrend", getHealthTrend(7));

        return dashboard;
    }

    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
