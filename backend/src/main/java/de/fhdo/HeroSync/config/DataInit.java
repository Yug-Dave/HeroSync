package de.fhdo.HeroSync.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.Activity;
import de.fhdo.HeroSync.entity.Achievement;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.AchievementRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds the database with demo data on first startup.
 * Each runner checks for existing records before inserting to remain idempotent.
 */
@Configuration
public class DataInit {

  @Bean
  @Order(0)
  CommandLineRunner cleanOldAchievements(
      de.fhdo.HeroSync.repository.AchievementRepository achRepo,
      de.fhdo.HeroSync.repository.UnlockedAchievementRepository unlockedRepo
  ) {
    return args -> {
      String[] codes = {"FIRST_STEP", "WEEK_STREAK", "WELCOME_HERO", "WEEK_WARRIOR"};
      for (String code : codes) {
        achRepo.findByCode(code).ifPresent(a -> {
          // Delete all unlock records first to satisfy foreign key constraint
          unlockedRepo.deleteByAchievementId(a.getId());
          achRepo.delete(a);
        });
      }
    };
  }

  @Bean
  @Order(1)
  CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder encoder) {
    return args -> {
      createUserIfMissing(userRepo, encoder, "Yug Dave", "daveyug2002@gmail.com", "password");
      createUserIfMissing(userRepo, encoder, "Test1",    "test1@gmail.com",        "password");
      createUserIfMissing(userRepo, encoder, "Test2",    "test2@gmail.com",        "password");
    };
  }

  private void createUserIfMissing(UserRepository repo, PasswordEncoder encoder,
                                   String name, String email, String rawPassword) {
    if (!repo.existsByEmail(email)) {
      User user = new User(name, email, encoder.encode(rawPassword));
      user.setEmailVerified(true);
      repo.save(user);
      System.out.println("[DataInit] Seeded user: " + email);
    }
  }

  @Bean
  @Order(2)
  CommandLineRunner initHabits(HabitRepository habitRepo, UserRepository userRepo) {
    return args -> {
      if (habitRepo.count() > 0) return;

      userRepo.findAll().stream().findFirst().ifPresent(user -> {
        Habit h1 = new Habit();
        h1.setUser(user);
        h1.setName("Drink Water");
        h1.setDescription("3L per day");
        h1.setArchived(false);
        h1.setCreatedAt(LocalDateTime.now());

        Habit h2 = new Habit();
        h2.setUser(user);
        h2.setName("Daily Walk");
        h2.setDescription("10,000 steps");
        h2.setArchived(false);
        h2.setCreatedAt(LocalDateTime.now());

        habitRepo.save(h1);
        habitRepo.save(h2);
      });
    };
  }

  @Bean
  @Order(3)
  CommandLineRunner initActivities(ActivityRepository actRepo, HabitRepository habitRepo) {
    return args -> {
      if (actRepo.count() > 0 || habitRepo.findAll().isEmpty()) return;

      Habit habit = habitRepo.findAll().get(0);
      LocalDate today = LocalDate.now();

      for (int i = 6; i >= 0; i--) {
        int completions = (int) (Math.random() * 2);
        actRepo.save(new Activity(habit, today.minusDays(i), completions));
      }
    };
  }

}
