package de.fhdo.HeroSync.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityTest {

  @Autowired MockMvc mvc;

  @Test
  void apiProfileMe_requiresAuth() throws Exception {
    mvc.perform(get("/api/profile/me"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void apiGoals_post_requiresAuth() throws Exception {
    mvc.perform(post("/api/goals")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"\"}"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void graphql_requiresAuth() throws Exception {
    mvc.perform(post("/graphql")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"query\":\"query { me { id name email } }\"}"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void apiProfileMe_withAuth_returnsDtoShape() throws Exception {
    mvc.perform(get("/api/profile/me")
            .with(httpBasic("daveyug2002@gmail.com", "password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.password").doesNotExist());
  }
}
