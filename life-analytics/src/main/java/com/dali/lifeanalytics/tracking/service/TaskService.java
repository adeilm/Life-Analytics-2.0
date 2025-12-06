package com.dali.lifeanalytics.tracking.service;

import com.dali.lifeanalytics.tracking.entity.Task;
import com.dali.lifeanalytics.tracking.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Task Service
 * ─────────────
 * Business logic for task management.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByCategory(String category) {
        return taskRepository.findByCategory(category);
    }

    public List<Task> getTasksByPriority(String priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<Task> getActiveTasks() {
        return taskRepository.findActiveTasks();
    }

    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now());
    }

    public List<Task> getTasksDueOn(LocalDate date) {
        return taskRepository.findByDueDate(date);
    }

    public List<Task> getTasksDueBetween(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByDueDateBetween(startDate, endDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Optional<Task> updateTask(Long id, Task taskDetails) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    existingTask.setStatus(taskDetails.getStatus());
                    existingTask.setPriority(taskDetails.getPriority());
                    existingTask.setDueDate(taskDetails.getDueDate());
                    existingTask.setCategory(taskDetails.getCategory());
                    return taskRepository.save(existingTask);
                });
    }

    @Transactional
    public Optional<Task> updateTaskStatus(Long id, String status) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(status);
                    if ("COMPLETED".equals(status)) {
                        task.setCompletedAt(LocalDateTime.now());
                    }
                    return taskRepository.save(task);
                });
    }

    @Transactional
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATISTICS
    // ─────────────────────────────────────────────────────────────────────────

    public long countByStatus(String status) {
        return taskRepository.countByStatus(status);
    }
}
