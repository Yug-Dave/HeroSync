<script setup>
import { Brain, Rocket, Shield } from 'lucide-vue-next';
</script>

<template>
  <section id="method" class="method-section">
    <div class="split-container">
      <!-- Left Side: Title & Description -->
      <div class="title-side">
        <div class="pill">The Science</div>
        <h2 class="title">How HeroSync <br/><span class="gradient-text">Rewires Your Brain</span></h2>
        <p class="subtitle">
          Traditional habit trackers fail because they rely on willpower. HeroSync leverages the neurobiology of play to make consistency effortless.
        </p>
        <div class="accent-line"></div>
      </div>

      <!-- Right Side: Content Cards -->
      <div class="content-side">
        <div class="method-grid">
          <div class="method-card">
            <div class="icon-box blue"><Brain /></div>
            <div class="card-text">
              <h3>Micro-Wins</h3>
              <p>Dopamine loops triggered by small, achievable tasks prevent the "daunting goal" paralysis that stops most habit apps.</p>
            </div>
          </div>
          
          <div class="method-card">
            <div class="icon-box green"><Rocket /></div>
            <div class="card-text">
              <h3>The Flow State</h3>
              <p>By synchronizing tasks with your AI companion, we find the perfect difficulty curve to keep you in the "Zone."</p>
            </div>
          </div>
          
          <div class="method-card">
            <div class="icon-box purple"><Shield /></div>
            <div class="card-text">
              <h3>Auto-Weaning</h3>
              <p>Our unique protocol slowly reduces app dependency as your habits become permanent neural pathways.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.method-section {
  height: 100vh; width: 100%;
  display: flex; align-items: center; justify-content: center;
  background: transparent; padding: 100px 5vw 40px;
}

.split-container {
  display: flex; width: 100%; max-width: 1400px; gap: 80px; align-items: center;
}

/* --- LEFT SIDE --- */
.title-side { flex: 1; text-align: left; }
.pill { display: inline-block; background: rgba(var(--accent-2-rgb), 0.1); color: var(--accent-2); padding: 8px 20px; border-radius: 30px; font-family: var(--ff-head); font-size: 0.9rem; font-weight: 700; margin-bottom: 24px; border: 1px solid rgba(var(--accent-2-rgb), 0.3); text-transform: uppercase; letter-spacing: 2px; }
.title { font-family: var(--ff-display); font-size: clamp(2.5rem, 5vw, 4rem); color: var(--text); font-weight: 950; line-height: 1.05; margin-bottom: 30px; letter-spacing: -2px; }
.gradient-text { background: linear-gradient(135deg, var(--accent-2), var(--accent-3)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.subtitle { color: var(--text-dim); font-size: 1.3rem; line-height: 1.6; font-weight: 500; max-width: 500px; margin-bottom: 40px; }
.accent-line { width: 80px; height: 4px; background: var(--accent-2); border-radius: 2px; }

/* --- RIGHT SIDE --- */
.content-side { flex: 1.2; }
.method-grid { display: flex; flex-direction: column; gap: 24px; }
.method-card { display: flex; gap: 24px; padding: 32px; background: rgba(255,255,255,0.03); backdrop-filter: blur(10px); border: 1px solid var(--border); border-radius: 24px; transition: all 0.4s ease; align-items: center; }
.method-card:hover { transform: translateX(15px); background: rgba(255,255,255,0.06); border-color: var(--accent-2); }
.icon-box { width: 64px; height: 64px; border-radius: 18px; display: grid; place-items: center; flex-shrink: 0; }
.icon-box.blue { background: rgba(59, 130, 246, 0.15); color: #3b82f6; }
.icon-box.green { background: rgba(0, 229, 160, 0.15); color: var(--accent); }
.icon-box.purple { background: rgba(139, 92, 246, 0.15); color: #8b5cf6; }
h3 { font-family: var(--ff-head); font-size: 1.4rem; font-weight: 800; color: var(--text); margin-bottom: 8px; }
p { color: var(--text-dim); line-height: 1.6; font-size: 1.05rem; margin: 0; }

@media (max-width: 1000px) {
  .split-container { flex-direction: column; gap: 40px; align-items: center; text-align: center; }
  .method-section { height: auto; padding: 120px 24px 80px; }
  .title-side { text-align: center; display: flex; flex-direction: column; align-items: center; }
  .subtitle { margin-inline: auto; }
  .accent-line { margin-inline: auto; }
  .method-card:hover { transform: translateY(-5px); }
}

.theme-light .method-card { background: rgba(255, 255, 255, 0.7); border-color: #e2e8f0; }
</style>
