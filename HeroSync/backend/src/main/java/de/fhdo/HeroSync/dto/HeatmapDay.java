package de.fhdo.HeroSync.dto;

import java.time.LocalDate;

public class HeatmapDay {
  private LocalDate date;
  private int totalCompletions;

  public HeatmapDay(LocalDate date, int totalCompletions) {
    this.date = date;
    this.totalCompletions = totalCompletions;
  }

  public LocalDate getDate() {
    return date;
  }

  public int getTotalCompletions() {
    return totalCompletions;
  }
}
