package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.ProfileDto;
import de.fhdo.HeroSync.entity.Profile;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

  private final UserRepository userRepository;

  public ProfileService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public ProfileDto getProfileByUserId(Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    Profile profile = user.getProfile();
    String bio = (profile != null) ? profile.getBio() : null;
    String avatar = (profile != null) ? profile.getAvatar() : null;

    return new ProfileDto(
      user.getUserId(),
      user.getName(),
      user.getEmail(),
      bio,
      avatar
    );
  }
  @Transactional
  public ProfileDto updateBio(Long userId, String bio) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    Profile profile = user.getProfile();

    if (profile == null) {
      profile = new Profile(user, bio, null);
      user.setProfile(profile);
    } else {
      profile.setBio(bio);
    }

    return new ProfileDto(
      user.getUserId(),
      user.getName(),
      user.getEmail(),
      profile.getBio(),
      profile.getAvatar()
    );
  }

}
