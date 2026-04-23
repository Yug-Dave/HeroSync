<template>
  <div class="reports-page">
    <div class="container">
      <header class="page-header">
        <div class="header-left">
          <div class="page-icon">
            <svg class="glow-icon glow-blue" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10 9 9 9 8 9"/>
            </svg>
          </div>
          <div>
            <h1 class="page-title">Weekly Chronicles</h1>
            <p class="page-sub">Analyze your hero's journey over the past 7 suns</p>
          </div>
        </div>
      </header>

      <WeeklyReportPanel />
    </div>
  </div>
</template>

<script setup>
import WeeklyReportPanel from "../components/WeeklyReportPanel.vue";
</script>

<style scoped>
.reports-page { width: 100%; }
.container { width: min(1100px, 92vw); margin: 0 auto; padding: 28px 0; display: grid; gap: 20px; }

/* Header */
.page-header {
  display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 14px;
  margin-bottom: 10px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.page-icon   { font-size: 36px; }
.page-title  { margin: 0; font-family: var(--ff-head); font-size: 1.8rem; font-weight: 700; color: var(--text); line-height: 1; }
.page-sub    { margin: 3px 0 0; font-size: .85rem; color: var(--muted); font-weight: 600; }
</style>
