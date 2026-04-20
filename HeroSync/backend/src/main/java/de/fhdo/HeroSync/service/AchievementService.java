package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.AchievementDto;
import de.fhdo.HeroSync.dto.UnlockedAchievementDto;
import de.fhdo.HeroSync.entity.Achievement;
import de.fhdo.HeroSync.entity.Activity;
import de.fhdo.HeroSync.entity.UnlockedAchievement;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.AchievementRepository;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.UnlockedAchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AchievementService {

  private final AchievementRepository achievements;
  private final UnlockedAchievementRepository unlocked;
  private final ActivityRepository activities;
  private final AchievementGeneratorService achievementGenerator;

  public AchievementService(AchievementRepository achievements,
                            UnlockedAchievementRepository unlocked,
                            ActivityRepository activities,
                            AchievementGeneratorService achievementGenerator) {
    this.achievements = achievements;
    this.unlocked = unlocked;
    this.activities = activities;
    this.achievementGenerator = achievementGenerator;
  }

  public List<AchievementDto> getAllAchievements() {
    return achievements.findAll().stream()
        .map(this::toDto)
        .toList();
  }

  public List<AchievementDto> getFilteredAchievements(User user) {
    List<Activity> userActivities = activities.findByHabit_User_UserId(user.getUserId());
    int totalCompletions = userActivities.stream().mapToInt(Activity::getCompletions).sum();
    int maxStreak = calculateMaxStreak(userActivities);

    return achievements.findByTargetUserOrTargetUserIsNull(user).stream()
        .map(a -> toDto(a, calculateProgress(a, totalCompletions, maxStreak)))
        .toList();
  }


  @Transactional
  public List<UnlockedAchievementDto> evaluateAndUnlock(User user) {
    // Repair existing achievement XP values to match new rarity logic
    achievementGenerator.repairExistingXp();

    List<Activity> userActivities = activities.findByHabit_User_UserId(user.getUserId());

    int totalCompletions = userActivities.stream()
        .mapToInt(Activity::getCompletions)
        .sum();

    int maxStreak = calculateMaxStreak(userActivities);

    List<UnlockedAchievement> newlyUnlockedEntities = new ArrayList<>();
    // Filter by targetUser (null for global or current user)
    List<Achievement> candidateAchievements = achievements.findByTargetUserOrTargetUserIsNull(user);

    for (Achievement a : candidateAchievements) {
      // Level gating
      if (a.getMinLevel() != null) {
        int currentLevel = Math.max(1, Math.floorDiv(user.getXp(), 2000) + 1);
        if (currentLevel < a.getMinLevel()) continue;
      }

      int progress = calculateProgress(a, totalCompletions, maxStreak);
      boolean conditionMet = progress >= a.getThreshold();

      boolean already = unlocked.existsByUserAndAchievement_Id(user, a.getId());

      if (conditionMet && !already) {
        UnlockedAchievement ua = unlocked.save(new UnlockedAchievement(user, a));
        newlyUnlockedEntities.add(ua);
        
        // Grant XP Reward
        user.setXp(user.getXp() + a.getXpReward());
      }
    }

    return newlyUnlockedEntities.stream().map(this::toDto).toList();
  }

  //  USER-ISOLATED list
  public List<UnlockedAchievementDto> listUnlocked(User user) {
    return unlocked.findByUser(user).stream()
        .map(this::toDto)
        .toList();
  }

  //  DASHBOARD-FRIENDLY: auto evaluate + return full list
  public List<UnlockedAchievementDto> listUnlockedAuto(User user) {
    evaluateAndUnlock(user);
    return listUnlocked(user);
  }


  @Deprecated
  public List<UnlockedAchievementDto> evaluateAndUnlock() {
    throw new UnsupportedOperationException("Use evaluateAndUnlock(User user) instead.");
  }

  @Deprecated
  public List<UnlockedAchievementDto> listUnlocked() {
    throw new UnsupportedOperationException("Use listUnlocked(User user) instead.");
  }

  // ---------------- mapping helpers ----------------

  private int calculateProgress(Achievement a, int totalCompletions, int maxStreak) {
    return switch (a.getRuleType()) {
      case "TOTAL_COMPLETIONS" -> totalCompletions;
      case "STREAK_DAYS" -> maxStreak;
      case "HABIT_SPECIFIC" -> {
        if (a.getCode().startsWith("GEN_HABIT_")) {
          String[] parts = a.getCode().split("_");
          try {
            Long habitId = Long.parseLong(parts[3]);
            yield activities.sumCompletionsByHabitId(habitId);
          } catch (Exception e) {
            yield 0;
          }
        }
        yield 0;
      }
      default -> 0;
    };
  }

  private AchievementDto toDto(Achievement a) {
    return toDto(a, null);
  }

  private AchievementDto toDto(Achievement a, Integer progress) {
    return new AchievementDto(
        a.getId(),
        a.getCode(),
        a.getTitle(),
        a.getDescription(),
        a.getRuleType(),
        a.getThreshold(),
        a.getXpReward(),
        a.getIconCode(),
        progress
    );
  }

  private UnlockedAchievementDto toDto(UnlockedAchievement ua) {
    return new UnlockedAchievementDto(
        ua.getId(),
        toDto(ua.getAchievement(), ua.getAchievement().getThreshold()), // Unlocked means 100% progress
        ua.getUnlockedAt()
    );
  }

  private int calculateMaxStreak(List<Activity> list) {
    var dates = list.stream()
        .filter(a -> a.getCompletions() > 0)
        .map(Activity::getDate)
        .distinct()
        .sorted()
        .toList();

    int max = 0;
    int current = 0;
    LocalDate prev = null;

    for (LocalDate d : dates) {
      if (prev == null || d.equals(prev.plusDays(1))) current++;
      else current = 1;

      max = Math.max(max, current);
      prev = d;
    }
    return max;
  }
}
