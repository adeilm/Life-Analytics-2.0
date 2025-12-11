package com.dali.wellness.analytics.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: Gemini API Key is missing.";
        }

        try {
            // Construct the URL with the API key
            String url = apiUrl + "?key=" + apiKey;

            // Construct the Request Body
            // { "contents": [{ "parts": [{"text": "..."}] }] }
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));

            // Set Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Send Request
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Parse Response
            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> candidate = candidates.get(0);
                        Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResponse.get("parts");
                        if (!parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            }
            return "No content generated.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
