package com.dali.lifeanalytics.tracking.service;

import com.dali.lifeanalytics.tracking.entity.Course;
import com.dali.lifeanalytics.tracking.entity.Exam;
import com.dali.lifeanalytics.tracking.repository.CourseRepository;
import com.dali.lifeanalytics.tracking.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Exam Service
 * ─────────────
 * Business logic for exam management.
 */
@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public List<Exam> getExamsByCourse(Long courseId) {
        return examRepository.findByCourseIdOrderByDateTime(courseId);
    }

    public List<Exam> getUpcomingExams() {
        return examRepository.findUpcomingExams(LocalDateTime.now());
    }

    public List<Exam> getExamsInNextDays(int days) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(days);
        return examRepository.findExamsInNextDays(start, end);
    }

    public List<Exam> getExamsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return examRepository.findByDateTimeBetween(startDateTime, endDateTime);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Exam createExam(Long courseId, Exam exam) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        exam.setCourse(course);
        return examRepository.save(exam);
    }

    @Transactional
    public Optional<Exam> updateExam(Long id, Exam examDetails) {
        return examRepository.findById(id)
                .map(existingExam -> {
                    existingExam.setTitle(examDetails.getTitle());
                    existingExam.setDateTime(examDetails.getDateTime());
                    existingExam.setLocation(examDetails.getLocation());
                    existingExam.setWeight(examDetails.getWeight());
                    existingExam.setDescription(examDetails.getDescription());
                    existingExam.setDurationMinutes(examDetails.getDurationMinutes());
                    return examRepository.save(existingExam);
                });
    }

    @Transactional
    public boolean deleteExam(Long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
