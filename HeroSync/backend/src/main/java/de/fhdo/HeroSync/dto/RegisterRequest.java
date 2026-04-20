package de.fhdo.HeroSync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/auth/register.
 * Validation annotations ensure the payload is well-formed before
 * reaching AuthService, keeping service logic free of boilerplate checks.
 */
public class RegisterRequest {

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Must be a valid email address")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 128, message = "Password must be at least 6 characters")
  private String password;

  public RegisterRequest() {}

  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getPassword() { return password; }

  public void setName(String name) { this.name = name; }
  public void setEmail(String email) { this.email = email; }
  public void setPassword(String password) { this.password = password; }
}
