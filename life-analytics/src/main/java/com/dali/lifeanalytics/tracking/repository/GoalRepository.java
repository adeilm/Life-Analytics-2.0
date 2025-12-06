package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Goal Repository
 * ─────────────────
 * Spring Data JPA repository for {@link Goal} entities.
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Find goals by domain.
     */
    List<Goal> findByDomain(String domain);

    /**
     * Find goals by status.
     */
    List<Goal> findByStatus(String status);

    /**
     * Find active goals.
     */
    List<Goal> findByStatusNot(String status);

    /**
     * Find goals by target date before a specific date.
     */
    List<Goal> findByTargetDateBefore(LocalDate date);

    /**
     * Find goals by target date between two dates.
     */
    List<Goal> findByTargetDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find active goals ordered by target date.
     */
    @Query("SELECT g FROM Goal g WHERE g.status = 'ACTIVE' ORDER BY g.targetDate ASC")
    List<Goal> findActiveGoalsOrderedByTargetDate();

    /**
     * Find goals that are overdue (target date passed but not completed).
     */
    @Query("SELECT g FROM Goal g WHERE g.status = 'ACTIVE' AND g.targetDate < :today")
    List<Goal> findOverdueGoals(@Param("today") LocalDate today);

    /**
     * Count goals by status.
     */
    long countByStatus(String status);
}
