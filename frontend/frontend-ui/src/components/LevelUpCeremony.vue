<script setup>
import { ref, computed, watch, onMounted } from 'vue';

const props = defineProps({
  level:     { type: Number, required: true },
  prevLevel: { type: Number, required: true },
  name:      { type: String, default: 'Hero' }
});
const emit = defineEmits(['done']);

// ── Tier data matching CharacterCard ───────────────────────────────────────
const TIERS = [
  { minLevel: 30, title: 'Dragon Slayer', color: '#f59e0b', glow: 'rgba(245,158,11,.5)',  gradient: 'linear-gradient(135deg,#92400e,#f59e0b,#78350f)', label: 'PRISMATIC' },
  { minLevel: 20, title: 'Legend',        color: '#ef4444', glow: 'rgba(239,68,68,.4)',   gradient: 'linear-gradient(135deg,#7f1d1d,#ef4444,#b91c1c)',  label: 'LEGENDARY' },
  { minLevel: 15, title: 'Champion',      color: '#eab308', glow: 'rgba(234,179,8,.4)',   gradient: 'linear-gradient(135deg,#713f12,#eab308,#92400e)',  label: 'GOLD'      },
  { minLevel: 10, title: 'Warrior',       color: '#8b5cf6', glow: 'rgba(139,92,246,.4)',  gradient: 'linear-gradient(135deg,#4c1d95,#8b5cf6,#6d28d9)',  label: 'EPIC'      },
  { minLevel:  5, title: 'Guardian',      color: '#3b82f6', glow: 'rgba(59,130,246,.4)',  gradient: 'linear-gradient(135deg,#1e3a8a,#3b82f6,#1d4ed8)',  label: 'RARE'      },
  { minLevel:  1, title: 'Apprentice',    color: '#94a3b8', glow: 'rgba(148,163,184,.3)', gradient: 'linear-gradient(135deg,#334155,#94a3b8,#475569)',  label: 'COMMON'    },
];

function getTier(lv) {
  return TIERS.find(t => lv >= t.minLevel) || TIERS[TIERS.length - 1];
}

const newTier  = computed(() => getTier(props.level));
const prevTier = computed(() => getTier(props.prevLevel));
const rankChanged = computed(() => newTier.value.title !== prevTier.value.title);

// Companion messages per tier unlock
const TIER_MESSAGES = {
  'Dragon Slayer': "The system wasn't built for you. And yet, here you are. Dragon Slayer.",
  'Legend':        "Legends don't ask for permission. You've earned the title.",
  'Champion':      "Champion. That word means something now. Make sure it stays that way.",
  'Warrior':       "You've crossed into Warrior territory. The weak don't get here.",
  'Guardian':      "Guardian protocol unlocked. You're building something real.",
  'Apprentice':    "Every legend started exactly here. Welcome to your origin story.",
};
const companionMsg = computed(() =>
  rankChanged.value
    ? (TIER_MESSAGES[newTier.value.title] || `Level ${props.level} reached.`)
    : `Level ${props.level} achieved. Keep compounding.`
);

// ── Animation state ─────────────────────────────────────────────────────────
const phase = ref('flash'); // flash → ring → title → msg → done
const particles = ref([]);

function generateParticles() {
  particles.value = Array.from({ length: 24 }, (_, i) => ({
    id: i,
    angle: (i / 24) * 360,
    distance: 80 + Math.random() * 80,
    size: 3 + Math.random() * 5,
    delay: Math.random() * 0.4,
  }));
}

onMounted(() => {
  generateParticles();
  const seq = [
    [50,  'ring'],
    [600, 'title'],
    [1200,'msg'],
    [4000,'done'],
  ];
  seq.forEach(([ms, p]) => {
    setTimeout(() => {
      if (p === 'done') emit('done');
      else phase.value = p;
    }, ms);
  });
});
</script>

<template>
  <Teleport to="body">
    <div class="lu-overlay" :class="`phase-${phase}`">
      <!-- Flash burst -->
      <div class="lu-flash" :style="{ background: newTier.color }" />

      <!-- Particle ring -->
      <div class="lu-ring-wrap" :class="{ visible: phase !== 'flash' }">
        <div
          v-for="p in particles" :key="p.id"
          class="lu-particle"
          :style="{
            '--angle':    p.angle + 'deg',
            '--dist':     p.distance + 'px',
            '--size':     p.size + 'px',
            '--delay':    p.delay + 's',
            '--color':    newTier.color,
          }"
        />

        <!-- Central glow ring -->
        <div class="lu-glow-ring" :style="{ borderColor: newTier.color, boxShadow: `0 0 40px ${newTier.glow}, 0 0 80px ${newTier.glow}` }" />

        <!-- Level number -->
        <div class="lu-level-num" :style="{ color: newTier.color, textShadow: `0 0 30px ${newTier.glow}` }">
          {{ level }}
        </div>
      </div>

      <!-- Tier title (only on rank change) -->
      <Transition name="lu-slide">
        <div v-if="phase === 'title' || phase === 'msg'" class="lu-title-wrap">
          <div class="lu-level-label">LEVEL UP</div>
          <div v-if="rankChanged" class="lu-rank-badge" :style="{ background: newTier.gradient }">
            {{ newTier.label }} TIER UNLOCKED
          </div>
          <div class="lu-rank-title" :style="{ color: newTier.color, textShadow: `0 0 20px ${newTier.glow}` }">
            {{ newTier.title }}
          </div>
          <div class="lu-hero-name">{{ name }}</div>
        </div>
      </Transition>

      <!-- Companion message -->
      <Transition name="lu-fade">
        <div v-if="phase === 'msg'" class="lu-msg-wrap">
          <div class="lu-msg-bar" :style="{ borderColor: newTier.color }" />
          <p class="lu-msg">{{ companionMsg }}</p>
          <button class="lu-continue" :style="{ borderColor: newTier.color, color: newTier.color }" @click="$emit('done')">
            Continue →
          </button>
        </div>
      </Transition>

      <!-- Scanlines for cinematic feel -->
      <div class="lu-scanlines" />
    </div>
  </Teleport>
