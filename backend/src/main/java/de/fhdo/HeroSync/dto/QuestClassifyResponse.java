package de.fhdo.HeroSync.dto;

public class QuestClassifyResponse {
  private String difficulty;   // EASY, STANDARD, HARD, ELITE
  private int minXp;
  private int maxXp;
  private int suggestedXp;
  private String reasoning;

  public QuestClassifyResponse() {}

  public QuestClassifyResponse(String difficulty, int minXp, int maxXp, int suggestedXp, String reasoning) {
    this.difficulty  = difficulty;
    this.minXp       = minXp;
    this.maxXp       = maxXp;
    this.suggestedXp = suggestedXp;
    this.reasoning   = reasoning;
  }

  public String getDifficulty()  { return difficulty; }
  public int    getMinXp()       { return minXp; }
  public int    getMaxXp()       { return maxXp; }
  public int    getSuggestedXp() { return suggestedXp; }
  public String getReasoning()   { return reasoning; }
}
