package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.AchievementDto;
import de.fhdo.HeroSync.dto.ReportDto;
import de.fhdo.HeroSync.dto.UnlockedAchievementDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.AchievementService;
import de.fhdo.HeroSync.service.ReportService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Controller
public class ReportsAchievementsGraphQLController {

  private final AchievementService achievementService;
  private final ReportService reportService;
  private final UserRepository userRepository;

  public ReportsAchievementsGraphQLController(AchievementService achievementService,
                                             ReportService reportService,
                                             UserRepository userRepository) {
    this.achievementService = achievementService;
    this.reportService = reportService;
    this.userRepository = userRepository;
  }

  private User requireUser(Principal principal) {
    if (principal == null) throw new RuntimeException("Not authenticated");
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  // --- Achievements ---

  @QueryMapping
  public List<AchievementDto> achievements() {
    return achievementService.getAllAchievements();
  }

  @QueryMapping
  public List<UnlockedAchievementDto> unlockedAchievements(Principal principal) {
    User user = requireUser(principal);
    return achievementService.listUnlocked(user);
  }

  @MutationMapping
  public List<UnlockedAchievementDto> evaluateAchievements(Principal principal) {
    User user = requireUser(principal);
    return achievementService.evaluateAndUnlock(user);
  }

  // --- Reports ---

  @QueryMapping
  public ReportDto weeklyReport(@Argument String date, Principal principal) {
    User user = requireUser(principal);
    return reportService.weekly(user, parseIsoDate(date));
  }

  @QueryMapping
  public ReportDto monthlyReport(@Argument int year, @Argument int month, Principal principal) {
    User user = requireUser(principal);
    return reportService.monthly(user, year, month);
  }

  @SchemaMapping(typeName = "Report", field = "completionsPerDay")
  public List<Map<String, Object>> completionsPerDay(ReportDto report) {
    return report.completionsPerDay().entrySet().stream()
        .map(e -> Map.<String, Object>of(
            "date", e.getKey().toString(),
            "completions", e.getValue()
        ))
        .toList();
  }

  private LocalDate parseIsoDate(String iso) {
    try {
      return LocalDate.parse(iso);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Invalid date. Use yyyy-MM-dd");
    }
  }
}