</template>

<style scoped>
.lu-overlay {
  position: fixed; inset: 0; z-index: 99999;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  background: rgba(0, 0, 0, 0.96);
  overflow: hidden;
}

/* Scanlines */
.lu-scanlines {
  position: absolute; inset: 0; pointer-events: none;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(255,255,255,.015) 2px,
    rgba(255,255,255,.015) 4px
  );
}

/* Flash */
.lu-flash {
  position: absolute; inset: 0;
  opacity: 0;
  animation: luFlash 0.5s ease-out forwards;
  pointer-events: none;
}
@keyframes luFlash {
  0%   { opacity: 0.8; }
  100% { opacity: 0; }
}

/* Particle ring */
.lu-ring-wrap {
  position: relative; width: 160px; height: 160px;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transform: scale(0.5);
  transition: opacity .4s ease, transform .4s cubic-bezier(.34,1.56,.64,1);
}
.lu-ring-wrap.visible { opacity: 1; transform: scale(1); }

.lu-particle {
  position: absolute;
  width: var(--size); height: var(--size);
  border-radius: 50%;
  background: var(--color);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation: luBurst 1s cubic-bezier(.2,.8,.4,1) var(--delay) both;
}
@keyframes luBurst {
  0%   { transform: translate(-50%,-50%) rotate(var(--angle)) translateX(0); opacity: 1; }
  60%  { opacity: 1; }
  100% { transform: translate(-50%,-50%) rotate(var(--angle)) translateX(var(--dist)); opacity: 0; }
}

.lu-glow-ring {
  position: absolute; width: 120px; height: 120px;
  border-radius: 50%; border: 2px solid;
  animation: luPulse 1.5s ease-in-out infinite;
}
@keyframes luPulse {
  0%,100% { transform: scale(1); opacity: 1; }
  50%     { transform: scale(1.08); opacity: 0.7; }
}

.lu-level-num {
  font-family: var(--ff-head, 'Orbitron', sans-serif);
  font-size: 3.5rem; font-weight: 900; line-height: 1;
  z-index: 2; position: relative;
  animation: luNumPop .4s cubic-bezier(.34,1.56,.64,1) .1s both;
}
@keyframes luNumPop {
  from { transform: scale(0.3); opacity: 0; }
  to   { transform: scale(1);   opacity: 1; }
}

/* Title block */
.lu-title-wrap {
  margin-top: 32px;
  text-align: center;
  display: flex; flex-direction: column; align-items: center; gap: 10px;
}
.lu-level-label {
  font-family: var(--ff-head, 'Orbitron', sans-serif);
  font-size: .7rem; font-weight: 900;
  letter-spacing: 5px; color: #94a3b8;
  text-transform: uppercase;
}
.lu-rank-badge {
  font-family: var(--ff-head, 'Orbitron', sans-serif);
  font-size: .65rem; font-weight: 900;
  letter-spacing: 2px; color: #000;
  padding: 4px 14px; border-radius: 20px;
  text-transform: uppercase;
}
.lu-rank-title {
  font-family: var(--ff-head, 'Orbitron', sans-serif);
  font-size: 2.8rem; font-weight: 900;
  letter-spacing: -1px; line-height: 1;
  text-transform: uppercase;
}
.lu-hero-name {
  font-size: 1rem; color: #64748b; font-weight: 600;
  letter-spacing: 1px; text-transform: uppercase;
}

/* Companion message */
.lu-msg-wrap {
  margin-top: 28px;
  text-align: center;
  max-width: min(480px, 88vw);
  display: flex; flex-direction: column; align-items: center; gap: 14px;
}
.lu-msg-bar {
  width: 40px; height: 2px;
  border-top: 2px solid;
}
.lu-msg {
  font-size: .95rem; color: #94a3b8; line-height: 1.65;
  margin: 0; font-style: italic;
}
.lu-continue {
  margin-top: 8px;
  padding: 10px 28px; border-radius: 8px;
  background: transparent; border: 1px solid;
  font-family: var(--ff-head, 'Orbitron', sans-serif);
  font-size: .8rem; font-weight: 700; letter-spacing: 2px;
  cursor: pointer; transition: all .2s;
  text-transform: uppercase;
}
.lu-continue:hover { background: rgba(255,255,255,.05); }

/* Transitions */
.lu-slide-enter-active { transition: all .5s cubic-bezier(.34,1.56,.64,1); }
.lu-slide-enter-from   { opacity: 0; transform: translateY(24px) scale(.95); }
.lu-fade-enter-active  { transition: all .5s ease .2s; }
.lu-fade-enter-from    { opacity: 0; transform: translateY(12px); }

/* Light theme */
:global(.theme-light) .lu-overlay { background: rgba(15,23,42,.97); }

/* Mobile */
@media (max-width: 480px) {
  .lu-glow-ring { width: 90px; height: 90px; }
  .lu-level-num { font-size: 2.6rem; }
  .lu-rank-title { font-size: 2rem; }
  .lu-msg { font-size: .85rem; }
  .lu-ring-wrap { width: 120px; height: 120px; }
}
</style>
