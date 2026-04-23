package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.AchievementDto;
import de.fhdo.HeroSync.dto.UnlockedAchievementDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.AchievementService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
  value = "/achievements",
  produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
)
public class AchievementRestController {

  private final AchievementService achievementService;
  private final UserRepository userRepository;

  public AchievementRestController(AchievementService achievementService, UserRepository userRepository) {
    this.achievementService = achievementService;
    this.userRepository = userRepository;
  }

  //Helper

  private User requireCurrentUser(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return userRepository.findByEmail(auth.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
  }

  //REST enpoints

  @GetMapping
  public List<AchievementDto> getAllAchievements(Authentication auth) {
    var user = requireCurrentUser(auth);
    return achievementService.getFilteredAchievements(user);
  }

  //  Auto-evaluate on read (dashboard loads automatically)
  @GetMapping("/unlocked")
  public List<UnlockedAchievementDto> getUnlockedAchievements(Authentication auth) {
    var user = requireCurrentUser(auth);
    achievementService.evaluateAndUnlock(user);
    return achievementService.listUnlocked(user);
  }

  /**
   * Manually triggers achievement evaluation for the current user.
   */
  @PostMapping("/evaluate")
  public List<UnlockedAchievementDto> evaluateAchievements(Authentication auth) {
    var user = requireCurrentUser(auth);
    return achievementService.evaluateAndUnlock(user);
  }
}
