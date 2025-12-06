package com.dali.lifeanalytics.tracking.service;

import com.dali.lifeanalytics.tracking.entity.Expense;
import com.dali.lifeanalytics.tracking.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Expense Service
 * ─────────────────
 * Business logic for expense management.
 */
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategory(category);
    }

    public List<Expense> getExpensesByDate(LocalDate date) {
        return expenseRepository.findByDate(date);
    }

    public List<Expense> getExpensesBetween(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate);
    }

    public List<Expense> getRecentExpenses(int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return expenseRepository.findRecentExpenses(fromDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Transactional
    public Optional<Expense> updateExpense(Long id, Expense expenseDetails) {
        return expenseRepository.findById(id)
                .map(existingExpense -> {
                    existingExpense.setAmount(expenseDetails.getAmount());
                    existingExpense.setCategory(expenseDetails.getCategory());
                    existingExpense.setDate(expenseDetails.getDate());
                    existingExpense.setNote(expenseDetails.getNote());
                    existingExpense.setDescription(expenseDetails.getDescription());
                    return expenseRepository.save(existingExpense);
                });
    }

    @Transactional
    public boolean deleteExpense(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ANALYTICS
    // ─────────────────────────────────────────────────────────────────────────

    public BigDecimal getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.sumByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalByCategory(String category, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.sumByCategory(category, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Map<String, BigDecimal> getExpensesByCategory(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = expenseRepository.getTotalsByCategory(startDate, endDate);
        Map<String, BigDecimal> totals = new HashMap<>();
        for (Object[] row : results) {
            String category = (String) row[0];
            BigDecimal total = (BigDecimal) row[1];
            totals.put(category, total);
        }
        return totals;
    }
}
