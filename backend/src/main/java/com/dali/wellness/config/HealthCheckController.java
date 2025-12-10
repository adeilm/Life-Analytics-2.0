package com.dali.wellness.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health Check Controller
 * ───────────────────────
 * Simple endpoints to verify the application is running.
 *
 * Endpoints:
 *   GET /ping  – Returns "OK" (quick health check)
 *   GET /info  – Returns app info with timestamp
 */
@RestController
@RequestMapping("/api")
public class HealthCheckController {

    /**
     * Simple health check endpoint.
     * Use this to verify the app is up.
     *
     * curl http://localhost:8080/api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Info endpoint with more details.
     *
     * curl http://localhost:8080/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "application", "Life Analytics 2.0",
                "status", "running",
                "timestamp", LocalDateTime.now().toString(),
                "owner", "Dali"
        ));
    }
}
