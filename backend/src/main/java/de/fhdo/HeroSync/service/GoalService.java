package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.CreateGoalRequest;
import de.fhdo.HeroSync.dto.GoalDto;
import de.fhdo.HeroSync.dto.UpdateGoalRequest;
import de.fhdo.HeroSync.entity.Goal;
import de.fhdo.HeroSync.entity.GoalStatus;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.GoalRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for goal management.
 * Handles creation, updates, completion, deletion, and automatic deadline
 * evaluation (marking overdue goals as FAILED).
 */
@Service
public class GoalService {

  private final GoalRepository goalRepository;
  private final HabitRepository habitRepository;

  public GoalService(GoalRepository goalRepository, HabitRepository habitRepository) {
    this.goalRepository = goalRepository;
    this.habitRepository = habitRepository;
  }

  /**
   * Returns all goals for the user. Evaluates deadlines before returning so
   * any overdue ACTIVE goals are immediately marked FAILED.
   */
  public List<GoalDto> listAllDtos(User user) {
    evaluateDeadlines(user);
    return goalRepository.findByUser_UserId(user.getUserId())
      .stream()
      .map(this::toDto)
      .toList();
  }

  /**
   * Returns a single goal by ID. Evaluates deadlines before returning.
   * Throws 404 if the goal does not exist or is not owned by the user.
   */
  public GoalDto getDtoById(Long id, User user) {
    evaluateDeadlines(user);
    return toDto(requireOwnedGoal(id, user));
  }

  /**
   * Creates a new goal. If a habitId is provided, the goal is linked to that
   * habit and requires a targetCount. Solo goals (no habitId) have no targetCount.
   */
  public GoalDto create(CreateGoalRequest req, User user) {
    Goal goal = new Goal();
    goal.setUser(user);
    goal.setTitle(req.getTitle());
    goal.setDescription(req.getDescription());
    goal.setProgressCount(0);
    goal.setStatus(GoalStatus.ACTIVE);
    goal.setBoss(req.isBoss());
    goal.setBossType(req.getBossType());

    if (req.getHabitId() != null) {
      Habit habit = habitRepository.findById(req.getHabitId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));

      goal.setHabit(habit);

      if (req.getTargetCount() == null) {
        throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "targetCount is required for habit-linked goals"
        );
      }
      goal.setTargetCount(req.getTargetCount());
    } else {
      goal.setHabit(null);
      goal.setTargetCount(null);
    }

    goal.setDeadline(req.getDeadline());
    return toDto(goalRepository.save(goal));
  }

  /**
   * Updates mutable fields of an existing goal.
   * If the goal was previously FAILED, editing it re-activates it so the
   * user can retry without losing their linked habit association.
   */
  public GoalDto update(Long id, UpdateGoalRequest req, User user) {
    Goal goal = requireOwnedGoal(id, user);

    if (req.getTitle()       != null) goal.setTitle(req.getTitle());
    if (req.getDescription() != null) goal.setDescription(req.getDescription());
    if (req.getTargetCount() != null) goal.setTargetCount(req.getTargetCount());
    if (req.getDeadline()    != null) goal.setDeadline(req.getDeadline());

    if (goal.getStatus() == GoalStatus.FAILED) {
      goal.setStatus(GoalStatus.ACTIVE);
      goal.setFailedAt(null);
    }

    return toDto(goalRepository.save(goal));
  }

  /**
   * Manually completes a solo (non-habit-linked) goal.
   * Habit-linked goals are progressed automatically through activity tracking.
   * Throws 400 if the goal is habit-linked or already FAILED.
   */
  public GoalDto completeSoloGoal(Long id, User user) {
    Goal goal = requireOwnedGoal(id, user);

    if (goal.getHabit() != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only solo goals can be completed manually");
    }

    if (goal.getStatus() == GoalStatus.COMPLETED) {
      return toDto(goal);
    }

    if (goal.getStatus() == GoalStatus.FAILED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed goals must be retried (edit) before completion");
    }

    goal.setStatus(GoalStatus.COMPLETED);
    goal.setCompletedAt(LocalDateTime.now());
    return toDto(goalRepository.save(goal));
  }

  /** Permanently deletes a goal owned by the user. */
  public void delete(Long id, User user) {
    goalRepository.delete(requireOwnedGoal(id, user));
  }

  /**
   * Marks any ACTIVE goal whose deadline has passed as FAILED.
   * Persists only when at least one goal's status changed.
   */
  private void evaluateDeadlines(User user) {
    var now   = java.time.LocalDate.now();
    var goals = goalRepository.findByUser_UserId(user.getUserId());
    boolean changed = false;

    for (var g : goals) {
      if (g.getStatus() == GoalStatus.ACTIVE
        && g.getDeadline() != null
        && g.getDeadline().isBefore(now)) {

        g.setStatus(GoalStatus.FAILED);
        g.setFailedAt(LocalDateTime.now());
        changed = true;
      }
    }

    if (changed) {
      goalRepository.saveAll(goals);
    }
  }

  /**
   * Verifies the goal exists and belongs to the given user.
   * Returns 404 for both "not found" and "not owned" cases to
   * avoid leaking the existence of other users' goals.
   */
  private Goal requireOwnedGoal(Long id, User user) {
    Goal goal = goalRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

    if (!goal.getUser().getUserId().equals(user.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found");
    }
    return goal;
  }

  /** Converts a {@link Goal} entity to its DTO representation. */
  private GoalDto toDto(Goal goal) {
    return new GoalDto(
      goal.getId(),
      goal.getTitle(),
      goal.getDescription(),
      goal.getHabit() != null ? goal.getHabit().getId() : null,
      goal.getTargetCount(),
      goal.getProgressCount(),
      goal.getDeadline(),
      goal.getStatus(),
      goal.getCreatedAt(),
      goal.getCompletedAt(),
      goal.getFailedAt(),
      goal.isBoss(),
      goal.getBossType()
    );
  }
}
