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
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--border);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.card-head { margin-bottom: 20px; }
.title-group { display: flex; align-items: center; gap: 14px; }
.title-icon {
  width: 40px; height: 40px; border-radius: 10px;
  background: rgba(59, 130, 246, 0.1); border: 1px solid rgba(59, 130, 246, 0.2);
  display: flex; align-items: center; justify-content: center; color: var(--accent-2);
}

.card-title { margin: 0; font-family: var(--ff-head); font-size: 1.15rem; font-weight: 700; color: #fff; }
.card-sub { margin: 4px 0 0; color: var(--muted); font-size: 0.85rem; font-weight: 500; }

.scroll-wrapper {
  overflow: auto; border-radius: 12px; border: 1px solid var(--border);
  background: rgba(0, 0, 0, 0.2);
}
.hero-table { width: 100%; border-collapse: collapse; min-width: 400px; }

.hero-table thead th {
  text-align: left; padding: 14px 18px;
  background: rgba(255, 255, 255, 0.03);
  color: var(--muted); font-size: 0.75rem; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.8px;
  border-bottom: 1px solid var(--border);
}

.hero-table tbody td {
  padding: 14px 18px; border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  color: #fff; font-size: 0.95rem;
}
.hero-table tbody tr:last-child td { border-bottom: none; }
.hero-table tbody tr:hover td { background: rgba(255, 255, 255, 0.02); }

.date-cell { font-family: monospace; font-weight: 600; color: var(--accent-2); }
.right { text-align: right; }

.count-badge {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 40px; height: 26px; border-radius: 13px;
  background: rgba(255, 255, 255, 0.05); border: 1px solid var(--border);
  color: var(--muted); font-family: var(--ff-head); font-size: 0.85rem; font-weight: 800;
}
.count-badge.has-done {
  background: rgba(0, 229, 160, 0.1); border-color: rgba(0, 229, 160, 0.3);
  color: var(--accent);
}

.empty-state {
  padding: 30px; text-align: center; color: var(--muted);
  border: 1px dashed var(--border); border-radius: 16px;
  background: rgba(255, 255, 255, 0.01); font-size: 0.95rem;
}
</style>
