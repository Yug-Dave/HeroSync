package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
  List<Habit> findByUser_UserId(Long userId);
  List<Habit> findByUserAndArchivedFalse(User user);
}
