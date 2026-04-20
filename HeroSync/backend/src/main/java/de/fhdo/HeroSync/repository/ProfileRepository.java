package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

  // Find profile using the linked user's id
  Optional<Profile> findByUserUserId(Long userId);
}
