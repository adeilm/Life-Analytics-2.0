package com.dali.wellness.tracking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dali.wellness.tracking.entity.Habit;
import com.dali.wellness.tracking.repository.HabitRepository;

/**
 * Habit Service
 * ─────────────
 * Business logic for habit management.
 *
 * Annotations explained:
 *   @Service           – Marks this as a Spring service bean (auto-registered).
 *   @RequiredArgsConstructor – Lombok: generates constructor for final fields
 *                              (used for dependency injection).
 *   @Transactional     – Wraps methods in a DB transaction (rollback on exception).
 */
@Service
public class HabitService {

    // Injected by Spring via constructor
    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get all habits.
     */
    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    /**
     * Get a habit by ID.
     * Returns Optional.empty() if not found.
     */
    public Optional<Habit> getHabitById(Long id) {
        return habitRepository.findById(id);
    }

    /**
     * Get habits filtered by category.
     */
    public List<Habit> getHabitsByCategory(String category) {
        return habitRepository.findByCategory(category);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Create a new habit.
     * @param habit The habit to create (id should be null).
     * @return The saved habit with generated ID.
     */
    @Transactional
    public Habit createHabit(Habit habit) {
        // Ensure we're creating, not updating
        habit.setId(null);
        return habitRepository.save(habit);
    }

    /**
     * Update an existing habit.
     * @param id The ID of the habit to update.
     * @param updated The new data.
     * @return The updated habit, or empty if not found.
     */
    @Transactional
    public Optional<Habit> updateHabit(Long id, Habit updated) {
        return habitRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setCategory(updated.getCategory());
                    existing.setTargetPerWeek(updated.getTargetPerWeek());
                    return habitRepository.save(existing);
                });
    }

    /**
     * Delete a habit by ID.
     * @return true if deleted, false if not found.
     */
    @Transactional
    public boolean deleteHabit(Long id) {
        if (habitRepository.existsById(id)) {
            habitRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
