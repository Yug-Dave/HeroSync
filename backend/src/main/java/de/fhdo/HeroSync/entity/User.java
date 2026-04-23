package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Core user entity stored in the {@code users} table.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private boolean emailVerified = false;

  @JsonIgnore
  @Column(unique = true)
  private String verificationToken;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonIgnore
  private Profile profile;

  @Column(nullable = false)
  private int xp = 0;

  public User(String name, String email, String passwordHash) {
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
    if (profile != null) {
      profile.setUser(this);
    }
  }
}
