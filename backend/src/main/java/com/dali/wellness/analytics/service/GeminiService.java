package com.dali.wellness.analytics.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Service for interacting with the Google Gemini API.
 * Handles API calls, retries, and fallbacks for AI content generation.
 */
@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Generates AI content based on the provided prompt.
     * Includes automatic retry logic for 503 errors and fallback to a lighter model.
     *
     * @param prompt The text prompt to send to the AI.
     * @return The generated text response, or an error message if all attempts fail.
     */
    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("Gemini API Key is missing.");
            return "Error: Gemini API Key is missing.";
        }

        try {
            return callGeminiApi(apiUrl, prompt)
                    .onErrorResume(e -> {
                        if (isOverloaded(e)) {
                            logger.warn("Primary model overloaded. Attempting fallback to gemini-2.0-flash-lite...");
                            // Simple string replacement to switch models. 
                            // Assumes apiUrl format: .../models/gemini-2.5-flash:generateContent
                            String fallbackUrl = apiUrl.replace("gemini-2.5-flash", "gemini-2.0-flash-lite");
                            return callGeminiApi(fallbackUrl, prompt);
                        }
                        return Mono.error(e);
                    })
                    .block(); // Blocking here to keep the controller simple for now
        } catch (Exception e) {
            logger.error("Error generating content", e);
            return "I'm currently experiencing high traffic. Please try again in a moment.";
        }
    }

    private Mono<String> callGeminiApi(String baseUrl, String prompt) {
        String url = baseUrl + "?key=" + apiKey;
        Map<String, Object> requestBody = createRequestBody(prompt);

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractText)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isOverloaded)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()));
    }

    private boolean isOverloaded(Throwable e) {
        return e instanceof WebClientResponseException && 
               ((WebClientResponseException) e).getStatusCode().value() == 503;
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));
        return requestBody;
    }

    private String extractText(JsonNode root) {
        if (root != null) {
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode parts = firstCandidate.path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
        }
        return "No content generated.";
    }
}
