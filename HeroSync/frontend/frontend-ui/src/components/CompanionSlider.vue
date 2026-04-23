<script setup>
import { ref } from 'vue';
import { ChevronLeft, ChevronRight, Zap, Shield, Target } from 'lucide-vue-next';

const companions = [
  {
    name: "Omega",
    type: "The Strategist",
    desc: "Minimalist and precise. Omega analyzes your data to find the 1% improvements that lead to exponential growth.",
    image: "/companion_omega.png",
    color: "#3b82f6",
    icon: Target,
    stats: { logic: 98, empathy: 45, energy: 88 }
  },
  {
    name: "Kaelen",
    type: "The Empathetic Guide",
    desc: "Soft and supportive. Kaelen focuses on your mental well-being, ensuring you don't burn out while chasing your goals.",
    image: "/companion_kaelen.png",
    color: "#8b5cf6",
    icon: Shield,
    stats: { logic: 72, empathy: 99, energy: 65 }
  },
  {
    name: "Rex",
    type: "The High-Energy Motivator",
    desc: "Loud and disciplined. Rex is for those who need a push. He turns every habit into a high-stakes competition.",
    image: "/companion_rex.png",
    color: "#f97316",
    icon: Zap,
    stats: { logic: 60, empathy: 30, energy: 99 }
  }
];

const activeIndex = ref(0);

function next() { activeIndex.value = (activeIndex.value + 1) % companions.length; }
function prev() { activeIndex.value = (activeIndex.value - 1 + companions.length) % companions.length; }
</script>

<template>
  <section id="companions" class="companion-section">
    <div class="split-container">
      <!-- Left Side: Section Intro & Navigation -->
      <div class="title-side">
        <div class="hero-pill">Desktop Companions</div>
        <h2 class="title">Meet Your <br/><span class="gradient-text">Sync-Buddy</span></h2>
        <p class="subtitle">An AI companion that lives on your dashboard, evolving alongside your habits.</p>
        
        <div class="slider-nav">
          <button class="nav-btn" @click="prev"><ChevronLeft /></button>
          <div class="dot-wrap">
            <div v-for="(c, i) in companions" :key="i" class="mini-dot" :class="{ active: i === activeIndex }" @click="activeIndex = i"></div>
          </div>
          <button class="nav-btn" @click="next"><ChevronRight /></button>
        </div>
      </div>

      <!-- Right Side: Companion Details -->
      <div class="content-side">
        <transition name="fade-slide" mode="out-in">
          <div :key="activeIndex" class="companion-card" :style="{ '--theme-color': companions[activeIndex].color }">
            <div class="image-area">
              <div class="glow-bg"></div>
              <img :src="companions[activeIndex].image" :alt="companions[activeIndex].name" />
            </div>
            
            <div class="info-area">
              <div class="char-type">{{ companions[activeIndex].type }}</div>
              <h3 class="char-name">{{ companions[activeIndex].name }}</h3>
              <p class="char-desc">{{ companions[activeIndex].desc }}</p>

              <div class="stats-list">
                <div v-for="(val, label) in companions[activeIndex].stats" :key="label" class="stat-row">
                  <div class="stat-info">
                    <span>{{ label }}</span>
                    <span>{{ val }}%</span>
                  </div>
                  <div class="stat-bar"><div class="fill" :style="{ width: val + '%' }"></div></div>
                </div>
              </div>

              <div class="char-cta">
                <component :is="companions[activeIndex].icon" size="20" />
                <span>Expertise in specialized growth</span>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </section>
</template>

<style scoped>
.companion-section {
  height: 100vh; width: 100%;
  display: flex; align-items: center; justify-content: center;
  padding: 100px 5vw 40px;
}

.split-container {
  display: flex; width: 100%; max-width: 1400px; gap: 60px; align-items: center;
}

/* --- LEFT SIDE --- */
.title-side { flex: 0.8; text-align: left; }

