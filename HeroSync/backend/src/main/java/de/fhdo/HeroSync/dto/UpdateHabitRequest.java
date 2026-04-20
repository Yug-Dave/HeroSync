package de.fhdo.HeroSync.dto;

public class UpdateHabitRequest {

  private String name;
  private String description;
  private Boolean archived;
  private Integer xpValue;

  public UpdateHabitRequest() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Integer getXpValue() { return xpValue; }
  public void setXpValue(Integer xpValue) { this.xpValue = xpValue; }
}
