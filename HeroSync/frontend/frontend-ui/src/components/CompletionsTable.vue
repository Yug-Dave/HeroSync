<template>
  <div class="table-card">
    <div class="card-head">
      <div class="title-group">
        <div class="title-icon">
          <svg class="glow-icon glow-blue" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/>
          </svg>
        </div>
        <div>
          <h3 class="card-title">Chronicles of Activity</h3>
          <p class="card-sub">Daily record of your heroic progress</p>
        </div>
      </div>
    </div>

    <div v-if="!items || items.length === 0" class="empty-state">
      No scrolls of activity found for this period.
    </div>

    <div v-else class="scroll-wrapper">
      <table class="hero-table">
        <thead>
          <tr>
            <th>Lunar Date</th>
            <th class="right">Completions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in items" :key="d.date">
            <td class="date-cell">{{ formatDate(d.date) }}</td>
            <td class="right">
              <span class="count-badge" :class="{ 'has-done': d.completions > 0 }">
                {{ d.completions }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
defineProps({ items: { type: Array, required: true } });
function formatDate(s){ return s ? String(s).slice(0,10) : "—"; }
</script>

<style scoped>
.table-card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 24px;
  padding: 24px;
  box-shadow: var(--shadow);
}

.card-head { margin-bottom: 20px; }
.title-group { display: flex; align-items: center; gap: 14px; }
.title-icon {
  width: 40px; height: 40px; border-radius: 10px;
  background: var(--card2); border: 1px solid var(--border);
  display: flex; align-items: center; justify-content: center; color: var(--accent);
}

.card-title { margin: 0; font-family: var(--ff-head); font-size: 1.3rem; font-weight: 700; color: var(--text); }
.card-sub { margin: 4px 0 0; color: var(--muted); font-size: 0.85rem; font-weight: 500; }

.scroll-wrapper {
  overflow: auto; border-radius: 16px; border: 1px solid var(--border);
  background: var(--bg);
}
.hero-table { width: 100%; border-collapse: collapse; min-width: 400px; }

.hero-table thead th {
  text-align: left; padding: 16px 20px;
  background: var(--card2);
  color: var(--muted); font-size: 0.75rem; font-weight: 700;
  text-transform: uppercase; letter-spacing: 1px;
  border-bottom: 1px solid var(--border);
}

.hero-table tbody td {
  padding: 16px 20px; border-bottom: 1px solid var(--border);
  color: var(--text); font-size: 0.95rem;
}
.hero-table tbody tr:last-child td { border-bottom: none; }
.hero-table tbody tr:hover td { background: var(--card2); }

.date-cell { font-family: monospace; font-weight: 600; color: var(--accent); }
.right { text-align: right; }

.count-badge {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 44px; height: 28px; border-radius: 14px;
  background: var(--card3); border: 1px solid var(--border);
  color: var(--muted); font-family: var(--ff-head); font-size: 0.9rem; font-weight: 800;
}
.count-badge.has-done {
  background: rgba(var(--accent-rgb), 0.1); border-color: rgba(var(--accent-rgb), 0.3);
  color: var(--accent);
}

.empty-state {
  padding: 32px; text-align: center; color: var(--muted);
  border: 1px dashed var(--border); border-radius: 20px;
  background: var(--card2); font-size: 0.95rem;
}
@media (max-width: 768px) { .hero-table { min-width: 100%; } .hero-table th, .hero-table td { padding: 12px; font-size: 0.85rem; } .count-badge { min-width: 32px; height: 24px; font-size: 0.8rem; } }
</style>
