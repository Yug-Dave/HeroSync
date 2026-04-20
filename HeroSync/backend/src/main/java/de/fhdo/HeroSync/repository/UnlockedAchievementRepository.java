package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.UnlockedAchievement;
import de.fhdo.HeroSync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface UnlockedAchievementRepository extends JpaRepository<UnlockedAchievement, Long> {

  List<UnlockedAchievement> findByUser(User user);

  boolean existsByUserAndAchievement_Id(User user, Long achievementId);

  @Transactional
  void deleteByAchievementId(Long achievementId);
}
