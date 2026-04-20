<script setup>
import { computed, ref } from 'vue';
import { getAvatarImageUrl, is3DAvatar, expandAvatarUrl } from '../utils/avatar';

const props = defineProps({
  level: { type: Number, default: 1 },
  name: { type: String, default: 'Hero' },
  xp: { type: Number, default: 0 },
  streak: { type: Number, default: 0 },
  size: { type: String, default: 'md' }, // 'sm', 'md', 'lg'
  avatarSeed: { type: String, default: '' }
});

const characterData = computed(() => {
  if (props.level >= 4) return { icon: 'legendary', title: 'Dragon Slayer', cls: 'tier-legendary', color: '#f59e0b' };
  if (props.level >= 3) return { icon: 'epic',      title: 'Warrior',       cls: 'tier-epic',      color: '#8b5cf6' };
  if (props.level >= 2) return { icon: 'rare',      title: 'Guardian',      cls: 'tier-rare',      color: '#3b82f6' };
  return                       { icon: 'common',    title: 'Apprentice',    cls: 'tier-common',    color: '#00e5a0' };
});

const effectiveSeed = computed(() => {
  return props.avatarSeed || characterData.value.title.replace(' ', '');
});

const nextLevelThreshold = computed(() => {
  return props.level * 2000;
});

const xpDisplay = computed(() => {
  return `${props.xp.toLocaleString()} / ${nextLevelThreshold.value.toLocaleString()}`;
});

const xpProgress = computed(() => {
  return ((props.xp % 2000) / 2000) * 100;
});

const isModalOpen = ref(false);
const viewerError = ref('');

const open3DModel = () => {
  isModalOpen.value = true;
  viewerError.value = '';
};

const close3DModel = () => {
  isModalOpen.value = false;
};

// --- Model Viewer Events ---
const onModelError = (event) => {
  console.error("3D Viewer Error:", event);
  viewerError.value = `Sync Failed: ${event.detail?.type || 'Protocol Error'}`;
};

const onModelLoad = () => {
  viewerError.value = '';
};

// AI-Generated Stats Analysis
const strengths = [
  { name: 'Consistency', value: 'S-Rank', desc: 'Maintains daily streaks with robotic precision.' },
  { name: 'Focus Window', value: 'Morning', desc: '92% efficiency boost before 10:00 AM.' }
];

const weaknesses = [
  { name: 'Fatigue Spike', value: 'High', desc: 'Habit velocity drops significantly on Sundays.' },
  { name: 'Mindfulness', value: 'D-Rank', desc: 'Neural scans show high resistance to meditation.' }
];

// --- Parallax Tilt Logic for 2D Avatars ---
const tiltX = ref(0);
const tiltY = ref(0);
const tiltRef = ref(null);

const handleMouseMove = (e) => {
  if (!tiltRef.value || is3DAvatar(effectiveSeed.value)) return;
  
  const rect = tiltRef.value.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  
  // console.log("Neural Link: Tracking Mouse...");
  tiltY.value = ((x - centerX) / centerX) * 35;
  tiltX.value = ((y - centerY) / centerY) * -35;
};

const resetTilt = () => {
  tiltX.value = 0;
  tiltY.value = 0;
};
</script>

