package com.dali.wellness.tracking.repository;

import com.dali.wellness.tracking.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Habit Repository
 * ─────────────────
 * Spring Data JPA repository for {@link Habit} entities.
 *
 * By extending JpaRepository, we get these methods for FREE (no implementation needed):
 *   • findAll()              – List<Habit>
 *   • findById(Long id)      – Optional<Habit>
 *   • save(Habit habit)      – Habit (insert or update)
 *   • deleteById(Long id)    – void
 *   • count()                – long
 *   • existsById(Long id)    – boolean
 *
 * Custom query methods:
 *   Spring Data parses the method name and generates the query automatically.
 *   Example: findByCategory("HEALTH") → SELECT * FROM habit WHERE category = 'HEALTH'
 */
@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    /**
     * Find all habits in a given category.
     * Query derived from method name: WHERE category = ?
     */
    List<Habit> findByCategory(String category);

    /**
     * Find habits whose name contains the given string (case-insensitive).
     * Query: WHERE LOWER(name) LIKE LOWER('%keyword%')
     */
    List<Habit> findByNameContainingIgnoreCase(String keyword);

    /**
     * Find a habit by exact name (case-insensitive).
     * Used by intake service to match AI-generated habit names.
     */
    Optional<Habit> findByNameIgnoreCase(String name);
}
