package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileDtoExposureTest {

  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;

  private final String email = "dto-test@example.com";
  private final String password = "test123";

  @BeforeEach
  void seedUser() {
    userRepository.findByEmail(email).orElseGet(() -> {
      User u = new User();
      u.setEmail(email);
      u.setName("DTO Test");
      u.setPasswordHash(passwordEncoder.encode(password));
      return userRepository.save(u);
    });
  }

  @Test
  void profileMe_returnsProfileDto_withoutSensitiveFields() throws Exception {
    mvc.perform(get("/api/profile/me")
        .with(httpBasic(email, password))
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.userId").exists())
      .andExpect(jsonPath("$.name").value("DTO Test"))
      .andExpect(jsonPath("$.email").value(email))

      // ensure sensitive/entity fields are not leaked
      .andExpect(jsonPath("$.passwordHash").doesNotExist())
      .andExpect(jsonPath("$.password").doesNotExist())
      .andExpect(jsonPath("$.roles").doesNotExist());
  }
}
