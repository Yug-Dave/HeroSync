<script setup>
import { computed, ref, watch } from 'vue';
import { getAvatarImageUrl, is3DAvatar, expandAvatarUrl } from '../utils/avatar';
import { http } from '../api/http';

const props = defineProps({
  level: { type: Number, default: 1 },
  name: { type: String, default: 'Hero' },
  xp: { type: Number, default: 0 },
  streak: { type: Number, default: 0 },
  size: { type: String, default: 'md' }, // 'sm', 'md', 'lg'
  avatarSeed: { type: String, default: '' }
});

const characterData = computed(() => {
  if (props.level >= 4) return { icon: 'legendary', title: 'Dragon Slayer', cls: 'tier-legendary', color: '#2563eb' };
  if (props.level >= 3) return { icon: 'epic',      title: 'Warrior',       cls: 'tier-epic',      color: '#3b82f6' };
  if (props.level >= 2) return { icon: 'rare',      title: 'Guardian',      cls: 'tier-rare',      color: '#60a5fa' };
  return                       { icon: 'common',    title: 'Apprentice',    cls: 'tier-common',    color: '#93c5fd' };
});

const effectiveSeed = computed(() => {
  return props.avatarSeed || characterData.value.title.replace(' ', '');
});

const nextLevelThreshold = computed(() => {
  return Math.pow(props.level, 2) * 1000;
});

const xpDisplay = computed(() => {
  return `${props.xp.toLocaleString()} / ${nextLevelThreshold.value.toLocaleString()}`;
});

const xpProgress = computed(() => {
  const currentLevelStart = Math.pow(props.level - 1, 2) * 1000;
  const nextLevelStart = Math.pow(props.level, 2) * 1000;
  const range = nextLevelStart - currentLevelStart;
  const progress = props.xp - currentLevelStart;
  return Math.min(Math.max((progress / range) * 100, 0), 100);
});

const isModalOpen = ref(false);
const viewerError = ref('');

const open3DModel = () => {
  isModalOpen.value = true;
  viewerError.value = '';
  runDiagnostics();
};

const close3DModel = () => {
  isModalOpen.value = false;
};

async function runDiagnostics() {
  isDiagnosticsLoading.value = true;
  try {
    const res = await http.post('/ai/diagnostics', {});
    // Response is raw JSON string
    const text = typeof res.data === 'string' ? res.data : JSON.stringify(res.data);
    diagnosticsData.value = JSON.parse(text);
  } catch (e) {
    console.warn('Diagnostics unavailable:', e);
    diagnosticsData.value = null;
  } finally {
    isDiagnosticsLoading.value = false;
  }
}

// --- Model Viewer Events ---
const onModelError = (event) => {
  console.error("3D Viewer Error:", event);
  viewerError.value = `Sync Failed: ${event.detail?.type || 'Protocol Error'}`;
};

const onModelLoad = () => {
  viewerError.value = '';
};

// AI-Generated Stats Analysis (live)
const isDiagnosticsLoading = ref(false);
const diagnosticsData = ref(null);

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
  
  tiltY.value = ((x - centerX) / centerX) * 35;
  tiltX.value = ((y - centerY) / centerY) * -35;
};

const resetTilt = () => {
  tiltX.value = 0;
  tiltY.value = 0;
};
const cardRef = ref(null);
const handleCardMouseMove = (e) => {
  if (!cardRef.value) return;
  const rect = cardRef.value.getBoundingClientRect();
  const x = ((e.clientX - rect.left) / rect.width) * 100;
  const y = ((e.clientY - rect.top) / rect.height) * 100;
  cardRef.value.style.setProperty('--mouse-x', `${x}%`);
  cardRef.value.style.setProperty('--mouse-y', `${y}%`);
};
</script>

