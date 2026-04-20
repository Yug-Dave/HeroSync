package de.fhdo.HeroSync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

/**
 * Entry point for the HeroSync Spring Boot application.
 */
@SpringBootApplication
public class HeroSyncApplication {

  public static void main(String[] args) {
    SpringApplication.run(HeroSyncApplication.class, args);
  }

  @Bean
  public CommandLineRunner startupInfo() {
    return args -> System.out.println("HeroSync is running at http://localhost:8080/");
  }
}
