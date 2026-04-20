<template>
  <div class="achievements-wrap">
    <p v-if="error" class="error-msg">{{ error }}</p>

    <div v-if="loading && achievements.length === 0" class="skeleton-card"></div>

    <div v-else-if="achievements.length === 0" class="empty-badges">
      <div class="empty-icon">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>
      </div>
      <p>No achievements yet. Complete habits to unlock badges!</p>
    </div>

    <div v-else class="carousel-wrapper">
      <button
        class="nav-arrow left"
        @click="scroll('left')"
        :disabled="currentIndex === 0"
        v-if="achievements.length > 1"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 18l-6-6 6-6"/></svg>
      </button>

      <div class="achievements-list" ref="scrollContainer" @scroll="onScroll">
        <div
          v-for="(a, index) in achievements" :key="a.id"
          class="achievement-card"
          :class="{ 'is-unlocked': !!unlockedMap[a.id] }"
        >
          <!-- Badge Icon -->
          <div class="badge-icon-wrap" :class="{ 'icon-unlocked': !!unlockedMap[a.id] }">
            <span class="badge-emoji">
              <svg v-if="badgeIcon(a, index) === 'flame'" :class="unlockedMap[a.id] ? 'glow-icon glow-red' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :style="unlockedMap[a.id] ? '' : 'color:var(--danger)'"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c-2.2-4.5-1-7.5-1-7.5s3 2 4 5c2 3.5 1 7 1 7a5 5 0 1 1-6.5-2z"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'star'" :class="unlockedMap[a.id] ? 'glow-icon glow-gold' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :style="unlockedMap[a.id] ? '' : 'color:var(--gold)'"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'diamond'" :class="unlockedMap[a.id] ? 'glow-icon glow-purple' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :style="unlockedMap[a.id] ? '' : 'color:#a78bfa'"><polygon points="12 2 22 8.5 22 15.5 12 22 2 15.5 2 8.5 12 2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'sun'" :class="unlockedMap[a.id] ? 'glow-icon glow-warning' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :style="unlockedMap[a.id] ? '' : 'color:var(--warning)'"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'calendar'" :class="unlockedMap[a.id] ? 'glow-icon glow-blue' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :style="unlockedMap[a.id] ? '' : 'color:var(--accent)'"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'sword'" :class="unlockedMap[a.id] ? 'glow-icon glow-blue' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M16 16l4 4"/><path d="M19 21l2-2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'shield'" :class="unlockedMap[a.id] ? 'glow-icon glow-green' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'target'" :class="unlockedMap[a.id] ? 'glow-icon glow-green' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
              <svg v-else-if="badgeIcon(a, index) === 'award'" :class="unlockedMap[a.id] ? 'glow-icon glow-gold' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>
              <svg v-else :class="unlockedMap[a.id] ? 'glow-icon glow-gold' : ''" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg>
            </span>
            <div v-if="!!unlockedMap[a.id]" class="glow-ring"></div>
          </div>

          <div class="card-body">
            <div class="card-top">
              <h3 class="title">{{ a.title }}</h3>
              <span class="badge" :class="unlockedMap[a.id] ? 'badge-unlocked' : 'badge-locked'">
                {{ unlockedMap[a.id] ? '✓ Unlocked' : '🔒 Locked' }}
              </span>
            </div>
            <p class="description">{{ a.description }}</p>

            <div class="stats-grid">
              <div class="stat-box">
                <span class="stat-label">Rule</span>
                <span class="stat-value">{{ a.ruleType }}</span>
              </div>
              <div class="stat-box">
                <span class="stat-label">Progress</span>
                <span class="stat-value">{{ a.currentProgress || 0 }} / {{ a.threshold }}</span>
              </div>
              <div class="stat-box full-width progress-container">
                <div class="mini-progress-bar">
                  <div 
                    class="mini-progress-fill" 
                    :style="{ width: Math.min(100, (((a.currentProgress || 0) / a.threshold) * 100)) + '%' }"
                  ></div>
                </div>
              </div>
              <div class="stat-box full-width" v-if="unlockedMap[a.id]">
                <span class="stat-label">Unlocked At</span>
                <span class="stat-value date-value">{{ formatDate(unlockedMap[a.id]) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <button
        class="nav-arrow right"
        @click="scroll('right')"
        :disabled="currentIndex === achievements.length - 1"
        v-if="achievements.length > 1"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 18l6-6-6-6"/></svg>
      </button>

      <div class="dots-container" v-if="achievements.length > 1">
        <button
          v-for="(a, index) in achievements" :key="index"
          class="dot"
          :class="{ active: index === currentIndex, 'dot-unlocked': !!unlockedMap[a.id] }"
          @click="scrollToIndex(index)"
        ></button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllAchievements, getUnlockedAchievements, evaluateAchievements } from '../api/achievements';

const achievements = ref([]);
const unlockedMap  = ref({});
const loading      = ref(false);
const error        = ref('');
const scrollContainer = ref(null);
const currentIndex    = ref(0);

function badgeIcon(achievement, index) {
  const rule = (achievement.ruleType || '').toUpperCase();
  if (rule.includes('STREAK'))  return 'flame';
  if (rule.includes('TOTAL'))   return 'star';
  if (rule.includes('PERFECT')) return 'diamond';
  if (rule.includes('DAILY'))   return 'sun';
  if (rule.includes('WEEK'))    return 'calendar';
  
  const icons = ['trophy', 'sword', 'shield', 'target', 'award'];
  return icons[index % icons.length];
}

async function loadAll() {
  loading.value = true;
  error.value   = '';
  try {
    await evaluateAchievements();
    const [all, unlocked] = await Promise.all([getAllAchievements(), getUnlockedAchievements()]);
    achievements.value = all;
    const map = {};
    for (const ua of unlocked) map[ua.achievement.id] = ua.unlockedAt;
    unlockedMap.value = map;
  } catch (e) {
    console.error(e);
    error.value = 'Failed to load achievements.';
  } finally {
    loading.value = false;
  }
}

function scroll(direction) {
  if (!scrollContainer.value) return;
  const newIndex = direction === 'right'
    ? Math.min(currentIndex.value + 1, achievements.value.length - 1)
    : Math.max(currentIndex.value - 1, 0);
  scrollToIndex(newIndex);
}

function scrollToIndex(index) {
  if (!scrollContainer.value) return;
  const width = scrollContainer.value.offsetWidth;
  scrollContainer.value.scrollTo({ left: index * (width + 16), behavior: 'smooth' });
  currentIndex.value = index;
}

function onScroll() {
  if (!scrollContainer.value) return;
  const index = Math.round(scrollContainer.value.scrollLeft / scrollContainer.value.offsetWidth);
  if (index !== currentIndex.value) currentIndex.value = index;
}

function formatDate(dateString) {
  if (!dateString) return '—';
  return new Date(dateString).toISOString().slice(0, 16).replace('T', ' ');
}

onMounted(loadAll);
defineExpose({ loadAll });
</script>

<style scoped>
.achievements-wrap { width: 100%; display: flex; flex-direction: column; }

.skeleton-card {
  min-width: 100%; height: 200px; border-radius: 14px;
  background: rgba(255,255,255,.03);
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: .5; }
  50%       { opacity: 1; }
}

