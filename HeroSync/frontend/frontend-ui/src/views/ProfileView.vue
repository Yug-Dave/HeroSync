<template>
  <main class="main">
    <h1 class="sr-only">My Hero Profile</h1>

    <section class="section">
      <div class="container">
        <div class="grid">
          <!-- Hero Card -->
          <div class="card span-6 hero-card" style="padding: 0; background: transparent; border: none; box-shadow: none;">
            <CharacterCard 
              :name="form.name || 'Unknown Hero'"
              :level="heroLevel"
              :xp="heroXp"
              :streak="heroStreak"
              :avatarSeed="form.avatar"
              size="md"
            >
              <div class="hero-stats">
                <div class="hstat">
                  <div class="hval">{{ completeness }}%</div>
                  <div class="hlbl">Profile Mastery</div>
                </div>
              </div>
            </CharacterCard>

            <div class="graph-container">
              <div class="graph-header">
                <div class="graph-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                </div>
                <div class="graph-title-wrap">
                  <div class="graph-title">Weekly XP Gain</div>
                  <div class="graph-subtitle">Your activity over the last 7 days</div>
                </div>
                <div class="graph-summary">
                  <div class="g-pill">
                    <span class="g-pill-lbl">Avg</span>
                    <span class="g-pill-val">{{ avgXp }}</span>
                  </div>
                  <div class="g-pill">
                    <span class="g-pill-lbl">Peak</span>
                    <span class="g-pill-val">{{ maxXp }}</span>
                  </div>
                </div>
              </div>

              <div class="chart-wrapper">
                <XPChart
                  v-if="dataReady && activityHistory.length > 0"
                  :history="activityHistory"
                />
                <div v-else-if="dataReady" class="no-data-msg">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-bottom:8px; opacity:0.5;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                  <p>No recent activity found.</p>
                </div>
                <div v-else class="loading">Loading Stats...</div>
              </div>
            </div>
          </div>

          <!-- Update profile -->
          <div class="card span-6 edit-card">
            <div class="card-inner">
              <div class="meta mb-12">
                <span class="badge">Edit Lore</span>
              </div>

              <form class="form" @submit.prevent="saveChanges">
                <div>
                  <label class="label">Hero Name</label>
                  <input class="input" type="text" v-model="form.name" disabled />
                </div>

                <div>
                  <label class="label">Choose Your Avatar</label>
                  <div class="avatar-selector">
                    <button 
                      v-for="seed in avatarSeeds" 
                      :key="seed"
                      type="button"
                      class="avatar-option"
                      :class="{ active: form.avatar === seed }"
                      @click="form.avatar = seed"
                    >
                      <img :src="`https://api.dicebear.com/9.x/adventurer/svg?seed=${seed}&backgroundColor=transparent`" alt="Avatar option" />
                    </button>
                    <button 
                      type="button"
                      class="avatar-option random-option"
                      @click="generateRandomAvatar"
                      title="Randomize"
                    >
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="color:var(--accent)"><polyline points="16 3 21 3 21 8"/><line x1="4" y1="20" x2="21" y2="3"/><polyline points="21 16 21 21 16 21"/><line x1="15" y1="15" x2="21" y2="21"/><line x1="4" y1="4" x2="9" y2="9"/></svg>
                    </button>
                    <button 
                      type="button"
                      class="avatar-option rpm-option"
                      @click="showRPM = true"
                      title="Design 3D Avatar"
                    >
                      <span style="font-family: var(--ff-head); font-weight: 800; font-size: 1rem; color: #fff;">3D</span>
                    </button>
                  </div>
                </div>

                <div>
                  <label class="label">Backstory</label>
                  <textarea
                    class="input"
                    rows="3"
                    v-model="form.bio"
                    placeholder="Write your origin story..."
                  ></textarea>
                </div>

                <button class="btn primary btn-save" type="submit" :disabled="saving || !isDirty">
                  {{ saving ? "Updating Lore..." : "Save Origin Story" }}
                </button>

                <div v-if="successMsg" class="helper success-msg">
                  {{ successMsg }}
                </div>

                <div v-if="errorMsg" class="helper error-msg">
                  {{ errorMsg }}
                </div>
              </form>
            </div>
          </div>

          <!-- Achievements Showcase -->
          <div class="card span-12 ach-card">
            <div class="card-inner">
              <div class="achHeader mb-16">
                <div class="meta" style="gap: 10px">
                  <span class="badge trophy-badge">Trophy Room</span>
                  <span class="achCount">{{ unlockedCount }} / {{ totalCount }}</span>
                </div>

                <div class="achActions">
                  <button
                    class="achBtn"
                    type="button"
                    @click="evaluateOnProfile"
                    :disabled="achievementsLoading"
                  >
                    <span class="btn-icon">
                      <svg v-if="!achievementsLoading" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="vertical-align: middle;"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
                    </span>
                    {{ achievementsLoading ? "Evaluating..." : "Evaluate" }}
                  </button>

                  <router-link to="/achievements" class="achLink">
                    View Vault ➔
                  </router-link>
                </div>
              </div>

              <div v-if="achievementsLoading" class="helper loading-text">
                Uncovering achievements...
              </div>

              <div v-else-if="achievementsError" class="achError">
                {{ achievementsError }}
              </div>

              <div v-else>
                <div v-if="latestUnlocked.length === 0" class="empty-state">
                  <div class="empty-icon">
                    <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                  </div>
                  <p>Your trophy room is empty. Complete quests to earn badges!</p>
                </div>

                <ul v-else class="achList">
                  <BadgeTile 
                    v-for="u in latestUnlocked" 
                    :key="u.id"
                    :title="u.achievement.title"
                    :description="u.achievement.description"
                    :ruleType="u.achievement.ruleType"
                    :threshold="u.achievement.threshold"
                    :xpReward="u.achievement.xpReward"
                    :iconCode="u.achievement.iconCode"
                    :unlocked="true"
                    :unlockedAt="u.unlockedAt"
                    size="md"
                  />
                </ul>
              </div>
            </div>
          </div>
          <!-- /Achievements -->
        </div>
      </div>
    </section>

    <AvaturnCreator 
      v-if="showRPM" 
      @close="showRPM = false" 
      @avatar-created="onAvatarCreated" 
    />
  </main>
