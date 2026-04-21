package de.fhdo.HeroSync.authentication;

import de.fhdo.HeroSync.controller.ProfileRestController;
import de.fhdo.HeroSync.dto.ProfileDto;
import de.fhdo.HeroSync.dto.UpdateProfileRequest;
import de.fhdo.HeroSync.entity.Profile;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.ProfileRepository;
import de.fhdo.HeroSync.repository.UserRepository;

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
class ProfileRestControllerTest {

/*
  @Mock
  private UserRepository userRepository;

  @Mock
  private ProfileRepository profileRepository;

  @InjectMocks
  private ProfileRestController controller;

  @Test
  @DisplayName("GET /api/profile/me should return 401 when not authenticated")
  void getMyProfile_WhenNotAuthenticated_ShouldThrow401() {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(false);

    ResponseStatusException ex =
      assertThrows(ResponseStatusException.class, () -> controller.getMe(auth));

    assertEquals(401, ex.getStatusCode().value());
  }

  @Test
  @DisplayName("GET /api/profile/me should return ProfileDto when authenticated")
  void getMyProfile_WhenAuthenticated_ShouldReturnProfileDto() throws Exception {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getPrincipal()).thenReturn("profile@test.com");

    User u = new User("Profile User", "profile@test.com", "hash");
    setPrivateField(u, "userId", 10L);

    when(userRepository.findByEmail("profile@test.com")).thenReturn(Optional.of(u));
    when(profileRepository.findByUserUserId(10L)).thenReturn(Optional.empty());

    ResponseEntity<ProfileDto> resp = controller.getMe(auth);

    assertEquals(200, resp.getStatusCode().value());
    assertNotNull(resp.getBody());

    ProfileDto dto = resp.getBody();
    assertEquals(10L, dto.getUserId());
    assertEquals("Profile User", dto.getName());
    assertEquals("profile@test.com", dto.getEmail());
    assertNull(dto.getBio());

    verify(userRepository).findByEmail("profile@test.com");
    verify(profileRepository).findByUserUserId(10L);
    verify(profileRepository, never()).save(any(Profile.class));
  }

  @Test
  @DisplayName("PUT /api/profile/me should update bio and return ProfileDto")
  void updateMyProfile_WhenAuthenticated_ShouldUpdateBioAndReturnDto() throws Exception {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getPrincipal()).thenReturn("profile@test.com");

    User u = new User("Profile User", "profile@test.com", "hash");
    setPrivateField(u, "userId", 10L);

    when(userRepository.findByEmail("profile@test.com")).thenReturn(Optional.of(u));

    Profile existing = new Profile(u, "old bio", null);
    when(profileRepository.findByUserUserId(10L)).thenReturn(Optional.of(existing));

    when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

    UpdateProfileRequest req = new UpdateProfileRequest();
    req.setBio("new bio");
    req.setName(null);

    ResponseEntity<ProfileDto> resp = controller.updateMe(auth, req);

    assertEquals(200, resp.getStatusCode().value());
    assertNotNull(resp.getBody());

    ProfileDto dto = resp.getBody();
    assertEquals(10L, dto.getUserId());
    assertEquals("Profile User", dto.getName());
    assertEquals("profile@test.com", dto.getEmail());
    assertEquals("new bio", dto.getBio());

    assertEquals("new bio", existing.getBio(), "profile entity bio should be updated before save");

    verify(profileRepository).findByUserUserId(10L);
    verify(profileRepository).save(any(Profile.class));
    verify(userRepository, never()).save(any(User.class));
  }

  private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }
*/
}
