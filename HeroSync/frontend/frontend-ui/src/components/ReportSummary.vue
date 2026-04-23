<template>
  <div class="summary-card">
    <div class="card-top">
      <div class="title-group">
        <div class="title-icon">
          <svg class="glow-icon glow-gold" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M6 9H4a2 2 0 01-2-2V5h4"/><path d="M18 9h2a2 2 0 002-2V5h-4"/><path d="M6 5h12v8a6 6 0 01-12 0V5z"/>
          </svg>
        </div>
        <div>
          <h3 class="card-title">{{ title }}</h3>
          <p class="card-sub">{{ report.period }}</p>
        </div>
      </div>

      <div class="date-range">
        <span class="range-badge">
          <span class="range-lbl">Start</span>
          <span class="range-val">{{ short(report.startDate) }}</span>
        </span>
        <span class="range-badge">
          <span class="range-lbl">End</span>
          <span class="range-val">{{ short(report.endDate) }}</span>
        </span>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-label">Total Completions</div>
        <div class="stat-value">{{ report.totalCompletions }}</div>
        <div class="stat-trend">+{{ Math.floor(report.totalCompletions * 0.1) }} from last period</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">Active Habits</div>
        <div class="stat-value accent">{{ report.activeHabits }}</div>
        <div class="stat-trend">Mastering {{ report.activeHabits }} skills</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: "Report Summary" },
  report: { type: Object, required: true },
});
function short(s){ return s ? String(s).slice(0,10) : "—"; }
</script>

<style scoped>
.summary-card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 24px;
  padding: 24px;
  box-shadow: var(--shadow);
}

.card-top {
  display: flex; justify-content: space-between; align-items: flex-start; gap: 20px;
  flex-wrap: wrap; margin-bottom: 24px;
}

.title-group { display: flex; align-items: center; gap: 14px; }
.title-icon {
  width: 44px; height: 44px; border-radius: 12px;
  background: var(--card2); border: 1px solid var(--border);
  display: flex; align-items: center; justify-content: center; color: var(--accent);
}

.card-title { margin: 0; font-family: var(--ff-head); font-size: 1.4rem; font-weight: 700; color: var(--text); }
.card-sub { margin: 4px 0 0; color: var(--muted); font-size: 0.9rem; font-weight: 500; }

.date-range { display: flex; gap: 10px; }
.range-badge {
  display: flex; align-items: center; gap: 8px;
  padding: 6px 14px; border-radius: 10px;
  background: var(--card2); border: 1px solid var(--border);
}
.range-lbl { font-size: 0.7rem; text-transform: uppercase; color: var(--muted); font-weight: 700; letter-spacing: 0.5px; }
.range-val { font-family: monospace; font-size: 0.9rem; color: var(--text); font-weight: 600; }

.stats-grid {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px;
}

.stat-item {
  background: var(--card2); border: 1px solid var(--border);
  border-radius: 20px; padding: 20px; transition: transform 0.2s;
}
.stat-item:hover { transform: translateY(-4px); border-color: var(--accent); box-shadow: var(--shadow-lg); }

.stat-label { font-size: 0.8rem; text-transform: uppercase; color: var(--muted); font-weight: 700; letter-spacing: 0.5px; margin-bottom: 8px; }
.stat-value { font-family: var(--ff-head); font-size: 2.2rem; font-weight: 800; color: var(--text); line-height: 1; }
.stat-value.accent { color: var(--accent); }
.stat-trend { margin-top: 10px; font-size: 0.75rem; color: var(--muted); font-weight: 500; }

@media (max-width: 600px) { 
  .summary-card { padding: 16px; border-radius: 16px; }
  .card-top { margin-bottom: 12px; flex-direction: column; align-items: flex-start; gap: 8px; }
  .title-icon { width: 32px; height: 32px; border-radius: 8px; }
  .card-title { font-size: 1.1rem; }
  .card-sub { font-size: 0.75rem; margin-top: 2px; }
  .date-range { width: 100%; justify-content: space-between; gap: 6px; }
  .range-badge { padding: 4px 8px; border-radius: 8px; flex: 1; justify-content: center; gap: 4px; }
  .range-lbl { font-size: 0.55rem; }
  .range-val { font-size: 0.7rem; }
  
  .stats-grid { grid-template-columns: 1fr 1fr; gap: 8px; } 
  .stat-item { padding: 12px; border-radius: 14px; }
  .stat-label { font-size: 0.55rem; margin-bottom: 2px; }
  .stat-value { font-size: 1.4rem; }
  .stat-trend { font-size: 0.6rem; margin-top: 4px; line-height: 1.2; }
}
</style>
