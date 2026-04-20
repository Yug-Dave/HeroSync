package de.fhdo.HeroSync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateGoalRequest {

  @NotBlank(message = "Title must not be blank")
  @Size(max = 120, message = "Title must be at most 120 characters")
  private String title;

  @Size(max = 500, message = "Description must be at most 500 characters")
  private String description;

  // Optional: null = solo goal, otherwise habit-linked goal
  private Long habitId;

  // For habit-linked goals: required (we validate in service to avoid assumptions)
  private Integer targetCount;

  private LocalDate deadline;
  
  @com.fasterxml.jackson.annotation.JsonProperty("isBoss")
  private boolean isBoss;
  private String bossType;

  public CreateGoalRequest() {}

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Long getHabitId() { return habitId; }
  public void setHabitId(Long habitId) { this.habitId = habitId; }

  public Integer getTargetCount() { return targetCount; }
  public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }

  public LocalDate getDeadline() { return deadline; }
  public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

  public boolean isBoss() { return isBoss; }
  public void setBoss(boolean boss) { isBoss = boss; }

  public String getBossType() { return bossType; }
  public void setBossType(String bossType) { this.bossType = bossType; }
}
