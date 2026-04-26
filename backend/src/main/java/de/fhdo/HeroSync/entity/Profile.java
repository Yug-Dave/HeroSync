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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CompanionChoice companionChoice = CompanionChoice.SYNC;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AiProviderChoice aiProvider = AiProviderChoice.GROQ;

  public Profile(User user, String bio, String avatar) {
    this.user = user;
    this.bio = bio;
    this.avatar = avatar;
    this.companionChoice = CompanionChoice.SYNC;
    this.aiProvider = AiProviderChoice.GROQ;
  }
}
