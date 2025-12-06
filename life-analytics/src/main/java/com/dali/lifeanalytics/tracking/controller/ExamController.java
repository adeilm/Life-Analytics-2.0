package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.Exam;
import com.dali.lifeanalytics.tracking.service.ExamService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Exam Controller
 * ─────────────────
 * REST API for exam management.
 *
 * Base path: /api/exams
 *
 * Endpoints:
 *   GET    /api/exams                    – List all exams
 *   GET    /api/exams/{id}               – Get a specific exam
 *   GET    /api/exams/course/{courseId}  – Get exams for a course
 *   GET    /api/exams/upcoming           – Get upcoming exams
 *   POST   /api/exams/course/{courseId}  – Create an exam for a course
 *   PUT    /api/exams/{id}               – Update an existing exam
 *   DELETE /api/exams/{id}               – Delete an exam
 */
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/exams
    // GET /api/exams?startDateTime=...&endDateTime=...
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        
        List<Exam> exams;
        
        if (startDateTime != null && endDateTime != null) {
            exams = examService.getExamsBetween(startDateTime, endDateTime);
        } else {
            exams = examService.getAllExams();
        }
        
        return ResponseEntity.ok(exams);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/exams/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        return examService.getExamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/exams/course/{courseId}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Exam>> getExamsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getExamsByCourse(courseId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/exams/upcoming
    // GET /api/exams/upcoming?days=7
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/upcoming")
    public ResponseEntity<List<Exam>> getUpcomingExams(
            @RequestParam(required = false) Integer days) {
        List<Exam> exams;
        if (days != null && days > 0) {
            exams = examService.getExamsInNextDays(days);
        } else {
            exams = examService.getUpcomingExams();
        }
        return ResponseEntity.ok(exams);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/exams/course/{courseId}
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/course/{courseId}")
    public ResponseEntity<Exam> createExam(
            @PathVariable Long courseId,
            @Valid @RequestBody Exam exam) {
        try {
            Exam createdExam = examService.createExam(courseId, exam);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/exams/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        return examService.updateExam(id, exam)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/exams/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (examService.deleteExam(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
