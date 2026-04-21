package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.repository.ActivityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
 @RequestMapping("/stats")
public class StatsRestController {

  private final UserRepository userRepository;
  private final ActivityRepository activityRepository;

  public StatsRestController(UserRepository userRepository, ActivityRepository activityRepository) {
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
  }

  @GetMapping("/xp-history")
  public ResponseEntity<List<Map<String, Object>>> getXpHistory(Authentication authentication) {
    User user = requireAuthenticatedUser(authentication);

    // Fetch REAL data from the database
    List<Map<String, Object>> history = activityRepository.getWeeklyXpHistory(user.getUserId());

    return ResponseEntity.ok(history);
  }

  private User requireAuthenticatedUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(UNAUTHORIZED, "Not authenticated");
    }

    String email;
    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails ud) {
      email = ud.getUsername();
    } else {
      email = principal.toString();
    }

    return userRepository.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
  }
}
