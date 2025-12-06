package com.dali.lifeanalytics.intake;

import com.dali.lifeanalytics.intake.dto.DailyLogDto;
import com.dali.lifeanalytics.intake.dto.IntakeResultDto;
import javax.validation.Valid;
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
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"object\",\n");
        sb.append("  \"properties\": {\n");
        sb.append("    \"date\": { \"type\": \"string\", \"format\": \"date\", \"description\": \"Date of the log (YYYY-MM-DD)\" },\n");
        sb.append("    \"health\": {\n");
        sb.append("      \"type\": \"object\",\n");
        sb.append("      \"properties\": {\n");
        sb.append("        \"sleepHours\": { \"type\": \"number\", \"description\": \"Hours of sleep\" },\n");
        sb.append("        \"moodScore\": { \"type\": \"integer\", \"minimum\": 1, \"maximum\": 10 },\n");
        sb.append("        \"stressLevel\": { \"type\": \"integer\", \"minimum\": 1, \"maximum\": 10 },\n");
        sb.append("        \"energyLevel\": { \"type\": \"integer\", \"minimum\": 1, \"maximum\": 10 },\n");
        sb.append("        \"notes\": { \"type\": \"string\" }\n");
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    \"activities\": {\n");
        sb.append("      \"type\": \"array\",\n");
        sb.append("      \"items\": {\n");
        sb.append("        \"type\": \"object\",\n");
        sb.append("        \"properties\": {\n");
        sb.append("          \"type\": { \"type\": \"string\", \"enum\": [\"STUDY\", \"WORK\", \"EXERCISE\", \"LEISURE\", \"SOCIAL\", \"OTHER\"] },\n");
        sb.append("          \"startTime\": { \"type\": \"string\", \"format\": \"date-time\" },\n");
        sb.append("          \"endTime\": { \"type\": \"string\", \"format\": \"date-time\" },\n");
        sb.append("          \"durationMinutes\": { \"type\": \"integer\" },\n");
        sb.append("          \"tags\": { \"type\": \"array\", \"items\": { \"type\": \"string\" } },\n");
        sb.append("          \"description\": { \"type\": \"string\" }\n");
        sb.append("        },\n");
        sb.append("        \"required\": [\"type\"]\n");
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    \"habits\": {\n");
        sb.append("      \"type\": \"array\",\n");
        sb.append("      \"items\": {\n");
        sb.append("        \"type\": \"object\",\n");
        sb.append("        \"properties\": {\n");
        sb.append("          \"habitName\": { \"type\": \"string\", \"description\": \"Must match an existing habit name\" },\n");
        sb.append("          \"value\": { \"type\": \"number\", \"description\": \"1 for completed, or specific value\" },\n");
        sb.append("          \"note\": { \"type\": \"string\" }\n");
        sb.append("        },\n");
        sb.append("        \"required\": [\"habitName\", \"value\"]\n");
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    \"expenses\": {\n");
        sb.append("      \"type\": \"array\",\n");
        sb.append("      \"items\": {\n");
        sb.append("        \"type\": \"object\",\n");
        sb.append("        \"properties\": {\n");
        sb.append("          \"amount\": { \"type\": \"number\" },\n");
        sb.append("          \"category\": { \"type\": \"string\", \"enum\": [\"FOOD\", \"TRANSPORT\", \"EDUCATION\", \"ENTERTAINMENT\", \"HEALTH\", \"UTILITIES\", \"OTHER\"] },\n");
        sb.append("          \"description\": { \"type\": \"string\" },\n");
        sb.append("          \"note\": { \"type\": \"string\" }\n");
        sb.append("        },\n");
        sb.append("        \"required\": [\"amount\", \"category\"]\n");
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    \"tasks\": {\n");
        sb.append("      \"type\": \"array\",\n");
        sb.append("      \"items\": {\n");
        sb.append("        \"type\": \"object\",\n");
        sb.append("        \"properties\": {\n");
        sb.append("          \"title\": { \"type\": \"string\" },\n");
        sb.append("          \"description\": { \"type\": \"string\" },\n");
        sb.append("          \"priority\": { \"type\": \"string\", \"enum\": [\"LOW\", \"MEDIUM\", \"HIGH\", \"URGENT\"] },\n");
        sb.append("          \"dueDate\": { \"type\": \"string\", \"format\": \"date\" },\n");
        sb.append("          \"category\": { \"type\": \"string\" },\n");
        sb.append("          \"status\": { \"type\": \"string\", \"enum\": [\"PENDING\", \"IN_PROGRESS\", \"COMPLETED\", \"CANCELLED\"] }\n");
        sb.append("        },\n");
        sb.append("        \"required\": [\"title\"]\n");
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    \"studySessions\": {\n");
        sb.append("      \"type\": \"array\",\n");
        sb.append("      \"items\": {\n");
        sb.append("        \"type\": \"object\",\n");
        sb.append("        \"properties\": {\n");
        sb.append("          \"courseCode\": { \"type\": \"string\", \"description\": \"Code of the course\" },\n");
        sb.append("          \"topic\": { \"type\": \"string\" },\n");
        sb.append("          \"startTime\": { \"type\": \"string\", \"format\": \"date-time\" },\n");
        sb.append("          \"endTime\": { \"type\": \"string\", \"format\": \"date-time\" },\n");
        sb.append("          \"durationMinutes\": { \"type\": \"integer\" },\n");
        sb.append("          \"notes\": { \"type\": \"string\" }\n");
        sb.append("        }\n");
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    \"notes\": { \"type\": \"string\", \"description\": \"General notes for the day\" }\n");
        sb.append("  },\n");
        sb.append("  \"required\": [\"date\"]\n");
        sb.append("}");
        return ResponseEntity.ok(sb.toString());
    }
}
