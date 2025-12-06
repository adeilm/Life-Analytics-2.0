package com.dali.lifeanalytics.tracking.service;

import com.dali.lifeanalytics.tracking.entity.Goal;
import com.dali.lifeanalytics.tracking.entity.GoalProgress;
import com.dali.lifeanalytics.tracking.repository.GoalProgressRepository;
import com.dali.lifeanalytics.tracking.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Goal Service
 * ─────────────
 * Business logic for goal management.
 */
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalProgressRepository goalProgressRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS - GOALS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    public List<Goal> getGoalsByDomain(String domain) {
        return goalRepository.findByDomain(domain);
    }

    public List<Goal> getGoalsByStatus(String status) {
        return goalRepository.findByStatus(status);
    }

    public List<Goal> getActiveGoals() {
        return goalRepository.findActiveGoalsOrderedByTargetDate();
    }

    public List<Goal> getOverdueGoals() {
        return goalRepository.findOverdueGoals(LocalDate.now());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS - GOALS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    @Transactional
    public Optional<Goal> updateGoal(Long id, Goal goalDetails) {
        return goalRepository.findById(id)
                .map(existingGoal -> {
                    existingGoal.setTitle(goalDetails.getTitle());
                    existingGoal.setDescription(goalDetails.getDescription());
                    existingGoal.setDomain(goalDetails.getDomain());
                    existingGoal.setTargetValue(goalDetails.getTargetValue());
                    existingGoal.setUnit(goalDetails.getUnit());
                    existingGoal.setTargetDate(goalDetails.getTargetDate());
                    existingGoal.setStatus(goalDetails.getStatus());
                    return goalRepository.save(existingGoal);
                });
    }

    @Transactional
    public Optional<Goal> updateGoalStatus(Long id, String status) {
        return goalRepository.findById(id)
                .map(goal -> {
                    goal.setStatus(status);
                    if ("COMPLETED".equals(status)) {
                        goal.setCompletedAt(LocalDateTime.now());
                    }
                    return goalRepository.save(goal);
                });
    }

    @Transactional
    public boolean deleteGoal(Long id) {
        if (goalRepository.existsById(id)) {
            goalRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS - PROGRESS
    // ─────────────────────────────────────────────────────────────────────────

    public List<GoalProgress> getProgressByGoalId(Long goalId) {
        return goalProgressRepository.findByGoalIdOrderByDateAsc(goalId);
    }

    public Optional<GoalProgress> getLatestProgress(Long goalId) {
        return goalProgressRepository.findLatestByGoalId(goalId);
    }

    public List<GoalProgress> getProgressBetween(Long goalId, LocalDate startDate, LocalDate endDate) {
        return goalProgressRepository.findByGoalIdAndDateBetween(goalId, startDate, endDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS - PROGRESS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public GoalProgress addProgress(Long goalId, GoalProgress progress) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));
        
        progress.setGoal(goal);
        GoalProgress savedProgress = goalProgressRepository.save(progress);
        
        // Update the goal's current value
        goal.setCurrentValue(progress.getCurrentValue());
        
        // Check if goal is completed
        if (goal.getCurrentValue().compareTo(goal.getTargetValue()) >= 0 
                && "ACTIVE".equals(goal.getStatus())) {
            goal.setStatus("COMPLETED");
            goal.setCompletedAt(LocalDateTime.now());
        }
        
        goalRepository.save(goal);
        return savedProgress;
    }

    @Transactional
    public boolean deleteProgress(Long progressId) {
        if (goalProgressRepository.existsById(progressId)) {
            goalProgressRepository.deleteById(progressId);
            return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATISTICS
    // ─────────────────────────────────────────────────────────────────────────

    public long countByStatus(String status) {
        return goalRepository.countByStatus(status);
    }
}
