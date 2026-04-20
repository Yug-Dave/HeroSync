package de.fhdo.HeroSync.reports_achievements;

import de.fhdo.HeroSync.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReportServiceTest {

  @Autowired
  private ReportService reportService;

  @Test
  void weeklyReportRunsWithoutError() {
    var result = reportService.weekly(LocalDate.now());

    assertThat(result).isNotNull();
    assertThat(result.completionsPerDay()).isNotNull();
    assertThat(result.completionsPerDay().size()).isEqualTo(7);
  }
}