</template>

<script>
import { me } from "../api/auth";
import { http } from "../api/http";
import { ref } from 'vue';
import { getXpHistory } from "../api/stats";

import {
  getAllAchievements,
  getUnlockedAchievements,
  evaluateAchievements,
} from "../api/achievements";

import CharacterCard from "../components/CharacterCard.vue";
import BadgeTile from "../components/BadgeTile.vue";
import XPChart from "../components/XPChart.vue";
import AvaturnCreator from "../components/AvaturnCreator.vue";
import { compressAvatarUrl } from "../utils/avatar";

export default {
  name: "ProfileView",
  components: {XPChart, CharacterCard, BadgeTile, AvaturnCreator },

  data() {
    return {
      dataReady: false,
      activityHistory: [],
      loading: true,
      saving: false,
      successMsg: "",
      errorMsg: "",

      form: {
        userId: null,
        name: "",
        email: "",
        bio: "",
        avatar: "",
      },

      savedBio: "",
      savedAvatar: "",
      
      heroLevel: 1,
      heroXp: 0,
      heroStreak: 0,

      avatarSeeds: ['Felix', 'Aneka', 'Milo', 'Luna', 'Jasper', 'Kiki'],
      achievements: [],
      unlockedAchievements: [],
      achievementsLoading: false,
      achievementsError: "",
      
      showRPM: false,

      BADGE_EMOJIS: ['🌟','🔥','⚡','🏆','💎','🐉','🌙','🎯','⚔️','🛡️','📅','☀️','💪','🧘','📚'],
    };
  },

  computed: {
    initials() {
      const n = (this.form.name || "").trim();
      return n ? n.charAt(0).toUpperCase() : "H";
    },

    completeness() {
      return this.savedBio && this.savedBio.trim().length > 0 ? 100 : 72;
    },

    isDirty() {
      return (this.form.bio || "") !== (this.savedBio || "") || (this.form.avatar || "") !== (this.savedAvatar || "");
    },

    totalCount() {
      return this.achievements.length;
    },

    unlockedCount() {
      return this.unlockedAchievements.length;
    },

    latestUnlocked() {
      return [...this.unlockedAchievements]
        .sort((a, b) => String(b.unlockedAt).localeCompare(String(a.unlockedAt)))
        .slice(0, 3);
    },

    avgXp() {
      if (!this.activityHistory.length) return 0;
      const recent = this.activityHistory.slice(-7);
      const total = recent.reduce((sum, item) => sum + (item.xpValue || item.xp_value || 0), 0);
      return Math.round(total / recent.length);
    },

    maxXp() {
      if (!this.activityHistory.length) return 0;
      const recent = this.activityHistory.slice(-7);
      return Math.max(...recent.map(item => item.xpValue || item.xp_value || 0));
    },
  },

  async mounted() {
    await this.fetchProfileData();
    window.addEventListener('refresh-dashboard', this.fetchProfileData);
  },

  beforeUnmount() {
    window.removeEventListener('refresh-dashboard', this.fetchProfileData);
  },

  methods: {
    async fetchProfileData() {
      this.loading = true;
      try {
        // 1. Load Profile
        const res = await http.get("/api/profile/me");
        const p = res.data || {};

        this.form.userId = p.userId ?? null;
        this.form.name = p.name ?? "";
        this.form.email = p.email ?? "";
        this.form.bio = p.bio ?? "";
        this.form.avatar = p.avatar ?? "";
        this.savedBio = p.bio ?? "";
        this.savedAvatar = p.avatar ?? "";

        // 2. Load Dashboard & XP History
        // Note: Promise.allSettled returns an array in the order provided
        const [dashRes, statsRes] = await Promise.allSettled([
          http.get("/api/dashboard"),
          getXpHistory()
        ]);

        // Handle Dashboard Result
        if (dashRes.status === 'fulfilled') {
          // Access the data through .value
          const d = dashRes.value.data || {};
          this.heroStreak = d.currentStreak || 0;
          this.heroXp = d.totalXP || 0;
          this.heroLevel = Math.max(1, Math.floor(this.heroXp / 2000) + 1);
        } else {
          console.error("Dashboard API Error:", dashRes.reason);
        }

        // Handle Stats/History Result
        if (statsRes.status === 'fulfilled') {
          // Access the data through .value
          this.activityHistory = statsRes.value || [];
        } else {
          console.error("History API Error:", statsRes.reason);
        }

        await this.loadAchievements();

      } catch (e) {
        console.error("Profile Fetch Error:", e);
        if (e.response?.status === 401 && this.$route.path !== '/login') {
          this.$router.push("/login");
        }
      } finally {
        this.loading = false;
        this.dataReady = true;
      }
    },
    formatDate(s) {
      if (!s) return "—";
      return String(s).replace("T", " ").slice(0, 16);
    },
    
    badgeEmoji(achievement) {
      const rule = (achievement.ruleType || '').toUpperCase();
      if (rule.includes('STREAK'))  return '🔥';
      if (rule.includes('TOTAL'))   return '⭐';
      if (rule.includes('PERFECT')) return '💎';
      if (rule.includes('DAILY'))   return '☀️';
      if (rule.includes('WEEK'))    return '📅';
      return '🏆';
    },

    generateRandomAvatar() {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let result = '';
      for (let i = 0; i < 8; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      this.form.avatar = result;
    },

    onAvatarCreated(payload) {
      // Support new Combo format for 2D previews
      if (payload && payload.model) {
        this.form.avatar = payload.model + '|||' + (payload.image || '');
      } else {
        this.form.avatar = compressAvatarUrl(payload);
      }
      this.showRPM = false;
    },

    async saveChanges() {
      this.successMsg = "";
      this.errorMsg = "";
      this.saving = true;

      try {
        const payload = { 
          name: this.form.name,
          bio: this.form.bio,
          avatar: this.form.avatar 
        };
        const res = await http.put("/api/profile/me", payload);

        const updated = res.data || {};
        const newSavedBio = updated.bio ?? this.form.bio ?? "";
        const newSavedAvatar = updated.avatar ?? this.form.avatar ?? "";

        this.savedBio = newSavedBio;
        this.form.bio = newSavedBio;
        this.savedAvatar = newSavedAvatar;
        this.form.avatar = newSavedAvatar;

        this.successMsg = "Hero lore saved successfully.";
        window.dispatchEvent(new CustomEvent('profile-updated'));
      } catch (e) {
        this.errorMsg =
          e?.response?.data?.message ||
          "Save failed. Please check backend and login session.";
      } finally {
        this.saving = false;
      }
    },

    async loadAchievements() {
      this.achievementsLoading = true;
      this.achievementsError = "";

      try {
        const [all, unlocked] = await Promise.all([
          getAllAchievements(),
          getUnlockedAchievements(),
        ]);
        this.achievements = all;
        this.unlockedAchievements = unlocked;
      } catch (e) {
        console.error(e);
        this.achievementsError =
          "Failed to load achievements. Please check backend/CORS/session.";
      } finally {
        this.achievementsLoading = false;
      }
    },

    async evaluateOnProfile() {
      this.achievementsLoading = true;
      this.achievementsError = "";

      try {
        await evaluateAchievements();
        await this.loadAchievements();
      } catch (e) {
        console.error(e);
        this.achievementsError = "Evaluation failed. Please check backend logs.";
      } finally {
        this.achievementsLoading = false;
      }
    },
  },
};
</script>

<style scoped>
.container { width: min(1100px, 92vw); margin: 0 auto; padding: 28px 0; }
.grid { display: grid; grid-template-columns: repeat(12, 1fr); gap: 20px; }
.span-6 { grid-column: span 6; }
.span-12 { grid-column: span 12; }
@media (max-width: 768px) { .span-6 { grid-column: span 12; } }

.card {
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border); border-radius: 16px;
  overflow: hidden;
}
.card-inner { padding: 24px; }

