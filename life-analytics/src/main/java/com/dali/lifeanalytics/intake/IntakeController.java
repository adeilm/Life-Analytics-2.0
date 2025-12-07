package com.dali.lifeanalytics.intake;

import com.dali.lifeanalytics.intake.dto.DailyLogDto;
import com.dali.lifeanalytics.intake.dto.IntakeResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Intake Controller
 * ──────────────────
 * REST API for AI-generated daily log ingestion.
 *
 * Base path: /api/intake
 *
 * This controller receives JSON structured data from an AI assistant
 * and transforms it into the appropriate entities in the system.
 *
 * Endpoints:
 *   POST /api/intake/daily-log  – Process a daily log
 *   GET  /api/intake/schema     – Get the expected JSON schema
 */
@RestController
@RequestMapping("/api/intake")
@RequiredArgsConstructor
public class IntakeController {

    private final IntakeService intakeService;

    /**
     * Process a daily log from AI.
     */
    @PostMapping("/daily-log")
    public ResponseEntity<IntakeResultDto> processDailyLog(@Valid @RequestBody DailyLogDto dailyLog) {
        IntakeResultDto result = intakeService.processDailyLog(dailyLog);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Get the expected JSON schema for daily logs.
     * This helps AI assistants understand the expected format.
     */
    @GetMapping("/schema")
    public ResponseEntity<String> getSchema() {
        String schema = """
            {
              "type": "object",
              "properties": {
                "date": { "type": "string", "format": "date", "description": "Date of the log (YYYY-MM-DD)" },
                "health": {
                  "type": "object",
                  "properties": {
                    "sleepHours": { "type": "number", "description": "Hours of sleep" },
                    "moodScore": { "type": "integer", "minimum": 1, "maximum": 10 },
                    "stressLevel": { "type": "integer", "minimum": 1, "maximum": 10 },
                    "energyLevel": { "type": "integer", "minimum": 1, "maximum": 10 },
                    "notes": { "type": "string" }
                  }
                },
                "activities": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "type": { "type": "string", "enum": ["STUDY", "WORK", "EXERCISE", "LEISURE", "SOCIAL", "OTHER"] },
                      "startTime": { "type": "string", "format": "date-time" },
                      "endTime": { "type": "string", "format": "date-time" },
                      "durationMinutes": { "type": "integer" },
                      "tags": { "type": "array", "items": { "type": "string" } },
                      "description": { "type": "string" }
                    },
                    "required": ["type"]
                  }
                },
                "habits": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "habitName": { "type": "string", "description": "Must match an existing habit name" },
                      "value": { "type": "number", "description": "1 for completed, or specific value" },
                      "note": { "type": "string" }
                    },
                    "required": ["habitName", "value"]
                  }
                },
                "expenses": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "amount": { "type": "number" },
                      "category": { "type": "string", "enum": ["FOOD", "TRANSPORT", "EDUCATION", "ENTERTAINMENT", "HEALTH", "UTILITIES", "OTHER"] },
                      "description": { "type": "string" },
                      "note": { "type": "string" }
                    },
                    "required": ["amount", "category"]
                  }
                },
                "tasks": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "title": { "type": "string" },
                      "description": { "type": "string" },
                      "priority": { "type": "string", "enum": ["LOW", "MEDIUM", "HIGH", "URGENT"] },
                      "dueDate": { "type": "string", "format": "date" },
                      "category": { "type": "string" },
                      "status": { "type": "string", "enum": ["PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"] }
                    },
                    "required": ["title"]
                  }
                },
                "studySessions": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "courseCode": { "type": "string", "description": "Code of the course" },
                      "topic": { "type": "string" },
                      "startTime": { "type": "string", "format": "date-time" },
                      "endTime": { "type": "string", "format": "date-time" },
                      "durationMinutes": { "type": "integer" },
                      "notes": { "type": "string" }
                    }
                  }
                },
                "notes": { "type": "string", "description": "General notes for the day" }
              },
              "required": ["date"]
            }
            """;
        return ResponseEntity.ok(schema);
    }
}
