<template>
  <div class="reports-page">
    <div class="container">
      <header class="page-header">
        <div class="header-left">
          <div class="page-icon">
            <svg class="glow-icon glow-gold" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
              <path d="M8 14h.01"/>
              <path d="M12 14h.01"/>
              <path d="M16 14h.01"/>
              <path d="M8 18h.01"/>
              <path d="M12 18h.01"/>
              <path d="M16 18h.01"/>
            </svg>
          </div>
          <div>
            <h1 class="page-title">Monthly Tomes</h1>
            <p class="page-sub">Review your legendary deeds across this lunar cycle</p>
          </div>
        </div>
      </header>

      <MonthlyReportPanel />
    </div>
  </div>
</template>

<script setup>
import MonthlyReportPanel from "../components/MonthlyReportPanel.vue";
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
