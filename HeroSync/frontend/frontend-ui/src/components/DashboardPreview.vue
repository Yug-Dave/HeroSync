<script setup>
import { Trophy, Flame, Target } from 'lucide-vue-next';
</script>

<template>
  <section id="preview" class="preview-section">
    <div class="container scrollable-content">
      <div class="section-header">
        <div class="pill">Live Preview</div>
        <h2 class="title">The Command <span class="gradient-text">Center</span></h2>
        <p class="subtitle">A dashboard designed for high-performance heroes.</p>
      </div>

      <div class="preview-window">
        <!-- Mock Sidebar -->
        <div class="mock-sidebar">
          <div class="sidebar-item active"></div>
          <div class="sidebar-item"></div>
          <div class="sidebar-item"></div>
          <div class="sidebar-item"></div>
        </div>

        <div class="mock-content">
          <!-- Top Row: Stats -->
          <div class="mock-header">
            <div class="mock-stat">
              <div class="stat-label">LEVEL</div>
              <div class="stat-val">42</div>
            </div>
            <div class="mock-stat">
              <div class="stat-label">STREAK</div>
              <div class="stat-val">🔥 14</div>
            </div>
          </div>

          <div class="mock-grid">
            <!-- Hologram Stage Mock -->
            <div class="mock-card hologram-card">
              <div class="hologram-label">NEURAL_LINK: ACTIVE</div>
              <div class="hologram-visual">
                <div class="avatar-glow"></div>
                <div class="scan-line"></div>
                <!-- 3D Avatar Placeholder (Silhouette) -->
                <svg class="avatar-svg" viewBox="0 0 100 100">
                  <path d="M50 12c-6.6 0-12 5.4-12 12s5.4 12 12 12 12-5.4 12-12-5.4-12-12-12zm0 24c-12.2 0-22 9.8-22 22v22h44V58c0-12.2-9.8-22-22-22z" fill="var(--accent)" opacity="0.3" />
                </svg>
              </div>
              <div class="hologram-footer">
                <div class="footer-pill">CLASS: DRAGON SLAYER</div>
                <div class="footer-pill">STABLE</div>
              </div>
            </div>

            <!-- Side Cards -->
            <div class="side-column">
              <!-- Heatmap Mock -->
              <div class="mock-card heatmap-card">
                <div class="card-label">CONSISTENCY_MAP</div>
                <div class="heatmap-grid">
                  <div v-for="i in 35" :key="i" class="heat-cell" :style="{ opacity: Math.random() > 0.5 ? 1 : 0.2, background: Math.random() > 0.8 ? 'var(--accent-2)' : 'var(--accent)' }"></div>
                </div>
              </div>

              <!-- Floating Achievement -->
              <div class="mock-card achievement-card">
                <Trophy class="ach-icon" />
                <div class="ach-info">
                  <div class="ach-title">GODLIKE_STREAK</div>
                  <div class="ach-desc">14 days without failure.</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.preview-section { 
  background: var(--bg); 
  position: relative;
  height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 80px 20px 40px;
  transition: background 0.5s ease; 
}
.container { max-width: 1200px; margin: 0 auto; width: 100%; }

.scrollable-content {
  max-height: 100%;
  overflow-y: auto;
  padding-right: 10px;
  scrollbar-width: none;
}
.scrollable-content::-webkit-scrollbar { display: none; }

