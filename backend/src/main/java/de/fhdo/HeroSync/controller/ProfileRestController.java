package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.ProfileDto;
import de.fhdo.HeroSync.dto.UpdateProfileRequest;
import de.fhdo.HeroSync.entity.AiProviderChoice;
import de.fhdo.HeroSync.entity.CompanionChoice;
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
  private final de.fhdo.HeroSync.service.AvatarStorageService avatarStorageService;

  public ProfileRestController(UserRepository userRepository,
                               ProfileRepository profileRepository,
                               de.fhdo.HeroSync.service.AvatarStorageService avatarStorageService) {
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
    this.avatarStorageService = avatarStorageService;
  }

  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDto> getMe(Authentication auth) {
    User u = requireUser(auth);
    Profile p = profileRepository.findByUserUserId(u.getUserId()).orElse(null);
    String companionChoice = (p != null && p.getCompanionChoice() != null)
        ? p.getCompanionChoice().name() : CompanionChoice.SYNC.name();
    String aiProvider = (p != null && p.getAiProvider() != null)
        ? p.getAiProvider().name() : AiProviderChoice.GROQ.name();
    return ResponseEntity.ok(new ProfileDto(
        u.getUserId(), u.getName(), u.getEmail(),
        p != null ? p.getBio() : null,
        p != null ? p.getAvatar() : null,
        companionChoice, aiProvider));
  }

  @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDto> updateMe(Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
    User u = requireUser(auth);
    if (req.getName() != null) { u.setName(req.getName()); userRepository.save(u); }
    String processedAvatar = avatarStorageService.processAvatar(req.getAvatar());
    Profile p = profileRepository.findByUserUserId(u.getUserId()).orElse(null);
    if (p == null) {
      p = new Profile(u, req.getBio(), processedAvatar);
      p.setCompanionChoice(CompanionChoice.SYNC);
      p.setAiProvider(AiProviderChoice.GROQ);
    } else {
      p.setBio(req.getBio());
      p.setAvatar(processedAvatar);
    }
    if (req.getCompanionChoice() != null) {
      try { p.setCompanionChoice(CompanionChoice.valueOf(req.getCompanionChoice())); }
      catch (IllegalArgumentException ignored) {}
    }
    if (req.getAiProvider() != null) {
      try { p.setAiProvider(AiProviderChoice.valueOf(req.getAiProvider())); }
      catch (IllegalArgumentException ignored) {}
    }
    profileRepository.save(p);
    return ResponseEntity.ok(new ProfileDto(
        u.getUserId(), u.getName(), u.getEmail(), p.getBio(), p.getAvatar(),
        p.getCompanionChoice().name(), p.getAiProvider().name()));
  }

  @PostMapping(value = "/update-companion", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDto> updateCompanion(Authentication auth, @RequestBody java.util.Map<String, String> req) {
    User u = requireUser(auth);
    Profile p = profileRepository.findByUserUserId(u.getUserId()).orElse(null);
    if (p == null) {
        p = new Profile(u, "", null);
        p.setCompanionChoice(CompanionChoice.SYNC);
        p.setAiProvider(AiProviderChoice.GROQ);
    }
    
    if (req.containsKey("companionChoice")) {
      try { p.setCompanionChoice(CompanionChoice.valueOf(req.get("companionChoice"))); }
      catch (IllegalArgumentException ignored) {}
    }
    if (req.containsKey("aiProvider")) {
      try { p.setAiProvider(AiProviderChoice.valueOf(req.get("aiProvider"))); }
      catch (IllegalArgumentException ignored) {}
    }
    
    profileRepository.save(p);
    return ResponseEntity.ok(new ProfileDto(
        u.getUserId(), u.getName(), u.getEmail(), p.getBio(), p.getAvatar(),
        p.getCompanionChoice().name(), p.getAiProvider().name()));
  }

  private User requireUser(Authentication auth) {
    if (auth == null) throw new ResponseStatusException(UNAUTHORIZED);
    String email = (auth.getPrincipal() instanceof UserDetails ud) ? ud.getUsername() : auth.getPrincipal().toString();
    return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED));
  }
}