<template>
  <div class="character-card" :class="[`size-${size}`, characterData.cls]">
    <!-- Background Accents -->
    <div class="card-glow" :style="{ background: `radial-gradient(circle at 0% 50%, ${characterData.color}11 0%, transparent 70%)` }"></div>
    <div class="tier-indicator" :style="{ background: characterData.color }"></div>

    <div class="card-main-layout">
      <!-- Left: Avatar Section -->
      <div class="avatar-section">
        <div class="avatar-ring clickable" @click="open3DModel" :style="{ borderColor: `${characterData.color}44` }">
          <img :src="getAvatarImageUrl(effectiveSeed)" class="avatar-img" alt="Hero Avatar" />
          <div class="hover-scan" :style="{ background: `${characterData.color}22` }">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width: 20px; height: 20px;">
              <path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/>
            </svg>
          </div>
        </div>
        <!-- Overlapping Level Badge -->
        <div class="level-badge" :style="{ background: `${characterData.color}22`, color: characterData.color, borderColor: `${characterData.color}44` }">
          LV. {{ level }}
        </div>
      </div>

      <!-- Right: Info Section -->
      <div class="content-section">
        <header class="content-header">
          <div class="header-left">
            <span class="tier-label" :style="{ color: characterData.color }">{{ characterData.title }}</span>
            <h2 class="hero-name">{{ name }}</h2>
          </div>
          <div class="header-right">
            <div class="streak-badge" v-if="streak > 0">
              <span class="flame">🔥</span> {{ streak }}d
            </div>
          </div>
        </header>

        <div class="xp-container">
          <div class="xp-meta">
            <span class="xp-label">EXPERIENCE POINTS</span>
            <span class="xp-fraction">{{ xpDisplay }} XP</span>
          </div>
          <div class="xp-track">
            <div class="xp-fill" :style="{ width: xpProgress + '%', background: characterData.color }">
              <div class="xp-glow" :style="{ background: characterData.color }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 3D Hologram Modal -->
    <Teleport to="body">
      <transition name="hologram-fade">
        <div v-if="isModalOpen" class="hologram-backdrop" @click.self="close3DModel">
          <div class="hologram-modal" :style="{ '--accent': characterData.color }">
            <button class="close-btn" @click="close3DModel">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
            
            <div class="hologram-layout">
              <!-- Stage Area -->
              <div class="stage-container">
                <header class="stage-header">
                  <div class="status-indicator">
                    <span class="pulse-dot"></span>
                    <span class="status-text">NEURAL_LINK: ACTIVE</span>
                  </div>
                </header>

                <div class="hologram-stage" ref="tiltRef" @mousemove="handleMouseMove" @mouseleave="resetTilt">
                  <div class="grid-floor"></div>
                  
                  <model-viewer 
                    v-if="is3DAvatar(effectiveSeed)"
                    class="viewer-3d"
                    :src="expandAvatarUrl(effectiveSeed).split('|||')[0]"
                    camera-controls
                    auto-rotate
                    autoplay
                    shadow-intensity="1"
                    environment-image="neutral"
                    exposure="1.5"
                    camera-orbit="0deg 75deg 2.8m"
                    camera-target="0m 1.2m 0m"
                    field-of-view="35deg"
                    @error="onModelError"
                    @load="onModelLoad"
                  >
                    <div slot="progress-bar" class="custom-loader">
                      <div class="loader-bar" :style="{ background: characterData.color }"></div>
                    </div>
                  </model-viewer>

                  <div v-else class="preview-2d" :style="{ transform: `rotateX(${tiltX}deg) rotateY(${tiltY}deg)`, transition: 'transform 0.1s ease-out' }">
                    <img :src="getAvatarImageUrl(effectiveSeed)" alt="Hero Preview" />
                  </div>
                  
                  <div class="hologram-scanline"></div>
                </div>

                <footer class="stage-footer">
                  <div class="data-pill">CLASS: {{ characterData.title }}</div>
                  <div class="data-pill status">STABLE</div>
                </footer>
              </div>

              <!-- Intel Area -->
              <div class="intel-panel">
                <div class="panel-accent" :style="{ background: characterData.color }"></div>
                
                <header class="intel-header" style="display: flex">
                  <div class="page-icon"><svg class="glow-icon glow-white" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg></div>
                  <div><h2 class="intel-title">Performance_Diagnostics</h2></div>
                </header>

                <div class="intel-scroll">
                  <section class="intel-section">
                    <h4 class="section-label">STRENGTHS</h4>
                    <div class="intel-card" v-for="s in strengths" :key="s.name">
                      <div class="intel-card-top">
                        <span class="intel-name">{{ s.name }}</span>
                        <span class="intel-rank success">{{ s.value }}</span>
                      </div>
                      <p class="intel-desc">{{ s.desc }}</p>
                      <div class="intel-meter"><div class="meter-fill success" style="width: 85%"></div></div>
                    </div>
                  </section>

                  <section class="intel-section">
                    <h4 class="section-label">WEAKNESSES</h4>
                    <div class="intel-card" v-for="w in weaknesses" :key="w.name">
                      <div class="intel-card-top">
                        <span class="intel-name">{{ w.name }}</span>
                        <span class="intel-rank danger">{{ w.value }}</span>
                      </div>
                      <p class="intel-desc">{{ w.desc }}</p>
                      <div class="intel-meter"><div class="meter-fill danger" style="width: 35%"></div></div>
                    </div>
                  </section>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<style scoped>
