package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.CreateHabitRequest;
import de.fhdo.HeroSync.dto.HabitDto;
import de.fhdo.HeroSync.dto.UpdateHabitRequest;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.security.Principal;
import java.util.List;

/**
 * REST controller for CRUD operations on habits.
 * All endpoints are session-protected and operate only on data owned by the
 * authenticated user.
 */
@RestController
@RequestMapping(
  value = "/api/habits",
  produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
)
@SuppressWarnings("null")
public class HabitRestController {

  private final HabitService habitService;
  private final UserRepository userRepository;

  public HabitRestController(HabitService habitService, UserRepository userRepository) {
    this.habitService = habitService;
    this.userRepository = userRepository;
  }

  /** Returns all habits (active and archived) for the authenticated user. */
  @GetMapping
  public List<HabitDto> list(Principal principal) {
    return habitService.listAllDtos(requireCurrentUser(principal));
  }

  /** Returns a single habit by ID, enforcing ownership. */
  @GetMapping("/{id}")
  public HabitDto getById(Principal principal, @PathVariable Long id) {
    return habitService.getDtoById(requireCurrentUser(principal), id);
  }

  /**
   * Creates a new habit and returns its representation.
   * Responds with 201 Created and a Location header pointing to the new resource.
   */
  @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public ResponseEntity<HabitDto> create(
    Principal principal,
    @Valid @RequestBody CreateHabitRequest req
  ) {
    User user = requireCurrentUser(principal);
    HabitDto created = habitService.createDto(user, req.getName(), req.getDescription(), req.getXpValue());
    URI location = URI.create("/api/habits/" + created.getId());
    return ResponseEntity.created(location).body(created);
  }

  /**
   * Partially updates a habit; any field not included in the request body
   * retains its existing value.
   */
  @PutMapping(value = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public ResponseEntity<HabitDto> updatePartial(
    Principal principal,
    @PathVariable Long id,
    @RequestBody UpdateHabitRequest req
  ) {
    User user = requireCurrentUser(principal);
    HabitDto existing = habitService.getDtoById(user, id);

    String  name        = req.getName()        != null ? req.getName()        : existing.getName();
    String  description = req.getDescription() != null ? req.getDescription() : existing.getDescription();
    boolean archived    = req.getArchived()    != null ? req.getArchived()    : existing.isArchived();
    Integer xpValue     = req.getXpValue()     != null ? req.getXpValue()     : existing.getXpValue();

    return ResponseEntity.ok(habitService.updateDto(user, id, name, description, archived, xpValue));
  }

  /**
   * Deletes a habit and its associated activities.
   * Any goal linked to this habit is converted to a standalone (solo) goal.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(Principal principal, @PathVariable Long id) {
    habitService.delete(requireCurrentUser(principal), id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Resolves the authenticated user from the session principal.
   * Throws 401 Unauthorized if the principal is absent or the user cannot be found.
   */
  private User requireCurrentUser(Principal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return userRepository.findByEmail(principal.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }
}
