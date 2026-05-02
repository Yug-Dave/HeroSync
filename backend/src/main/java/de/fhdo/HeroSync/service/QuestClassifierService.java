package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.QuestClassifyResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Uses AI to classify a quest name+description into a difficulty tier
 * (EASY / STANDARD / HARD / ELITE) and returns XP range + suggested XP.
 *
 * The AI always has the final say — it can classify ANY habit even if it's
 * not on a fixed list, by reasoning about effort and consistency required.
 */
@Service
public class QuestClassifierService {

  private static final String CLASSIFY_PROMPT = """
      You are a habit difficulty classifier for the gamified productivity app HeroSync.
      
      Classify the habit into exactly one tier based on the physical, mental, or time effort required to complete it DAILY:
      
      EASY   (30-60 XP)  — Minimal effort. Routine micro-habits. Examples: drink water, take vitamins, 5-min stretch, brush teeth, journal 1 sentence.
      STANDARD (80-120 XP) — Moderate consistent effort. Examples: 30-min workout, 20-min reading, daily meditation 10min, cook a healthy meal, walk 5000 steps.
      HARD   (150-200 XP) — High effort, requires real discipline. Examples: 1hr gym session, 45-min language study, cold shower, intermittent fasting, run 5K.
      ELITE  (250-300 XP) — Extreme commitment, life-changing level. Examples: marathon training run, 2hr deep work session, full day fasting, 90-min language immersion.
      
      IMPORTANT: Classify based on actual effort, not how impressive it sounds. "Drink 8 glasses of water" is always EASY regardless of how the user phrases it.
      For unusual or creative habits not on the list, reason about the physical/mental/time effort and classify accordingly.
      
      Return ONLY valid JSON in exactly this format, no markdown, no explanation outside JSON:
      {
        "difficulty": "EASY|STANDARD|HARD|ELITE",
        "minXp": <number>,
        "maxXp": <number>,
        "suggestedXp": <number>,
        "reasoning": "<one sentence, max 60 chars>"
      }
      """;

  private final AiProviderService groqProvider;
  private final ObjectMapper mapper = new ObjectMapper();

  public QuestClassifierService(@Qualifier("groqProvider") AiProviderService groqProvider) {
    this.groqProvider = groqProvider;
  }

  public QuestClassifyResponse classify(String habitName, String habitDescription) {
    String userMessage = "Habit name: \"" + habitName + "\""
        + (habitDescription != null && !habitDescription.isBlank()
            ? "\nDescription: \"" + habitDescription + "\"" : "");

    try {
      String raw = groqProvider.callAi(CLASSIFY_PROMPT, userMessage, 200);
      // Strip markdown fences if present
      String json = raw.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").strip();
      JsonNode node = mapper.readTree(json);

      String difficulty  = node.path("difficulty").asText("STANDARD");
      int    minXp       = node.path("minXp").asInt(80);
      int    maxXp       = node.path("maxXp").asInt(120);
      int    suggestedXp = node.path("suggestedXp").asInt(100);
      String reasoning   = node.path("reasoning").asText("");

      // Validate tier bounds — protect against hallucinated out-of-range values
      suggestedXp = clamp(suggestedXp, minXp, maxXp);

      return new QuestClassifyResponse(difficulty, minXp, maxXp, suggestedXp, reasoning);

    } catch (Exception e) {
      // Fallback: return STANDARD on any parse failure
      return new QuestClassifyResponse("STANDARD", 80, 120, 100, "Unable to classify — defaulting to Standard.");
    }
  }

  private int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
