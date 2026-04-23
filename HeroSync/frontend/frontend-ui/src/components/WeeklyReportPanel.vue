<template>
  <div class="wrap">
    <div class="toolbar">
      <div class="field">
        <label>Date</label>
        <input type="date" v-model="date" />
      </div>

      <button class="btn primary" @click="load" :disabled="loading">
        {{ loading ? "Loading..." : "Load" }}
      </button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <ReportSummary v-if="report" title="Weekly Summary" :report="report" />
    <CompletionsTable v-if="report" :items="perDayRows" />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { getWeeklyReport } from "../api/reports";
import ReportSummary from "./ReportSummary.vue";
import CompletionsTable from "./CompletionsTable.vue";

const date = ref(new Date().toISOString().slice(0, 10));
const report = ref(null);
const loading = ref(false);
const error = ref("");

const perDayRows = computed(() => {
  const map = report.value?.completionsPerDay || {};
  return Object.entries(map)
    .sort(([a],[b]) => a.localeCompare(b))
    .map(([date, completions]) => ({ date, completions }));
});

async function load() {
  loading.value = true;
  error.value = "";
  report.value = null;
  try {
    report.value = await getWeeklyReport(date.value);
  } catch (e) {
    console.error(e);
    error.value = "Could not load weekly report. Check backend/CORS/session.";
  } finally {
    loading.value = false;
  }
}

load();
</script>

<style scoped>
.wrap { display: grid; gap: 24px; }

.toolbar {
  display: flex; align-items: flex-end; gap: 16px; flex-wrap: wrap;
  padding: 24px; border-radius: 24px;
  background: var(--card);
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
}

.field { display: grid; gap: 8px; min-width: 220px; }
label { color: var(--muted); font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }

input {
  height: 44px; padding: 0 16px; border-radius: 12px;
  border: 1px solid var(--border); background: var(--bg);
  color: var(--text); font-family: var(--ff-body); font-size: 1rem; outline: none; transition: all 0.2s;
}
input:focus { border-color: var(--accent); background: var(--card2); }

.btn {
  height: 44px; padding: 0 28px; border-radius: 12px;
  border: 1px solid var(--border); background: var(--card2);
  color: var(--text); font-family: var(--ff-head); font-weight: 700; font-size: 1rem;
  cursor: pointer; transition: all 0.2s;
}
.btn:hover:not(:disabled) { border-color: var(--accent); color: var(--accent); transform: translateY(-2px); }
.btn:disabled { opacity: .5; cursor: not-allowed; }

.btn.primary {
  background: var(--accent);
  color: #fff; border: none;
}
.btn.primary:hover:not(:disabled) { 
  background: var(--accent-2);
  box-shadow: 0 4px 15px rgba(var(--accent-rgb), 0.3); 
}

.error {
  padding: 14px 18px; border-radius: 12px;
  background: rgba(var(--danger-rgb, 239, 68, 68), 0.1); border: 1px solid rgba(var(--danger-rgb, 239, 68, 68), 0.2);
  color: var(--danger); font-size: 0.95rem; font-weight: 600;
}
</style>