.badge {
  display: inline-block; padding: 4px 12px; border-radius: 20px;
  font-family: var(--ff-head); font-size: .75rem; font-weight: 800; text-transform: uppercase; letter-spacing: .5px;
  background: rgba(255,255,255,.05); border: 1px solid var(--border); color: var(--muted);
}
.hero-badge { background: rgba(0,229,160,.1); border-color: rgba(0,229,160,.3); color: var(--accent); }
.trophy-badge { background: rgba(245,158,11,.1); border-color: rgba(245,158,11,.3); color: var(--gold); }

/* Hero Card Styles */
.hero-card { border-color: rgba(59,130,246,.25); box-shadow: 0 8px 32px rgba(0,0,0,0.2); }
.hero-header { display: flex; align-items: center; gap: 20px; }
.hero-avatar-wrap { position: relative; }
.hero-avatar {
  width: 80px; height: 80px; border-radius: 24px; border: none;
  background: linear-gradient(135deg, var(--accent-2), var(--accent));
  color: #1a1a2e; font-family: var(--ff-head); font-size: 2rem; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 20px rgba(0,229,160,.2);
}
.hero-level-badge {
  position: absolute; bottom: -8px; left: 50%; transform: translateX(-50%);
  background: var(--gold); color: #000; font-family: var(--ff-head); font-size: .7rem; font-weight: 800;
  padding: 2px 8px; border-radius: 10px; white-space: nowrap; border: 2px solid var(--bg);
}
.hero-name { margin: 0; font-family: var(--ff-head); font-size: 1.6rem; font-weight: 700; color: #fff; }
.hero-class { font-size: .9rem; color: var(--muted); margin-top: 4px; font-weight: 500; }

.stat-row { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 8px; }
.stat-label { font-size: .8rem; color: var(--muted); text-transform: uppercase; letter-spacing: .5px; font-weight: 700; }
.stat-val { font-family: var(--ff-head); font-size: 1.1rem; font-weight: 800; color: var(--accent); }

.progress-track { height: 8px; background: rgba(255,255,255,.05); border-radius: 4px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2)); border-radius: 4px; transition: width .5s ease; }

