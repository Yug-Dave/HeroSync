<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { getAllAchievements, getUnlockedAchievements, evaluateAchievements } from '../api/achievements';

const achievements = ref([]);
const unlockedMap  = ref({});
const loading      = ref(false);
const error        = ref('');
const filter       = ref('all'); // 'all' | 'unlocked' | 'locked'

function badgeIcon(achievement, index) {
  if (achievement.iconCode) return achievement.iconCode;
  
  const rule = (achievement.ruleType || '').toUpperCase();
  if (rule.includes('STREAK'))  return 'flame';
  if (rule.includes('TOTAL'))   return 'star';
  if (rule.includes('PERFECT')) return 'diamond';
  if (rule.includes('DAILY'))   return 'sun';
  if (rule.includes('WEEK'))    return 'calendar';
  
  const icons = ['trophy', 'sword', 'shield', 'target', 'award'];
  return icons[index % icons.length];
}

function getRarity(achievement) {
  const th = achievement.threshold || 0;
  if (th >= 100) return 'legendary';
  if (th >= 50) return 'epic';
  if (th >= 10) return 'rare';
  return 'common';
}

function getRarityLabel(rarity) {
  switch (rarity) {
    case 'legendary': return 'Legendary';
    case 'epic': return 'Epic';
    case 'rare': return 'Rare';
    default: return 'Common';
  }
}

const unlockedCount = computed(() => Object.keys(unlockedMap.value).length);
const totalCount    = computed(() => achievements.value.length);
const progressPct   = computed(() =>
  totalCount.value ? Math.round((unlockedCount.value / totalCount.value) * 100) : 0
);

const filteredAchievements = computed(() => {
  if (filter.value === 'unlocked') return achievements.value.filter(a => !!unlockedMap.value[a.id]);
  if (filter.value === 'locked')   return achievements.value.filter(a => !unlockedMap.value[a.id]);
  return achievements.value;
});

function formatDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' });
}

async function loadAll() {
  loading.value = true; error.value = '';
  try {
    const [all, unlocked] = await Promise.all([getAllAchievements(), getUnlockedAchievements()]);
    achievements.value = all;
    const map = {};
    for (const ua of unlocked) map[ua.achievement.id] = ua.unlockedAt;
    unlockedMap.value = map;
  } catch (e) {
    console.error(e); error.value = 'Failed to load achievements.';
  } finally { loading.value = false; }
}

async function runEvaluation() {
  loading.value = true; error.value = '';
  try { await evaluateAchievements(); await loadAll(); }
  catch (e) { error.value = 'Evaluation failed.'; loading.value = false; }
}

onMounted(async () => {
  await loadAll();
  window.addEventListener('refresh-dashboard', runEvaluation);
});
onUnmounted(() => {
  window.removeEventListener('refresh-dashboard', runEvaluation);
});
</script>

