package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.CreateGoalRequest;
import de.fhdo.HeroSync.dto.GoalDto;
import de.fhdo.HeroSync.dto.UpdateGoalRequest;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing user goals.
 * Supports listing, creation, completion, update, and deletion.
 * All endpoints are session-protected and scoped to the authenticated user.
 */
@RestController
@RequestMapping("/api/goals")
public class GoalRestController {

  private final GoalService goalService;
  private final UserRepository userRepository;

  public GoalRestController(GoalService goalService, UserRepository userRepository) {
    this.goalService = goalService;
    this.userRepository = userRepository;
  }

  /** Returns all goals belonging to the authenticated user. */
  @GetMapping
  public List<GoalDto> list(Principal principal) {
    return goalService.listAllDtos(requireCurrentUser(principal));
  }

  /** Returns a single goal by ID, scoped to the authenticated user. */
  @GetMapping("/{id}")
  public GoalDto get(@PathVariable Long id, Principal principal) {
    return goalService.getDtoById(id, requireCurrentUser(principal));
  }

  /** Creates a new goal for the authenticated user. */
  @PostMapping
  public GoalDto create(
    @Valid @RequestBody CreateGoalRequest request,
    Principal principal
  ) {
    return goalService.create(request, requireCurrentUser(principal));
  }

  /**
   * Marks a solo (non-habit-linked) goal as completed.
   * Habit-linked goals are completed automatically via activity tracking.
   */
  @PostMapping("/{id}/complete")
  public GoalDto completeSolo(@PathVariable Long id, Principal principal) {
    return goalService.completeSoloGoal(id, requireCurrentUser(principal));
  }

  /** Updates mutable fields (title, description, targetCount, deadline) of an existing goal. */
  @PutMapping("/{id}")
  public GoalDto update(
    @PathVariable Long id,
    @Valid @RequestBody UpdateGoalRequest request,
    Principal principal
  ) {
    return goalService.update(id, request, requireCurrentUser(principal));
  }

  /** Permanently deletes a goal owned by the authenticated user. */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, Principal principal) {
    goalService.delete(id, requireCurrentUser(principal));
  }

  /**
   * Resolves the current user from the session principal.
   * Throws 401 Unauthorized if the principal is missing or the email is not found.
   */
  private User requireCurrentUser(Principal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return userRepository.findByEmail(principal.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
  }
}
