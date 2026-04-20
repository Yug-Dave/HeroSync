package de.fhdo.HeroSync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateHabitRequest {

  @NotBlank
  @Size(max = 120)
  private String name;

  @Size(max = 500)
  private String description;

  private Integer xpValue;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Integer getXpValue() { return xpValue; }
  public void setXpValue(Integer xpValue) { this.xpValue = xpValue; }
}