<template>
  <div class="achievements-page">
    <div class="container">

      <!-- Page Header -->
      <div class="page-header">
        <div class="header-left">
          <div class="page-icon"><svg class="glow-icon glow-gold" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg></div>
          <div>
            <h1 class="page-title">Achievement Vault</h1>
            <p class="page-sub">Unlock badges by mastering your quests</p>
          </div>
        </div>
        <div class="header-actions">
          <button class="btn-reload" @click="loadAll" :disabled="loading">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
            Reload
          </button>
          <button class="btn-evaluate" @click="runEvaluation" :disabled="loading">
            <svg v-if="!loading" class="glow-icon glow-green" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="margin-right:4px; vertical-align: middle;"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
            {{ loading ? 'Working…' : 'Evaluate' }}
          </button>
        </div>
      </div>

      <!-- Progress Overview -->
      <div class="progress-card">
        <div class="progress-info">
          <div class="progress-nums">
            <span class="prog-unlocked">{{ unlockedCount }}</span>
            <span class="prog-sep">/</span>
            <span class="prog-total">{{ totalCount }}</span>
            <span class="prog-label">badges unlocked</span>
          </div>
          <span class="prog-pct-badge">{{ progressPct }}%</span>
        </div>
        <div class="progress-track">
          <div class="progress-fill" :style="{ width: progressPct + '%' }"></div>
        </div>
        <div class="progress-milestones">
          <div class="milestone" :class="{ reached: progressPct >= 25 }">25%<br><span>Bronze</span></div>
          <div class="milestone" :class="{ reached: progressPct >= 50 }">50%<br><span>Silver</span></div>
          <div class="milestone" :class="{ reached: progressPct >= 75 }">75%<br><span>Gold</span></div>
          <div class="milestone" :class="{ reached: progressPct >= 100 }">100%<br><span>Legend</span></div>
        </div>
      </div>

      <!-- Filter Tabs -->
      <div class="filter-tabs">
        <button class="filter-tab" :class="{ active: filter === 'all' }" @click="filter = 'all'">
          All ({{ totalCount }})
        </button>
        <button class="filter-tab unlocked-tab" :class="{ active: filter === 'unlocked' }" @click="filter = 'unlocked'">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="vertical-align: middle; margin-right: 4px; margin-bottom: 2px;"><polyline points="20 6 9 17 4 12"/></svg> Unlocked ({{ unlockedCount }})
        </button>
        <button class="filter-tab locked-tab" :class="{ active: filter === 'locked' }" @click="filter = 'locked'">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="vertical-align: middle; margin-right: 4px; margin-bottom: 2px;"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg> Locked ({{ totalCount - unlockedCount }})
        </button>
      </div>

      <!-- Error -->
      <div v-if="error" class="error-box">{{ error }}</div>

      <!-- Skeleton -->
      <div v-if="loading && achievements.length === 0" class="badge-grid">
        <div v-for="n in 6" :key="n" class="skeleton-badge"></div>
      </div>

      <!-- Badge Grid -->
      <div v-else class="badge-grid">
        <div
          v-for="(a, index) in filteredAchievements" :key="a.id"
          class="badge-card"
          :class="[
            { 'is-unlocked': !!unlockedMap[a.id], 'is-locked': !unlockedMap[a.id] },
            `tier-${getRarity(a)}`
          ]"
        >
          <!-- Glow effect for unlocked -->
          <div v-if="!!unlockedMap[a.id]" class="unlock-glow"></div>

          <!-- Rarity Tag -->
          <div class="rarity-tag" :class="`rarity-${getRarity(a)}`">
            {{ getRarityLabel(getRarity(a)) }}
          </div>
          
          <!-- XP Reward Tag -->
          <div v-if="a.xpReward > 0" class="xp-reward-badge">
            +{{ a.xpReward }} XP
          </div>
 
          <!-- Icon -->
          <div class="badge-icon-wrap" :class="{ 'icon-unlocked': !!unlockedMap[a.id] }">
            <span class="badge-emoji">
              <svg v-if="badgeIcon(a, index) === 'flame'" class="glow-icon glow-red" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c-2.2-4.5-1-7.5-1-7.5s3 2 4 5c2 3.5 1 7 1 7a5 5 0 1 1-6.5-2z"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'star'" class="glow-icon glow-gold" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'diamond' || badgeIcon(a, index) === 'shield'" class="glow-icon glow-purple" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 22 8.5 22 15.5 12 22 2 15.5 2 8.5 12 2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'sun' || badgeIcon(a, index) === 'bolt'" class="glow-icon glow-warning" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'calendar'" class="glow-icon glow-blue" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'sword'" class="glow-icon glow-blue" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M16 16l4 4"/><path d="M19 21l2-2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'leaf' || badgeIcon(a, index) === 'brain'" class="glow-icon glow-green" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 3.5 1 9.8a7 7 0 0 1-9 8.2z"/><path d="M12 11.5a4.5 4.5 0 1 1-4.5-4.5A4.5 4.5 0 0 1 12 11.5z"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'award'" class="glow-icon glow-gold" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>
              <svg v-else class="glow-icon glow-gold" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg>
            </span>
          </div>

          <!-- Status -->
          <span class="badge-status" :class="unlockedMap[a.id] ? 'status-unlocked' : 'status-locked'">
            <svg v-if="unlockedMap[a.id]" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" style="vertical-align: middle; margin-right: 2px; margin-bottom: 2px;"><polyline points="20 6 9 17 4 12"/></svg>
            <svg v-else width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" style="vertical-align: middle; margin-right: 2px; margin-bottom: 2px;"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            {{ unlockedMap[a.id] ? 'Unlocked' : 'Locked' }}
          </span>

          <!-- Info -->
          <h3 class="badge-name">{{ a.title }}</h3>
          <p class="badge-desc">{{ a.description }}</p>

          <!-- Meta -->
          <div class="badge-meta">
            <div class="meta-row">
              <span class="meta-label">Rule</span>
              <span class="meta-val">{{ a.ruleType }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">Progress</span>
              <span class="meta-val">{{ a.currentProgress || 0 }} / {{ a.threshold }}</span>
            </div>
            
            <!-- Progress Bar -->
            <div class="card-progress-wrap">
              <div class="card-progress-bar">
                <div 
                  class="card-progress-fill" 
                  :style="{ width: Math.min(100, (((a.currentProgress || 0) / a.threshold) * 100)) + '%' }"
                ></div>
              </div>
            </div>
            <div class="meta-row" v-if="unlockedMap[a.id]">
              <span class="meta-label">Earned</span>
              <span class="meta-val unlocked-date">{{ formatDate(unlockedMap[a.id]) }}</span>
            </div>
          </div>
        </div>

        <!-- Empty state -->
        <div v-if="filteredAchievements.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg v-if="filter === 'unlocked'" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 9.9-1"/></svg>
            <svg v-else width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          </div>
          <p>No {{ filter === 'unlocked' ? 'unlocked' : 'locked' }} achievements yet.</p>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.achievements-page { width: 100%; }
.container { width: min(1100px, 92vw); margin: 0 auto; padding: 28px 0; display: grid; gap: 20px; }

/* Header */
.page-header {
  display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 14px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.page-icon   { font-size: 36px; }
.page-title  { margin: 0; font-family: var(--ff-head); font-size: 1.8rem; font-weight: 700; color: var(--text); line-height: 1; }
.page-sub    { margin: 3px 0 0; font-size: .85rem; color: var(--muted); font-weight: 600; }

.header-actions { display: flex; gap: 10px; }
.btn-reload, .btn-evaluate {
  display: flex; align-items: center; gap: 7px;
  padding: 9px 16px; border-radius: 10px;
  font-family: var(--ff-head); font-size: .9rem; font-weight: 700;
  cursor: pointer; transition: all .2s; letter-spacing: .3px;
}
.btn-reload {
  background: transparent; border: 1px solid var(--border2); color: var(--text);
}
.btn-reload:hover { background: rgba(255,255,255,.06); }
.btn-evaluate {
  background: linear-gradient(135deg, rgba(0,229,160,.15), rgba(59,130,246,.1));
  border: 1px solid rgba(0,229,160,.3); color: var(--accent);
}
.btn-evaluate:hover { border-color: rgba(0,229,160,.5); box-shadow: 0 4px 16px rgba(0,229,160,.2); }
.btn-evaluate:disabled, .btn-reload:disabled { opacity: .5; cursor: not-allowed; }

/* Progress card */
.progress-card {
  background: linear-gradient(135deg, rgba(0,229,160,.06), rgba(59,130,246,.06));
  border: 1px solid rgba(0,229,160,.15);
  border-radius: 16px; padding: 20px 24px;
}
.progress-info {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px;
}
.progress-nums { display: flex; align-items: baseline; gap: 4px; }
.prog-unlocked { font-family: var(--ff-head); font-size: 2rem; font-weight: 700; color: var(--accent); }
.prog-sep      { font-family: var(--ff-head); font-size: 1.5rem; color: var(--muted); }
.prog-total    { font-family: var(--ff-head); font-size: 1.5rem; color: var(--muted); }
.prog-label    { font-size: .85rem; color: var(--muted); margin-left: 8px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.prog-pct-badge {
  background: var(--gold); color: #1a1200;
  font-family: var(--ff-head); font-weight: 800; font-size: .85rem;
  padding: 4px 12px; border-radius: 20px;
}
.progress-track {
  height: 8px; background: rgba(255,255,255,.07); border-radius: 4px; overflow: hidden; margin-bottom: 12px;
}
.progress-fill {
  height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2));
  border-radius: 4px; transition: width .6s ease;
}
.progress-milestones { display: flex; justify-content: space-between; }
.milestone {
  text-align: center; font-size: .7rem; color: var(--muted); font-weight: 700; line-height: 1.3;
  opacity: .5; transition: opacity .3s;
}
.milestone.reached { opacity: 1; color: var(--accent); }
.milestone span { display: block; font-size: .65rem; font-weight: 600; margin-top: 1px; }

/* Filter tabs */
.filter-tabs { display: flex; gap: 8px; flex-wrap: wrap; }
.filter-tab {
  padding: 8px 16px; border-radius: 20px;
  border: 1px solid var(--border); background: rgba(255,255,255,.03);
  color: var(--muted); font-family: var(--ff-body); font-size: .83rem; font-weight: 700;
  cursor: pointer; transition: all .15s;
}
.filter-tab:hover { border-color: var(--border2); color: var(--text); }
.filter-tab.active { background: rgba(255,255,255,.08); border-color: var(--border2); color: var(--text); }
.filter-tab.unlocked-tab.active { background: rgba(0,229,160,.1); border-color: rgba(0,229,160,.3); color: var(--accent); }
.filter-tab.locked-tab.active   { background: rgba(255,255,255,.05); border-color: var(--border2); color: var(--muted); }

/* Error */
.error-box {
  padding: 14px 18px; border-radius: 12px;
  background: rgba(239,68,68,.08); border: 1px solid rgba(239,68,68,.2); color: #fca5a5;
}

/* Badge grid */
.badge-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 14px;
}

