package de.fhdo.HeroSync.dto;

import java.time.LocalDate;

public class ActivityDto {
  private LocalDate date;
  private int completions;
  private Long habitId;
  private String habitName;
  private int xpValue;

  public ActivityDto(LocalDate date, int completions, Long habitId, String habitName, int xpValue) {
    this.date = date;
    this.completions = completions;
    this.habitId = habitId;
    this.habitName = habitName;
    this.xpValue = xpValue;
  }

  public LocalDate getDate() { return date; }
  public int getCompletions() { return completions; }
  public Long getHabitId() { return habitId; }
  public String getHabitName() { return habitName; }
  public int getXpValue() { return xpValue; }
}
