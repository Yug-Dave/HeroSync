package de.fhdo.HeroSync.dto;

import java.time.LocalDateTime;

public record UnlockedAchievementDto(
    Long id,
    AchievementDto achievement,
    LocalDateTime unlockedAt
) {}
