package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.AiChatRequest;
import de.fhdo.HeroSync.dto.AiChatResponse;
import de.fhdo.HeroSync.entity.*;
import de.fhdo.HeroSync.repository.HabitRepository;
import de.fhdo.HeroSync.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final AiContextBuilder aiContextBuilder;
    private final AiPromptBuilder aiPromptBuilder;
    private final ProfileRepository profileRepository;
    private final HabitRepository habitRepository;
    private final AiProviderService groqProvider;
    private final AiProviderService gptProvider;
    private final AiProviderService geminiProvider;
    private final int maxTokens;

    public AiChatService(AiContextBuilder aiContextBuilder,
                         AiPromptBuilder aiPromptBuilder,
                         ProfileRepository profileRepository,
                         HabitRepository habitRepository,
                         @Qualifier("groqProvider") AiProviderService groqProvider,
                         @Qualifier("gptProvider") AiProviderService gptProvider,
                         @Qualifier("geminiProvider") AiProviderService geminiProvider,
                         @Value("${ai.max-tokens:350}") int maxTokens) {
        this.aiContextBuilder = aiContextBuilder;
        this.aiPromptBuilder = aiPromptBuilder;
        this.profileRepository = profileRepository;
        this.habitRepository = habitRepository;
        this.groqProvider = groqProvider;
        this.gptProvider = gptProvider;
        this.geminiProvider = geminiProvider;
        this.maxTokens = maxTokens;
    }

    public AiChatResponse chat(User user, AiChatRequest request) {
        // Load profile — default to SYNC + CLAUDE if none exists
        Profile profile = profileRepository.findByUserUserId(user.getUserId()).orElse(null);
        CompanionChoice companion = (profile != null && profile.getCompanionChoice() != null)
                ? profile.getCompanionChoice() : CompanionChoice.SYNC;
        AiProviderChoice providerChoice = (profile != null && profile.getAiProvider() != null)
                ? profile.getAiProvider() : AiProviderChoice.GROQ;

        // Build context string
        String context = aiContextBuilder.buildContext(user);

        // Build existing quests string (for coaching mode)
        String existingQuests = habitRepository.findByUser_UserId(user.getUserId())
                .stream()
                .filter(h -> !h.isArchived())
                .map(Habit::getName)
                .collect(Collectors.joining(", "));
        if (existingQuests.isEmpty()) existingQuests = "None";

        // Build system prompt
        String mode = request.getMode() != null ? request.getMode() : "chat";
        String systemPrompt = aiPromptBuilder.buildSystemPrompt(companion, mode, context, existingQuests);

        // Select provider
        AiProviderService provider = switch (providerChoice) {
          case GROQ -> groqProvider;
          case GEMINI -> geminiProvider;
          case GPT -> gptProvider;
        };

        // Call AI
        String userMessage = request.getMessage() != null ? request.getMessage() : "";
        String reply = provider.callAi(systemPrompt, userMessage, maxTokens);

        return new AiChatResponse(reply, companion.name(), providerChoice.name());
    }
}
