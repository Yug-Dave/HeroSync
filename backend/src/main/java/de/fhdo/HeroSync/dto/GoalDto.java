package de.fhdo.HeroSync.dto;

import de.fhdo.HeroSync.entity.GoalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class  GoalDto {

  private Long id;
  private String title;
  private String description;

  private Long habitId; // null = solo goal

  private Integer targetCount; // null allowed for solo goals
  private int progressCount;

  private LocalDate deadline;
  private GoalStatus status;

  private LocalDateTime createdAt;
  private LocalDateTime completedAt;
  private LocalDateTime failedAt;
  
  @com.fasterxml.jackson.annotation.JsonProperty("isBoss")
  private boolean isBoss;
  private String bossType;

  public GoalDto() {}

  public GoalDto(
    Long id,
    String title,
    String description,
    Long habitId,
    Integer targetCount,
    int progressCount,
    LocalDate deadline,
    GoalStatus status,
    LocalDateTime createdAt,
    LocalDateTime completedAt,
    LocalDateTime failedAt,
    boolean isBoss,
    String bossType
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.habitId = habitId;
    this.targetCount = targetCount;
    this.progressCount = progressCount;
    this.deadline = deadline;
    this.status = status;
    this.createdAt = createdAt;
    this.completedAt = completedAt;
    this.failedAt = failedAt;
    this.isBoss = isBoss;
    this.bossType = bossType;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Long getHabitId() { return habitId; }
  public void setHabitId(Long habitId) { this.habitId = habitId; }

  public Integer getTargetCount() { return targetCount; }
  public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }

  public int getProgressCount() { return progressCount; }
  public void setProgressCount(int progressCount) { this.progressCount = progressCount; }

  public LocalDate getDeadline() { return deadline; }
  public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

  public GoalStatus getStatus() { return status; }
  public void setStatus(GoalStatus status) { this.status = status; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getCompletedAt() { return completedAt; }
  public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

  public LocalDateTime getFailedAt() { return failedAt; }
  public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }

  public boolean isBoss() { return isBoss; }
  public void setBoss(boolean boss) { isBoss = boss; }

  public String getBossType() { return bossType; }
  public void setBossType(String bossType) { this.bossType = bossType; }
}
