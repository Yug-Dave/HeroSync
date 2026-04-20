package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.ReportDto;
import de.fhdo.HeroSync.entity.Activity;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReportService {

  private final ActivityRepository activities;

  public ReportService(ActivityRepository activities) {
    this.activities = activities;
  }

  //  USER-ISOLATED (Assignment 5)
  public ReportDto weekly(User user, LocalDate anyDayInWeek) {
    LocalDate start = anyDayInWeek.with(DayOfWeek.MONDAY);
    LocalDate end = start.plusDays(6);

    List<Activity> list =
        activities.findByHabit_User_UserIdAndDateBetween(user.getUserId(), start, end);

    return build("WEEKLY", start, end, list);
  }

  //  USER-ISOLATED (Assignment 5)
  public ReportDto monthly(User user, int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    List<Activity> list =
        activities.findByHabit_User_UserIdAndDateBetween(user.getUserId(), start, end);

    return build("MONTHLY", start, end, list);
  }

  // ---------------- LEGACY overloads (keep Assignments 6–8 safe) ----------------

  @Deprecated
  public ReportDto weekly(LocalDate anyDayInWeek) {
    LocalDate start = anyDayInWeek.with(DayOfWeek.MONDAY);
    LocalDate end = start.plusDays(6);
    List<Activity> list = activities.findByDateBetween(start, end);
    return build("WEEKLY", start, end, list);
  }

  @Deprecated
  public ReportDto monthly(int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
    List<Activity> list = activities.findByDateBetween(start, end);
    return build("MONTHLY", start, end, list);
  }

  // ---------------- shared builder ----------------

  private ReportDto build(String period, LocalDate start, LocalDate end, List<Activity> list) {
    Map<LocalDate, Integer> perDay = new TreeMap<>();
    for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
      perDay.put(d, 0);
    }
    list.forEach(a -> perDay.merge(a.getDate(), a.getCompletions(), Integer::sum));

    int total = list.stream().mapToInt(Activity::getCompletions).sum();
    int activeHabits = (int) list.stream()
        .map(a -> a.getHabit().getId())
        .distinct()
        .count();

    return new ReportDto(period, start, end, total, activeHabits, perDay);
  }
}
