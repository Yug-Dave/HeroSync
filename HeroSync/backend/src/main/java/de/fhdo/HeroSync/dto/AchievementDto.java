package de.fhdo.HeroSync.dto;

public record AchievementDto(
    Long id,
    String code,
    String title,
    String description,
    String ruleType,
    int threshold,
    int xpReward,
    String iconCode,
    Integer currentProgress
) {}