.graph-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02));
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  margin-top: 20px;
  padding: 20px;
  min-height: 240px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s, border-color 0.2s;
}

.graph-container:hover {
  border-color: rgba(0, 229, 160, 0.3);
  transform: translateY(-2px);
}

.graph-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.graph-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(0, 229, 160, 0.1);
  color: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 15px rgba(0, 229, 160, 0.1);
}

.graph-title-wrap {
  flex: 1;
  min-width: 140px;
}

.graph-title {
  font-family: var(--ff-head);
  font-weight: 700;
  font-size: 1rem;
  color: #fff;
  line-height: 1.2;
}

.graph-subtitle {
  font-size: 0.75rem;
  color: var(--muted);
  font-weight: 500;
  margin-top: 2px;
}

.graph-summary {
  display: flex;
  gap: 8px;
}

.g-pill {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 4px 10px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.g-pill-lbl {
  font-size: 0.6rem;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 800;
  letter-spacing: 0.5px;
}

.g-pill-val {
  font-family: var(--ff-head);
  font-size: 0.85rem;
  font-weight: 800;
  color: var(--accent);
}

.chart-wrapper {
  flex: 1;
  width: 100%;
}

.no-data-msg {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--muted);
  font-size: 0.9rem;
  font-weight: 500;
}


