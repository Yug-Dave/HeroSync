package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.entity.Activity;
import de.fhdo.HeroSync.entity.Goal;
import de.fhdo.HeroSync.entity.GoalStatus;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.GoalRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@SuppressWarnings("null")
public class ActivityService {

  private final ActivityRepository activityRepository;
  private final HabitRepository habitRepository;
  private final GoalRepository goalRepository;

  public ActivityService(
    ActivityRepository activityRepository,
    HabitRepository habitRepository,
    GoalRepository goalRepository
  ) {
    this.activityRepository = activityRepository;
    this.habitRepository = habitRepository;
    this.goalRepository = goalRepository;
  }

  /**
   * Upserts an activity for (habitId + date) for the current user.
   * If completions flips from 0 -> 1, increments progress for ACTIVE goals linked to that habit.
   */
  @Transactional
  public void createOrUpdateActivity(User user, ActivityDto dto) {

    if (dto.getDate().isAfter(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot log activity for the future!");
    }

    Habit habit = habitRepository.findById(dto.getHabitId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));

    // User isolation (404 – no guessing)
    if (habit.getUser() == null || !habit.getUser().getUserId().equals(user.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found");
    }

    Activity activity = activityRepository
      .findByHabit_IdAndDate(habit.getId(), dto.getDate())
      .orElseGet(() -> {
        Activity a = new Activity();
        a.setHabit(habit);
        a.setDate(dto.getDate());
        a.setCompletions(0);
        return a;
      });

    int before = activity.getCompletions();
    int after = dto.getCompletions();

    // Persist activity state (0/1) exactly as sent
    activity.setCompletions(after);
    activityRepository.save(activity);

    // ---- Goal hook: ONLY when 0 -> 1 ----
    if (before == 0 && after == 1) {
      List<Goal> linkedGoals = goalRepository.findByUser_UserIdAndHabit_IdAndStatus(
        user.getUserId(),
        habit.getId(),
        GoalStatus.ACTIVE
      );

      for (Goal g : linkedGoals) {
        g.setProgressCount(g.getProgressCount() + 1);

        Integer target = g.getTargetCount();
        if (target != null && g.getProgressCount() >= target) {
          g.setStatus(GoalStatus.COMPLETED);
          g.setCompletedAt(LocalDateTime.now());
        }
      }

      if (!linkedGoals.isEmpty()) {
        goalRepository.saveAll(linkedGoals);
      }
    }
    else if (before == 1 && after == 0) {
      List<Goal> goalsToRevert = goalRepository.findByUser_UserIdAndHabit_Id(
        user.getUserId(),
        habit.getId()
      );

      for (Goal g : goalsToRevert) {
        // Decrement progress
        if (g.getProgressCount() > 0) {
          g.setProgressCount(g.getProgressCount() - 1);
        }

        // Revert "COMPLETED" status if they dip below the target
        if (g.getStatus() == GoalStatus.COMPLETED && g.getTargetCount() != null && g.getProgressCount() < g.getTargetCount()) {
          g.setStatus(GoalStatus.ACTIVE);
          g.setCompletedAt(null);
        }
      }

      // Update the save check
      if (!goalsToRevert.isEmpty()) goalRepository.saveAll(goalsToRevert);
    }
  }
}
