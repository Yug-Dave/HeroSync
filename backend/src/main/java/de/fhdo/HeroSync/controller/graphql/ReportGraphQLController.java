package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.ReportDto;

import java.util.List;

public class ReportGraphQLController {
  private final String period;
  private final String startDate;
  private final String endDate;
  private final int totalCompletions;
  private final int activeHabits;
  private final List<DailyCompletionGql> completionsPerDay;

  public ReportGraphQLController(String period, String startDate, String endDate,
                                 int totalCompletions, int activeHabits,
                                 List<DailyCompletionGql> completionsPerDay) {
    this.period = period;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalCompletions = totalCompletions;
    this.activeHabits = activeHabits;
    this.completionsPerDay = completionsPerDay;
  }

  public String getPeriod() { return period; }
  public String getStartDate() { return startDate; }
  public String getEndDate() { return endDate; }
  public int getTotalCompletions() { return totalCompletions; }
  public int getActiveHabits() { return activeHabits; }
  public List<DailyCompletionGql> getCompletionsPerDay() { return completionsPerDay; }

  public static ReportGraphQLController from(ReportDto dto) {
    var perDay = dto.completionsPerDay().entrySet().stream()
      .sorted(java.util.Map.Entry.comparingByKey())
      .map(e -> new DailyCompletionGql(e.getKey().toString(), e.getValue()))
      .toList();

    return new ReportGraphQLController(
      dto.period(),
      dto.startDate().toString(),
      dto.endDate().toString(),
      dto.totalCompletions(),
      dto.activeHabits(),
      perDay
    );
  }

  public static class DailyCompletionGql {
    private final String date;
    private final int completions;

    public DailyCompletionGql(String date, int completions) {
      this.date = date;
      this.completions = completions;
    }

    public String getDate() { return date; }
    public int getCompletions() { return completions; }
  }
}
