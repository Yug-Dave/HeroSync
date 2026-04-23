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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GoalValidationTest {

  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;

  private final String email = "goal-validation@example.com";
  private final String password = "test123";

  @BeforeEach
  void seedUser() {
    userRepository.findByEmail(email).orElseGet(() -> {
      User u = new User();
      u.setEmail(email);
      u.setName("Goal Validation");
      u.setPasswordHash(passwordEncoder.encode(password));
      return userRepository.save(u);
    });
  }

  @Test
  void createGoal_blankTitle_returns400() throws Exception {
    String body = """
      {
        "title": "",
        "description": "x",
        "habitId": null,
        "targetCount": 5,
        "deadline": null
      }
      """;

    mvc.perform(post("/api/goals")
        .with(httpBasic(email, password))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(org.hamcrest.Matchers.containsString("Title must not be blank")));
  }

  @Test
  void updateGoal_titleTooLong_returns400() throws Exception {
    // Create a valid goal first
    String create = """
      {
        "title": "Valid Goal",
        "description": "x",
        "habitId": null,
        "targetCount": 5,
        "deadline": null
      }
      """;

    var createResult = mvc.perform(post("/api/goals")
        .with(httpBasic(email, password))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(create))
      .andExpect(status().is2xxSuccessful())
      .andExpect(jsonPath("$.id").exists())
      .andReturn();

    String json = createResult.getResponse().getContentAsString();
    String id = json.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");

    // 121 chars > max 120
    String longTitle = "A".repeat(121);
    String update = "{ \"title\": \"" + longTitle + "\" }";

    mvc.perform(put("/api/goals/{id}", id)
        .with(httpBasic(email, password))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(update))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(org.hamcrest.Matchers.containsString("Title must be at most 120 characters")));
  }
}
