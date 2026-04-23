package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.HabitDto;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.HabitRepository;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.GoalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for habit management.
 * Enforces ownership checks so users can only access and mutate their own habits.
 */
@Service
public class HabitService {

  private final HabitRepository repo;
  private final ActivityRepository activityRepo;
  private final GoalRepository goalRepo;

  public HabitService(HabitRepository repo, ActivityRepository activityRepo, GoalRepository goalRepo) {
    this.repo = repo;
    this.activityRepo = activityRepo;
    this.goalRepo = goalRepo;
  }

  /** Returns all habits (active and archived) for the given user as DTOs. */
  public List<HabitDto> listAllDtos(User user) {
    return repo.findByUser_UserId(user.getUserId()).stream().map(this::toDto).toList();
  }

  /** Returns a single habit DTO, enforcing that it belongs to the given user. */
  public HabitDto getDtoById(User user, Long id) {
    return toDto(requireHabitOwned(user, id));
  }

  /** Creates and persists a new habit for the given user. */
  public HabitDto createDto(User user, String name, String description, Integer xpValue) {
    Habit created = new Habit();
    created.setUser(user);
    created.setName(requireNonBlank(name, "Name must not be blank"));
    created.setDescription(description);
    created.setArchived(false);
    created.setCreatedAt(LocalDateTime.now());
    created.setXpValue(xpValue != null ? xpValue : 80);

    return toDto(repo.save(created));
  }

  /** Updates all mutable fields of an existing habit. */
  public HabitDto updateDto(User user, Long id, String name, String description, boolean archived, Integer xpValue) {
    Habit existing = requireHabitOwned(user, id);

    existing.setName(requireNonBlank(name, "Name must not be blank"));
    existing.setDescription(description);
    existing.setArchived(archived);
    if (xpValue != null) existing.setXpValue(xpValue);

    return toDto(repo.save(existing));
  }

  /**
   * Deletes a habit along with its activity records.
   * Goals linked to the habit are converted to standalone goals instead of
   * being deleted, preserving the user's progress history.
   */
  @Transactional
  public void delete(User user, Long id) {
    Habit habit = requireHabitOwned(user, id);

    goalRepo.findByUser_UserIdAndHabit_Id(user.getUserId(), habit.getId())
        .forEach(g -> {
            g.setHabit(null);
            goalRepo.save(g);
        });

    activityRepo.deleteByHabit_Id(habit.getId());
    repo.delete(habit);
  }

  /**
   * Looks up a habit by ID and verifies the requesting user owns it.
   * Returns 404 for both missing habits and ownership mismatches to
   * avoid leaking the existence of other users' data.
   */
  private Habit requireHabitOwned(User user, Long id) {
    Habit habit = repo.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));
    if (habit.getUser() == null || !habit.getUser().getUserId().equals(user.getUserId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found");
    }
    return habit;
  }

  /** Validates that a string field is non-null and non-blank. */
  private String requireNonBlank(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    return value.trim();
  }

  /** Converts a {@link Habit} entity to its DTO representation. */
  private HabitDto toDto(Habit h) {
    return new HabitDto(
      h.getId(),
      h.getName(),
      h.getDescription(),
      h.isArchived(),
      h.getCreatedAt(),
      h.getXpValue()
    );
  }
}
