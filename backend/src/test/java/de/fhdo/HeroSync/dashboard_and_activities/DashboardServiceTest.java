package de.fhdo.HeroSync.dashboard_and_activities;

import de.fhdo.HeroSync.dto.ActivityDto;
import de.fhdo.HeroSync.service.DashboardService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @InjectMocks
  private DashboardService dashboardService;

  //Should count consecutive days correctly
  @Test
  @DisplayName("Should count consecutive days correctly")
  void calculateCurrentStreak_ConsecutiveDays_ReturnsCorrectCount() {
    List<ActivityDto> activities = new ArrayList<>();
    activities.add(new ActivityDto(LocalDate.now(), 1, 1L, "Run", 0));
    activities.add(new ActivityDto(LocalDate.now().minusDays(1), 1, 1L, "Run", 0));
    activities.add(new ActivityDto(LocalDate.now().minusDays(2), 1, 1L, "Run", 0));

    int streak = dashboardService.calculateCurrentStreak(activities);

    assertEquals(3, streak);
  }

  //Should count duplicate activities on the same day as only 1 streak day
  @Test
  @DisplayName("Should count duplicate activities on the same day as only 1 streak day")
  void calculateCurrentStreak_WhenDuplicatesExist_ShouldNotDoubleCount() {
    List<ActivityDto> activities = new ArrayList<>();
    activities.add(new ActivityDto(LocalDate.now(), 1, 1L, "Run", 0));
    activities.add(new ActivityDto(LocalDate.now(), 1, 1L, "Run (Evening)", 0));
    activities.add(new ActivityDto(LocalDate.now().minusDays(1), 1, 1L, "Run", 0));

    int streak = dashboardService.calculateCurrentStreak(activities);

    assertEquals(2, streak);
  }

  //Should handle unsorted lists correctly
  @Test
  @DisplayName("Handle unsorted lists")
  void calculateCurrentStreak_WhenUnsorted_ShouldSortAndCount() {
    List<ActivityDto> activities = new ArrayList<>();
    activities.add(new ActivityDto(LocalDate.now().minusDays(2), 1, 1L, "Run", 0));
    activities.add(new ActivityDto(LocalDate.now(), 1, 1L, "Run", 0));
    activities.add(new ActivityDto(LocalDate.now().minusDays(1), 1, 1L, "Run", 0));

    int streak = dashboardService.calculateCurrentStreak(activities);

    assertEquals(3, streak);
  }

  //Should return 0 gracefully if the input list is null
  @Test
  @DisplayName("Handle null input list")
  void calculateCurrentStreak_WhenInputIsNull_ShouldReturnZero() {
    int streak = dashboardService.calculateCurrentStreak(null);
    assertEquals(0, streak);
  }

  //Should handle future dates by either ignoring them or counting them safely
  @Test
  @DisplayName("Handling future dates")
  void calculateCurrentStreak_WithFutureDate_ShouldNotCrash() {
    List<ActivityDto> activities = new ArrayList<>();
    activities.add(new ActivityDto(LocalDate.now().plusDays(1), 1, 1L, "Future Run", 0));
    activities.add(new ActivityDto(LocalDate.now(), 1, 1L, "Run", 0));

    int streak = dashboardService.calculateCurrentStreak(activities);
    assertTrue(streak >= 1);
  }
}
