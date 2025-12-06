package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Exam Repository
 * ─────────────────
 * Spring Data JPA repository for {@link Exam} entities.
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    /**
     * Find all exams for a course.
     */
    List<Exam> findByCourseId(Long courseId);

    /**
     * Find exams between two dates.
     */
    List<Exam> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Find upcoming exams (from now).
     */
    @Query("SELECT e FROM Exam e WHERE e.dateTime >= :now ORDER BY e.dateTime ASC")
    List<Exam> findUpcomingExams(@Param("now") LocalDateTime now);

    /**
     * Find exams for a specific course ordered by date.
     */
    @Query("SELECT e FROM Exam e WHERE e.course.id = :courseId ORDER BY e.dateTime ASC")
    List<Exam> findByCourseIdOrderByDateTime(@Param("courseId") Long courseId);

    /**
     * Find exams in the next N days.
     */
    @Query("SELECT e FROM Exam e WHERE e.dateTime BETWEEN :startDateTime AND :endDateTime ORDER BY e.dateTime ASC")
    List<Exam> findExamsInNextDays(@Param("startDateTime") LocalDateTime startDateTime, 
                                    @Param("endDateTime") LocalDateTime endDateTime);
}
