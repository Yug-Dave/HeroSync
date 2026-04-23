package de.fhdo.HeroSync.dto;

import java.time.LocalDate;

public class ActivityDto {
  private LocalDate date;
  private int completions;
  private Long habitId;
  private String habitName;
  private int xpValue;

  public ActivityDto() {} // REQUIRED FOR SPRING TO WORK

  public ActivityDto(LocalDate date, int completions, Long habitId, String habitName, int xpValue) {
    this.date = date;
    this.completions = completions;
    this.habitId = habitId;
    this.habitName = habitName;
    this.xpValue = xpValue;
  }

  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }

  public int getCompletions() { return completions; }
  public void setCompletions(int completions) { this.completions = completions; }

  public Long getHabitId() { return habitId; }
  public void setHabitId(Long habitId) { this.habitId = habitId; }

  public String getHabitName() { return habitName; }
  public void setHabitName(String habitName) { this.habitName = habitName; }

  public int getXpValue() { return xpValue; }
  public void setXpValue(int xpValue) { this.xpValue = xpValue; }
}
