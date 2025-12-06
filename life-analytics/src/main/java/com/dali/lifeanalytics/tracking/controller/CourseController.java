package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.Course;
import com.dali.lifeanalytics.tracking.service.CourseService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Course Controller
 * ──────────────────
 * REST API for course management.
 *
 * Base path: /api/courses
 *
 * Endpoints:
 *   GET    /api/courses                – List all courses (optional filters)
 *   GET    /api/courses/{id}           – Get a specific course
 *   GET    /api/courses/code/{code}    – Get a course by code
 *   GET    /api/courses/search         – Search courses by name
 *   POST   /api/courses                – Create a new course
 *   PUT    /api/courses/{id}           – Update an existing course
 *   DELETE /api/courses/{id}           – Delete a course
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/courses
    // GET /api/courses?semester=Fall%202025
    // GET /api/courses?instructor=Dr.%20Smith
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String instructor) {
        
        List<Course> courses;
        
        if (semester != null && !semester.isBlank()) {
            courses = courseService.getCoursesBySemester(semester);
        } else if (instructor != null && !instructor.isBlank()) {
            courses = courseService.getCoursesByInstructor(instructor);
        } else {
            courses = courseService.getAllCourses();
        }
        
        return ResponseEntity.ok(courses);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/courses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/courses/code/{code}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/code/{code}")
    public ResponseEntity<Course> getCourseByCode(@PathVariable String code) {
        return courseService.getCourseByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/courses/search?name=math
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String name) {
        return ResponseEntity.ok(courseService.searchCoursesByName(name));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/courses
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/courses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course course) {
        return courseService.updateCourse(id, course)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/courses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.deleteCourse(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
