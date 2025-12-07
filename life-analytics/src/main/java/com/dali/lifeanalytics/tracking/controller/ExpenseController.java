package com.dali.lifeanalytics.tracking.controller;

import com.dali.lifeanalytics.tracking.entity.Expense;
import com.dali.lifeanalytics.tracking.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Expense Controller
 * ───────────────────
 * REST API for expense management.
 *
 * Base path: /api/expenses
 *
 * Endpoints:
 *   GET    /api/expenses                      – List all expenses (optional filters)
 *   GET    /api/expenses/{id}                 – Get a specific expense
 *   GET    /api/expenses/recent               – Get recent expenses (last N days)
 *   GET    /api/expenses/summary              – Get expense summary by category
 *   POST   /api/expenses                      – Create a new expense
 *   PUT    /api/expenses/{id}                 – Update an existing expense
 *   DELETE /api/expenses/{id}                 – Delete an expense
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses
    // GET /api/expenses?category=FOOD
    // GET /api/expenses?date=2025-12-05
    // GET /api/expenses?startDate=2025-12-01&endDate=2025-12-31
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Expense> expenses;
        
        if (category != null && !category.isBlank()) {
            expenses = expenseService.getExpensesByCategory(category);
        } else if (date != null) {
            expenses = expenseService.getExpensesByDate(date);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.getExpensesBetween(startDate, endDate);
        } else {
            expenses = expenseService.getAllExpenses();
        }
        
        return ResponseEntity.ok(expenses);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/recent?days=7
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/recent")
    public ResponseEntity<List<Expense>> getRecentExpenses(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(expenseService.getRecentExpenses(days));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/summary?startDate=...&endDate=...
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getExpenseSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal total = expenseService.getTotalExpenses(startDate, endDate);
        Map<String, BigDecimal> byCategory = expenseService.getExpensesByCategory(startDate, endDate);
        
        return ResponseEntity.ok(Map.of(
                "total", total,
                "byCategory", byCategory,
                "startDate", startDate,
                "endDate", endDate
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/expenses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/expenses
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/expenses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/expenses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        if (expenseService.deleteExpense(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