/* Edit Card Styles */
.form { display: flex; flex-direction: column; gap: 16px; margin-top: 16px; }
.label { display: block; margin-bottom: 6px; font-size: .85rem; color: var(--muted); font-weight: 600; }
.input {
  width: 100%; padding: 12px 14px; background: rgba(255,255,255,.03); border: 1px solid var(--border);
  border-radius: 10px; color: var(--text); font-family: var(--ff-body); font-size: .95rem; transition: border-color .2s;
}
.input:focus { outline: none; border-color: var(--accent); background: rgba(255,255,255,.05); }
.input:disabled { opacity: 0.6; cursor: not-allowed; }

/* Avatar Selector */
.avatar-selector {
  display: flex; gap: 8px; flex-wrap: wrap; margin-top: 8px;
}
.avatar-option {
  width: 48px; height: 48px; border-radius: 10px; border: 2px solid var(--border);
  background: rgba(255,255,255,0.03); cursor: pointer; padding: 4px; transition: all 0.2s;
  display: flex; align-items: center; justify-content: center;
}
.avatar-option img { width: 100%; height: 100%; object-fit: contain; }
.avatar-option:hover { border-color: var(--border3); background: rgba(255,255,255,0.06); }
.avatar-option.active { border-color: var(--accent); background: rgba(0,229,160,0.1); }
.random-option { background: rgba(255,255,255,0.05); }
.rpm-option { background: linear-gradient(135deg, rgba(59,130,246,0.3), rgba(139,92,246,0.3)); border-color: rgba(139,92,246,0.5); box-shadow: 0 0 10px rgba(139,92,246,0.2); }
.rpm-option:hover { filter: brightness(1.2); border-color: #8b5cf6; }

.btn-save {
  padding: 12px 20px; border-radius: 10px; border: none; cursor: pointer;
  background: linear-gradient(135deg, var(--accent-2), var(--accent));
  color: #111; font-family: var(--ff-head); font-size: 1rem; font-weight: 800;
  transition: all .2s; margin-top: 8px;
}
.btn-save:hover:not(:disabled) { box-shadow: 0 4px 16px rgba(0,229,160,.3); transform: translateY(-1px); }
.btn-save:disabled { opacity: 0.5; cursor: not-allowed; }

.success-msg { color: var(--accent); padding: 10px; background: rgba(0,229,160,.1); border-radius: 8px; font-size: .9rem; text-align: center; }
.error-msg { color: var(--danger); padding: 10px; background: rgba(239,68,68,.1); border-radius: 8px; font-size: .9rem; text-align: center; }

/* Achievements Showcase */
.achHeader{
  display:flex; align-items:center; justify-content:space-between; gap: 12px; border-bottom: 1px solid var(--border); padding-bottom: 16px;
}

.achCount{
  padding: 4px 12px; border-radius: 20px; background: rgba(59,130,246,.15);
  border: 1px solid rgba(59,130,246,.25); color: #93c5fd; font-weight: 800; font-size: .8rem; font-family: var(--ff-head);
}

.achActions{ display:flex; align-items:center; gap: 16px; }

.achBtn{
  display: flex; align-items: center; gap: 6px;
  height: 36px; padding: 0 16px; border-radius: 12px;
  border: 1px solid rgba(245,158,11,.3); background: rgba(245,158,11,.1);
  color: var(--gold); font-family: var(--ff-head); font-weight: 800; font-size: .85rem; cursor: pointer; transition: all .2s;
}
.achBtn:hover:not(:disabled) { background: rgba(245,158,11,.2); transform: translateY(-1px); }
.achBtn:disabled{ opacity: 0.6; cursor: not-allowed; }
.btn-icon { font-size: 14px; }

.achLink{
  color: var(--muted); font-weight: 700; font-size: .9rem; text-decoration: none; transition: color .2s; font-family: var(--ff-head); text-transform: uppercase; letter-spacing: .5px;
}
.achLink:hover{ color: var(--text); }

.achList{ list-style: none; padding: 0; margin: 16px 0 0; display: grid; gap: 12px; }

.achItem{
  display:flex; align-items: center; justify-content:space-between; gap: 16px;
  padding: 16px; border-radius: 14px; background: rgba(255,255,255,.02); border: 1px solid var(--border);
  transition: transform .2s;
}
.achItem:hover { transform: translateY(-2px); border-color: var(--border2); }

/* Trophy tiers */
.achItem.tier-legendary { border-color: rgba(245,158,11,.3); background: linear-gradient(90deg, rgba(245,158,11,.05), transparent); }
.achItem.tier-epic { border-color: rgba(139,92,246,.3); background: linear-gradient(90deg, rgba(139,92,246,.05), transparent); }
.achItem.tier-rare { border-color: rgba(59,130,246,.3); background: linear-gradient(90deg, rgba(59,130,246,.05), transparent); }

.ach-icon-box {
  width: 48px; height: 48px; border-radius: 12px; background: rgba(255,255,255,.05); border: 1px solid var(--border);
  display: flex; align-items: center; justify-content: center; font-size: 24px; flex-shrink: 0;
}
.achItem.tier-legendary .ach-icon-box { border-color: rgba(245,158,11,.4); box-shadow: 0 0 15px rgba(245,158,11,.15); }
.achItem.tier-epic .ach-icon-box { border-color: rgba(139,92,246,.4); }

.achText{ flex: 1; min-width: 0; }
.achTitle{ color: #fff; font-family: var(--ff-head); font-weight: 700; font-size: 1.05rem; }
.achDesc{ margin-top: 4px; color: var(--muted); font-size: .85rem; line-height: 1.4; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.achDate{ white-space: nowrap; text-align: right; color: var(--muted); font-size: .8rem; font-family: monospace; font-weight: 600; }

.achError{ padding: 12px 14px; border-radius: 12px; background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.25); color: #fecaca; margin-top: 16px; }

.empty-state { text-align: center; padding: 32px; background: rgba(255,255,255,.02); border: 1px dashed var(--border); border-radius: 16px; color: var(--muted); margin-top: 16px; }
.empty-icon { font-size: 40px; margin-bottom: 8px; }
.empty-state p { margin: 0; font-size: .95rem; }

.loading-text { font-style: italic; color: var(--muted); }
</style>
