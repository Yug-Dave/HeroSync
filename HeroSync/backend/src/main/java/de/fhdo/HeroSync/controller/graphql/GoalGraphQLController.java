package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.CreateGoalRequest;
import de.fhdo.HeroSync.dto.GoalDto;
import de.fhdo.HeroSync.dto.UpdateGoalRequest;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.GoalService;
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
public class  GoalGraphQLController {

  private final GoalService goalService;
  private final UserRepository userRepository;

  public GoalGraphQLController(GoalService goalService, UserRepository userRepository) {
    this.goalService = goalService;
    this.userRepository = userRepository;
  }

  //Queries

  @QueryMapping
  public List<GoalDto> goals() {
    User user = requireCurrentUser();
    return goalService.listAllDtos(user);
  }

  @QueryMapping
  public GoalDto goalById(@Argument Long id) {
    User user = requireCurrentUser();
    return goalService.getDtoById(id, user);
  }

  //Mutations

  @MutationMapping
  public GoalDto createGoal(@Argument("input") @Valid CreateGoalRequest input) {
    User user = requireCurrentUser();
    return goalService.create(input, user);
  }

  @MutationMapping
  public GoalDto updateGoal(@Argument Long id, @Argument("input") UpdateGoalRequest input) {
    User user = requireCurrentUser();
    return goalService.update(id, input, user);
  }

  @MutationMapping
  public Boolean deleteGoal(@Argument Long id) {
    User user = requireCurrentUser();
    goalService.delete(id, user);
    return true;
  }

  @MutationMapping
  public GoalDto completeSoloGoal(@Argument Long id) {
    User user = requireCurrentUser();
    return goalService.completeSoloGoal(id, user);
  }

  //Auth helper

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
