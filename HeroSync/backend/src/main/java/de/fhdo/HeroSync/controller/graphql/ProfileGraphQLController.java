package de.fhdo.HeroSync.controller.graphql;

import de.fhdo.HeroSync.dto.UserSummaryDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public class ProfileGraphQLController {

  private final UserRepository userRepository;

  public ProfileGraphQLController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @QueryMapping
  public UserSummaryDto me() {
    User user = getCurrentUser();
    if (user == null) {
      return null;
    }

    // DTO constructor is (id, name, email)
    return new UserSummaryDto(
      user.getUserId(),
      user.getName(),
      user.getEmail()
    );
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }

    Object principal = auth.getPrincipal();
    if (principal == null || "anonymousUser".equals(principal)) {
      return null;
    }

    String email;
    if (principal instanceof UserDetails ud) {
      email = ud.getUsername();
    } else {
      email = principal.toString();
    }

    return userRepository.findByEmail(email).orElse(null);
  }
}
