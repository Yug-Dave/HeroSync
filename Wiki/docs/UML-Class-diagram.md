## UML Class Diagram
The following diagrams illustrate the structure of our application “HeroSync – The Habit Tracker.”
They describe the key classes, attributes, methods, and relationships between components.

```mermaid
classDiagram
direction TB

class User {
  +Long userId
  +String name
  +String email
  +String passwordHash
  +int xp
  +boolean emailVerified
  --
  +AuthResponse register(request)
  +AuthResponse login(request)
}

class Profile {
  +Long profileId
  +String bio
  +String avatar
  --
  +ProfileDto getProfileByUserId(id)
  +ProfileDto updateBio(id, bio)
}

class Dashboard {
  +Long dashboardId
  --
  +DashboardDTO getDashboard(id)
  +List~HeatmapDay~ getHeatmapData(userId)
  +int calculateCurrentStreak(activities)
}

class Report {
  +ReportDto weekly(user, date)
  +ReportDto monthly(user, year, month)
}

class Habit {
  +Long id
  +String name
  +String description
  +boolean archived
  +LocalDateTime createdAt
  +int xpValue
  --
  +List~HabitDto~ listAllDtos(user)
  +HabitDto createDto(user, name)
  +HabitDto updateDto(user, id)
  +void delete(user, id)
}

class Activity {
  +Long id
  +LocalDate date
  +int completions
  --
  +void createOrUpdateActivity(user, dto)
}

class Goal {
  +Long id
  +String title
  +int targetCount
  +int progressCount
  +boolean isBoss
  +String bossType
  +LocalDate deadline
  +GoalStatus status
  --
  +List~GoalDto~ listAllDtos(user)
  +GoalDto create(req)
  +GoalDto completeSoloGoal(id)
}

class Achievement {
  +Long id
  +String code
  +String title
  +String description
  +String ruleType
  +int threshold
  +int xpReward
  +String iconCode
  +int minLevel
  --
  +List~Dto~ evaluateAndUnlock(user)
  +List~Dto~ listUnlocked(user)
}

class UnlockedAchievement {
  +Long id
  +LocalDateTime unlockedAt
}

class GoalStatus {
  <<enumeration>>
  ACTIVE
  COMPLETED
  FAILED
}

%% --- RELATIONSHIPS ---

User "1" *-- "1" Profile : has
User "1" *-- "1" Dashboard : has
User "1" *-- "0..*" Habit : manages
User "1" *-- "0..*" Goal : defines
User "1" *-- "0..*" UnlockedAchievement : unlocks

Habit "1" *-- "0..*" Activity : records

Habit "0..1" <-- "0..*" Goal : belongs to

Dashboard ..> Habit : shows
Dashboard ..> Activity : visualizes

Report ..> Activity : analyzes
User ..> Report : generates

Achievement "1" -- "0..*" UnlockedAchievement : defined by

Goal ..> GoalStatus

```

---
