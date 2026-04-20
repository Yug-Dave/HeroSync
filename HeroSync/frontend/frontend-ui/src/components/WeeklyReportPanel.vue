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
.wrap { display: grid; gap: 20px; }

.toolbar {
  display: flex; align-items: flex-end; gap: 16px; flex-wrap: wrap;
  padding: 20px; border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.field { display: grid; gap: 8px; min-width: 220px; }
label { color: var(--muted); font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }

input {
  height: 42px; padding: 0 14px; border-radius: 10px;
  border: 1px solid var(--border); background: rgba(0, 0, 0, 0.2);
  color: var(--text); font-family: var(--ff-body); font-size: 0.95rem; outline: none; transition: all 0.2s;
}
input:focus { border-color: var(--accent); background: rgba(255, 255, 255, 0.05); }

.btn {
  height: 42px; padding: 0 24px; border-radius: 10px;
  border: 1px solid var(--border2); background: rgba(255, 255, 255, 0.05);
  color: var(--text); font-family: var(--ff-head); font-weight: 700;
  cursor: pointer; transition: all 0.2s;
}
.btn:hover:not(:disabled) { background: rgba(255, 255, 255, 0.08); border-color: var(--accent); transform: translateY(-1px); }
.btn:disabled { opacity: .6; cursor: not-allowed; }

.btn.primary {
  background: linear-gradient(135deg, var(--accent-2), var(--accent));
  color: #111; border: none;
}
.btn.primary:hover:not(:disabled) { box-shadow: 0 4px 15px rgba(0, 229, 160, 0.3); }

.error {
  padding: 12px 16px; border-radius: 12px;
  background: rgba(239, 68, 68, 0.1); border: 1px solid rgba(239, 68, 68, 0.2);
  color: #fca5a5; font-size: 0.9rem; font-weight: 500;
}
</style>
