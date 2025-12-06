package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Course Repository
 * ──────────────────
 * Spring Data JPA repository for {@link Course} entities.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Find a course by its code.
     */
    Optional<Course> findByCode(String code);

    /**
     * Find courses by semester.
     */
    List<Course> findBySemester(String semester);

    /**
     * Find courses by instructor.
     */
    List<Course> findByInstructor(String instructor);

    /**
     * Check if a course code already exists.
     */
    boolean existsByCode(String code);

    /**
     * Search courses by name (case-insensitive).
     */
    List<Course> findByNameContainingIgnoreCase(String name);
}
