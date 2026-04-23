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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GraphqlMeTest {

  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;

  private final String email = "gql-test@example.com";
  private final String password = "test123";

  @BeforeEach
  void seedUser() {
    userRepository.findByEmail(email).orElseGet(() -> {
      User u = new User();
      u.setEmail(email);
      u.setName("GraphQL Test");
      u.setPasswordHash(passwordEncoder.encode(password));
      return userRepository.save(u);
    });
  }

  @Test
  void me_withAuth_returnsUserSummary() throws Exception {
    String body = """
      { "query": "query { me { id name email } }" }
      """;

    mvc.perform(post("/graphql")
        .with(httpBasic(email, password))
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.errors").doesNotExist())
      .andExpect(jsonPath("$.data.me.id").exists())
      .andExpect(jsonPath("$.data.me.name").value("GraphQL Test"))
      .andExpect(jsonPath("$.data.me.email").value(email));
  }
}