.title { font-family: var(--ff-display); font-size: clamp(2.5rem, 5vw, 4rem); color: var(--text); font-weight: 950; line-height: 1.05; margin-bottom: 24px; letter-spacing: -2px; }
.gradient-text { background: linear-gradient(135deg, var(--accent-3), var(--accent)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.subtitle { color: var(--text-dim); font-size: 1.2rem; line-height: 1.6; margin-bottom: 48px; max-width: 450px; }

.slider-nav { display: flex; align-items: center; gap: 24px; }
.nav-btn { width: 50px; height: 50px; border-radius: 14px; background: rgba(255,255,255,0.05); border: 1px solid var(--border); color: var(--text); cursor: pointer; display: grid; place-items: center; transition: all 0.3s; }
.nav-btn:hover { background: var(--accent-3); border-color: var(--accent-3); color: #000; transform: scale(1.1); }
.dot-wrap { display: flex; gap: 10px; }
.mini-dot { width: 8px; height: 8px; border-radius: 4px; background: var(--border2); cursor: pointer; transition: all 0.3s; }
.mini-dot.active { width: 24px; background: var(--accent-3); }

/* --- RIGHT SIDE --- */
.content-side { flex: 1.4; position: relative; }

.companion-card {
  background: rgba(30, 41, 59, 0.5); backdrop-filter: blur(20px);
  border: 1px solid var(--border); border-radius: 32px;
  display: flex; overflow: hidden; min-height: 520px;
  box-shadow: 0 40px 80px rgba(0,0,0,0.5);
}

.image-area { flex: 1; background: #000; position: relative; display: flex; align-items: center; justify-content: center; }
.image-area img { width: 100%; height: 100%; object-fit: cover; }
.glow-bg { position: absolute; inset: 0; background: radial-gradient(circle at center, var(--theme-color) 0%, transparent 75%); opacity: 0.25; }

.info-area { flex: 1.2; padding: 50px; display: flex; flex-direction: column; justify-content: center; text-align: left; }
.char-type { color: var(--theme-color); font-family: var(--ff-head); font-weight: 800; font-size: 0.85rem; letter-spacing: 2px; text-transform: uppercase; margin-bottom: 10px; }
.char-name { font-family: var(--ff-display); font-size: 3rem; font-weight: 950; color: var(--text); margin-bottom: 20px; }
.char-desc { color: var(--text-dim); line-height: 1.6; margin-bottom: 32px; font-size: 1.05rem; }

.stats-list { display: flex; flex-direction: column; gap: 18px; margin-bottom: 32px; }
.stat-info { display: flex; justify-content: space-between; font-family: monospace; font-size: 0.8rem; color: var(--muted); text-transform: uppercase; margin-bottom: 6px; }
.stat-bar { height: 6px; background: rgba(255,255,255,0.05); border-radius: 3px; }
.fill { height: 100%; background: var(--theme-color); border-radius: inherit; box-shadow: 0 0 10px var(--theme-color); transition: width 1s ease; }

.char-cta { display: flex; align-items: center; gap: 12px; color: var(--muted); font-size: 0.9rem; font-style: italic; }

/* Transitions */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.5s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateX(30px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(-30px); }

@media (max-width: 1100px) {
  .split-container { flex-direction: column; gap: 40px; align-items: center; text-align: center; }
  .companion-section { height: auto; padding: 120px 24px 80px; }
  .title-side { text-align: center; display: flex; flex-direction: column; align-items: center; }
  .subtitle { margin-inline: auto; }
  .slider-nav { justify-content: center; width: 100%; }
  .companion-card { flex-direction: column; width: 100%; }
  .image-area { height: 300px; }
  .info-area { padding: 30px 24px; text-align: center; align-items: center; }
  .stats-list { width: 100%; }
  .char-cta { justify-content: center; }
}

.theme-light .companion-card { background: rgba(255, 255, 255, 0.7); border-color: #e2e8f0; }

.theme-light .hero-pill {
  background: rgba(var(--accent-3-rgb), 0.1);
  border-color: rgba(var(--accent-3-rgb), 0.4);
  color: var(--accent-3);
}
</style>
