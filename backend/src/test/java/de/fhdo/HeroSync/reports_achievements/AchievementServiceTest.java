package de.fhdo.HeroSync.reports_achievements;

import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.AchievementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AchievementServiceTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;


  @Autowired
  private AchievementService achievementService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void achievementEvaluationRunsWithoutError() {
    var user = userRepository.findByEmail("test1@gmail.com")
      .orElseThrow(() -> new IllegalStateException("Seeded test user not found"));

    var result = achievementService.evaluateAndUnlock(user);

    assertThat(result).isNotNull();
  }
}

