package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.Goal;
import de.fhdo.HeroSync.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

  List<Goal> findByUser_UserId(Long userId);

  List<Goal> findByUser_UserIdAndHabit_Id(Long userId, Long habitId);

  List<Goal> findByUser_UserIdAndHabit_IdAndStatus(Long userId, Long habitId, GoalStatus status);

  List<Goal> findByUser_UserIdAndHabitIsNull(Long userId);

  List<Goal> findByUser_UserIdAndStatus(Long userId, GoalStatus status);
}

