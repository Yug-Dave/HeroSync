package de.fhdo.HeroSync.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateGoalRequest {

  @Size(max = 120, message = "Title must be at most 120 characters")
  private String title;

  @Size(max = 500, message = "Description must be at most 500 characters")
  private String description;

  // allow increasing target during retry (and general edits)
  private Integer targetCount;

  private LocalDate deadline;

  // NOTE: no progressCount here (we don't allow arbitrary writes from client)
  // NOTE: no status here (we'll control via service methods)

  public UpdateGoalRequest() {}

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Integer getTargetCount() { return targetCount; }
  public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }

  public LocalDate getDeadline() { return deadline; }
  public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}
