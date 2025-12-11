package com.dali.wellness.analytics.controller;

import com.dali.wellness.analytics.AnalyticsService;
import com.dali.wellness.analytics.service.GeminiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AIInsightsController {

    private final AnalyticsService analyticsService;
    private final GeminiService geminiService;

    public AIInsightsController(AnalyticsService analyticsService, GeminiService geminiService) {
        this.analyticsService = analyticsService;
        this.geminiService = geminiService;
    }

    @GetMapping("/ai-insights")
    public Map<String, String> getAIInsights() {
        // 1. Gather Data
        Map<String, Object> dashboardData = analyticsService.getDashboard();

        // 2. Construct Prompt
        String prompt = constructPrompt(dashboardData);

        // 3. Call Gemini
        String insight = geminiService.generateContent(prompt);

        // 4. Return Result
        return Map.of("insight", insight);
    }

    private String constructPrompt(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a supportive and knowledgeable wellness coach. ");
        sb.append("Analyze the following weekly wellness data for a user and provide 3 concise, actionable insights or encouraging remarks to help them improve their habits and health. ");
        sb.append("Keep the tone positive and motivating. Format the output as a simple list or paragraph.\n\n");
        sb.append("Data Summary:\n");
        sb.append(data.toString()); // Map.toString() provides a readable enough JSON-like structure
        return sb.toString();
    }
}
