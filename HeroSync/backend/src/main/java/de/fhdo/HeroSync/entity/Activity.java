package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Habit habit;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private int completions = 0;

  public Activity(Habit habit, LocalDate date, int completions) {
    this.habit = habit;
    this.date = date;
    this.completions = completions;
  }
}
