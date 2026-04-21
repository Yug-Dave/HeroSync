package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.dto.DashboardDTO;
import de.fhdo.HeroSync.dto.HeatmapDay;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.service.ActivityService;
import de.fhdo.HeroSync.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * REST controller providing dashboard summary, activity log, and heatmap data
 * for the authenticated user.
 */
@RestController
@RequestMapping("")
public class DashboardRestController {

  private final DashboardService dashboardService;
  private final ActivityService activityService;

  public DashboardRestController(DashboardService dashboardService, ActivityService activityService) {
    this.dashboardService = dashboardService;
    this.activityService = activityService;
  }

  /**
   * Returns a summary of the user's dashboard: name, avatar, current streak,
   * and total accumulated XP.
   */
  @GetMapping(value = "/dashboard", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public ResponseEntity<DashboardDTO> getDashboard(Principal principal) {
    User user = requirePrincipal(principal);

    String avatar = (user.getProfile() != null) ? user.getProfile().getAvatar() : null;
    List<ActivityDto> activities = dashboardService.getActivitiesByUser(user.getUserId());
    int streak   = dashboardService.calculateCurrentStreak(activities);
    int totalXP  = dashboardService.calculateTotalXP(user.getUserId(), streak);

    // Trigger dynamic achievement generation
    int level = Math.max(1, (totalXP / 2000) + 1);
    dashboardService.triggerAchievementGeneration(user, level);

    return ResponseEntity.ok(new DashboardDTO(user.getUserId(), user.getName(), avatar, streak, totalXP));
  }

  /**
   * Returns all activity records for the authenticated user.
   */
  @GetMapping(value = "/activities", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public ResponseEntity<List<ActivityDto>> getAllActivities(Principal principal) {
    User user = requirePrincipal(principal);
    return ResponseEntity.ok(dashboardService.getActivitiesByUser(user.getUserId()));
  }

  /**
   * Creates or updates an activity record for a given habit and date.
   * If an entry for the same habit and date already exists, its completion
   * count is updated; otherwise a new record is created.
   */
  @PostMapping(value = "/activities", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public ResponseEntity<String> addActivity(Principal principal, @RequestBody ActivityDto activityDto) {
    User user = requirePrincipal(principal);
    activityService.createOrUpdateActivity(user, activityDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Activity saved successfully");
  }

  /**
   * Returns 365 days of heatmap data for the authenticated user, used to
   * render the contribution-style activity grid on the dashboard.
   */
  @GetMapping(value = "/heatmap", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public ResponseEntity<List<HeatmapDay>> getHeatmap(Principal principal) {
    User user = requirePrincipal(principal);
    return ResponseEntity.ok(dashboardService.getHeatmapData(user.getUserId()));
  }

  /**
   * Resolves the current user from the session principal, throwing 401
   * if the request is unauthenticated.
   */
  private User requirePrincipal(Principal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return dashboardService.findUserByEmail(principal.getName());
  }
}