/* Badge card */
.badge-card {
  position: relative; overflow: hidden;
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border); border-radius: 16px;
  padding: 24px 20px 20px; display: flex; flex-direction: column; align-items: center;
  text-align: center; gap: 8px;
  transition: all .2s;
}
.badge-card:hover { transform: translateY(-3px); box-shadow: 0 12px 30px rgba(0,0,0,.35); }

/* Tiers */
.badge-card.tier-common.is-unlocked    { border-color: rgba(0,229,160,.25); }
.badge-card.tier-rare.is-unlocked      { border-color: rgba(59,130,246,.25); }
.badge-card.tier-epic.is-unlocked      { border-color: rgba(139,92,246,.3); }
.badge-card.tier-legendary.is-unlocked { border-color: rgba(245,158,11,.35); }

.badge-card.is-locked   { opacity: .65; filter: grayscale(80%); }
.badge-card.is-locked:hover { opacity: .9; filter: grayscale(40%); }

.rarity-tag {
  position: absolute; top: 10px; left: 10px;
  font-size: 0.65rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px;
  padding: 2px 6px; border-radius: 4px; font-family: var(--ff-head);
}
.rarity-common { color: var(--accent); background: rgba(0,229,160,0.1); }
.rarity-rare   { color: var(--accent-2); background: rgba(59,130,246,0.1); }
.rarity-epic   { color: #c084fc; background: rgba(139,92,246,0.1); }
.rarity-legendary { color: var(--gold); background: rgba(245,158,11,0.1); }

.xp-reward-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: var(--accent);
  color: #111;
  font-family: var(--ff-head);
  font-size: 0.65rem;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 4px;
  box-shadow: 0 4px 10px rgba(0,229,160,0.3);
  z-index: 2;
}

