package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.Achievement;
import de.fhdo.HeroSync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
  List<Achievement> findByTargetUserOrTargetUserIsNull(User user);
  Optional<Achievement> findByCode(String code);
  boolean existsByCode(String code);
}
