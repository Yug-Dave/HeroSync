package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "unlocked_achievement")
@Data
@NoArgsConstructor
public class UnlockedAchievement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "achievement_id")
  private Achievement achievement;

  @Column(nullable = false)
  private LocalDateTime unlockedAt;

  public UnlockedAchievement(User user, Achievement achievement) {
    this.user = user;
    this.achievement = achievement;
    this.unlockedAt = LocalDateTime.now();
  }
}