.empty-badges {
  text-align: center; padding: 24px 16px; color: var(--muted);
}
.empty-icon { font-size: 36px; margin-bottom: 8px; }
.empty-badges p { font-size: .85rem; margin: 0; }

/* Carousel */
.carousel-wrapper {
  position: relative; display: flex; flex-direction: column; gap: 10px; padding: 0 8px;
}
.achievements-list {
  display: flex; gap: 16px;
  overflow-x: auto; scroll-snap-type: x mandatory;
  scroll-behavior: smooth; scrollbar-width: none;
}
.achievements-list::-webkit-scrollbar { display: none; }

/* Card */
.achievement-card {
  min-width: 100%; flex: 0 0 100%;
  scroll-snap-align: center;
  background: rgba(255,255,255,.03);
  border: 1px solid rgba(255,255,255,.07);
  border-radius: 14px;
  display: flex; flex-direction: column; gap: 12px;
  padding: 18px; min-height: 210px;
  transition: border-color .3s;
}
.achievement-card.is-unlocked {
  background: radial-gradient(circle at top left, rgba(0,229,160,.07), transparent 60%),
              rgba(255,255,255,.03);
  border-color: rgba(0,229,160,.25);
}

/* Badge icon */
.badge-icon-wrap {
  width: 56px; height: 56px; border-radius: 14px;
  background: rgba(255,255,255,.05);
  border: 1px solid rgba(255,255,255,.1);
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; position: relative;
  flex-shrink: 0;
}
.badge-icon-wrap.icon-unlocked {
  background: rgba(0,229,160,.1);
  border-color: rgba(0,229,160,.3);
}
.glow-ring {
  position: absolute; inset: -4px;
  border-radius: 18px;
  border: 1px solid rgba(0,229,160,.3);
  animation: glowPulse 2s ease-in-out infinite;
}
@keyframes glowPulse {
  0%, 100% { opacity: .4; transform: scale(1); }
  50%       { opacity: 1; transform: scale(1.05); }
}

