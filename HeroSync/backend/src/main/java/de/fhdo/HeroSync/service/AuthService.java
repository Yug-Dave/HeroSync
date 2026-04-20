package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.AuthResponse;
import de.fhdo.HeroSync.dto.LoginRequest;
import de.fhdo.HeroSync.dto.RegisterRequest;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

/**
 * Service handling user registration, email verification, and login.
 *
 * <p>Authentication state is persisted server-side via an HTTP session cookie.
 * Email ownership is proven by sending a verification link to the address
 * supplied at registration; unverified accounts are blocked from logging in.
 */
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final HttpServletRequest httpServletRequest;
  private final EmailService emailService;

  public AuthService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    AuthenticationManager authenticationManager,
    HttpServletRequest httpServletRequest,
    EmailService emailService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.httpServletRequest = httpServletRequest;
    this.emailService = emailService;
  }

  /**
   * Registers a new account and sends an email verification link.
   *
   * <p>The account is created in an unverified state. The user must click
   * the link in the email before they can log in. Throws
   * {@link IllegalArgumentException} if the email is already registered.
   */
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already registered");
    }

    String token = UUID.randomUUID().toString();

    User user = new User(
      request.getName(),
      request.getEmail(),
      passwordEncoder.encode(request.getPassword())
    );
    user.setEmailVerified(false);
    user.setVerificationToken(token);

    User saved = userRepository.save(user);

    emailService.sendVerificationEmail(saved.getEmail(), token, saved.getName());

    return new AuthResponse(saved.getUserId(), saved.getName(), saved.getEmail());
  }

  /**
   * Verifies a user's email address using the one-time token from the link.
   *
   * <p>On success the token is cleared and {@code emailVerified} is set to
   * {@code true}. Throws 400 for unknown or already-used tokens.
   */
  @Transactional
  public void verifyEmail(String token) {
    User user = userRepository.findByVerificationToken(token)
      .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST,
          "Invalid or expired verification link. Please request a new one."));

    user.setEmailVerified(true);
    user.setVerificationToken(null);
    userRepository.save(user);
  }

  /**
   * Re-sends the verification email for an unverified account.
   * Throws 404 if the email is not registered, 400 if it is already verified.
   */
  @Transactional
  public void resendVerification(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
          "No account found for this email address."));

    if (user.isEmailVerified()) {
      throw new ResponseStatusException(BAD_REQUEST, "This email address is already verified.");
    }

    String token = UUID.randomUUID().toString();
    user.setVerificationToken(token);
    userRepository.save(user);

    emailService.sendVerificationEmail(email, token, user.getName());
  }

  /**
   * Authenticates the user and creates an HTTP session.
   *
   * <p>Returns 403 Forbidden with a clear message if the email has not been
   * verified yet, so the frontend can surface a "resend verification" prompt.
   * Returns 401 Unauthorized for invalid credentials.
   */
  @Transactional
  public AuthResponse login(LoginRequest request) {
    return authenticateAndCreateSession(request);
  }

  /**
   * GraphQL-facing login delegate — shares the same session-creation flow.
   */
  @Transactional
  public AuthResponse loginAndCreateSession(LoginRequest request) {
    return authenticateAndCreateSession(request);
  }

  /**
   * Core authentication flow shared by REST and GraphQL login endpoints.
   * Verifies credentials, checks email-verified status, and persists the
   * {@link SecurityContext} in the HTTP session on success.
   */
  private AuthResponse authenticateAndCreateSession(LoginRequest request) {
    Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );
    } catch (BadCredentialsException ex) {
      throw new ResponseStatusException(UNAUTHORIZED, "Invalid email or password.");
    }

    User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid email or password."));

    if (!user.isEmailVerified()) {
      throw new ResponseStatusException(FORBIDDEN,
        "EMAIL_NOT_VERIFIED: Please check your inbox and verify your email before logging in.");
    }

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    HttpSession session = httpServletRequest.getSession(true);
    session.setAttribute(
      HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
      context
    );

    return new AuthResponse(user.getUserId(), user.getName(), user.getEmail());
  }
}