<template>
  <div ref="cardRef" @mousemove="handleCardMouseMove" class="character-card" :class="[`size-${size}`, characterData.cls]">
    <!-- Background Accents & Particles -->
    <div class="card-particles">
      <div v-for="i in 6" :key="i" class="particle" :style="{ 
        top: Math.random() * 100 + '%', 
        left: Math.random() * 100 + '%',
        animationDelay: (Math.random() * 5) + 's',
        background: characterData.color 
      }"></div>
    </div>
    
    <!-- Shine Effect -->
    <div class="card-shine"></div>

    <div class="card-glow" :style="{ background: `radial-gradient(circle at 0% 50%, ${characterData.color}15 0%, transparent 70%)` }"></div>
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
              <span class="flame" style="font-size: 1.1em; filter: drop-shadow(0 0 5px rgba(255,122,0,0.6));">🔥</span> {{ streak }} Day Streak
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
                
                <header class="intel-header">
                  <div class="intel-header-main">
                    <div class="page-icon"><svg class="glow-icon glow-white" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg></div>
                    <h2 class="intel-title">Performance_Diagnostics</h2>
                  </div>
                  <button class="close-btn" @click="close3DModel">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                  </button>
                </header>

                <div class="intel-scroll">
                  <!-- Loading Skeleton -->
                  <template v-if="isDiagnosticsLoading">
                    <section class="intel-section">
                      <h4 class="section-label">STRENGTHS</h4>
                      <div class="intel-card" v-for="i in 2" :key="'sk'+i">
                        <div class="skeleton" style="height:14px; width:60%; margin-bottom:8px;"></div>
                        <div class="skeleton" style="height:10px; width:90%;"></div>
                        <div class="skeleton" style="height:2px; width:100%; margin-top:10px;"></div>
                      </div>
                    </section>
                    <section class="intel-section">
                      <h4 class="section-label">WEAKNESSES</h4>
                      <div class="intel-card" v-for="i in 2" :key="'skw'+i">
                        <div class="skeleton" style="height:14px; width:55%; margin-bottom:8px;"></div>
                        <div class="skeleton" style="height:10px; width:85%;"></div>
                      </div>
                    </section>
                  </template>

                  <!-- Live Diagnostics Data -->
                  <template v-else-if="diagnosticsData">
                    <div class="status-row" style="margin-bottom:20px;">
                      <span class="status-pill"
                        :class="{
                          'status-stable':    diagnosticsData.status === 'STABLE',
                          'status-improving': diagnosticsData.status === 'IMPROVING',
                          'status-critical':  diagnosticsData.status === 'CRITICAL'
                        }">
                        ● {{ diagnosticsData.status }}
                      </span>
                      <span class="intel-summary">{{ diagnosticsData.summary }}</span>
                    </div>

                    <section class="intel-section">
                      <h4 class="section-label">STRENGTHS</h4>
                      <div class="intel-card" v-for="s in diagnosticsData.strengths" :key="s.name">
                        <div class="intel-card-top">
                          <span class="intel-name">{{ s.name }}</span>
                          <span class="intel-rank success">{{ s.rank }}</span>
                        </div>
                        <p class="intel-desc">{{ s.detail }}</p>
                        <div class="intel-meter"><div class="meter-fill success" :style="{ width: (s.score || 0) + '%' }"></div></div>
                      </div>
                    </section>

                    <section class="intel-section">
                      <h4 class="section-label">WEAKNESSES</h4>
                      <div class="intel-card" v-for="w in diagnosticsData.weaknesses" :key="w.name">
                        <div class="intel-card-top">
                          <span class="intel-name">{{ w.name }}</span>
                          <span class="intel-rank"
                            :class="{
                              danger:  w.severity === 'High',
                              warning: w.severity === 'Medium',
                              muted:   w.severity === 'Low'
                            }">
                            {{ w.severity }}
                          </span>
                        </div>
                        <p class="intel-desc">{{ w.detail }}</p>
                      </div>
                    </section>

                    <button class="rescan-btn" :disabled="isDiagnosticsLoading" @click="runDiagnostics">
                      {{ isDiagnosticsLoading ? 'Scanning...' : '↻ Re-run Neural Scan' }}
                    </button>
                  </template>

                  <!-- Fallback (API error) -->
                  <template v-else>
                    <p style="color: var(--muted); font-size: 0.85rem;">Diagnostics unavailable — add an AI key to enable.</p>
                    <button class="rescan-btn" @click="runDiagnostics">↻ Re-run Neural Scan</button>
                  </template>
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
  padding: 24px; border-radius: 28px;
  background: var(--card);
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.character-card::before {
  content: "";
  position: absolute; inset: 0;
  background: radial-gradient(circle at var(--mouse-x, 50%) var(--mouse-y, 50%), rgba(var(--accent-rgb), 0.08) 0%, transparent 60%);
  pointer-events: none;
  z-index: 0;
}
.character-card:hover { 
  border-color: var(--accent); 
  transform: translateY(-8px) scale(1.01); 
  box-shadow: var(--shadow-lg), 0 0 20px rgba(var(--accent-rgb), 0.1); 
}


