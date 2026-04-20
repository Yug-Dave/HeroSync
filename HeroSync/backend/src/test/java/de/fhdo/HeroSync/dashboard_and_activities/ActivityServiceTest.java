package de.fhdo.HeroSync.dashboard_and_activities;

import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.entity.Activity;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.entity.Goal;
import de.fhdo.HeroSync.entity.GoalStatus;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.GoalRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import de.fhdo.HeroSync.service.ActivityService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

  @Mock private ActivityRepository activityRepository;
  @Mock private HabitRepository habitRepository;
  @Mock private GoalRepository goalRepository;

  @InjectMocks
  private ActivityService activityService;

  //Should save activity successfully when the user owns the habit
  @Test
  @DisplayName("Successfully save activity when the user owns the habit")
  void createOrUpdateActivity_WhenUserIsOwner_ShouldSaveActivity() {
    Long userId = 1L;
    Long habitId = 10L;
    LocalDate today = LocalDate.now();

    User owner = mock(User.class);
    when(owner.getUserId()).thenReturn(userId);

    Habit habit = new Habit();
    habit.setUser(owner);

    ActivityDto dto = new ActivityDto(today, 1, habitId, "Test Habit", 0);

    when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
    when(activityRepository.findByHabit_IdAndDate(any(), any())).thenReturn(Optional.empty());

    activityService.createOrUpdateActivity(owner, dto);

    verify(activityRepository).save(any(Activity.class));
  }

  //Should throw 404 Not Found when a user tries to modify another's habit
  @Test
  @DisplayName("User Isolation")
  void createOrUpdateActivity_WhenUserIsNotOwner_ShouldThrow404() {
    Long ownerId = 1L;
    Long attackerId = 99L;
    Long habitId = 10L;

    User owner = mock(User.class);
    when(owner.getUserId()).thenReturn(ownerId);

    User attacker = mock(User.class);
    when(attacker.getUserId()).thenReturn(attackerId);

    Habit habit = new Habit();
    habit.setUser(owner);

    ActivityDto dto = new ActivityDto(LocalDate.now(), 1, habitId, "Alice's Habit", 0);

    when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      activityService.createOrUpdateActivity(attacker, dto);
    });

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(activityRepository, never()).save(any());
  }

  //Should decrease goal progress when a habit is unchecked (1 -> 0)
  @Test
  @DisplayName("Should decrease goal progress when a habit is unchecked (1 -> 0)")
  void createOrUpdateActivity_WhenUnchecked_ShouldDecreaseGoalProgress() {
    Long userId = 1L;
    Long habitId = 10L;
    LocalDate today = LocalDate.now();

    User owner = mock(User.class);
    when(owner.getUserId()).thenReturn(userId);

    Habit habit = new Habit();
    habit.setUser(owner);
    habit.setId(habitId);

    Goal goal = new Goal();
    goal.setTargetCount(5);
    goal.setProgressCount(1);
    goal.setStatus(GoalStatus.ACTIVE);

    Activity existingActivity = new Activity();
    existingActivity.setHabit(habit);
    existingActivity.setCompletions(1);
    existingActivity.setDate(today);

    when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
    when(activityRepository.findByHabit_IdAndDate(eq(habitId), eq(today)))
      .thenReturn(Optional.of(existingActivity));
    when(goalRepository.findByUser_UserIdAndHabit_Id(eq(userId), eq(habitId)))
      .thenReturn(List.of(goal));

    ActivityDto dto = new ActivityDto(today, 0, habitId, "Test Habit", 0);
    activityService.createOrUpdateActivity(owner, dto);

    assertEquals(0, goal.getProgressCount());
  }

  //Should revert goal status from COMPLETED to ACTIVE when progress drops below target
  @Test
  @DisplayName("Should revert goal status")
  void createOrUpdateActivity_WhenGoalReverted_ShouldResetStatusToActive() {
    Long userId = 1L;
    Long habitId = 10L;
    LocalDate today = LocalDate.now();

    User owner = mock(User.class);
    when(owner.getUserId()).thenReturn(userId);

    Habit habit = new Habit();
    habit.setUser(owner);
    habit.setId(habitId);

    Goal goal = new Goal();
    goal.setTargetCount(5);
    goal.setProgressCount(5);
    goal.setStatus(GoalStatus.COMPLETED);
    goal.setCompletedAt(java.time.LocalDateTime.now());

    Activity existingActivity = new Activity();
    existingActivity.setHabit(habit);
    existingActivity.setCompletions(1);
    existingActivity.setDate(today);

    when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
    when(activityRepository.findByHabit_IdAndDate(eq(habitId), eq(today)))
      .thenReturn(Optional.of(existingActivity));
    when(goalRepository.findByUser_UserIdAndHabit_Id(eq(userId), eq(habitId)))
      .thenReturn(List.of(goal));

    ActivityDto dto = new ActivityDto(today, 0, habitId, "Test Habit", 0);
    activityService.createOrUpdateActivity(owner, dto);

    assertEquals(4, goal.getProgressCount());
    assertEquals(GoalStatus.ACTIVE, goal.getStatus());
    assertNull(goal.getCompletedAt());
  }

  //Should NOT increment goal progress if activity is already completed (1 -> 1)
  @Test
  @DisplayName("Should NOT increment goal progress if activity is already completed")
  void createOrUpdateActivity_WhenAlreadyCompleted_ShouldDoNothingToGoals() {
    Long userId = 1L;
    Long habitId = 10L;
    LocalDate today = LocalDate.now();

    User owner = mock(User.class);
    when(owner.getUserId()).thenReturn(userId);

    Habit habit = new Habit();
    habit.setUser(owner);
    habit.setId(habitId);

    // Goal is already at 1
    Goal goal = new Goal();
    goal.setTargetCount(5);
    goal.setProgressCount(1);
    goal.setStatus(GoalStatus.ACTIVE);

    // Activity ALREADY exists as completed (1)
    Activity existingActivity = new Activity();
    existingActivity.setHabit(habit);
    existingActivity.setCompletions(1);
    existingActivity.setDate(today);

    when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));
    when(activityRepository.findByHabit_IdAndDate(eq(habitId), eq(today)))
      .thenReturn(Optional.of(existingActivity));

    // Act: Send "1" again
    ActivityDto dto = new ActivityDto(today, 1, habitId, "Test Habit", 0);
    activityService.createOrUpdateActivity(owner, dto);

    // Assert
    assertEquals(1, goal.getProgressCount());
    verify(goalRepository, never()).saveAll(any());
  }
}
