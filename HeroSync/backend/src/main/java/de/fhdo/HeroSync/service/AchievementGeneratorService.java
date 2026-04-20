package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.entity.Achievement;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.AchievementRepository;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class AchievementGeneratorService {

  private final AchievementRepository achievementRepository;
  private final HabitRepository habitRepository;
  private final ActivityRepository activityRepository;
  private final Random random = new Random();

  public AchievementGeneratorService(AchievementRepository achievementRepository,
                                     HabitRepository habitRepository,
                                     ActivityRepository activityRepository) {
    this.achievementRepository = achievementRepository;
    this.habitRepository = habitRepository;
    this.activityRepository = activityRepository;
  }

  @Transactional
  public void generateForLevel(User user, int level) {
    List<Habit> activeHabits = habitRepository.findByUserAndArchivedFalse(user);
    
    // 1. Habit Specific Achievement (Only if they have active habits)
    if (!activeHabits.isEmpty()) {
      Habit target = activeHabits.get(random.nextInt(activeHabits.size()));
      createHabitAchievement(user, target, level);
    }

    // 2. Consistency Achievement
    createConsistencyAchievement(user, level);

    // 3. Streak Challenge
    createStreakAchievement(user, level);
  }

  private void createHabitAchievement(User user, Habit habit, int level) {
    int currentCompletions = activityRepository.sumCompletionsByHabitId(habit.getId());
    int target = currentCompletions + (level * 5) + 5;
    
    String code = "GEN_HABIT_" + user.getUserId() + "_" + habit.getId() + "_" + level;
    if (achievementRepository.existsByCode(code)) return;

    Achievement a = new Achievement(
        code,
        "Master of " + habit.getName(),
        "Complete your habit '" + habit.getName() + "' a total of " + target + " times.",
        "HABIT_SPECIFIC",
        target,
        calculateXp(target, level),
        "star"
    );
    a.setTargetUser(user);
    a.setMinLevel(level);
    achievementRepository.save(a);
  }

  private void createConsistencyAchievement(User user, int level) {
    int currentTotal = activityRepository.findByHabit_User_UserId(user.getUserId()).size();
    int target = currentTotal + (level * 10) + 10;

    String code = "GEN_CONSISTENCY_" + user.getUserId() + "_" + level;
    if (achievementRepository.existsByCode(code)) return;

    Achievement a = new Achievement(
        code,
        "Consistency King Lvl " + level,
        "Achieve a total of " + target + " completions across all your habits.",
        "TOTAL_COMPLETIONS",
        target,
        calculateXp(target, level),
        "shield"
    );
    a.setTargetUser(user);
    a.setMinLevel(level);
    achievementRepository.save(a);
  }

  private void createStreakAchievement(User user, int level) {
    int target = 3 + level; // Simple scaling

    String code = "GEN_STREAK_" + user.getUserId() + "_" + level;
    if (achievementRepository.existsByCode(code)) return;

    Achievement a = new Achievement(
        code,
        "Streak Legend Lvl " + level,
        "Maintain a best streak of " + target + " days.",
        "STREAK_DAYS",
        target,
        calculateXp(target, level),
        "flame"
    );
    a.setTargetUser(user);
    a.setMinLevel(level);
    achievementRepository.save(a);
  }

  @Transactional
  public void seedGlobalAchievements() {
    if (!achievementRepository.existsByCode("CENTURION")) {
      achievementRepository.save(new Achievement("CENTURION", "Centurion", "Reach 100 total completions.", "TOTAL_COMPLETIONS", 100, 1000, "star"));
    }
    // Also repair any existing achievements to match new XP logic
    repairExistingXp();
  }

  @Transactional
  public void repairExistingXp() {
    List<Achievement> all = achievementRepository.findAll();
    for (Achievement a : all) {
      // For level, we try to infer from the code if it's generated, otherwise default to 1
      int level = 1;
      try {
        if (a.getCode().startsWith("GEN_")) {
          String[] parts = a.getCode().split("_");
          level = Integer.parseInt(parts[parts.length - 1]);
        }
      } catch (Exception ignored) {}
      
      int newXp = calculateXp(a.getThreshold(), level);
      if (a.getXpReward() != newXp) {
        a.setXpReward(newXp);
        achievementRepository.save(a);
      }
    }
  }

  private int calculateXp(int threshold, int level) {
    if (threshold >= 100) return level * 1000; // Legendary
    if (threshold >= 50)  return level * 500;  // Epic
    if (threshold >= 10)  return level * 250;  // Rare
    return level * 100;                        // Common
  }
}
