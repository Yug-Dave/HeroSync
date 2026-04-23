package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.HabitDto;
import de.fhdo.HeroSync.entity.Habit;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.ActivityRepository;
import de.fhdo.HeroSync.repository.GoalRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private HabitService habitService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test Hero");
    }

    @Test
    void createDto_ShouldCalculateXPBasedOnName() {
        // Arrange
        String name = "Drink Water";
        when(habitRepository.save(any(Habit.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        HabitDto result = habitService.createDto(testUser, name, "Stay hydrated", 200);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertTrue(result.getXpValue() >= 50 && result.getXpValue() <= 150);
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void delete_ShouldUnlinkGoalsAndRemoveActivities() {
        // Arrange
        Long habitId = 100L;
        Habit habit = new Habit();
        habit.setId(habitId);
        habit.setUser(testUser);

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));

        // Act
        habitService.delete(testUser, habitId);

        // Assert
        verify(goalRepository, times(1)).findByUser_UserIdAndHabit_Id(testUser.getUserId(), habitId);
        verify(activityRepository, times(1)).deleteByHabit_Id(habitId);
        verify(habitRepository, times(1)).delete(habit);
    }

    @Test
    void delete_ShouldThrowExceptionIfNotOwner() {
        // Arrange
        Long habitId = 100L;
        User otherUser = new User();
        otherUser.setUserId(2L);

        Habit habit = new Habit();
        habit.setId(habitId);
        habit.setUser(otherUser);

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> habitService.delete(testUser, habitId));
        verify(habitRepository, never()).delete(any());
    }
}
