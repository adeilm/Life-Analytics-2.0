package com.dali.lifeanalytics.analytics;

import com.dali.lifeanalytics.analytics.dto.*;
import com.dali.lifeanalytics.tracking.entity.Habit;
import com.dali.lifeanalytics.tracking.entity.HealthMetric;
import com.dali.lifeanalytics.tracking.repository.HabitLogRepository;
import com.dali.lifeanalytics.tracking.repository.HabitRepository;
import com.dali.lifeanalytics.tracking.repository.HealthMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analytics Service - Wellness Tracker
 * ─────────────────────────────────────
 * Correlates sleep, mood, and habits.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final HealthMetricRepository healthMetricRepository;

    /**
     * Weekly habit completion report.
     */
    public WeeklyHabitReportDto getWeeklyHabitReport() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Habit> habits = habitRepository.findAll();
        List<HabitStatsDto> habitStats = new ArrayList<>();

        double totalCompletion = 0;
        int habitsWithTarget = 0;

        for (Habit habit : habits) {
            long completed = habitLogRepository.countByHabitIdAndDateRange(
                    habit.getId(), weekStart, weekEnd);

            int target = habit.getTargetPerWeek() != null ? habit.getTargetPerWeek() : 7;
            double rate = target > 0 ? Math.min(1.0, (double) completed / target) : 1.0;

            int streak = calculateStreak(habit.getId());

            HabitStatsDto stats = HabitStatsDto.builder()
                    .habitId(habit.getId())
                    .habitName(habit.getName())
                    .category(habit.getCategory())
                    .targetPerWeek(target)
                    .completedThisWeek((int) completed)
                    .completionRate(round2(rate))
                    .currentStreak(streak)
                    .build();

            habitStats.add(stats);

            if (habit.getTargetPerWeek() != null && habit.getTargetPerWeek() > 0) {
                totalCompletion += rate;
                habitsWithTarget++;
            }
        }

        double overallRate = habitsWithTarget > 0 ? totalCompletion / habitsWithTarget : 0;

        return WeeklyHabitReportDto.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .totalHabits(habits.size())
                .overallCompletionRate(round2(overallRate))
                .habits(habitStats)
                .build();
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
    public HealthTrendDto getHealthTrend(int days) {
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

        List<HealthTrendDto.DailyHealthDto> dailyData = metrics.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getRecordedAt().toLocalDate(),
                        Collectors.reducing((a, b) ->
                                a.getRecordedAt().isAfter(b.getRecordedAt()) ? a : b)))
                .values().stream()
                .filter(opt -> opt.isPresent())
                .map(opt -> {
                    HealthMetric m = opt.get();
                    return HealthTrendDto.DailyHealthDto.builder()
                            .date(m.getRecordedAt().toLocalDate())
                            .sleepHours(m.getSleepHours())
                            .moodScore(m.getMoodScore())
                            .stressLevel(m.getStressLevel())
                            .energyLevel(m.getEnergyLevel())
                            .build();
                })
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .collect(Collectors.toList());

        return HealthTrendDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRecords(metrics.size())
                .avgSleepHours(round2(avgSleep))
                .avgMoodScore(round2(avgMood))
                .avgStressLevel(round2(avgStress))
                .avgEnergyLevel(round2(avgEnergy))
                .dailyData(dailyData)
                .build();
    }

    /**
     * Dashboard: today's snapshot + trends.
     */
    public DashboardDto getDashboard() {
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

        DashboardDto.TodaySnapshot snapshot = DashboardDto.TodaySnapshot.builder()
                .habitsCompletedToday(habitsCompletedToday)
                .habitsTotal(allHabits.size())
                .sleepLastNight(latestHealth != null ? latestHealth.getSleepHours() : null)
                .currentMood(latestHealth != null ? latestHealth.getMoodScore() : null)
                .build();

        return DashboardDto.builder()
                .asOf(today)
                .today(snapshot)
                .habitReport(getWeeklyHabitReport())
                .healthTrend(getHealthTrend(7))
                .build();
    }

    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
