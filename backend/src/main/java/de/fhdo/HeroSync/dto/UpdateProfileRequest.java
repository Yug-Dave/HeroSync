package de.fhdo.HeroSync.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

  @NotBlank
  private String name;

  private String bio;
  private String avatar;

  public UpdateProfileRequest() {}

  public UpdateProfileRequest(String name, String bio, String avatar) {
    this.name = name;
    this.bio = bio;
    this.avatar = avatar;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getBio() { return bio; }
  public void setBio(String bio) { this.bio = bio; }

  public String getAvatar() { return avatar; }
  public void setAvatar(String avatar) { this.avatar = avatar; }
}
