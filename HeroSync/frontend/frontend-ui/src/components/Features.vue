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
        <div class="pill reveal-item">System Features</div>
        <h2 class="title reveal-item">Everything You Need to <span class="gradient-text">Level Up</span></h2>
        <p class="subtitle reveal-item">Powerful tools designed to make consistency your superpower.</p>
      </div>

      <div class="features-grid">
        <div v-for="(item, index) in features" :key="index" 
             class="feature-card reveal-item"
             :style="{ transitionDelay: `${index * 0.1}s` }">
          <div class="card-glow" :style="{ background: `radial-gradient(circle at top right, ${item.color}22 0%, transparent 70%)` }"></div>
          <div class="icon-wrapper" :style="{ color: item.color, background: `${item.color}15`, borderColor: `${item.color}33` }">
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
  height: 100vh; width: 100%;
  display: flex; align-items: center; justify-content: center;
  padding: 100px 20px 40px; background: transparent;
}

.container { width: min(1300px, 94vw); margin: 0 auto; gap: 80px  }

.section-header { text-align: center; max-width: 800px; margin: 0 auto 50px; }
.pill {
  display: inline-block; background: rgba(0, 229, 160, 0.1); color: var(--accent);
  padding: 8px 24px; border-radius: 30px; font-family: var(--ff-head); font-size: 0.9rem;
  font-weight: 700; border: 1px solid rgba(0, 229, 160, 0.3);
  text-transform: uppercase; letter-spacing: 2px; margin-top: 20px;
}
.title { font-family: var(--ff-display); font-size: clamp(2.5rem, 6vw, 3.5rem); color: var(--text); font-weight: 950; margin-bottom: 24px; letter-spacing: -1px; line-height: 1.1; }
.gradient-text { background: linear-gradient(135deg, var(--accent), var(--accent-2)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.subtitle { color: var(--text-dim); font-size: 1.2rem; font-weight: 500; line-height: 1.6; }

.features-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }

.feature-card {
  position: relative; background: rgba(30, 41, 59, 0.4); backdrop-filter: blur(15px);
  border: 1px solid var(--border); border-radius: 24px; padding: 32px;
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); overflow: hidden;
}
.feature-card:hover { transform: translateY(-10px); border-color: var(--accent); background: rgba(30, 41, 59, 0.6); }

.card-glow { position: absolute; inset: 0; pointer-events: none; opacity: 0; transition: opacity 0.5s; }
.feature-card:hover .card-glow { opacity: 1; }

.icon-wrapper { width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; border: 1px solid transparent; }
.icon { width: 26px; height: 26px; }

h3 { font-family: var(--ff-head); color: var(--text); font-size: 1.3rem; font-weight: 800; margin-bottom: 10px; }
p { color: var(--text-dim); line-height: 1.6; font-size: 0.95rem; margin: 0; }

/* Animations - Ensure visibility */
.reveal-item { 
  opacity: 1; /* Default to visible */
  transform: translateY(0);
}

@media (max-width: 1000px) { .features-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 650px) { .features-grid { grid-template-columns: 1fr; } .features-section { height: auto; padding: 120px 20px 60px; } }

.theme-light .feature-card { background: rgba(255, 255, 255, 0.7); border-color: #e2e8f0; }
</style>
