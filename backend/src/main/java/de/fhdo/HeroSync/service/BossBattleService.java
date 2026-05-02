package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.entity.*;
import de.fhdo.HeroSync.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates a Weekly Boss Battle goal every Monday at 06:00.
 *
 * The Boss is auto-assigned to the user's WEAKEST habit from the previous
 * 7 days (lowest completion rate). If the user has no habits, no boss is created.
 * Only one ACTIVE boss goal per user per week is ever created.
 */
@Service
public class BossBattleService {

  private static final Logger log = LoggerFactory.getLogger(BossBattleService.class);
  private static final int BOSS_XP_MULTIPLIER = 3;
  private static final int BOSS_TARGET_COUNT   = 5; // complete the habit 5 times this week

  private final UserRepository      userRepository;
  private final HabitRepository     habitRepository;
  private final ActivityRepository  activityRepository;
  private final GoalRepository      goalRepository;

  public BossBattleService(UserRepository userRepository,
                            HabitRepository habitRepository,
                            ActivityRepository activityRepository,
                            GoalRepository goalRepository) {
    this.userRepository     = userRepository;
    this.habitRepository    = habitRepository;
    this.activityRepository = activityRepository;
    this.goalRepository     = goalRepository;
  }

  /**
   * Runs every Monday at 06:00 server time.
   * Cron: second minute hour day-of-month month day-of-week
   */
  @Scheduled(cron = "0 0 6 * * MON")
  @Transactional
  public void generateBossesForAllUsers() {
    log.info("[BossBattle] Weekly boss generation started");
    List<User> users = userRepository.findAll();
    int created = 0;
    for (User user : users) {
      try {
        if (generateBossForUser(user)) created++;
      } catch (Exception e) {
        log.warn("[BossBattle] Failed for user {}: {}", user.getUserId(), e.getMessage());
      }
    }
    log.info("[BossBattle] Created {} boss goals", created);
  }

  /**
   * Generates a boss for a single user if one doesn't already exist this week.
   * Returns true if a new boss was created.
   */
  public boolean generateBossForUser(User user) {
    LocalDate today   = LocalDate.now();
    LocalDate monday  = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate sunday  = monday.plusDays(6);

    // Check if boss already exists for this week
    boolean bossExists = goalRepository.findByUser_UserId(user.getUserId())
        .stream()
        .anyMatch(g -> g.isBoss()
            && g.getStatus() == GoalStatus.ACTIVE
            && g.getDeadline() != null
            && !g.getDeadline().isBefore(monday));

    if (bossExists) return false;

    // Find weakest habit from last 7 days
    List<Habit> habits = habitRepository.findByUser_UserId(user.getUserId())
        .stream().filter(h -> !h.isArchived()).collect(Collectors.toList());

    if (habits.isEmpty()) return false;

    LocalDate weekAgo = today.minusDays(7);
    List<Activity> recentActivity = activityRepository
        .findByHabit_User_UserIdAndDateBetween(user.getUserId(), weekAgo, today);

    // Count completions per habit
    Map<Long, Long> completionCounts = recentActivity.stream()
        .filter(a -> a.getCompletions() > 0)
        .collect(Collectors.groupingBy(a -> a.getHabit().getId(), Collectors.counting()));

    // Find the habit with fewest completions (weakest)
    Habit weakestHabit = habits.stream()
        .min(Comparator.comparingLong(h -> completionCounts.getOrDefault(h.getId(), 0L)))
        .orElse(habits.get(0));

    // Build the boss goal
    Goal boss = new Goal();
    boss.setUser(user);
    boss.setBoss(true);
    boss.setBossType("WEEKLY");
    boss.setHabit(weakestHabit);
    boss.setTitle("⚔ WEEKLY BOSS: " + weakestHabit.getName());
    boss.setDescription("Your weakest quest this week. Complete it " + BOSS_TARGET_COUNT
        + " times before Sunday to earn " + BOSS_XP_MULTIPLIER + "× XP reward.");
    boss.setTargetCount(BOSS_TARGET_COUNT);
    boss.setProgressCount(0);
    boss.setStatus(GoalStatus.ACTIVE);
    boss.setDeadline(sunday);

    goalRepository.save(boss);
    log.info("[BossBattle] Created boss for user {} — habit: {}", user.getUserId(), weakestHabit.getName());
    return true;
  }
}
