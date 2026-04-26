package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.DashboardDTO;
import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.dto.HabitDto;
import de.fhdo.HeroSync.dto.HeatmapDay;
import de.fhdo.HeroSync.entity.Activity;
import de.fhdo.HeroSync.entity.Dashboard;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.Goal;
import de.fhdo.HeroSync.entity.GoalStatus;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.DashboardRepository;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import de.fhdo.HeroSync.repository.GoalRepository;
import de.fhdo.HeroSync.repository.UnlockedAchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@SuppressWarnings("null")
public class DashboardService {

  private final DashboardRepository dashboardRepository;
  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;
  private final HabitRepository habitRepository;
  private final GoalRepository goalRepository;
  private final UnlockedAchievementRepository unlockedAchievementRepository;
  private final AchievementGeneratorService achievementGenerator;

  public DashboardService(DashboardRepository dashboardRepository,
                          ActivityRepository activityRepository,
                          UserRepository userRepository,
                          HabitRepository habitRepository,
                          GoalRepository goalRepository,
                          UnlockedAchievementRepository unlockedAchievementRepository,
                          AchievementGeneratorService achievementGenerator) {
    this.dashboardRepository = dashboardRepository;
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
    this.habitRepository = habitRepository;
    this.goalRepository = goalRepository;
    this.unlockedAchievementRepository = unlockedAchievementRepository;
    this.achievementGenerator = achievementGenerator;
    
    this.achievementGenerator.seedGlobalAchievements();
  }

  public int calculateLevel(int xp) {
    if (xp <= 0) return 1;
    // Formula: Level = floor(sqrt(XP / 1000)) + 1
    // 0-999: Lvl 1, 1000-3999: Lvl 2, 4000-8999: Lvl 3, 9000-15999: Lvl 4
    return (int) Math.floor(Math.sqrt((double) xp / 1000)) + 1;
  }

  public void triggerAchievementGeneration(User user, int level) {
    try {
      achievementGenerator.generateForLevel(user, level);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      // Ignore concurrent insertion attempt
    }
  }

  public User findUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  public List<HeatmapDay> getHeatmapData(Long userId) {
    List<HeatmapDay> heatmap = new ArrayList<>();
    LocalDate today = LocalDate.now();
    List<ActivityDto> userActivities = getActivitiesByUser(userId);

    for (int i = 0; i < 10; i++) {
      LocalDate dateToCheck = today.minusDays(i);

      int dailyTotal = userActivities.stream()
        .filter(a -> a.getDate().equals(dateToCheck))
        .mapToInt(ActivityDto::getCompletions)
        .sum();

      heatmap.add(new HeatmapDay(dateToCheck, dailyTotal));
    }
    return heatmap;
  }

  public List<HabitDto> getHabitsByUser(Long userId) {
    List<Habit> habits = habitRepository.findByUser_UserId(userId);

    return habits.stream()
      .map(h -> new HabitDto(
        h.getId(),
        h.getName(),
        h.getDescription(),
        h.isArchived(),
        h.getCreatedAt(),
        h.getXpValue()
      ))
      .collect(Collectors.toList());
  }

  @Transactional
  public void addHabit(String name, String description, Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    Habit newHabit = new Habit();
    newHabit.setName(name);
    newHabit.setDescription(description);
    newHabit.setUser(user);
    newHabit.setCreatedAt(java.time.LocalDateTime.now());
    newHabit.setArchived(false);
    
    // Set XP value based on name length for variety [50, 150]
    int xp = 50 + (name.length() * 5) % 100;
    newHabit.setXpValue(xp);
    
    habitRepository.save(newHabit);
  }

  /**
   * Calculates the total XP for a user based on habit completions, goals, and achievements.
   */
  public int calculateTotalXP(Long userId) {
    List<Activity> completions = activityRepository.findByHabit_User_UserId(userId);
    int habitXP = completions.stream()
      .filter(a -> a.getCompletions() > 0)
      .mapToInt(a -> a.getHabit().getXpValue())
      .sum();
      
    List<Goal> completedGoals = goalRepository.findByUser_UserIdAndStatus(userId, GoalStatus.COMPLETED);
    int goalXP = completedGoals.stream()
      .mapToInt(g -> g.isBoss() ? 1000 : 200)
      .sum();

    User user = userRepository.findById(userId).orElse(null);
    int achievementXP = 0;
    if (user != null) {
      achievementXP = unlockedAchievementRepository.findByUser(user).stream()
          .mapToInt(ua -> ua.getAchievement().getXpReward())
          .sum();
    }

    return habitXP + goalXP + achievementXP;
  }