/* ── Shine Animation ── */
.card-shine {
  position: absolute; top: 0; left: -100%; width: 50%; height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.05),
    transparent
  );
  transform: skewX(-20deg);
  pointer-events: none;
  z-index: 2;
}
.character-card:hover .card-shine {
  animation: shineSweep 1.5s ease-in-out infinite;
}
@keyframes shineSweep {
  0% { left: -100%; }
  100% { left: 200%; }
}

/* ── Floating Particles ── */
.card-particles { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }
.particle {
  position: absolute; width: 4px; height: 4px; border-radius: 50%;
  opacity: 0.2;
  animation: floatParticle 6s infinite ease-in-out;
}
@keyframes floatParticle {
  0%, 100% { transform: translate(0, 0); opacity: 0.2; }
  50% { transform: translate(20px, -40px); opacity: 0.5; }
}

.card-glow { position: absolute; inset: 0; pointer-events: none; }
.tier-indicator { position: absolute; top: 0; left: 0; width: 6px; height: 100%; opacity: 0.8; }

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
  border: 2px solid; background: var(--bg);
  display: flex; align-items: center; justify-content: center;
  overflow: hidden; cursor: pointer; position: relative;
  z-index: 2;
  box-shadow: 0 4px 15px rgba(var(--accent-rgb), 0.1);
}
.avatar-img { 
  width: 100%; height: 100%; object-fit: cover; 
  animation: floatAvatar 4s ease-in-out infinite; 
}
@keyframes floatAvatar {
  0%, 100% { transform: translateY(0); filter: drop-shadow(0 4px 8px rgba(0,0,0,0.4)); }
  50% { transform: translateY(-4px); filter: drop-shadow(0 12px 24px rgba(var(--accent-rgb, 0, 229, 160), 0.4)); }
}
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
  background: var(--card) !important;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* Content */
.content-section { flex: 1; min-width: 0; }
.content-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }

.tier-label { font-size: 0.65rem; font-weight: 900; letter-spacing: 2px; text-transform: uppercase; display: block; margin-bottom: 4px; }
.hero-name { font-size: 1.5rem; font-weight: 800; color: var(--text); margin: 0; letter-spacing: -0.5px; }

.header-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }
.streak-badge { 
  font-size: 0.85rem; font-weight: 800; color: var(--accent); 
  background: rgba(var(--accent-rgb), 0.1); padding: 6px 12px; 
  border-radius: 12px; border: 1px solid rgba(var(--accent-rgb), 0.2); 
  box-shadow: 0 0 15px rgba(var(--accent-rgb), 0.1);
}

/* XP Bar */
.xp-container { margin-top: auto; }
.xp-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.xp-label { font-size: 0.6rem; font-weight: 800; color: var(--muted); letter-spacing: 1px; }
.xp-fraction { font-size: 0.75rem; font-weight: 700; color: var(--text-dim); font-family: monospace; }

.xp-track { height: 8px; background: var(--bg); border-radius: 100px; overflow: hidden; position: relative; border: 1px solid var(--border); }
.xp-fill { height: 100%; border-radius: 100px; position: relative; transition: width 1s cubic-bezier(0.34, 1.56, 0.64, 1); }
.xp-glow { position: absolute; right: 0; top: 0; bottom: 0; width: 20px; filter: blur(8px); opacity: 0.3; }

