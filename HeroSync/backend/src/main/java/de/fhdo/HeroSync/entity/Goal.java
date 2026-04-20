package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal")
@Data
@NoArgsConstructor
public class Goal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String title;

  @Column(length = 500)
  private String description;

  @ManyToOne
  @JoinColumn(name = "habit_id")
  private Habit habit;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private Integer targetCount;

  @Column(nullable = false)
  private Integer progressCount = 0;

  @Column(nullable = false)
  private boolean isBoss = false;

  @Column
  private String bossType;

  private LocalDate deadline;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private GoalStatus status = GoalStatus.ACTIVE;

  private LocalDateTime createdAt;
  private LocalDateTime completedAt;
  private LocalDateTime failedAt;

  @PrePersist
  void onCreate() {
    if (createdAt == null) createdAt = LocalDateTime.now();
    if (status == null) status = GoalStatus.ACTIVE;
  }
}
