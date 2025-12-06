package com.dali.lifeanalytics.intake;

import com.dali.lifeanalytics.calendar.ActivityLog;
import com.dali.lifeanalytics.calendar.ActivityLogService;
import com.dali.lifeanalytics.intake.dto.DailyLogDto;
import com.dali.lifeanalytics.intake.dto.IntakeResultDto;
import com.dali.lifeanalytics.tracking.entity.*;
import com.dali.lifeanalytics.tracking.repository.HabitRepository;
import com.dali.lifeanalytics.tracking.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Intake Service
 * ───────────────
 * Processes daily logs from AI and creates corresponding entities.
 * This is the core of the AI integration - it transforms natural language
 * descriptions (converted to JSON by the AI) into structured data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntakeService {

    private final HealthMetricService healthMetricService;
    private final ActivityLogService activityLogService;
    private final HabitRepository habitRepository;
    private final HabitLogService habitLogService;
    private final ExpenseService expenseService;
    private final TaskService taskService;
    private final CourseService courseService;

    /**
     * Process a daily log and create all corresponding entities.
     */
    @Transactional
    public IntakeResultDto processDailyLog(DailyLogDto dailyLog) {
        IntakeResultDto result = new IntakeResultDto();
        result.setSuccess(true);
        
        LocalDate logDate = dailyLog.getDate() != null ? dailyLog.getDate() : LocalDate.now();
        
        log.info("Processing daily log for date: {}", logDate);

        // Process health metrics
        if (dailyLog.getHealth() != null) {
            processHealth(dailyLog.getHealth(), logDate, result);
        }

        // Process activities
        if (dailyLog.getActivities() != null && !dailyLog.getActivities().isEmpty()) {
            processActivities(dailyLog.getActivities(), logDate, result);
        }

        // Process habits
        if (dailyLog.getHabits() != null && !dailyLog.getHabits().isEmpty()) {
            processHabits(dailyLog.getHabits(), logDate, result);
        }

        // Process expenses
        if (dailyLog.getExpenses() != null && !dailyLog.getExpenses().isEmpty()) {
            processExpenses(dailyLog.getExpenses(), logDate, result);
        }

        // Process tasks
        if (dailyLog.getTasks() != null && !dailyLog.getTasks().isEmpty()) {
            processTasks(dailyLog.getTasks(), result);
        }

        // Process study sessions
        if (dailyLog.getStudySessions() != null && !dailyLog.getStudySessions().isEmpty()) {
            processStudySessions(dailyLog.getStudySessions(), logDate, result);
        }

        result.setMessage(buildSummaryMessage(result));
        log.info("Daily log processing completed: {}", result.getMessage());
        
        return result;
    }

    private void processHealth(DailyLogDto.HealthEntry health, LocalDate date, IntakeResultDto result) {
        try {
            HealthMetric metric = HealthMetric.builder()
                    .recordedAt(date.atStartOfDay())
                    .sleepHours(health.getSleepHours())
                    .moodScore(normalizeScore(health.getMoodScore()))
                    .stressLevel(normalizeScore(health.getStressLevel()))
                    .energyLevel(normalizeScore(health.getEnergyLevel()))
                    .note(health.getNotes())
                    .build();
            
            healthMetricService.createMetric(metric);
            result.setHealthMetricsCreated(1);
            result.addCreatedEntity("HealthMetric for " + date);
            log.debug("Created health metric for date: {}", date);
        } catch (Exception e) {
            log.error("Error creating health metric: {}", e.getMessage());
            result.addError("Failed to create health metric: " + e.getMessage());
        }
    }
    
    /**
     * Normalize score from 1-10 to 1-5 scale if needed
     */
    private Integer normalizeScore(Integer score) {
        if (score == null) return null;
        if (score > 5) {
            return (int) Math.round(score / 2.0);
        }
        return score;
    }

    private void processActivities(java.util.List<DailyLogDto.ActivityEntry> activities, 
                                    LocalDate date, IntakeResultDto result) {
        int count = 0;
        for (DailyLogDto.ActivityEntry activity : activities) {
            try {
                LocalDateTime startTime = activity.getStartTime() != null 
                        ? activity.getStartTime() 
                        : date.atTime(9, 0);
                LocalDateTime endTime = activity.getEndTime() != null 
                        ? activity.getEndTime() 
                        : startTime.plusMinutes(activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 60);
                
                Integer duration = activity.getDurationMinutes();
                if (duration == null && activity.getStartTime() != null && activity.getEndTime() != null) {
                    duration = (int) java.time.Duration.between(activity.getStartTime(), activity.getEndTime()).toMinutes();
                }
                if (duration == null) {
                    duration = 60; // default 1 hour
                }

                ActivityLog activityLog = ActivityLog.builder()
                        .activity(activity.getDescription() != null ? activity.getDescription() : activity.getType())
                        .category(activity.getType())
                        .logDate(date)
                        .durationMinutes(duration)
                        .startTime(startTime)
                        .endTime(endTime)
                        .note(activity.getTags() != null ? String.join(",", activity.getTags()) : null)
                        .build();
                
                activityLogService.create(activityLog);
                count++;
                result.addCreatedEntity("Activity: " + activity.getType());
            } catch (Exception e) {
                log.error("Error creating activity: {}", e.getMessage());
                result.addError("Failed to create activity '" + activity.getType() + "': " + e.getMessage());
            }
        }
        result.setActivitiesCreated(count);
    }

    private void processHabits(java.util.List<DailyLogDto.HabitEntry> habits, 
                               LocalDate date, IntakeResultDto result) {
        int count = 0;
        for (DailyLogDto.HabitEntry habitEntry : habits) {
            try {
                // Find habit by name
                Optional<Habit> habitOpt = habitRepository.findByNameIgnoreCase(habitEntry.getHabitName());
                
                if (habitOpt.isEmpty()) {
                    result.addWarning("Habit not found: '" + habitEntry.getHabitName() + "'. Skipping.");
                    continue;
                }
                
                Habit habit = habitOpt.get();
                HabitLog habitLog = HabitLog.builder()
                        .habit(habit)
                        .logDate(date)
                        .value(habitEntry.getValue() != null ? habitEntry.getValue().intValue() : 1)
                        .note(habitEntry.getNote())
                        .build();
                
                habitLogService.createLog(habit.getId(), habitLog);
                count++;
                result.addCreatedEntity("HabitLog: " + habitEntry.getHabitName());
            } catch (Exception e) {
                log.error("Error logging habit: {}", e.getMessage());
                result.addError("Failed to log habit '" + habitEntry.getHabitName() + "': " + e.getMessage());
            }
        }
        result.setHabitsLogged(count);
    }

    private void processExpenses(java.util.List<DailyLogDto.ExpenseEntry> expenses, 
                                  LocalDate date, IntakeResultDto result) {
        int count = 0;
        for (DailyLogDto.ExpenseEntry expenseEntry : expenses) {
            try {
                Expense expense = Expense.builder()
                        .amount(expenseEntry.getAmount())
                        .category(expenseEntry.getCategory())
                        .description(expenseEntry.getDescription())
                        .note(expenseEntry.getNote())
                        .date(date)
                        .build();
                
                expenseService.createExpense(expense);
                count++;
                result.addCreatedEntity("Expense: " + expenseEntry.getCategory() + " - " + expenseEntry.getAmount());
            } catch (Exception e) {
                log.error("Error creating expense: {}", e.getMessage());
                result.addError("Failed to create expense: " + e.getMessage());
            }
        }
        result.setExpensesCreated(count);
    }

    private void processTasks(java.util.List<DailyLogDto.TaskEntry> tasks, IntakeResultDto result) {
        int count = 0;
        for (DailyLogDto.TaskEntry taskEntry : tasks) {
            try {
                Task task = Task.builder()
                        .title(taskEntry.getTitle())
                        .description(taskEntry.getDescription())
                        .priority(taskEntry.getPriority() != null ? taskEntry.getPriority() : "MEDIUM")
                        .dueDate(taskEntry.getDueDate())
                        .category(taskEntry.getCategory())
                        .status(taskEntry.getStatus() != null ? taskEntry.getStatus() : "PENDING")
                        .build();
                
                taskService.createTask(task);
                count++;
                result.addCreatedEntity("Task: " + taskEntry.getTitle());
            } catch (Exception e) {
                log.error("Error creating task: {}", e.getMessage());
                result.addError("Failed to create task '" + taskEntry.getTitle() + "': " + e.getMessage());
            }
        }
        result.setTasksCreated(count);
    }

    private void processStudySessions(java.util.List<DailyLogDto.StudySessionEntry> sessions, 
                                       LocalDate date, IntakeResultDto result) {
        int count = 0;
        for (DailyLogDto.StudySessionEntry session : sessions) {
            try {
                LocalDateTime startTime = session.getStartTime() != null 
                        ? session.getStartTime() 
                        : date.atTime(10, 0);
                LocalDateTime endTime = session.getEndTime() != null 
                        ? session.getEndTime() 
                        : startTime.plusMinutes(session.getDurationMinutes() != null ? session.getDurationMinutes() : 60);
                
                Integer duration = session.getDurationMinutes();
                if (duration == null && session.getStartTime() != null && session.getEndTime() != null) {
                    duration = (int) java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
                }
                if (duration == null) {
                    duration = 60;
                }

                // Build activity name with course code and topic
                String activityName = "Study";
                if (session.getCourseCode() != null) {
                    activityName = session.getCourseCode();
                }
                if (session.getTopic() != null) {
                    activityName += ": " + session.getTopic();
                }

                ActivityLog activityLog = ActivityLog.builder()
                        .activity(activityName)
                        .category("LEARNING")
                        .logDate(date)
                        .durationMinutes(duration)
                        .startTime(startTime)
                        .endTime(endTime)
                        .note(session.getNotes())
                        .build();
                
                activityLogService.create(activityLog);
                count++;
                result.addCreatedEntity("Study Session: " + activityName);
            } catch (Exception e) {
                log.error("Error creating study session: {}", e.getMessage());
                result.addError("Failed to create study session: " + e.getMessage());
            }
        }
        result.setStudySessionsCreated(count);
    }

    private String buildSummaryMessage(IntakeResultDto result) {
        StringBuilder sb = new StringBuilder("Daily log processed: ");
        sb.append(result.getHealthMetricsCreated()).append(" health metrics, ");
        sb.append(result.getActivitiesCreated()).append(" activities, ");
        sb.append(result.getHabitsLogged()).append(" habits, ");
        sb.append(result.getExpensesCreated()).append(" expenses, ");
        sb.append(result.getTasksCreated()).append(" tasks, ");
        sb.append(result.getStudySessionsCreated()).append(" study sessions.");
        
        if (!result.getWarnings().isEmpty()) {
            sb.append(" Warnings: ").append(result.getWarnings().size());
        }
        if (!result.getErrors().isEmpty()) {
            sb.append(" Errors: ").append(result.getErrors().size());
        }
        
        return sb.toString();
    }
}