/* ── Main Card Design ── */
.character-card {
  position: relative; overflow: hidden;
  padding: 24px; border-radius: 20px;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.character-card:hover { border-color: rgba(255, 255, 255, 0.12); transform: translateY(-2px); background: rgba(15, 23, 42, 0.6); }

.card-glow { position: absolute; inset: 0; pointer-events: none; }
.tier-indicator { position: absolute; top: 0; left: 0; width: 4px; height: 100%; opacity: 0.6; }

.card-main-layout { display: flex; align-items: center; gap: 24px; position: relative; z-index: 1; }

/* Avatar Section with Anchored Badge */
.avatar-section { 
  flex-shrink: 0; 
  position: relative; 
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-ring {
  width: 84px; height: 84px; border-radius: 50%;
  border: 2px solid; background: #000;
  display: flex; align-items: center; justify-content: center;
  overflow: hidden; cursor: pointer; position: relative;
  z-index: 2;
}
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.hover-scan {
  position: absolute; inset: 0; display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.2s; color: #fff;
}
.avatar-ring:hover .hover-scan { opacity: 1; }

.level-badge {
  position: absolute !important;
  bottom: -12% !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  z-index: 5 !important;
  
  font-size: 0.6rem; 
  font-weight: 900; 
  padding: 2px 8px; 
  border-radius: 6px;
  border: 1px solid; 
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.4);
}

/* Content */
.content-section { flex: 1; min-width: 0; }
.content-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }

