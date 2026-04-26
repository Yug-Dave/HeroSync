package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.entity.*;
import de.fhdo.HeroSync.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiContextBuilder {

    private final HabitRepository habitRepository;
    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;
    private final DashboardService dashboardService;

    public AiContextBuilder(HabitRepository habitRepository,
                            GoalRepository goalRepository,
                            ActivityRepository activityRepository,
                            DashboardService dashboardService) {
        this.habitRepository = habitRepository;
        this.goalRepository = goalRepository;
        this.activityRepository = activityRepository;
        this.dashboardService = dashboardService;
    }

    public String buildContext(User user) {
        Long userId = user.getUserId();
        LocalDate today = LocalDate.now();

        // Active (non-archived) habits
        List<Habit> habits = habitRepository.findByUser_UserId(userId)
                .stream().filter(h -> !h.isArchived()).collect(Collectors.toList());

        // Which habits were completed today
        Set<Long> completedTodayIds = habits.stream()
                .filter(h -> activityRepository.findByHabit_IdAndDate(h.getId(), today)
                        .map(a -> a.getCompletions() > 0).orElse(false))
                .map(Habit::getId)
                .collect(Collectors.toSet());

        int doneToday = completedTodayIds.size();
        int totalToday = habits.size();

        String questsStr = habits.isEmpty() ? "None" : habits.stream()
                .map(h -> h.getName() + " [" + (completedTodayIds.contains(h.getId()) ? "Done" : "Pending") + "]")
                .collect(Collectors.joining(", "));

        // Active goals/missions
        List<Goal> activeGoals = goalRepository.findByUser_UserIdAndStatus(userId, GoalStatus.ACTIVE);
        String missionsStr = activeGoals.isEmpty() ? "None" : activeGoals.stream()
                .map(g -> g.getTitle() + " — " + g.getProgressCount() + "/"
                        + (g.getTargetCount() != null ? g.getTargetCount() : "?")
                        + (g.getDeadline() != null ? ", deadline: " + g.getDeadline() : ""))
                .collect(Collectors.joining(", "));

        // Last 7 days daily completion totals
        List<ActivityDto> allActivities = dashboardService.getActivitiesByUser(userId);
        List<String> dailyTotals = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            long count = allActivities.stream()
                    .filter(a -> a.getDate().equals(day) && a.getCompletions() > 0)
                    .count();
            dailyTotals.add(String.valueOf(count));
        }

        // XP, level, streak
        int xp = dashboardService.calculateTotalXP(userId);
        int level = dashboardService.calculateLevel(xp);
        long nextLevelXp = (long) Math.pow(level, 2) * 1000;
        int streak = dashboardService.calculateCurrentStreak(allActivities);

        String rank;
        if (level >= 6) rank = "Mythic";
        else if (level == 5) rank = "Legend";
        else if (level == 4) rank = "Champion";
        else if (level == 3) rank = "Warrior";
        else if (level == 2) rank = "Guardian";
        else rank = "Recruit";

        return "Hero: " + user.getName() + " | Rank: " + rank + " | Level: " + level
                + " | XP: " + xp + "/" + nextLevelXp + "\n"
                + "Streak: " + streak + " days\n"
                + "Today's Quests: " + questsStr + " (" + doneToday + "/" + totalToday + " complete)\n"
                + "Active Missions: " + missionsStr + "\n"
                + "Last 7 days completions: " + String.join(", ", dailyTotals);
    }
}
