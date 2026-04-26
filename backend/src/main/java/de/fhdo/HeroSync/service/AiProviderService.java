package de.fhdo.HeroSync.service;

public interface AiProviderService {
    String callAi(String systemPrompt, String userMessage, int maxTokens);
}
