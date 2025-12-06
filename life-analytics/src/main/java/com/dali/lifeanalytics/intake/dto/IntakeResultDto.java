package com.dali.lifeanalytics.intake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Intake Result DTO
 * ──────────────────
 * Result of processing a daily log.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntakeResultDto {

    @Builder.Default
    private boolean success = true;

    private String message;

    @Builder.Default
    private List<String> createdEntities = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    private int healthMetricsCreated;
    private int activitiesCreated;
    private int habitsLogged;
    private int expensesCreated;
    private int tasksCreated;
    private int studySessionsCreated;

    public void addCreatedEntity(String entity) {
        createdEntities.add(entity);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addError(String error) {
        errors.add(error);
        success = false;
    }
}
