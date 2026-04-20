package de.fhdo.HeroSync.dto;

/**
 * Simple request body carrying only an email address, used by the
 * resend-verification and future password-reset endpoints.
 */
public class EmailRequest {

  private String email;

  public EmailRequest() {}

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
