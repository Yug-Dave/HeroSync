package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dashboard")
@Data
@NoArgsConstructor
public class Dashboard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int dashboardId;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  public Dashboard(User user) {
    this.user = user;
  }
}
