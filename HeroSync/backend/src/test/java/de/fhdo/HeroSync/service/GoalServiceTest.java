package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.CreateGoalRequest;
import de.fhdo.HeroSync.dto.GoalDto;
import de.fhdo.HeroSync.entity.Goal;
import de.fhdo.HeroSync.entity.GoalStatus;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.GoalRepository;
import de.fhdo.HeroSync.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private GoalService goalService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUserId(1L);
    }

    @Test
    void create_ShouldCreateSoloGoal() {
        // Arrange
        CreateGoalRequest req = new CreateGoalRequest();
        req.setTitle("Run Marathon");
        req.setDeadline(LocalDate.now().plusDays(30));

        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        GoalDto result = goalService.create(req, testUser);

        // Assert
        assertNotNull(result);
        assertEquals("Run Marathon", result.getTitle());
        assertNull(result.getHabitId());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
    }

    @Test
    void completeSoloGoal_ShouldWorkForActiveSoloGoal() {
        // Arrange
        Long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setUser(testUser);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setHabit(null);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        GoalDto result = goalService.completeSoloGoal(goalId, testUser);

        // Assert
        assertEquals(GoalStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
    }
}
