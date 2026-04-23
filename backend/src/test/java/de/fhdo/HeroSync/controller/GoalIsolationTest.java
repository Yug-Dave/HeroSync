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

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GoalIsolationTest {

  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;

  private final String userAEmail = "goal-a@example.com";
  private final String userAPass = "test123";
  private final String userBEmail = "goal-b@example.com";
  private final String userBPass = "test123";

  @BeforeEach
  void seedUsers() {
    userRepository.findByEmail(userAEmail).orElseGet(() -> {
      User u = new User();
      u.setEmail(userAEmail);
      u.setName("Goal A");
      u.setPasswordHash(passwordEncoder.encode(userAPass));
      return userRepository.save(u);
    });

    userRepository.findByEmail(userBEmail).orElseGet(() -> {
      User u = new User();
      u.setEmail(userBEmail);
      u.setName("Goal B");
      u.setPasswordHash(passwordEncoder.encode(userBPass));
      return userRepository.save(u);
    });
  }

  @Test
  void userBCannotUpdateOrDeleteUserAGoal() throws Exception {
    // Create a goal as User A
    String createBody = """
      {
        "title": "Isolation Test Goal",
        "description": "created by A",
        "habitId": null,
        "targetCount": 5,
        "deadline": null
      }
      """;

    var createResult = mvc.perform(post("/api/goals")
        .with(httpBasic(userAEmail, userAPass))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(createBody))
      .andExpect(status().is2xxSuccessful())
      .andExpect(jsonPath("$.id").exists())
      .andReturn();

    String json = createResult.getResponse().getContentAsString();
    String id = json.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");

    // Update attempt as User B must NOT succeed (prefer 404, sometimes 403)
    String updateBody = """
      {
        "title": "Hacked Title"
      }
      """;

    mvc.perform(put("/api/goals/{id}", id)
        .with(httpBasic(userBEmail, userBPass))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(updateBody))
      .andExpect(status().is4xxClientError());

    // Delete attempt as User B must NOT succeed (prefer 404, sometimes 403)
    mvc.perform(delete("/api/goals/{id}", id)
        .with(httpBasic(userBEmail, userBPass)))
      .andExpect(status().is4xxClientError());

    // Optional: User A can still delete their own goal (ensures goal exists and access works)
    mvc.perform(delete("/api/goals/{id}", id)
        .with(httpBasic(userAEmail, userAPass)))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  void userBDoesNotSeeUserAGoalInList() throws Exception {
    // Create a goal as User A
    String createBody = """
      {
        "title": "List Isolation Goal",
        "description": "created by A",
        "habitId": null,
        "targetCount": 3,
        "deadline": null
      }
      """;

    mvc.perform(post("/api/goals")
        .with(httpBasic(userAEmail, userAPass))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(createBody))
      .andExpect(status().is2xxSuccessful());

    // List as User B should not contain A's goal title
    mvc.perform(get("/api/goals")
        .with(httpBasic(userBEmail, userBPass))
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(not(containsString("List Isolation Goal"))));
  }
}