.glow-green { color: #10b981; filter: drop-shadow(0 0 5px rgba(16,185,129,0.4)); }

.unlock-glow {
  position: absolute; top: -20px; right: -20px;
  width: 100px; height: 100px; border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,.05) 0%, transparent 70%);
  pointer-events: none;
}
.badge-card.tier-legendary .unlock-glow { background: radial-gradient(circle, rgba(245,158,11,.15) 0%, transparent 70%); }
.badge-card.tier-epic .unlock-glow { background: radial-gradient(circle, rgba(139,92,246,.15) 0%, transparent 70%); }
.badge-card.tier-rare .unlock-glow { background: radial-gradient(circle, rgba(59,130,246,.15) 0%, transparent 70%); }
.badge-card.tier-common .unlock-glow { background: radial-gradient(circle, rgba(0,229,160,.15) 0%, transparent 70%); }

.badge-icon-wrap {
  width: 72px; height: 72px; border-radius: 18px;
  background: rgba(255,255,255,.05); border: 1px solid var(--border);
  display: flex; align-items: center; justify-content: center;
  font-size: 36px; transition: all .2s; margin-top: 10px;
}
.badge-card.is-unlocked .badge-icon-wrap {
  background: rgba(255,255,255,.08);
  animation: floatBadge 3s ease-in-out infinite;
}
.badge-card.tier-legendary.is-unlocked .badge-icon-wrap { border-color: rgba(245,158,11,.3); box-shadow: 0 0 20px rgba(245,158,11,.2); }
.badge-card.tier-epic.is-unlocked .badge-icon-wrap { border-color: rgba(139,92,246,.3); box-shadow: 0 0 20px rgba(139,92,246,.2); }
.badge-card.tier-rare.is-unlocked .badge-icon-wrap { border-color: rgba(59,130,246,.3); box-shadow: 0 0 20px rgba(59,130,246,.2); }
.badge-card.tier-common.is-unlocked .badge-icon-wrap { border-color: rgba(0,229,160,.3); box-shadow: 0 0 20px rgba(0,229,160,.15); }

