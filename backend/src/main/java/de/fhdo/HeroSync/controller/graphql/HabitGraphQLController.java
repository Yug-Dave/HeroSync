package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.CreateHabitRequest;
import de.fhdo.HeroSync.dto.HabitDto;
import de.fhdo.HeroSync.dto.UpdateHabitRequest;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class HabitGraphQLController {

  private final HabitService habitService;
  private final UserRepository userRepository;

  public HabitGraphQLController(HabitService habitService, UserRepository userRepository) {
    this.habitService = habitService;
    this.userRepository = userRepository;
  }

  // -------------------- Queries --------------------

  @QueryMapping
  public List<HabitDto> habits() {
    User user = requireCurrentUser();
    return habitService.listAllDtos(user);
  }

  @QueryMapping
  public HabitDto habitById(@Argument Long id) {
    User user = requireCurrentUser();
    return habitService.getDtoById(user, id);
  }

  // -------------------- Mutations --------------------

  @MutationMapping
  public HabitDto createHabit(@Argument("input") @Valid CreateHabitRequest input) {
    User user = requireCurrentUser();
    return habitService.createDto(user, input.getName(), input.getDescription(), input.getXpValue());
  }

  @MutationMapping
  public HabitDto updateHabit(@Argument Long id, @Argument("input") UpdateHabitRequest input) {
    User user = requireCurrentUser();

    // Partial update merge
    HabitDto existing = habitService.getDtoById(user, id);

    String name = input.getName() != null ? input.getName() : existing.getName();
    String description = input.getDescription() != null ? input.getDescription() : existing.getDescription();
    boolean archived = input.getArchived() != null ? input.getArchived() : existing.isArchived();
    Integer xpValue = input.getXpValue() != null ? input.getXpValue() : existing.getXpValue();

    return habitService.updateDto(user, id, name, description, archived, xpValue);
  }

  @MutationMapping
  public Boolean deleteHabit(@Argument Long id) {
    User user = requireCurrentUser();
    habitService.delete(user, id);
    return true;
  }

  // -------------------- Auth helper (same as ProfileGraphQLController) --------------------

  private User requireCurrentUser() {
    User user = getCurrentUser();
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
    return user;
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }

    Object principal = auth.getPrincipal();
    if (principal == null || "anonymousUser".equals(principal)) {
      return null;
    }

    String email;
    if (principal instanceof UserDetails ud) {
      email = ud.getUsername();
    } else {
      email = principal.toString();
    }

    return userRepository.findByEmail(email).orElse(null);
  }
}