.card-body { display: flex; flex-direction: column; flex: 1; }
.card-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; gap: 8px; }

.title { margin: 0; font-family: var(--ff-head); font-size: 1rem; font-weight: 700; color: #fff; }
.description {
  color: #94a3b8; font-size: .83rem; margin: 0 0 14px; line-height: 1.5;
  flex-grow: 1;
}

.badge {
  white-space: nowrap; font-size: .65rem; padding: 3px 9px;
  border-radius: 20px; font-weight: 800; letter-spacing: .4px;
  font-family: var(--ff-head);
}
.badge-locked   { background: rgba(255,255,255,.06); color: var(--muted); border: 1px solid rgba(255,255,255,.1); }
.badge-unlocked { background: rgba(0,229,160,.15); color: var(--accent); border: 1px solid rgba(0,229,160,.3); }

.stats-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 8px;
  padding-top: 12px; border-top: 1px solid rgba(255,255,255,.06);
}
.stat-box { display: flex; flex-direction: column; }
.stat-box.full-width { grid-column: span 2; margin-top: 2px; }
.stat-label { font-size: .62rem; text-transform: uppercase; color: #4b6279; font-weight: 700; margin-bottom: 3px; letter-spacing: .5px; }
.stat-value { font-size: .83rem; color: #c8d6e4; font-weight: 600; font-family: monospace; }
.date-value { color: var(--accent); }

.progress-container { margin-top: 4px; }
.mini-progress-bar {
  height: 4px;
  background: rgba(255,255,255,0.08);
  border-radius: 2px;
  overflow: hidden;
}
.mini-progress-fill {
  height: 100%;
  background: var(--accent);
  border-radius: 2px;
  transition: width 0.4s ease;
}

/* Nav arrows */
.nav-arrow {
  position: absolute; top: 45%; transform: translateY(-50%); z-index: 10;
  width: 28px; height: 28px; border-radius: 50%;
  background: rgba(15,23,42,.5); border: 1px solid rgba(255,255,255,.08);
  color: #fff; display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all .2s; backdrop-filter: blur(4px);
}
.nav-arrow:hover:not(:disabled) { background: var(--accent); border-color: var(--accent); color: #062015; }
.nav-arrow:disabled { opacity: 0; pointer-events: none; }
.nav-arrow.left  { left: -4px; }
.nav-arrow.right { right: -4px; }

/* Dots */
.dots-container { display: flex; justify-content: center; gap: 5px; margin-top: 2px; }
.dot {
  width: 7px; height: 7px; border-radius: 50%; border: none;
  background: rgba(255,255,255,.1); cursor: pointer; padding: 0; transition: all .2s;
}
.dot:hover { background: rgba(255,255,255,.25); }
.dot.active { background: var(--accent); transform: scale(1.3); }
.dot.dot-unlocked { background: rgba(0,229,160,.3); }
.dot.active.dot-unlocked { background: var(--accent); }

.error-msg { color: var(--danger); font-size: .85rem; margin: 0; }
</style>