@keyframes floatBadge {
  0%,100% { transform: translateY(0); }
  50%      { transform: translateY(-4px); }
}

.badge-status {
  font-size: .68rem; font-weight: 800; letter-spacing: .5px;
  padding: 3px 10px; border-radius: 20px; font-family: var(--ff-head);
}
.status-unlocked { background: rgba(0,229,160,.15); color: var(--accent); border: 1px solid rgba(0,229,160,.3); }
.status-locked   { background: rgba(255,255,255,.06); color: var(--muted); border: 1px solid var(--border); }

.badge-name { margin: 0; font-family: var(--ff-head); font-size: 1.05rem; font-weight: 700; color: #fff; }
.badge-desc { margin: 0; font-size: .8rem; color: var(--text-dim); line-height: 1.4; }

.badge-meta {
  width: 100%; margin-top: 8px; padding-top: 12px;
  border-top: 1px solid rgba(255,255,255,.06);
  display: flex; flex-direction: column; gap: 6px;
}
.meta-row { display: flex; justify-content: space-between; align-items: center; }
.meta-label { font-size: .65rem; text-transform: uppercase; color: var(--muted); font-weight: 700; letter-spacing: .5px; }
.meta-val   { font-size: .78rem; color: var(--text); font-weight: 600; font-family: monospace; }
.unlocked-date { color: var(--accent); font-family: var(--ff-body); }

/* Progress Bar on Card */
.card-progress-wrap {
  margin-top: 4px;
}
.card-progress-bar {
  height: 6px;
  background: rgba(255,255,255,0.08);
  border-radius: 3px;
  overflow: hidden;
}
.card-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--accent), var(--accent-2));
  border-radius: 3px;
  transition: width 0.5s ease;
}
.tier-legendary .card-progress-fill { background: linear-gradient(90deg, var(--gold), #fbbf24); }
.tier-epic .card-progress-fill { background: linear-gradient(90deg, #8b5cf6, #c084fc); }
.tier-rare .card-progress-fill { background: linear-gradient(90deg, #3b82f6, #60a5fa); }

/* Skeleton */
.skeleton-badge {
  height: 280px; border-radius: 16px;
  background: rgba(255,255,255,.03);
  animation: shimmer 1.4s ease-in-out infinite;
}
@keyframes shimmer { 0%,100%{opacity:.4} 50%{opacity:.8} }

/* Empty state */
.empty-state {
  grid-column: 1 / -1; text-align: center; padding: 48px;
  background: rgba(255,255,255,.02); border: 1px dashed var(--border); border-radius: 16px;
  color: var(--muted);
}
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-state p { margin: 0; font-size: .95rem; }
</style>
