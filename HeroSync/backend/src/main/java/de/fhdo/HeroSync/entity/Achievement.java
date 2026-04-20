package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievement")
@Data
@NoArgsConstructor
public class Achievement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String title;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  private String ruleType;

  @Column(nullable = false)
  private int threshold;

  @Column(nullable = false)
  private int xpReward;

  private String iconCode;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User targetUser;

  private Integer minLevel;

  public Achievement(String code, String title, String description, String ruleType, int threshold, int xpReward, String iconCode) {
    this.code = code;
    this.title = title;
    this.description = description;
    this.ruleType = ruleType;
    this.threshold = threshold;
    this.xpReward = xpReward;
    this.iconCode = iconCode;
  }
}
