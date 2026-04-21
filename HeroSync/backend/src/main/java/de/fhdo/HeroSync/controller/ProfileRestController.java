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

@RestController
@RequestMapping("/profile")
public class ProfileRestController {
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  public ProfileRestController(UserRepository userRepository, ProfileRepository profileRepository) {
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
  }
  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDto> getMe(Authentication auth) {
    User u = requireUser(auth);
    Profile p = profileRepository.findByUserUserId(u.getUserId()).orElse(null);
    return ResponseEntity.ok(new ProfileDto(u.getUserId(), u.getName(), u.getEmail(), p != null ? p.getBio() : null, p != null ? p.getAvatar() : null));
  }
  @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDto> updateMe(Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
    User u = requireUser(auth);
    if (req.getName() != null) { u.setName(req.getName()); userRepository.save(u); }
    Profile p = profileRepository.findByUserUserId(u.getUserId()).orElse(null);
    if (p == null) { p = new Profile(u, req.getBio(), req.getAvatar()); } else { p.setBio(req.getBio()); p.setAvatar(req.getAvatar()); }
    profileRepository.save(p);
    return ResponseEntity.ok(new ProfileDto(u.getUserId(), u.getName(), u.getEmail(), p.getBio(), p.getAvatar()));
  }
  private User requireUser(Authentication auth) {
    if (auth == null) throw new ResponseStatusException(UNAUTHORIZED);
    String email = (auth.getPrincipal() instanceof UserDetails ud) ? ud.getUsername() : auth.getPrincipal().toString();
    return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED));
  }
}
