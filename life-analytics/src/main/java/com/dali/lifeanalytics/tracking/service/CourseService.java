package com.dali.lifeanalytics.tracking.service;

import com.dali.lifeanalytics.tracking.entity.Course;
import com.dali.lifeanalytics.tracking.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Course Service
 * ───────────────
 * Business logic for course management.
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> getCourseByCode(String code) {
        return courseRepository.findByCode(code);
    }

    public List<Course> getCoursesBySemester(String semester) {
        return courseRepository.findBySemester(semester);
    }

    public List<Course> getCoursesByInstructor(String instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    public List<Course> searchCoursesByName(String name) {
        return courseRepository.findByNameContainingIgnoreCase(name);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Course createCourse(Course course) {
        // Check if course code already exists
        if (course.getCode() != null && courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException("Course code already exists: " + course.getCode());
        }
        return courseRepository.save(course);
    }

    @Transactional
    public Optional<Course> updateCourse(Long id, Course courseDetails) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setName(courseDetails.getName());
                    existingCourse.setCode(courseDetails.getCode());
                    existingCourse.setDescription(courseDetails.getDescription());
                    existingCourse.setCredits(courseDetails.getCredits());
                    existingCourse.setInstructor(courseDetails.getInstructor());
                    existingCourse.setSemester(courseDetails.getSemester());
                    return courseRepository.save(existingCourse);
                });
    }

    @Transactional
    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
