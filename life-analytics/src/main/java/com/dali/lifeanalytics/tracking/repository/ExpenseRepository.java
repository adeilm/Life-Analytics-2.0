package com.dali.lifeanalytics.tracking.repository;

import com.dali.lifeanalytics.tracking.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Expense Repository
 * ───────────────────
 * Spring Data JPA repository for {@link Expense} entities.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find expenses by category.
     */
    List<Expense> findByCategory(String category);

    /**
     * Find expenses on a specific date.
     */
    List<Expense> findByDate(LocalDate date);

    /**
     * Find expenses between two dates.
     */
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Sum expenses by category within a date range.
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category = :category AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByCategory(@Param("category") String category, 
                              @Param("startDate") LocalDate startDate, 
                              @Param("endDate") LocalDate endDate);

    /**
     * Sum all expenses within a date range.
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Get expense totals grouped by category for a date range.
     */
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate GROUP BY e.category")
    List<Object[]> getTotalsByCategory(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find recent expenses (last N days).
     */
    @Query("SELECT e FROM Expense e WHERE e.date >= :fromDate ORDER BY e.date DESC")
    List<Expense> findRecentExpenses(@Param("fromDate") LocalDate fromDate);
}
