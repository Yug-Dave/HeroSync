package de.fhdo.HeroSync.dto;

public class DashboardDTO {
  private Long dashboardId;
  private String userName;
  private String avatar;
  private int currentStreak;
  private int totalXP;
  private int totalHabitsDone;

  public DashboardDTO(Long dashboardId, String userName, String avatar, int currentStreak, int totalXP, int totalHabitsDone) {
    this.dashboardId = dashboardId;
    this.userName = userName;
    this.avatar = avatar;
    this.currentStreak = currentStreak;
    this.totalXP = totalXP;
    this.totalHabitsDone = totalHabitsDone;
  }

  // Getters
  public Long getDashboardId() { return dashboardId; }
  public String getUserName() { return userName; }
  public String getAvatar() { return avatar; }
  public int getCurrentStreak() { return currentStreak; }
  public int getTotalXP() { return totalXP; }
  public int getTotalHabitsDone() { return totalHabitsDone; }

  @Override
  public String toString() {
    return "DashboardDTO{id=" + dashboardId + ", user='" + userName + "', avatar='" + avatar + "', streak=" + currentStreak + ", xp=" + totalXP + "}";
  }
}
