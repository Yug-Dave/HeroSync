<script setup>
import { ref, onMounted, defineEmits } from 'vue';

const emit = defineEmits(['learn-more']);

const mouseX = ref(0);
const mouseY = ref(0);

function handleMouseMove(e) {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 20;
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 20;
}

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove);
});
</script>

<template>
  <section id="hero" class="hero-section">
    <!-- Starfield with parallax -->
    <div class="stars-container">
      <div class="stars" :style="{ transform: `translate(${mouseX * 0.5}px, ${mouseY * 0.5}px)` }"></div>
      <div class="stars2" :style="{ transform: `translate(${mouseX * 0.8}px, ${mouseY * 0.8}px)` }"></div>
    </div>
    
    <div class="hero-glow" :style="{ transform: `translate(calc(-50% + ${mouseX}px), calc(-50% + ${mouseY}px))` }"></div>

    <div class="hero-content">
      <div class="hero-pill">
        <span class="xp-icon">⚡</span>
        Your Journey Begins Here
      </div>

      <h1 class="hero-title">
        Level Up ! <br/>
        <span class="gradient-text">Slay Real Bosses</span>
      </h1>

      <p class="hero-subtitle">
        Your habits are your weapons. Build your 3D Avatar, earn XP, unlock achievements, and become the hero of your own story.
      </p>

      <div class="hero-actions">
        <router-link to="/signup" class="btn-epic">
          <span class="btn-text">Start Your Quest</span>
          <span class="btn-glow"></span>
        </router-link>
        <button @click="emit('learn-more')" class="btn-ghost">Learn Our Method</button>
      </div>

      <div class="hero-stats-row">
        <div class="stat-pill">🔥 Build Streaks</div>
        <div class="stat-pill">🏆 Earn Badges</div>
        <div class="stat-pill">⚔️ Complete Quests</div>
      </div>

    </div>
  </section>
</template>

<style scoped>
.hero-section {
  position: relative;
  height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 100px 20px 20px;
  background: transparent;
}

.stars-container { position: absolute; inset: 0; pointer-events: none; }
.hero-glow {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 800px; height: 800px;
  background: radial-gradient(circle, rgba(var(--accent-rgb), 0.08) 0%, transparent 70%);
  border-radius: 50%; pointer-events: none; filter: blur(80px);
}
.theme-light .hero-glow { display: none; }

.hero-content {
  position: relative; z-index: 10;
  max-width: 900px; text-align: center;
  display: flex; flex-direction: column; align-items: center;
}



.hero-title {
  font-family: var(--ff-display); font-size: clamp(3rem, 10vw, 5.5rem);
  font-weight: 950; line-height: 0.95; margin: 0 0 32px;
  letter-spacing: -2px; color: var(--text);
}

.gradient-text {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: clamp(1.1rem, 2.5vw, 1.3rem); color: var(--text-dim);
  max-width: 650px; margin: 0 auto 48px; line-height: 1.5; font-weight: 500;
}

.hero-actions { display: flex; gap: 20px; justify-content: center; margin-bottom: 60px; }

.btn-epic {
  position: relative; display: inline-flex; align-items: center; justify-content: center;
  padding: 18px 42px; background: linear-gradient(135deg, var(--accent), #1dbd88);
  color: #000; font-family: var(--ff-head); font-size: 1.1rem; font-weight: 800;
  border-radius: 14px; text-decoration: none; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 10px 30px rgba(var(--accent-rgb), 0.3);
}
.btn-epic:hover { transform: translateY(-5px); box-shadow: 0 15px 40px rgba(var(--accent-rgb), 0.4); }



.btn-ghost {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 18px 42px; background: rgba(255,255,255,0.05); border: 1px solid var(--border);
  color: var(--text); font-family: var(--ff-head); font-size: 1.1rem; font-weight: 700;
  border-radius: 14px; cursor: pointer; transition: all 0.3s ease;
}
.btn-ghost:hover { border-color: var(--accent); color: var(--accent); }

.hero-stats-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  justify-content: center;
  animation: fadeUp 0.8s ease 0.4s forwards;
  opacity: 0;
}

.stat-pill {
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--border);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  backdrop-filter: blur(4px);
  color: var(--text);
}

@keyframes scroll-wheel { 0% { opacity: 1; transform: translateX(-50%) translateY(0); } 100% { opacity: 0; transform: translateX(-50%) translateY(12px); } }

/* Stars */
.stars, .stars2 {
  position: absolute; inset: 0;
  background-image: radial-gradient(1.5px 1.5px at 10% 20%, #fff, transparent), radial-gradient(2px 2px at 30% 50%, #fff, transparent), radial-gradient(1.5px 1.5px at 70% 30%, #fff, transparent);
  background-size: 300px 300px; opacity: 0.1;
}
.theme-light .stars, .theme-light .stars2 { opacity: 0; }
@media (max-width: 768px) {
  .hero-section { padding: 120px 24px 40px; }
  .hero-title { font-size: clamp(2.4rem, 11vw, 3.2rem); line-height: 1.1; margin-bottom: 24px; letter-spacing: -1.5px; }
  .hero-subtitle { font-size: 1rem; margin-bottom: 32px; line-height: 1.5; }
  .hero-actions { flex-direction: column; width: 100%; gap: 14px; margin-bottom: 40px; }
  .btn-epic { width: 100%; padding: 18px 30px; font-size: 1.05rem; border-radius: 16px; }
  .btn-ghost { width: 100%; padding: 18px 30px; font-size: 1.05rem; border-radius: 16px; background: rgba(255,255,255,0.03); }
  .hero-stats-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; width: 100%; }
  .stat-pill { font-size: 0.75rem; padding: 10px; text-align: center; border-radius: 12px; }
  .stat-pill:last-child { grid-column: span 2; }
}
</style>
