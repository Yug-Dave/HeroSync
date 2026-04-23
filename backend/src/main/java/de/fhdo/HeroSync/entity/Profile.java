package de.fhdo.HeroSync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long profileId;

  @Column(length = 500)
  private String bio;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  private String avatar;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  public Profile(User user, String bio, String avatar) {
    this.user = user;
    this.bio = bio;
    this.avatar = avatar;
  }
}
