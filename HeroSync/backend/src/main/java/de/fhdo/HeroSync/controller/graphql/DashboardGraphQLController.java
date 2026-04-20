package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.dto.DashboardDTO;
import de.fhdo.HeroSync.dto.HeatmapDay;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.service.ActivityService;
import de.fhdo.HeroSync.service.DashboardService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardGraphQLController {

  private final DashboardService dashboardService;
  private final ActivityService activityService;

  public DashboardGraphQLController(DashboardService dashboardService, ActivityService activityService) {
    this.dashboardService = dashboardService;
    this.activityService = activityService;
  }

  @QueryMapping
  public DashboardDTO dashboard(@Argument String id, Principal principal) {
    if (principal == null) throw new RuntimeException("Unauthorized");
    User user = dashboardService.findUserByEmail(principal.getName());
    
    List<ActivityDto> activities = dashboardService.getActivitiesByUser(user.getUserId());
    int streak = dashboardService.calculateCurrentStreak(activities);
    int totalXP = dashboardService.calculateTotalXP(user.getUserId(), streak);
    String avatar = (user.getProfile() != null) ? user.getProfile().getAvatar() : null;

    return new DashboardDTO(user.getUserId(), user.getName(), avatar, streak, totalXP);
  }

  @QueryMapping
  public List<ActivityDto> activities(Principal principal) {
    if (principal == null) throw new RuntimeException("Unauthorized");
    User user = dashboardService.findUserByEmail(principal.getName());
    return dashboardService.getActivitiesByUser(user.getUserId());
  }

  @QueryMapping
  public List<HeatmapDay> heatmap(Principal principal) {
    if (principal == null) throw new RuntimeException("Unauthorized");
    User user = dashboardService.findUserByEmail(principal.getName());
    return dashboardService.getHeatmapData(user.getUserId());
  }

  @QueryMapping
  public List<Long> habitsCompletedToday(Principal principal) {
    if (principal == null) throw new RuntimeException("Unauthorized");
    User user = dashboardService.findUserByEmail(principal.getName());
    return dashboardService.getHabitIdsCompletedToday(user.getUserId()).stream().toList();
  }

  @MutationMapping
  public Boolean addActivity(@Argument ActivityInput input, Principal principal) {
    if (principal == null) throw new RuntimeException("Unauthorized");

    User user = dashboardService.findUserByEmail(principal.getName());

    ActivityDto dto = new ActivityDto(
      LocalDate.parse(input.date),
      input.completions,
      Long.parseLong(input.habitId),
      null,
      0
    );

    activityService.createOrUpdateActivity(user, dto);
    return true;
  }

  record ActivityInput(String date, int completions, String habitId) {}
}