/* ── Modal Design ── */
.hologram-backdrop { 
  position: fixed; inset: 0; 
  background: rgba(1, 4, 12, 0.7); 
  backdrop-filter: blur(10px); 
  display: flex; align-items: center; justify-content: center; 
  z-index: 99999; 
}
.theme-light .hologram-backdrop { background: rgba(240, 244, 255, 0.5); backdrop-filter: blur(12px); }

.hologram-modal { 
  position: relative;
  background: var(--bg2); 
  border: 1px solid var(--border); 
  width: 95%; max-width: 960px; height: 600px; 
  border-radius: 24px; overflow: hidden; display: flex; 
  box-shadow: 0 0 50px rgba(0,0,0,0.3);
}
.theme-light .hologram-modal { box-shadow: 0 20px 60px rgba(0,0,0,0.1); }

.hologram-layout { display: flex; width: 100%; height: 100%; }

.stage-container { 
  flex: 1.4; display: flex; flex-direction: column; 
  background: #000; 
  border-right: 1px solid var(--border); 
}
.theme-light .stage-container { background: #f1f5f9; }

.stage-header { padding: 20px 30px; border-bottom: 1px solid var(--border); }
.status-indicator { display: flex; align-items: center; gap: 8px; font-family: monospace; font-size: 11px; color: var(--accent); }
.pulse-dot { width: 6px; height: 6px; background: var(--accent); border-radius: 50%; animation: pulse 2s infinite; }

.hologram-stage { 
  flex: 1; position: relative; min-height: 450px; display: flex; align-items: center; justify-content: center; 
  overflow: hidden; perspective: 1000px; transition: background 0.5s ease;
}
.hologram-stage:hover { background: rgba(var(--accent-rgb), 0.03); }
.viewer-3d { width: 100%; height: 500px; z-index: 10; }
.grid-floor { position: absolute; bottom: 0; width: 100%; height: 100px; background: linear-gradient(to top, rgba(var(--accent-rgb), 0.05), transparent); pointer-events: none; }
.preview-2d { position: relative; z-index: 2; width: 280px; height: 280px; display: flex; align-items: center; justify-content: center; transform-style: preserve-3d; pointer-events: none; }
.preview-2d img { width: 100%; height: 100%; object-fit: contain; filter: drop-shadow(0 0 20px var(--accent)); }
.hologram-scanline { position: absolute; top: 0; left: 0; width: 100%; height: 2px; background: rgba(var(--accent-rgb), 0.1); animation: scan 4s linear infinite; z-index: 15; pointer-events: none; }

.stage-footer { padding: 15px 30px; display: flex; gap: 15px; border-top: 1px solid var(--border); }
.data-pill { font-family: monospace; font-size: 10px; color: var(--muted); border: 1px solid var(--border); padding: 2px 8px; border-radius: 4px; }
.data-pill.status { color: var(--accent); }

.intel-panel { flex: 1; padding: 48px; position: relative; background: var(--card); display: flex; flex-direction: column; }
.panel-accent { position: absolute; top: 0; left: 0; bottom: 0; width: 4px; }
.intel-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 48px; gap: 30px; }
.intel-header-main { display: flex; align-items: center; gap: 24px; }
.intel-title { font-family: var(--ff-head); font-size: 2.2rem; color: var(--text); margin: 0; letter-spacing: -0.5px; font-weight: 800; }
.intel-scroll { flex: 1; overflow-y: auto; }
.section-label { font-size: 0.7rem; color: var(--muted); margin-bottom: 15px; letter-spacing: 1px; }
.intel-card { background: var(--card2); padding: 15px; border-radius: 10px; margin-bottom: 20px; border: 1px solid var(--border); }
.intel-card-top { display: flex; justify-content: space-between; margin-bottom: 5px; }
.intel-name { font-weight: 700; color: var(--text); }
.intel-rank { font-size: 0.7rem; font-weight: 800; }
.success { color: #10b981; }
.danger { color: #f43f5e; }
.intel-desc { font-size: 0.8rem; color: var(--muted); margin-bottom: 10px; }
.intel-meter { height: 2px; background: var(--border); }
.meter-fill { height: 100%; }
.meter-fill.success { background: #10b981; }
.meter-fill.danger { background: #f43f5e; }

/* ── New Diagnostics Styles ── */
.skeleton {
  background: linear-gradient(90deg, var(--border) 25%, var(--border2) 50%, var(--border) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 4px;
}
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

.status-row { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.status-pill {
  font-family: monospace; font-size: 0.72rem; font-weight: 700;
  padding: 3px 10px; border-radius: 20px; letter-spacing: 1px;
}
.status-stable    { background: rgba(16,185,129,0.12); color: #10b981; border: 1px solid rgba(16,185,129,0.3); }
.status-improving { background: rgba(59,130,246,0.12); color: #3b82f6; border: 1px solid rgba(59,130,246,0.3); }
.status-critical  { background: rgba(239,68,68,0.12);  color: #ef4444; border: 1px solid rgba(239,68,68,0.3); }

.intel-summary { font-size: 0.8rem; color: var(--text-dim); font-style: italic; }
.warning { color: #f59e0b; }
.muted   { color: var(--muted); }

.rescan-btn {
  margin-top: 20px; width: 100%;
  padding: 10px; border-radius: 10px;
  border: 1px solid var(--border); background: rgba(255,255,255,0.03);
  color: var(--text-dim); font-family: var(--ff-head); font-size: 0.8rem;
  font-weight: 700; cursor: pointer; transition: all 0.2s;
}
.rescan-btn:hover:not(:disabled) { border-color: var(--accent); color: var(--accent); background: rgba(0,229,160,0.05); }
.rescan-btn:disabled { opacity: 0.5; cursor: not-allowed; }


.close-btn { 
  width: 32px; height: 32px; border-radius: 10px; 
  color: var(--muted); background: var(--card2); border: 1px solid var(--border); 
  cursor: pointer; display: flex; align-items: center; justify-content: center; 
  transition: all 0.2s; flex-shrink: 0;
}
.close-btn:hover { background: var(--border); color: var(--text); transform: scale(1.05); }


@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }
@keyframes scan { 0% { top: 0; } 100% { top: 100%; } }

/* Transitions */
.hologram-fade-enter-active { transition: all .3s cubic-bezier(.34,1.56,.64,1); }
.hologram-fade-leave-active { transition: all .2s ease; }
.hologram-fade-enter-from, .hologram-fade-leave-to { opacity: 0; }
.hologram-fade-enter-from .hologram-modal { transform: scale(.9) translateY(20px); }
@media (max-width: 768px) {
  .character-card { padding: 16px; border-radius: 20px; }
  .card-main-layout { 
    flex-direction: row !important; 
    text-align: left !important; 
    gap: 16px !important; 
    align-items: center;
  }
  .avatar-ring { width: 64px !important; height: 64px !important; }
  .level-badge { 
    bottom: -8% !important; 
    font-size: 0.5rem !important; 
    padding: 1px 6px !important; 
  }
  .content-header { 
    flex-direction: row !important; 
    justify-content: space-between !important;
    align-items: center !important; 
    margin-bottom: 8px !important;
  }
  .hero-name { font-size: 1.1rem !important; }
  .tier-label { font-size: 0.55rem !important; margin-bottom: 2px !important; }
  .streak-badge { font-size: 0.7rem !important; padding: 4px 8px !important; }
  .xp-meta { flex-direction: row !important; justify-content: space-between !important; }
  .xp-fraction { font-size: 0.65rem !important; }
  .xp-track { height: 6px !important; }
  
  .hologram-layout { flex-direction: column; overflow-y: auto; }
  .hologram-modal { height: 90vh; max-height: none; overflow: hidden; }
  .stage-container { flex: none; height: 320px; border-right: none; border-bottom: 1px solid var(--border); }
  .intel-panel { padding: 24px !important; }
  .intel-header { margin-bottom: 20px !important; }
  .intel-title { font-size: 1.2rem !important; }
  .hologram-stage { min-height: 200px !important; }
  .viewer-3d { height: 260px !important; }
}
</style>