  @Transactional
  public void markHabitComplete(Long habitId) {
    LocalDate today = LocalDate.now();

    Habit habit = habitRepository.findById(habitId)
      .orElseThrow(() -> new RuntimeException("Habit not found"));

    User user = habit.getUser();
    int currentStreak = calculateCurrentStreak(getActivitiesByUser(user.getUserId()));
    int xpBefore = calculateTotalXP(user.getUserId());
    int levelBefore = calculateLevel(xpBefore);

    Activity activity = activityRepository
      .findByHabit_IdAndDate(habitId, today)
      .orElseGet(() -> {
        Activity a = new Activity();
        a.setHabit(habit);
        a.setDate(today);
        a.setCompletions(0);
        return a;
      });

    activity.setCompletions(activity.getCompletions() > 0 ? 0 : 1);
    activityRepository.save(activity);

    int xpAfter = calculateTotalXP(user.getUserId());
    int levelAfter = calculateLevel(xpAfter);

    if (levelAfter > levelBefore) {
      try {
        achievementGenerator.generateForLevel(user, levelAfter);
      } catch (org.springframework.dao.DataIntegrityViolationException e) {
        // Ignore concurrent insertion
      }
    }
  }

  public void editHabit(Long id, String newName, String newDescription) {
    Optional<Habit> habitOpt = habitRepository.findById(id);
    if (habitOpt.isPresent()) {
      Habit habit = habitOpt.get();
      habit.setName(newName);
      habit.setDescription(newDescription);
      habitRepository.save(habit);
    }
  }

  @Transactional
  public void deleteHabit(Long habitId) {
    if (!habitRepository.existsById(habitId)) {
      return;
    }

    List<Goal> linkedGoals = goalRepository.findAll().stream()
        .filter(g -> g.getHabit() != null && g.getHabit().getId().equals(habitId))
        .toList();
    linkedGoals.forEach(g -> {
        g.setHabit(null);
        goalRepository.save(g);
    });

    activityRepository.deleteByHabit_Id(habitId);
    habitRepository.deleteById(habitId);
  }

  public Set<Long> getHabitIdsCompletedToday(Long userId) {
    LocalDate today = LocalDate.now();
    return getActivitiesByUser(userId).stream()
      .filter(a -> a.getDate().equals(today) && a.getCompletions() > 0)
      .map(ActivityDto::getHabitId)
      .collect(Collectors.toSet());
  }

  @Transactional
  public DashboardDTO getDashboard(Long dashboardId) {
    Dashboard db = dashboardRepository.findById(dashboardId.intValue())
      .orElse(null);

    if (db == null) {
      return new DashboardDTO(0L, "Guest", null, 0, 0, 0);
    }

    User user = db.getUser();
    List<ActivityDto> activities = getActivitiesByUser(user.getUserId());
    int streak = calculateCurrentStreak(activities);
    int xp = calculateTotalXP(user.getUserId());
    
    int level = calculateLevel(xp);
    try {
      achievementGenerator.generateForLevel(user, level);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      // Ignore concurrent insertion attempt
    }

    String avatar = (user.getProfile() != null) ? user.getProfile().getAvatar() : null;
    int habitsDone = calculateTotalHabitsDone(activities);

    return new DashboardDTO((long) db.getDashboardId(), user.getName(), avatar, streak, xp, habitsDone);
  }

  @Transactional
  public List<ActivityDto> getActivitiesByUser(Long userId) {
    List<Activity> all = activityRepository.findByHabit_User_UserId(userId);
    all.sort((a, b) -> b.getDate().compareTo(a.getDate()));
    return all.stream()
      .map(a -> new ActivityDto(
        a.getDate(),
        a.getCompletions(),
        a.getHabit().getId(),
        a.getHabit().getName(),
        a.getHabit().getXpValue()
      ))
      .collect(Collectors.toList());
  }

  public int calculateCurrentStreak(List<ActivityDto> activities) {
    if (activities == null || activities.isEmpty()) return 0;

    LocalDate today = LocalDate.now();

    List<LocalDate> completedDates = activities.stream()
      .filter(a -> a.getCompletions() > 0)
      .map(ActivityDto::getDate)
      .filter(d -> d != null && !d.isAfter(today))
      .distinct()
      .sorted((d1, d2) -> d2.compareTo(d1))
      .toList();

    if (completedDates.isEmpty()) return 0;

    int streak = 0;
    LocalDate checkDate = LocalDate.now();

    if (!completedDates.contains(checkDate)) {
      checkDate = checkDate.minusDays(1);
    }

    for (LocalDate date : completedDates) {
      if (date.equals(checkDate)) {
        streak++;
        checkDate = checkDate.minusDays(1);
      } else {
        break;
      }
    }
    return streak;
  }

  public int calculateTotalHabitsDone(List<ActivityDto> activities) {
    return activities.stream().mapToInt(ActivityDto::getCompletions).sum();
  }

  public int calculateProgressPercentage(int total) {
    int maxTarget = 100;
    int percentage = (total * 100) / maxTarget;
    return Math.min(percentage, 100);
  }
}
