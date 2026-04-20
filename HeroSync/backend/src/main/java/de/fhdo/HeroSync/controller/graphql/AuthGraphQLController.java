package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.AuthResponse;
import de.fhdo.HeroSync.dto.LoginRequest;
import de.fhdo.HeroSync.dto.RegisterRequest;
import de.fhdo.HeroSync.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthGraphQLController {

  private final AuthService authService;

  public AuthGraphQLController(AuthService authService) {
    this.authService = authService;
  }

  @MutationMapping
  public AuthResponse login(@Argument LoginRequest input) {
    return authService.loginAndCreateSession(input);
  }

  @MutationMapping
  public AuthResponse register(@Argument RegisterRequest input) {
    return authService.register(input);
  }
}



