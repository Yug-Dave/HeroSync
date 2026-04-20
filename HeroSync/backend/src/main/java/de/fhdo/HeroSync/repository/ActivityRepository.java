package de.fhdo.HeroSync.repository;

import de.fhdo.HeroSync.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
  @Query(value = "SELECT a.date as date, SUM(h.xp_value * a.completions) as xpValue " +
    "FROM activity a " +
    "JOIN habit h ON a.habit_id = h.id " +
    "WHERE h.user_id = :userId " +
    "AND a.date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
    "GROUP BY a.date " +
    "ORDER BY a.date ASC", nativeQuery = true)
  List<Map<String, Object>> getWeeklyXpHistory(@Param("userId") Long userId);

  List<Activity> findByDateBetween(LocalDate start, LocalDate end);
  List<Activity> findByHabit_User_UserId(Long userId);

  Optional<Activity> findByHabit_IdAndDate(Long habitId, LocalDate date);
  // needed for user-isolated reports
  List<Activity> findByHabit_User_UserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

  void deleteByHabit_Id(Long habitId);

  @Query("SELECT COALESCE(SUM(a.completions), 0) FROM Activity a WHERE a.habit.id = :habitId")
  int sumCompletionsByHabitId(@Param("habitId") Long habitId);
}

