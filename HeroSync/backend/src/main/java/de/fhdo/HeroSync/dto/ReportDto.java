package de.fhdo.HeroSync.dto;

import java.time.LocalDate;
import java.util.Map;

public record ReportDto(
  String period,                 // "WEEKLY"
  LocalDate startDate,           // Monday of the week
  LocalDate endDate,             // Sunday of the week
  int totalCompletions,          // sum of completions in the window
  int activeHabits,              // distinct habits that have entries
  Map<LocalDate, Integer> completionsPerDay // 7 entries, by date
) {}
