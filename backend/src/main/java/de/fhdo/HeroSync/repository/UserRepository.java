package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data access layer for {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  /** Used to look up a user by their one-time email verification token. */
  Optional<User> findByVerificationToken(String token);
}
