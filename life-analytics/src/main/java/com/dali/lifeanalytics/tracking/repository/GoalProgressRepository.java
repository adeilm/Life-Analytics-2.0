package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.GoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * GoalProgress Repository
 * ────────────────────────
 * Spring Data JPA repository for {@link GoalProgress} entities.
 */
@Repository
public interface GoalProgressRepository extends JpaRepository<GoalProgress, Long> {

    /**
     * Find all progress entries for a goal.
     */
    List<GoalProgress> findByGoalId(Long goalId);

    /**
     * Find progress entries for a goal ordered by date.
     */
    List<GoalProgress> findByGoalIdOrderByDateAsc(Long goalId);

    /**
     * Find progress entries for a goal between two dates.
     */
    List<GoalProgress> findByGoalIdAndDateBetween(Long goalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find the latest progress entry for a goal.
     */
    @Query("SELECT gp FROM GoalProgress gp WHERE gp.goal.id = :goalId ORDER BY gp.date DESC, gp.createdAt DESC LIMIT 1")
    Optional<GoalProgress> findLatestByGoalId(@Param("goalId") Long goalId);

    /**
     * Find progress entries on a specific date for a goal.
     */
    List<GoalProgress> findByGoalIdAndDate(Long goalId, LocalDate date);
}
