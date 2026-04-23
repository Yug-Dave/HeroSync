package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.AchievementDto;
import de.fhdo.HeroSync.dto.UnlockedAchievementDto;

public class UnlockedAchievementGraphQLController {
  private final Long id;
  private final String unlockedAt;
  private final AchievementDto achievement;

  public UnlockedAchievementGraphQLController(Long id, String unlockedAt, AchievementDto achievement) {
    this.id = id;
    this.unlockedAt = unlockedAt;
    this.achievement = achievement;
  }

  public Long getId() { return id; }
  public String getUnlockedAt() { return unlockedAt; }
  public AchievementDto getAchievement() { return achievement; }

  public static UnlockedAchievementGraphQLController fromDto(UnlockedAchievementDto dto) {
    return new UnlockedAchievementGraphQLController(
      dto.id(),
      dto.unlockedAt().toString(),
      dto.achievement()
    );
  }
}
