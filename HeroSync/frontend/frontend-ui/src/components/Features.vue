<script setup>
import { CheckCircle2, TrendingUp, Trophy, Calendar, Target, Zap } from 'lucide-vue-next';

const features = [
  { title: "Daily Quests", desc: "Easily log your habits every day with a simple, rewarding interface.", icon: CheckCircle2, color: "var(--accent)" },
  { title: "Progress & Stats", desc: "See your improvement over time with beautiful, interactive charts.", icon: TrendingUp, color: "var(--accent-2)" },
  { title: "Achievement Badges", desc: "Unlock exclusive rewards and celebrate wins as you hit milestones.", icon: Trophy, color: "var(--accent-3)" },
  { title: "Streak Tracking", desc: "Never break the chain! Track your consecutive days and build fire.", icon: Calendar, color: "var(--danger)" },
  { title: "Epic Missions", desc: "Set meaningful long-term goals and track your progress towards them.", icon: Target, color: "var(--warning)" },
  { title: "Smart Reminders", desc: "Get timely notifications that keep you on track without being annoying.", icon: Zap, color: "var(--gold)" },
];
</script>

<template>
  <section id="features" class="features-section">
    <div class="container">

      <div class="section-header">
        <div class="pill">Features</div>
        <h2 class="title">Everything You Need to Level Up</h2>
        <p class="subtitle">Powerful tools designed to make consistency your superpower.</p>
      </div>

      <div class="features-grid">
        <div v-for="(item, index) in features" :key="index" class="feature-card">
          <div class="card-glow" :style="{ background: `radial-gradient(circle, ${item.color}22 0%, transparent 70%)` }" />
          <div class="icon-wrapper" :style="{ color: item.color, backgroundColor: `${item.color}15`, border: `1px solid ${item.color}33` }">
            <component :is="item.icon" class="icon" />
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </div>
      </div>

    </div>
  </section>
</template>

<style scoped>
.features-section {
  padding: 100px 20px;
  background-color: var(--bg);
  position: relative;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  max-width: 600px;
  margin: 0 auto 60px;
}

.pill {
  display: inline-block;
  background: rgba(0, 229, 160, 0.1);
  color: var(--accent);
  padding: 6px 16px;
  border-radius: 20px;
  font-family: var(--ff-head);
  font-size: 0.9rem;
  font-weight: 700;
  margin-bottom: 20px;
  border: 1px solid rgba(0, 229, 160, 0.3);
  letter-spacing: 0.5px;
}

.title {
  font-family: var(--ff-display);
  font-size: clamp(2rem, 4vw, 2.5rem);
  color: var(--text);
  margin-bottom: 16px;
  font-weight: 800;
  line-height: 1.2;
}

.subtitle { color: var(--text-dim); font-size: 1.1rem; line-height: 1.6; }

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.feature-card {
  position: relative;
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 32px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.feature-card:hover {
  border-color: var(--border2);
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
}

.card-glow {
  position: absolute;
  top: -50px; right: -50px;
  width: 150px; height: 150px;
  border-radius: 50%;
  pointer-events: none;
  transition: opacity 0.3s;
  opacity: 0.5;
}
.feature-card:hover .card-glow { opacity: 1; }

.icon-wrapper {
  width: 50px; height: 50px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}
.icon { width: 24px; height: 24px; }

h3 { 
  font-family: var(--ff-head);
  color: var(--text);
  font-size: 1.3rem; 
  margin-bottom: 10px; 
  font-weight: 700; 
  position: relative;
  z-index: 1;
}

p { 
  color: var(--muted); 
  line-height: 1.6; 
  font-size: 0.95rem; 
  margin: 0; 
  position: relative;
  z-index: 1;
}
</style>
