package de.fhdo.HeroSync.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service("gptProvider")
public class GptProviderService implements AiProviderService {

    private static final Logger log = LoggerFactory.getLogger(GptProviderService.class);

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.max-tokens:350}")
    private int defaultMaxTokens;

    private final RestTemplate restTemplate;

    public GptProviderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String callAi(String systemPrompt, String userMessage, int maxTokens) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "max_tokens", maxTokens,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
                )
            );

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> data = response.getBody();
            if (data == null) return "GPT is offline.";
            List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (HttpClientErrorException e) {
            log.error("GPT API HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return "Neural link rejected. Authentication failed.";
            }
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return "Neural link overloaded. Please wait for the signal to stabilize.";
            }
            return "Neural link interrupted: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("GPT API call failed: {}", e.getMessage(), e);
            return "Companion is currently offline. Restoring connection...";
        }
    }
}
