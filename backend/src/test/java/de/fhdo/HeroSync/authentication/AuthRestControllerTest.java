package de.fhdo.HeroSync.authentication;

import de.fhdo.HeroSync.controller.AuthRestController;
import de.fhdo.HeroSync.dto.UserSummaryDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.AuthService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

  @Mock
  private AuthService authService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthRestController controller;

/*
  @Test
  @DisplayName("GET /api/auth/me should return 401 when not authenticated")
  void me_WhenNotAuthenticated_ShouldThrow401() {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(false);

    ResponseStatusException ex =
      assertThrows(ResponseStatusException.class, () -> controller.me(auth));

    assertEquals(401, ex.getStatusCode().value());
  }
*/

//  @Test
//  @DisplayName("GET /api/auth/me should return UserSummaryDto when authenticated")
//  void me_WhenAuthenticated_ShouldReturnUserSummaryDto() throws Exception {
//    Authentication auth = mock(Authentication.class);
//    when(auth.isAuthenticated()).thenReturn(true);
//    when(auth.getPrincipal()).thenReturn("test@example.com");
//
//    User u = new User("Test User", "test@example.com", "hash");
//    setPrivateField(u, "userId", 1L);
//
//    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(u));
//
//    ResponseEntity<UserSummaryDto> resp = controller.me(auth);
//
//    assertEquals(200, resp.getStatusCode().value());
//    assertNotNull(resp.getBody());
//
//    UserSummaryDto dto = resp.getBody();
//    assertEquals(1L, dto.getId());
//    assertEquals("Test User", dto.getName());
//    assertEquals("test@example.com", dto.getEmail());
//
//    verify(userRepository).findByEmail("test@example.com");
//  }

  private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }
}
