package de.fhdo.HeroSync.dto;

public class ProfileDto {

  private Long userId;
  private String name;
  private String email;
  private String bio;
  private String avatar;
  private String companionChoice;
  private String aiProvider;

  public ProfileDto() {}

  public ProfileDto(Long userId, String name, String email, String bio, String avatar,
                    String companionChoice, String aiProvider) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.bio = bio;
    this.avatar = avatar;
    this.companionChoice = companionChoice;
    this.aiProvider = aiProvider;
  }

  public Long getUserId() { return userId; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getBio() { return bio; }
  public String getAvatar() { return avatar; }
  public String getCompanionChoice() { return companionChoice; }
  public String getAiProvider() { return aiProvider; }

  public void setUserId(Long userId) { this.userId = userId; }
  public void setName(String name) { this.name = name; }
  public void setEmail(String email) { this.email = email; }
  public void setBio(String bio) { this.bio = bio; }
  public void setAvatar(String avatar) { this.avatar = avatar; }
  public void setCompanionChoice(String companionChoice) { this.companionChoice = companionChoice; }
  public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }
}

