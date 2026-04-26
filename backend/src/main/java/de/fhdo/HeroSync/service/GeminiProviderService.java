package de.fhdo.HeroSync.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service("geminiProvider")
public class GeminiProviderService implements AiProviderService {

    private static final Logger log = LoggerFactory.getLogger(GeminiProviderService.class);

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.max-tokens:350}")
    private int defaultMaxTokens;

    private final RestTemplate restTemplate;

    public GeminiProviderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String callAi(String systemPrompt, String userMessage, int maxTokens) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = UriComponentsBuilder
                .fromHttpUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent")
                .queryParam("key", apiKey)
                .toUriString();

            Map<String, Object> body = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", List.of(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", userMessage))
                )),
                "generationConfig", Map.of("maxOutputTokens", maxTokens)
            );

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> data = response.getBody();
            if (data == null) return "Gemini is offline.";
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) data.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (HttpClientErrorException e) {
            log.error("Gemini API HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return "Neural link rejected. Authentication failed.";
            }
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return "Neural link overloaded. Please wait for the signal to stabilize.";
            }
            return "Neural link interrupted: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage(), e);
            return "Companion is currently offline. Restoring connection...";
        }
    }
}
