package de.fhdo.HeroSync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for the POST /api/auth/login endpoint.
 * Bean Validation annotations enforce that both fields are present
 * and that the email is a syntactically valid address before the
 * request even reaches the service layer.
 */
public class LoginRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Must be a valid email address")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  public LoginRequest() {}

  public String getEmail() { return email; }
  public String getPassword() { return password; }

  public void setEmail(String email) { this.email = email; }
  public void setPassword(String password) { this.password = password; }
}
