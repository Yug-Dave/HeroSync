package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.ProfileDto;
import de.fhdo.HeroSync.dto.UpdateProfileRequest;
import de.fhdo.HeroSync.entity.Profile;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.ProfileRepository;
import de.fhdo.HeroSync.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST controller exposing profile read and update operations for the
 * currently authenticated user. All endpoints require an active session.
 */
@RestController("profileRestApiController")
@RequestMapping("/api/profile")
public class ProfileRestController {

  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;

  public ProfileRestController(UserRepository userRepository, ProfileRepository profileRepository) {
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
  }

  /**
   * Returns the profile of the currently authenticated user.
   * If no extended profile record exists yet, bio and avatar will be null.
   */
  @GetMapping(
    value = "/me",
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<ProfileDto> getMyProfile(Authentication authentication) {
    User user = requireAuthenticatedUser(authentication);

    Profile profile = profileRepository.findByUserUserId(user.getUserId()).orElse(null);

    ProfileDto dto = new ProfileDto(
      user.getUserId(),
      user.getName(),
      user.getEmail(),
      profile != null ? profile.getBio() : null,
      profile != null ? profile.getAvatar() : null
    );

    return ResponseEntity.ok(dto);
  }

  /**
   * Updates the display name, bio, and avatar seed of the authenticated user.
   * Name changes are applied to the core User entity; bio and avatar are
   * persisted in the associated Profile record (created if not yet existing).
   */
  @PutMapping(
    value = "/me",
    consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<ProfileDto> updateMyProfile(
    Authentication authentication,
    @Valid @RequestBody UpdateProfileRequest request
  ) {
    User user = requireAuthenticatedUser(authentication);

    if (request.getName() != null && !request.getName().isBlank()) {
      user.setName(request.getName());
      userRepository.save(user);
    }

    Profile profile = profileRepository.findByUserUserId(user.getUserId()).orElse(null);

    if (profile == null) {
      profile = new Profile(user, request.getBio(), request.getAvatar());
    } else {
      profile.setBio(request.getBio());
      profile.setAvatar(request.getAvatar());
    }

    Profile saved = profileRepository.save(profile);

    ProfileDto dto = new ProfileDto(
      user.getUserId(),
      user.getName(),
      user.getEmail(),
      saved.getBio(),
      saved.getAvatar()
    );

    return ResponseEntity.ok(dto);
  }

  /**
   * Resolves the currently authenticated {@link User} from the Spring Security
   * {@link Authentication} context. Throws 401 if the session is missing or anonymous.
   */
  private User requireAuthenticatedUser(Authentication authentication) {
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

    return userRepository.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Not authenticated"));
  }
}
