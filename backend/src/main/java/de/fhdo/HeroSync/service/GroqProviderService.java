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

@Service("groqProvider")
public class GroqProviderService implements AiProviderService {

    private static final Logger log = LoggerFactory.getLogger(GroqProviderService.class);

  @Value("${ai.groq.api-key}")
  private String apiKey;

  @Value("${ai.max-tokens:350}")
  private int maxTokens;

  private final RestTemplate restTemplate;

  public GroqProviderService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public String callAi(String systemPrompt, String userMessage, int maxTokens) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + apiKey);
      headers.setContentType(MediaType.APPLICATION_JSON);

      Map<String, Object> body = Map.of(
          "model", "llama-3.1-8b-instant",
          "max_tokens", maxTokens,
          "messages", List.of(
              Map.of("role", "system", "content", systemPrompt),
              Map.of("role", "user", "content", userMessage)));

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
      ResponseEntity<Map> response = restTemplate.postForEntity(
          "https://api.groq.com/openai/v1/chat/completions",
          entity, Map.class);

      List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
      Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
      return (String) message.get("content");

    } catch (HttpClientErrorException e) {
        log.error("Groq API HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
            return "Neural link rejected. Authentication failed.";
        }
        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return "Neural link overloaded. Please wait for the signal to stabilize.";
        }
        return "Neural link interrupted: " + e.getStatusCode();
    } catch (Exception e) {
        log.error("Groq API call failed: {}", e.getMessage(), e);
        return "Companion is currently offline. Restoring connection...";
    }
  }
}
