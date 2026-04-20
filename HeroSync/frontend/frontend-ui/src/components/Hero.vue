<script setup>
</script>

<template>
  <section class="hero-section">
    <!-- Starfield background elements -->
    <div class="stars"></div>
    <div class="stars2"></div>
    <div class="stars3"></div>
    <div class="hero-glow"></div>

    <div class="hero-content">
      <div class="xp-badge">
        <span class="xp-icon">⚡</span>
        Your journey begins here
      </div>

      <h1 class="hero-title">
        Level Up <br/>
        <span class="gradient-text">Your Life</span>
      </h1>

      <p class="hero-subtitle">
        Turn your daily habits into an epic quest. Earn XP, unlock achievements, and become the hero of your own story.
      </p>

      <div class="hero-actions">
        <router-link to="/signup" class="btn-epic">
          <span class="btn-text">Start Your Quest</span>
          <span class="btn-glow"></span>
        </router-link>
        <a href="#features" class="btn-ghost">Explore Features</a>
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
  min-height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 40px 20px;
  background: radial-gradient(ellipse at bottom, #0d162a 0%, #070a10 100%);
}

.hero-glow {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(0,229,160,0.15) 0%, transparent 60%);
  border-radius: 50%;
  pointer-events: none;
  filter: blur(40px);
}

.hero-content {
  position: relative;
  z-index: 10;
  max-width: 800px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.xp-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(0,229,160,0.1);
  border: 1px solid rgba(0,229,160,0.3);
  padding: 6px 16px;
  border-radius: 30px;
  color: var(--accent);
  font-family: var(--ff-head);
  font-weight: 700;
  font-size: 0.9rem;
  margin-bottom: 24px;
  animation: fadeUp 0.8s ease forwards;
}

.hero-title {
  font-family: var(--ff-display);
  font-size: clamp(3rem, 8vw, 5rem);
  font-weight: 900;
  line-height: 1.1;
  margin: 0 0 24px;
  letter-spacing: -1px;
  animation: fadeUp 0.8s ease 0.1s forwards;
  opacity: 0;
}

.gradient-text {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 40px rgba(0,229,160,0.3);
}

.hero-subtitle {
  font-size: clamp(1.1rem, 2vw, 1.3rem);
  color: var(--text-dim);
  max-width: 600px;
  margin: 0 auto 40px;
  line-height: 1.6;
  animation: fadeUp 0.8s ease 0.2s forwards;
  opacity: 0;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 60px;
  animation: fadeUp 0.8s ease 0.3s forwards;
  opacity: 0;
}

@media (max-width: 500px) {
  .hero-actions {
    flex-direction: column;
    width: 100%;
    max-width: 300px;
  }
}

.btn-epic {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 16px 36px;
  background: linear-gradient(135deg, var(--accent), #1dbd88);
  color: #040d09;
  font-family: var(--ff-head);
  font-size: 1.1rem;
  font-weight: 800;
  border-radius: 12px;
  text-decoration: none;
  overflow: hidden;
  transition: transform 0.2s;
  box-shadow: 0 8px 30px rgba(0,229,160,0.3);
}

.btn-epic:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0,229,160,0.4);
}

.btn-glow {
  position: absolute;
  top: 0; left: -100%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: shimmer-slide 3s infinite;
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 16px 36px;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border2);
  color: var(--text);
  font-family: var(--ff-head);
  font-size: 1.1rem;
  font-weight: 700;
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.2s;
}

.btn-ghost:hover {
  background: rgba(255,255,255,0.08);
  border-color: var(--accent);
  color: var(--accent);
}

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

/* Very simple CSS stars */
.stars, .stars2, .stars3 {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: 
    radial-gradient(2px 2px at 20px 30px, #eee, rgba(0,0,0,0)),
    radial-gradient(2px 2px at 40px 70px, #fff, rgba(0,0,0,0)),
    radial-gradient(2px 2px at 50px 160px, #ddd, rgba(0,0,0,0)),
    radial-gradient(2px 2px at 90px 40px, #fff, rgba(0,0,0,0)),
    radial-gradient(2px 2px at 130px 80px, #fff, rgba(0,0,0,0)),
    radial-gradient(2px 2px at 160px 120px, #ddd, rgba(0,0,0,0));
  background-repeat: repeat;
  background-size: 200px 200px;
  opacity: 0.2;
}
.stars2 { background-size: 300px 300px; opacity: 0.15; animation: float 10s linear infinite; }
.stars3 { background-size: 400px 400px; opacity: 0.1; animation: float 15s linear infinite; }
</style>
