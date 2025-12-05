package com.dali.lifeanalytics.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public ActivityLog create(ActivityLog log) {
        return repository.save(log);
    }

    /**
     * Quick log: just activity, category, and minutes (uses today)
     */
    public ActivityLog quickLog(String activity, String category, int minutes) {
        ActivityLog log = ActivityLog.builder()
            .activity(activity)
            .category(category)
            .durationMinutes(minutes)
            .logDate(LocalDate.now())
            .build();
        return repository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ActivityLog findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Activity log not found: " + id));
    }

    public ActivityLog update(Long id, ActivityLog updated) {
        ActivityLog existing = findById(id);
        existing.setActivity(updated.getActivity());
        existing.setCategory(updated.getCategory());
        existing.setLogDate(updated.getLogDate());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setQualityRating(updated.getQualityRating());
        existing.setNote(updated.getNote());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Get today's activity logs
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> findToday() {
        return repository.findToday();
    }

    /**
     * Get activities for a specific date
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> findByDate(LocalDate date) {
        return repository.findByLogDateOrderByCreatedAtDesc(date);
    }

    /**
     * Get activities within a date range
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> findByDateRange(LocalDate start, LocalDate end) {
        return repository.findByDateRange(start, end);
    }

    /**
     * Get this week's breakdown by category (hours)
     */
    @Transactional(readOnly = true)
    public Map<String, Double> getWeeklyBreakdown() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);
        
        List<Object[]> results = repository.weeklyBreakdownByCategory(weekStart, weekEnd);
        Map<String, Double> breakdown = new HashMap<>();
        
        for (Object[] row : results) {
            String category = (String) row[0];
            Long minutes = (Long) row[1];
            breakdown.put(category != null ? category : "UNCATEGORIZED", minutes / 60.0);
        }
        
        return breakdown;
    }

    /**
     * Get total hours for today
     */
    @Transactional(readOnly = true)
    public double getTodayTotalHours() {
        List<ActivityLog> today = findToday();
        int totalMinutes = today.stream()
            .mapToInt(ActivityLog::getDurationMinutes)
            .sum();
        return totalMinutes / 60.0;
    }

    /**
     * Get time spent on a specific category this week
     */
    @Transactional(readOnly = true)
    public double getWeeklyCategoryHours(String category) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);
        
        Integer minutes = repository.sumMinutesByCategoryAndDateRange(category, weekStart, weekEnd);
        return minutes / 60.0;
    }
}
