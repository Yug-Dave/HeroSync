<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';

const props = defineProps({ active: { type: Boolean, default: false } });
const emit = defineEmits(['complete', 'skip']);

// SVG icons (inline, crisp, professional)
const ICONS = {
  profile:     `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  stats:       `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>`,
  companion:   `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="10" rx="2"/><circle cx="12" cy="5" r="2"/><path d="M12 7v4"/><line x1="8" y1="16" x2="8" y2="16"/><line x1="16" y1="16" x2="16" y2="16"/></svg>`,
  quests:      `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>`,
  activity:    `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`,
  achievement: `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/></svg>`,
  launch:      `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/><path d="m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/><path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/><path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5"/></svg>`,
  next:        `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg>`,
  back:        `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>`,
};

const steps = [
  { target: '[data-tour-step="character"]',    iconKey: 'profile',     title: 'Your Hero Profile',        description: 'Your hero\'s command center. Name, level, XP bar, and streak — all live. Click the card anytime to run a full performance diagnostic: strengths, weaknesses, status.', position: 'bottom' },
  { target: '[data-tour-step="stats"]',        iconKey: 'stats',       title: 'Live Intelligence Panel',  description: 'Real-time operational data: current streak, total XP, daily completion rate, and a 10-day consistency heatmap. Know exactly where you stand at a glance.', position: 'left' },
  { target: '[data-tour-step="companion"]',    iconKey: 'companion',   title: 'AI Companion Briefing',    description: 'Every day your AI companion reads your stats and writes a personalized briefing. A daily mission brief — grounded in your numbers, tailored to your momentum.', position: 'bottom' },
  { target: '[data-tour-step="quests"]',       iconKey: 'quests',      title: 'Active Quests',            description: 'Quests are your daily habits. Complete them to earn XP, extend your streak, and advance your hero. Add your first quest here — every legend starts somewhere.', position: 'right' },
  { target: '[data-tour-step="activity"]',     iconKey: 'activity',    title: 'Activity Log',             description: 'A full record of every completed quest — dates, status, XP earned. Use this to spot patterns, track momentum, and stay accountable to yourself.', position: 'top' },
  { target: '[data-tour-step="achievements"]', iconKey: 'achievement', title: 'Achievement Vault',        description: 'Badges are earned, never handed out. Each one reflects a real milestone. Scroll to see what\'s available and how close you are to unlocking the next one.', position: 'top' },
  { target: '[data-tour-step="quick-actions"]',iconKey: 'launch',      title: 'Command Bar',              description: 'Your quick-launch hub: create quests, set goals, view progress reports, and open the full AI chat panel. Everything you need, one click away.', position: 'top' },
];

const currentStep   = ref(0);
const spotlightRect = ref({ top: 0, left: 0, width: 0, height: 0 });
const tooltipStyle  = ref({});
const isMobile      = ref(false);
const isTransitioning = ref(false);
const tourVisible   = ref(false);

// The AI sidebar is ~60px wide on desktop — reserve that space on the right
const AI_SIDEBAR_W = 70;

const checkMobile = () => { isMobile.value = window.innerWidth < 769; };

function updateSpotlight(idx) {
  const el = document.querySelector(steps[idx].target);
  if (!el) { tooltipStyle.value = { top: '50%', left: '50%', transform: 'translate(-50%,-50%)' }; return; }

  el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  const rect = el.getBoundingClientRect();
  const pad  = 10;

  spotlightRect.value = { top: rect.top - pad, left: rect.left - pad, width: rect.width + pad * 2, height: rect.height + pad * 2 };

  const pos       = steps[idx].position;
  const TW        = 330;   // tooltip width
  const TH        = 240;   // estimated tooltip height
  const margin    = 16;
  const vw        = window.innerWidth - AI_SIDEBAR_W; // exclude AI sidebar
  const vh        = window.innerHeight;

  let top, left;

  if (pos === 'right') {
    left = rect.right + margin;
    top  = rect.top + rect.height / 2 - TH / 2;
  } else if (pos === 'left') {
    left = rect.left - TW - margin;
    top  = rect.top + rect.height / 2 - TH / 2;
  } else if (pos === 'top') {
    left = rect.left + rect.width / 2 - TW / 2;
    top  = rect.top - TH - margin;
  } else { // bottom
    left = rect.left + rect.width / 2 - TW / 2;
    top  = rect.bottom + margin;
  }

  left = Math.max(margin, Math.min(left, vw - TW - margin));
  top  = Math.max(80,     Math.min(top,  vh - TH - 80));

  tooltipStyle.value = { top: `${top}px`, left: `${left}px` };
}

async function goToStep(idx) {
  if (isTransitioning.value) return;
  isTransitioning.value = true;
  currentStep.value = idx;
  await nextTick();
  if (!isMobile.value) updateSpotlight(idx);
  setTimeout(() => { isTransitioning.value = false; }, 300);
}

async function next() {
  if (currentStep.value < steps.length - 1) await goToStep(currentStep.value + 1);
  else finish();
}

function finish() {
  tourVisible.value = false;
  localStorage.setItem('hs_tour_done', '1');
  setTimeout(() => emit('complete'), 300);
}

function skip() {
  tourVisible.value = false;
  localStorage.setItem('hs_tour_done', '1');
  setTimeout(() => emit('skip'), 300);
}

onMounted(() => {
  checkMobile();
  window.addEventListener('resize', checkMobile);
  window.addEventListener('resize', () => { if (!isMobile.value) updateSpotlight(currentStep.value); });
});
onUnmounted(() => window.removeEventListener('resize', checkMobile));

watch(() => props.active, async (val) => {
  if (val) {
    tourVisible.value = true;
    await nextTick();
    if (!isMobile.value) setTimeout(() => updateSpotlight(0), 120);
  }
}, { immediate: true });
</script>

<template>
  <Transition name="tour-fade">
    <div v-if="active && tourVisible" class="tour-overlay" role="dialog" aria-modal="true" aria-label="Welcome Tour">

      <!-- ── DESKTOP ── -->
      <template v-if="!isMobile">
        <svg class="tour-spotlight-svg" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <mask id="spotlight-mask">
              <rect width="100%" height="100%" fill="white"/>
              <rect :x="spotlightRect.left" :y="spotlightRect.top"
                    :width="spotlightRect.width" :height="spotlightRect.height"
                    rx="14" fill="black"/>
            </mask>
          </defs>
          <rect width="100%" height="100%" fill="rgba(0,0,0,0.75)" mask="url(#spotlight-mask)"/>
        </svg>

        <div class="spotlight-ring" :style="{
          top: spotlightRect.top + 'px', left: spotlightRect.left + 'px',
          width: spotlightRect.width + 'px', height: spotlightRect.height + 'px'
        }"/>

        <Transition name="tooltip-pop">
          <div v-if="!isTransitioning" class="tour-tooltip" :style="tooltipStyle">
            <div class="tooltip-header">
              <div class="step-icon-wrap" v-html="ICONS[steps[currentStep].iconKey]"/>
              <span class="step-counter">{{ currentStep + 1 }} / {{ steps.length }}</span>
            </div>
            <h3 class="tooltip-title">{{ steps[currentStep].title }}</h3>
            <p  class="tooltip-desc">{{ steps[currentStep].description }}</p>
            <div class="tour-dots">
              <button v-for="(_, i) in steps" :key="i" class="tour-dot"
                      :class="{ active: i === currentStep, done: i < currentStep }"
                      @click="goToStep(i)" :aria-label="`Go to step ${i + 1}`"/>
            </div>
            <div class="tooltip-actions">
              <button class="tour-btn-skip" @click="skip">Skip Tour</button>
              <button class="tour-btn-next" @click="next">
                <span v-if="currentStep < steps.length - 1">Next <span v-html="ICONS.next"/></span>
                <span v-else>Begin <span v-html="ICONS.launch" style="display:inline-flex;vertical-align:middle"/></span>
              </button>
            </div>
          </div>
        </Transition>
      </template>

      <!-- ── MOBILE ── -->
      <template v-else>
        <div class="tour-mobile-backdrop" @click.self="skip"/>
        <Transition name="card-slide">
          <div v-if="!isTransitioning" class="tour-mobile-card">
            <div class="mobile-card-header">
              <span class="step-counter-mobile">{{ currentStep + 1 }} of {{ steps.length }}</span>
              <button class="mobile-skip-btn" @click="skip">Skip</button>
            </div>
            <div class="mobile-card-body">
              <div class="mobile-step-icon" v-html="ICONS[steps[currentStep].iconKey]"/>
              <h3 class="mobile-step-title">{{ steps[currentStep].title }}</h3>
              <p  class="mobile-step-desc">{{ steps[currentStep].description }}</p>
            </div>
            <div class="mobile-progress-bar">
              <div class="mobile-progress-fill" :style="{ width: ((currentStep + 1) / steps.length * 100) + '%' }"/>
            </div>
            <div class="mobile-card-actions">
              <button v-if="currentStep > 0" class="mobile-btn-back" @click="goToStep(currentStep - 1)">
                <span v-html="ICONS.back"/> Back
              </button>
              <button class="mobile-btn-next" @click="next">
                <span v-if="currentStep < steps.length - 1">Next <span v-html="ICONS.next" style="display:inline-flex;vertical-align:middle"/></span>
                <span v-else>Begin <span v-html="ICONS.launch" style="display:inline-flex;vertical-align:middle"/></span>
              </button>
            </div>
            <div class="tour-dots mobile-dots">
              <button v-for="(_, i) in steps" :key="i" class="tour-dot"
                      :class="{ active: i === currentStep, done: i < currentStep }"
                      @click="goToStep(i)"/>
            </div>
          </div>
        </Transition>
      </template>

    </div>
  </Transition>
</template>

<style scoped>
.tour-overlay { position: fixed; inset: 0; z-index: 9000; pointer-events: all; }

/* SVG backdrop */
.tour-spotlight-svg { position: fixed; inset: 0; width: 100%; height: 100%; pointer-events: all; }

/* Glowing border around target */
.spotlight-ring {
  position: fixed;
  border-radius: 16px;
  border: 2px solid var(--accent);
  box-shadow: 0 0 0 4px rgba(var(--accent-rgb),.15), 0 0 30px rgba(var(--accent-rgb),.35);
  pointer-events: none;
  animation: ringPulse 2s ease-in-out infinite;
  transition: top .4s cubic-bezier(.4,0,.2,1), left .4s cubic-bezier(.4,0,.2,1),
              width .4s cubic-bezier(.4,0,.2,1), height .4s cubic-bezier(.4,0,.2,1);
}
@keyframes ringPulse {
  0%,100% { box-shadow: 0 0 0 4px rgba(var(--accent-rgb),.15), 0 0 30px rgba(var(--accent-rgb),.3); }
  50%     { box-shadow: 0 0 0 8px rgba(var(--accent-rgb),.25), 0 0 50px rgba(var(--accent-rgb),.55); }
}

/* Tooltip card */
.tour-tooltip {
  position: fixed;
  width: 330px;
  background: var(--card);
  border: 1px solid var(--border2);
  border-radius: 20px;
  padding: 22px;
  box-shadow: 0 24px 64px rgba(0,0,0,.45), 0 0 40px rgba(var(--accent-rgb),.07);
  z-index: 9100;
  backdrop-filter: blur(20px);
}
:global(.theme-light) .tour-tooltip {
  background: #fff;
  border-color: rgba(0,0,0,.1);
  box-shadow: 0 16px 48px rgba(0,0,0,.12);
}

.tooltip-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px;
}
.step-icon-wrap {
  width: 40px; height: 40px;
  background: rgba(var(--accent-rgb),.1);
  border: 1px solid rgba(var(--accent-rgb),.2);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: var(--accent);
}
.step-counter {
  font-family: var(--ff-head); font-size: .7rem; font-weight: 700;
  color: var(--muted); letter-spacing: 1px; text-transform: uppercase;
  background: rgba(var(--accent-rgb),.08);
  padding: 4px 10px; border-radius: 20px;
  border: 1px solid rgba(var(--accent-rgb),.15);
}
.tooltip-title { margin: 0 0 10px; font-family: var(--ff-head); font-size: 1.08rem; font-weight: 800; color: var(--text); letter-spacing: .4px; }
.tooltip-desc  { margin: 0 0 18px; font-size: .84rem; line-height: 1.65; color: var(--text-dim); }

/* Dots */
.tour-dots { display: flex; gap: 6px; margin-bottom: 18px; align-items: center; }
.tour-dot  { width: 7px; height: 7px; border-radius: 50%; background: var(--border2); border: none; cursor: pointer; transition: all .25s; padding: 0; }
.tour-dot.active { background: var(--accent); width: 22px; border-radius: 4px; box-shadow: 0 0 8px rgba(var(--accent-rgb),.5); }
.tour-dot.done   { background: rgba(var(--accent-rgb),.4); }

/* Action buttons */
.tooltip-actions { display: flex; gap: 10px; }
.tour-btn-skip {
  flex: 1; padding: 9px 12px; border: 1px solid var(--border2); background: transparent;
  color: var(--muted); border-radius: 10px; font-size: .8rem; font-weight: 600; cursor: pointer; transition: all .2s;
}
.tour-btn-skip:hover { color: var(--text); border-color: var(--border3); }
.tour-btn-next {
  flex: 2; padding: 9px 16px; background: var(--accent); color: #000; border: none;
  border-radius: 10px; font-family: var(--ff-head); font-size: .85rem; font-weight: 800;
  cursor: pointer; transition: all .2s; display: flex; align-items: center; justify-content: center; gap: 6px;
  box-shadow: 0 4px 15px rgba(var(--accent-rgb),.3);
}
.tour-btn-next:hover { transform: translateY(-1px); box-shadow: 0 6px 22px rgba(var(--accent-rgb),.45); }
:global(.theme-light) .tour-btn-next { color: #fff; }

/* ── Mobile ── */
.tour-mobile-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,.65); backdrop-filter: blur(4px); }
.tour-mobile-card {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: var(--card); border-radius: 28px 28px 0 0;
  padding: 28px 24px 36px;
  box-shadow: 0 -20px 60px rgba(0,0,0,.4);
  z-index: 9100; border-top: 1px solid var(--border2);
  max-height: 75dvh; overflow-y: auto;
}
:global(.theme-light) .tour-mobile-card { background: #fff; border-top-color: rgba(0,0,0,.1); }

.mobile-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.step-counter-mobile { font-family: var(--ff-head); font-size: .75rem; font-weight: 700; color: var(--muted); letter-spacing: 1.5px; text-transform: uppercase; }
.mobile-skip-btn { background: transparent; border: 1px solid var(--border2); color: var(--muted); padding: 6px 14px; border-radius: 8px; font-size: .8rem; font-weight: 600; cursor: pointer; }

.mobile-card-body { text-align: center; padding: 0 8px; }
.mobile-step-icon {
  width: 72px; height: 72px; margin: 0 auto 20px;
  background: rgba(var(--accent-rgb),.1);
  border: 1px solid rgba(var(--accent-rgb),.2);
  border-radius: 20px;
  display: flex; align-items: center; justify-content: center;
  color: var(--accent);
}
.mobile-step-icon :deep(svg) { width: 34px; height: 34px; }

.mobile-step-title { font-family: var(--ff-head); font-size: 1.4rem; font-weight: 800; color: var(--text); margin: 0 0 14px; letter-spacing: .5px; }
.mobile-step-desc  { font-size: .92rem; line-height: 1.7; color: var(--text-dim); margin: 0 0 28px; }

.mobile-progress-bar  { height: 4px; background: var(--border); border-radius: 2px; margin-bottom: 24px; overflow: hidden; }
.mobile-progress-fill { height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2)); border-radius: 2px; transition: width .4s cubic-bezier(.4,0,.2,1); }

.mobile-card-actions { display: flex; gap: 12px; margin-bottom: 20px; }
.mobile-btn-back {
  flex: 1; padding: 14px; border: 1px solid var(--border2); background: transparent;
  color: var(--muted); border-radius: 14px; font-family: var(--ff-head); font-size: .9rem; font-weight: 700; cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.mobile-btn-next {
  flex: 2; padding: 14px; background: var(--accent); color: #000; border: none;
  border-radius: 14px; font-family: var(--ff-head); font-size: .95rem; font-weight: 800;
  cursor: pointer; box-shadow: 0 6px 20px rgba(var(--accent-rgb),.3);
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
:global(.theme-light) .mobile-btn-next { color: #fff; }
.mobile-dots { justify-content: center; }

/* Ensure step-icon SVGs size correctly */
.step-icon-wrap :deep(svg) { width: 18px; height: 18px; }
.tour-btn-next :deep(svg)  { width: 14px; height: 14px; }
.mobile-btn-back :deep(svg){ width: 14px; height: 14px; }
.mobile-btn-next :deep(svg){ width: 14px; height: 14px; }

/* Transitions */
.tour-fade-enter-active,.tour-fade-leave-active { transition: opacity .3s ease; }
.tour-fade-enter-from,.tour-fade-leave-to { opacity: 0; }
.tooltip-pop-enter-active { transition: all .3s cubic-bezier(.34,1.56,.64,1); }
.tooltip-pop-leave-active { transition: all .2s ease; }
.tooltip-pop-enter-from { opacity: 0; transform: scale(.92) translateY(6px); }
.tooltip-pop-leave-to   { opacity: 0; transform: scale(.95); }
.card-slide-enter-active { transition: all .35s cubic-bezier(.34,1.56,.64,1); }
.card-slide-leave-active { transition: all .25s ease; }
.card-slide-enter-from   { opacity: 0; transform: translateY(40px); }
.card-slide-leave-to     { opacity: 0; transform: translateY(20px); }
</style>