.section-header { text-align: center; margin: 0 auto 40px; }
.pill { display: inline-block; background: rgba(var(--accent-rgb), 0.1); color: var(--accent); padding: 6px 16px; border-radius: 20px; font-family: var(--ff-head); font-size: 0.9rem; font-weight: 700; margin-bottom: 20px; border: 1px solid rgba(var(--accent-rgb), 0.3); }
.title { font-family: var(--ff-display); font-size: 2.5rem; color: var(--text); font-weight: 900; margin-bottom: 15px; }
.gradient-text { background: linear-gradient(135deg, var(--accent), var(--accent-2)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.subtitle { color: var(--text-dim); font-size: 1.1rem; }

/* Mock Window Styles */
.preview-window {
  background: #000;
  border: 1px solid var(--border);
  border-radius: 24px;
  display: flex;
  height: 460px;
  overflow: hidden;
  box-shadow: 0 50px 100px rgba(0,0,0,0.6);
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
}
.preview-window::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 30px; 
  background: rgba(255,255,255,0.03); border-bottom: 1px solid var(--border);
}

.mock-sidebar { width: 60px; background: rgba(255,255,255,0.02); border-right: 1px solid var(--border); padding: 40px 12px; display: flex; flex-direction: column; gap: 16px; }
.sidebar-item { width: 100%; height: 24px; background: rgba(255,255,255,0.05); border-radius: 6px; }
.sidebar-item.active { background: var(--accent); opacity: 0.2; }

.mock-content { flex: 1; padding: 32px 40px; display: flex; flex-direction: column; gap: 20px; }

.mock-header { display: flex; gap: 40px; margin-bottom: -5px; }
.mock-stat { display: flex; flex-direction: column; gap: 2px; }
.stat-label { font-family: monospace; font-size: 9px; color: var(--muted); letter-spacing: 1px; }
.stat-val { font-family: var(--ff-display); font-size: 1.6rem; color: var(--text); }

.mock-grid { display: flex; gap: 20px; flex: 1; min-height: 0; }
.mock-card { background: rgba(255,255,255,0.03); border: 1px solid var(--border); border-radius: 16px; padding: 16px; position: relative; overflow: hidden; }

.hologram-card { flex: 1.4; display: flex; flex-direction: column; justify-content: space-between; border-color: var(--accent-2); }
.hologram-label { font-family: monospace; font-size: 9px; color: var(--accent-2); margin-bottom: 8px; }
.hologram-visual { flex: 1; display: grid; place-items: center; position: relative; }
.avatar-glow { position: absolute; width: 150px; height: 150px; background: radial-gradient(circle, rgba(var(--accent-rgb), 0.2) 0%, transparent 70%); animation: float 4s infinite; }
.avatar-svg { width: 120px; height: 120px; position: relative; z-index: 2; filter: drop-shadow(0 0 10px var(--accent)); }
.scan-line {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent), transparent);
  box-shadow: 
    0 0 10px var(--accent),
    0 0 20px rgba(var(--accent-rgb), 0.4);
  z-index: 3;
  animation: scan 3.5s cubic-bezier(0.4, 0, 0.2, 1) infinite;
  filter: blur(0.5px);
}
.theme-light .scan-line {
  background: linear-gradient(90deg, transparent, var(--accent-2), transparent);
  box-shadow: 0 0 10px var(--accent-2);
}

@keyframes scan { 
  0% { top: -5%; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 105%; opacity: 0; }
}

.hologram-footer { display: flex; gap: 8px; }
.footer-pill { font-family: monospace; font-size: 9px; color: var(--muted); border: 1px solid var(--border); padding: 4px 8px; border-radius: 4px; }

.side-column { flex: 1; display: flex; flex-direction: column; gap: 30px; }
.heatmap-card { flex: 1; }
.card-label { font-family: monospace; font-size: 9px; color: var(--muted); margin-bottom: 16px; }
.heatmap-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 6px; }
.heat-cell { aspect-ratio: 1; background: var(--accent); border-radius: 3px; }

.achievement-card { display: flex; align-items: center; gap: 16px; border-color: var(--gold); }
.ach-icon { color: var(--gold); filter: drop-shadow(0 0 5px var(--gold)); }
.ach-title { font-family: var(--ff-head); font-weight: 700; color: var(--text); font-size: 0.9rem; }
.ach-desc { color: var(--muted); font-size: 0.8rem; }

@media (max-width: 900px) {
  .preview-section { height: auto; padding: 120px 20px 60px; }
  .preview-window { height: auto; flex-direction: column; max-height: none; }
  .mock-sidebar { display: none; }
  .mock-grid { flex-direction: column; }
  .hologram-card { height: 400px; }
}

/* Theme Light Support */
.theme-light .preview-window { background: #fff; border-color: #e2e8f0; box-shadow: 0 20px 50px rgba(0, 0, 0, 0.05); }
.theme-light .mock-card { background: #f8fafc; border-color: #e2e8f0; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03); }
.theme-light .stat-val { color: #0f172a; }
.theme-light .title { color: #0f172a; }
.theme-light .subtitle { color: #64748b; }
.theme-light .hologram-card { background: linear-gradient(180deg, #f0f9ff 0%, #fff 100%); border-color: var(--accent-2); }
.theme-light .ach-title { color: #0f172a; }
.theme-light .ach-desc { color: #64748b; }
.theme-light .sidebar-item { background: #e2e8f0; }
.theme-light .preview-window::before { background: #f1f5f9; border-color: #e2e8f0; }
</style>
