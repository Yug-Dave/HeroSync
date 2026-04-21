package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.ReportDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping(
    value = "/reports",
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
)
public class ReportRestController {

  private final ReportService reportService;
  private final UserRepository userRepository;

  public ReportRestController(ReportService reportService, UserRepository userRepository) {
    this.reportService = reportService;
    this.userRepository = userRepository;
  }

  private User requireCurrentUser(Principal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }

  @GetMapping("/weekly")
  public ReportDto getWeeklyReport(
      Principal principal,
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    User user = requireCurrentUser(principal);
    return reportService.weekly(user, date);
  }

  @GetMapping("/monthly")
  public ReportDto getMonthlyReport(
      Principal principal,
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    User user = requireCurrentUser(principal);
    return reportService.monthly(user, year, month);
  }
}
