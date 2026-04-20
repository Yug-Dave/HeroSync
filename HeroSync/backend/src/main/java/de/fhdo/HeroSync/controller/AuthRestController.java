package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.AuthResponse;
import de.fhdo.HeroSync.dto.EmailRequest;
import de.fhdo.HeroSync.dto.LoginRequest;
import de.fhdo.HeroSync.dto.RegisterRequest;
import de.fhdo.HeroSync.dto.UserSummaryDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * REST controller for authentication operations: registration, email verification,
 * login, session query, and verification-email resend.
 */
@RestController("authRestApiController")
@RequestMapping("/api/auth")
public class AuthRestController {

  private final AuthService authService;
  private final UserRepository userRepository;

  public AuthRestController(AuthService authService, UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  /**
   * Registers a new user and sends a verification email.
   * Returns 409 Conflict if the email is already registered.
   * The caller should redirect to a "check your inbox" page rather than the dashboard.
   */
  @PostMapping(
    value = "/register",
    consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    try {
      AuthResponse created = authService.register(request);
      return ResponseEntity.status(CREATED).body(created);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(CONFLICT, ex.getMessage());
    }
  }

  /**
   * Confirms a user's email address using the one-time token sent in the verification email.
   * On success returns a plain JSON message; the frontend should redirect to /login.
   * Returns 400 if the token is invalid or has already been used.
   */
  @GetMapping("/verify")
  public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
    authService.verifyEmail(token);
    return ResponseEntity.ok(Map.of("message", "Email verified successfully. You may now log in."));
  }

  /**
   * Re-sends the verification email for an account that has not yet been verified.
   * Returns 404 if no account exists for the provided email,
   * 400 if the account is already verified.
   */
  @PostMapping("/resend-verification")
  public ResponseEntity<Map<String, String>> resendVerification(@RequestBody EmailRequest body) {
    authService.resendVerification(body.getEmail());
    return ResponseEntity.ok(Map.of("message", "Verification email resent. Please check your inbox."));
  }

  /**
   * Authenticates a user and establishes an HTTP session.
   * Returns 401 for invalid credentials, 403 if the email is not yet verified
   * (the body message begins with "EMAIL_NOT_VERIFIED:" for frontend detection).
   */
  @PostMapping(
    value = "/login",
    consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse user = authService.login(request);
    return ResponseEntity.ok(user);
  }

  /**
   * Returns the summary (id, name, email) of the currently authenticated user.
   * Returns 401 if no valid session exists.
   */
  @GetMapping(
    value = "/me",
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<UserSummaryDto> me(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(UNAUTHORIZED, "Not authenticated");
    }

    Object principal = authentication.getPrincipal();
    if (principal == null || "anonymousUser".equals(principal)) {
      throw new ResponseStatusException(UNAUTHORIZED, "Not authenticated");
    }

    String email;
    if (principal instanceof UserDetails ud) {
      email = ud.getUsername();
    } else {
      email = principal.toString();
    }

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Not authenticated"));

    UserSummaryDto dto = new UserSummaryDto(user.getUserId(), user.getName(), user.getEmail());
    return ResponseEntity.ok(dto);
  }
}
