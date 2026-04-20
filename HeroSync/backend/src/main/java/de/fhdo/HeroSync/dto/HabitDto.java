package de.fhdo.HeroSync.dto;

import java.time.LocalDateTime;

public class HabitDto {
  private Long id;
  private String name;
  private String description;
  private boolean archived;
  private LocalDateTime createdAt;
  private int xpValue;

  public HabitDto() { }

  public HabitDto(Long id, String name, String description, boolean archived, LocalDateTime createdAt, int xpValue) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.archived = archived;
    this.createdAt = createdAt;
    this.xpValue = xpValue;
  }

  public int getXpValue() { return xpValue; }
  public void setXpValue(int xpValue) { this.xpValue = xpValue; }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public boolean isArchived() { return archived; }
  public void setArchived(boolean archived) { this.archived = archived; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