.tier-label { font-size: 0.65rem; font-weight: 900; letter-spacing: 2px; text-transform: uppercase; display: block; margin-bottom: 4px; }
.hero-name { font-size: 1.5rem; font-weight: 800; color: #fff; margin: 0; letter-spacing: -0.5px; }

.header-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }
.streak-badge { font-size: 0.85rem; font-weight: 700; color: #fff; background: rgba(255,255,255,0.05); padding: 4px 10px; border-radius: 8px; }

/* XP Bar */
.xp-container { margin-top: auto; }
.xp-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.xp-label { font-size: 0.6rem; font-weight: 800; color: rgba(255,255,255,0.3); letter-spacing: 1px; }
.xp-fraction { font-size: 0.75rem; font-weight: 700; color: rgba(255,255,255,0.7); font-family: monospace; }

.xp-track { height: 6px; background: rgba(255,255,255,0.05); border-radius: 100px; overflow: hidden; position: relative; }
.xp-fill { height: 100%; border-radius: 100px; position: relative; transition: width 1s cubic-bezier(0.34, 1.56, 0.64, 1); }
.xp-glow { position: absolute; right: 0; top: 0; bottom: 0; width: 20px; filter: blur(8px); opacity: 0.5; }

/* ── Modal Design ── */
.hologram-backdrop { position: fixed; inset: 0; background: rgba(1,4,12,0.95); backdrop-filter: blur(20px); display: flex; align-items: center; justify-content: center; z-index: 99999; }
.hologram-modal { background: #020617; border: 1px solid rgba(255,255,255,0.08); width: 95%; max-width: 960px; height: 600px; border-radius: 24px; overflow: hidden; display: flex; }
.hologram-layout { display: flex; width: 100%; height: 100%; }

.stage-container { flex: 1.4; display: flex; flex-direction: column; background: #000; border-right: 1px solid rgba(255,255,255,0.05); }
.stage-header { padding: 20px 30px; border-bottom: 1px solid rgba(255,255,255,0.03); }
.status-indicator { display: flex; align-items: center; gap: 8px; font-family: monospace; font-size: 11px; color: var(--accent); }
.pulse-dot { width: 6px; height: 6px; background: var(--accent); border-radius: 50%; animation: pulse 2s infinite; }

.hologram-stage { 
  flex: 1; position: relative; min-height: 450px; display: flex; align-items: center; justify-content: center; 
  overflow: hidden; perspective: 1000px; transition: background 0.5s ease;
}
.hologram-stage:hover { background: rgba(255,255,255,0.02); }
.viewer-3d { width: 100%; height: 500px; z-index: 10; }
.grid-floor { position: absolute; bottom: 0; width: 100%; height: 100px; background: linear-gradient(to top, rgba(255,255,255,0.03), transparent); pointer-events: none; }
.preview-2d { position: relative; z-index: 2; width: 280px; height: 280px; display: flex; align-items: center; justify-content: center; transform-style: preserve-3d; pointer-events: none; }
.preview-2d img { width: 100%; height: 100%; object-fit: contain; filter: drop-shadow(0 0 20px var(--accent)); }
.hologram-scanline { position: absolute; top: 0; left: 0; width: 100%; height: 2px; background: rgba(255,255,255,0.1); animation: scan 4s linear infinite; z-index: 15; pointer-events: none; }

.stage-footer { padding: 15px 30px; display: flex; gap: 15px; border-top: 1px solid rgba(255,255,255,0.03); }
.data-pill { font-family: monospace; font-size: 10px; color: var(--muted); border: 1px solid rgba(255,255,255,0.1); padding: 2px 8px; border-radius: 4px; }
.data-pill.status { color: var(--accent); }

.intel-panel { flex: 1; padding: 40px; position: relative; background: rgba(255,255,255,0.01); display: flex; flex-direction: column; }
.panel-accent { position: absolute; top: 0; left: 0; bottom: 0; width: 3px; }
.intel-title { font-family: var(--ff-head); font-size: 2 rem; color: #fff; margin-bottom: 25px; margin-top: -1%; margin-left: 5%}
.intel-scroll { flex: 1; overflow-y: auto; }
.section-label { font-size: 0.7rem; color: var(--muted); margin-bottom: 15px; letter-spacing: 1px; }
.intel-card { background: rgba(255,255,255,0.03); padding: 15px; border-radius: 10px; margin-bottom: 20px; }
.intel-card-top { display: flex; justify-content: space-between; margin-bottom: 5px; }
.intel-name { font-weight: 700; color: #fff; }
.intel-rank { font-size: 0.7rem; font-weight: 800; }
.success { color: #10b981; }
.danger { color: #f43f5e; }
.intel-desc { font-size: 0.8rem; color: var(--muted); margin-bottom: 10px; }
.intel-meter { height: 2px; background: rgba(255,255,255,0.05); }
.meter-fill { height: 100%; }
.meter-fill.success { background: #10b981; }
.meter-fill.danger { background: #f43f5e; }

.close-btn { position: absolute; top: 20px; right: 20px; width: 32px; height: 32px; color: #fff; background: rgba(255,255,255,0.05); border-radius: 50%; cursor: pointer; z-index: 100; display: grid; place-items: center; }

@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }
@keyframes scan { 0% { top: 0; } 100% { top: 100%; } }

/* Transitions */
.hologram-fade-enter-active, .hologram-fade-leave-active { transition: all 0.4s ease; }
.hologram-fade-enter-from, .hologram-fade-leave-to { opacity: 0; transform: scale(0.95); }
</style>
