<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { listGoals, createGoal, updateGoal, deleteGoal } from '../api/goals';
import GoalsAddEditModal from '../components/GoalsAddEditModal.vue';

const goals    = ref([]);
const habits   = ref([]);
const loading  = ref(true);
const error    = ref('');
const showModal   = ref(false);
const modalMode   = ref('create');
const goalToEdit  = ref(null);
const saving      = ref(false);
const modalError  = ref('');
const selectedStatus = ref('ACTIVE');
const showDeleteConfirm = ref(false);
const goalToDelete      = ref(null);

const visibleGoals = computed(() => {
  if (selectedStatus.value === 'ACTIVE')    return activeGoals.value;
  if (selectedStatus.value === 'FAILED')    return failedGoals.value;
  return completedGoals.value;
});
const activeGoals    = computed(() => goals.value.filter(g => g.status === 'ACTIVE'));
const completedGoals = computed(() => goals.value.filter(g => g.status === 'COMPLETED'));
const failedGoals    = computed(() => goals.value.filter(g => g.status === 'FAILED'));

const API_BASE   = '/api';
const getHeaders = () => {
  const headers = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
  const getCookie = (n) => {
    if (!document.cookie) return null;
    const m = document.cookie.match(new RegExp('(^| )' + n + '=([^;]+)'));
    return m ? decodeURIComponent(m[2]) : null;
  };
  const csrf = getCookie('XSRF-TOKEN');
  if (csrf) headers['X-XSRF-TOKEN'] = csrf;
  return headers;
};

async function graphqlRequest(query) {
  const res  = await fetch(`${API_BASE}/graphql`, {
    method: 'POST', headers: getHeaders(), credentials: 'include',
    body: JSON.stringify({ query })
  });
  const json = await res.json();
  if (json.errors) throw new Error(json.errors[0].message);
  return json.data;
}

async function loadGoals() {
  loading.value = true; error.value = '';
  try { const res = await listGoals(); goals.value = res || []; }
  catch (e) { error.value = 'Failed to load goals.'; }
  finally { loading.value = false; }
}

async function loadHabits() {
  try {
    const data = await graphqlRequest('query { habits { id name description archived } }');
    habits.value = (data.habits || []).map(h => ({ id: h.id, name: h.name }));
  } catch (e) { habits.value = []; }
}

const habitNameById = computed(() => {
  const map = new Map();
  for (const h of habits.value) { map.set(h.id, h.name); map.set(String(h.id), h.name); }
  return map;
});

function progressPct(g) {
  if (!g.habitId || !g.targetCount) return 0;
  return Math.min(100, Math.round(((g.progressCount || 0) / g.targetCount) * 100));
}
function deadlineText(g) { return g.deadline ? g.deadline : null; }
function isOverdue(g) {
  if (!g.deadline || g.status !== 'ACTIVE') return false;
  return new Date(g.deadline) < new Date();
}

function goalIcon(g) {
  if (g.status === 'COMPLETED') return 'completed';
  if (g.status === 'FAILED')    return 'failed';
  if (isOverdue(g))             return 'overdue';
  return g.habitId ? 'habit' : 'solo';
}

function openCreate()  { modalMode.value = 'create'; goalToEdit.value = null;   modalError.value = ''; showModal.value = true; }
function openEdit(g)   { modalMode.value = 'edit';   goalToEdit.value = { ...g }; modalError.value = ''; showModal.value = true; }
function openRetry(g)  { modalMode.value = 'retry';  goalToEdit.value = { ...g }; modalError.value = ''; showModal.value = true; }
function confirmDelete(g) { goalToDelete.value = g; showDeleteConfirm.value = true; }

async function executeDelete() {
  if (!goalToDelete.value) return;
  try { await deleteGoal(goalToDelete.value.id); await loadGoals(); }
  catch (e) { alert('Failed to delete goal.'); }
  finally { showDeleteConfirm.value = false; goalToDelete.value = null; }
}

async function saveFromModal(payload) {
  saving.value = true; modalError.value = '';
  try {
    if (modalMode.value === 'create') await createGoal(payload);
    else await updateGoal(goalToEdit.value.id, payload);
    showModal.value = false; await loadGoals();
  } catch (e) { modalError.value = 'Failed to save goal.'; }
  finally { saving.value = false; }
}

async function refreshData() {
  await Promise.all([loadHabits(), loadGoals()]);
}

onMounted(async () => { 
  await refreshData();
  window.addEventListener('refresh-dashboard', refreshData);
});
onUnmounted(() => {
  window.removeEventListener('refresh-dashboard', refreshData);
});
</script>

<template>
  <div class="goals-wrapper">
    <div class="container">

      <!-- Header -->
      <div class="page-header">
        <div class="header-left">
          <div class="page-icon"><svg class="glow-icon glow-green" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg></div>
          <div>
            <h1 class="page-title">Mission Control</h1>
            <p class="page-sub">Set targets, track progress, conquer missions</p>
          </div>
        </div>
        <button class="btn-new" @click="openCreate">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          New Mission
        </button>
      </div>

      <!-- Summary Cards -->
      <div class="summary-row">
        <div class="summary-card active-card">
          <div class="sum-icon"><svg class="glow-icon glow-red" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg></div>
          <div class="sum-val">{{ activeGoals.length }}</div>
          <div class="sum-lbl">Active</div>
        </div>
        <div class="summary-card completed-card">
          <div class="sum-icon"><svg class="glow-icon glow-gold" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg></div>
          <div class="sum-val">{{ completedGoals.length }}</div>
          <div class="sum-lbl">Completed</div>
        </div>
        <div class="summary-card failed-card">
          <div class="sum-icon"><svg class="glow-icon glow-red" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg></div>
          <div class="sum-val">{{ failedGoals.length }}</div>
          <div class="sum-lbl">Failed</div>
        </div>
      </div>

      <!-- Filter Tabs -->
      <div class="filter-tabs">
        <button
          class="filter-tab"
          :class="['active-tab', selectedStatus === 'ACTIVE' ? 'is-active' : '']"
          @click="selectedStatus = 'ACTIVE'"
        ><svg class="glow-icon glow-red" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle; margin-right: 4px; margin-bottom: 2px;"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>Active ({{ activeGoals.length }})</button>
        <button
          class="filter-tab"
          :class="['completed-tab', selectedStatus === 'COMPLETED' ? 'is-active' : '']"
          @click="selectedStatus = 'COMPLETED'"
        ><svg class="glow-icon glow-gold" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle; margin-right: 4px; margin-bottom: 2px;"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg>Completed ({{ completedGoals.length }})</button>
        <button
          class="filter-tab"
          :class="['failed-tab', selectedStatus === 'FAILED' ? 'is-active' : '']"
          @click="selectedStatus = 'FAILED'"
        ><svg class="glow-icon glow-red" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle; margin-right: 4px; margin-bottom: 2px;"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>Failed ({{ failedGoals.length }})</button>
      </div>

      <!-- Error -->
      <div v-if="error" class="error-box">{{ error }}</div>

      <!-- Loading -->
      <div v-if="loading" class="skeleton-list">
        <div v-for="n in 3" :key="n" class="skeleton-goal"></div>
      </div>

      <!-- Goals List -->
      <div v-else class="goals-list">

        <div v-if="visibleGoals.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg v-if="selectedStatus === 'COMPLETED'" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg>
            <svg v-else-if="selectedStatus === 'FAILED'" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
            <svg v-else width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
          </div>
          <p class="empty-text">No {{ selectedStatus.toLowerCase() }} missions yet.</p>
        </div>

        <div
          v-for="g in visibleGoals" :key="g.id"
          class="goal-card"
          :class="{
            'card-completed': g.status === 'COMPLETED',
            'card-failed':    g.status === 'FAILED',
            'card-overdue':   isOverdue(g),
            'card-boss':      g.isBoss || g.boss
          }"
        >
          <!-- Boss Pulse Effect -->
          <div v-if="(g.isBoss || g.boss) && g.status === 'ACTIVE'" class="boss-pulse"></div>
          <!-- Icon & Title -->
          <div class="goal-top">
            <div class="goal-icon">
              <svg v-if="goalIcon(g) === 'completed'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--gold)"><path d="M12 15l-2 5l9-9l-9-9l2 5l-9 9z"/></svg>
              <svg v-else-if="goalIcon(g) === 'failed'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--danger)"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
              <svg v-else-if="goalIcon(g) === 'overdue'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--warning)"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
              
              <!-- Boss Icons (Priority) -->
              <svg v-else-if="(g.isBoss || g.boss) && g.bossType === 'DRAGON'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color:var(--danger)"><path d="M12 2L4.5 9.5l.5 3.5 1 2 2 1 1.5 3L12 22l2.5-3 1.5-3 2-1 1-2 .5-3.5L12 2z"/><path d="M12 2l1.5 5.5m-3 0L12 2m-4 7.5L4.5 9.5m15 0L16 9.5"/></svg>
              <svg v-else-if="(g.isBoss || g.boss) && g.bossType === 'GOLEM'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color:#94a3b8"><path d="M7 21h10V9H7v12zM5 9h14M8 3h8v6H8V3zM3 13h4m10 0h4"/><rect x="9" y="12" width="2" height="2"/><rect x="13" y="12" width="2" height="2"/></svg>
              <svg v-else-if="(g.isBoss || g.boss)" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color:#c084fc"><path d="M12 2a5 5 0 0 0-5 5v3l-2 2v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6l-2-2V7a5 5 0 0 0-5-5z"/><circle cx="9" cy="9" r="1"/><circle cx="15" cy="9" r="1"/><path d="M9 14h6"/></svg>

              <!-- Regular Habit / Solo Icons -->
              <svg v-else-if="goalIcon(g) === 'habit'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--accent-2)"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
              <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:#c4b5fd"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </div>
            <div class="goal-main">
              <div class="goal-title">{{ g.title }}</div>
              <div class="goal-desc" v-if="g.description">{{ g.description }}</div>

              <!-- Tags row -->
              <div class="goal-tags">
                <span class="goal-tag habit-tag" v-if="g.habitId">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:2px"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M16 16l4 4"/><path d="M19 21l2-2"/></svg> {{ habitNameById.get(String(g.habitId)) || 'Unknown Quest' }}
                </span>
                <span class="goal-tag solo-tag" v-else>
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:2px"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg> Solo Mission
                </span>
                <span class="goal-tag deadline-tag overdue" v-if="isOverdue(g)">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:2px"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg> Overdue
                </span>
                <span class="goal-tag deadline-tag" v-else-if="deadlineText(g)">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:2px"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg> {{ deadlineText(g) }}
                </span>
                <span class="goal-tag boss-tag" v-if="g.isBoss || g.boss">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:2px"><path d="M12 2L4 9l4 13h8l4-13-8-7z"/></svg> BOSS RAID
                </span>
              </div>
            </div>

            <!-- Actions -->
            <div class="goal-actions">
              <template v-if="g.status === 'ACTIVE'">
                <button class="action-btn edit-btn" aria-label="Edit Goal" title="Edit" @click="openEdit(g)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                </button>
                <button class="action-btn delete-btn" aria-label="Delete Goal" title="Delete" @click="confirmDelete(g)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/></svg>
                </button>
              </template>
              <template v-else-if="g.status === 'FAILED'">
                <button class="action-btn retry-btn" aria-label="Retry Failed Goal" title="Retry" @click="openRetry(g)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
                </button>
                <button class="action-btn delete-btn" aria-label="Delete Goal" title="Delete" @click="confirmDelete(g)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/></svg>
                </button>
              </template>
              <template v-else>
                <span class="completed-badge" role="status" aria-label="Goal Completed">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:2px"><polyline points="20 6 9 17 4 12"/></svg> Done
                </span>
              </template>
            </div>
          </div>

          <!-- Progress bar (only for habit-linked goals) -->
          <div v-if="(g.habitId && g.targetCount) || (g.isBoss || g.boss)" class="goal-progress-wrap" role="progressbar" :aria-valuenow="progressPct(g)" aria-valuemin="0" aria-valuemax="100">
            <div class="goal-progress-info">
              <span class="prog-label">{{ (g.isBoss || g.boss) ? 'Boss Health' : 'Progress' }}</span>
              <span class="prog-nums">{{ g.progressCount || 0 }} / {{ g.targetCount }}</span>
            </div>
            <div class="goal-progress-track">
              <div
                class="goal-progress-fill"
                :class="{ 
                  'fill-complete': g.status === 'COMPLETED', 
                  'fill-failed': g.status === 'FAILED',
                  'fill-boss': (g.isBoss || g.boss) && g.status === 'ACTIVE'
                }"
                :style="{ width: progressPct(g) + '%' }"
              ></div>
            </div>
            <span class="prog-pct" :class="{ 'text-boss': (g.isBoss || g.boss) }">{{ progressPct(g) }}%</span>
          </div>
        </div>
      </div>

    </div>

    <!-- Delete Confirm Modal -->
    <Teleport to="body">
      <transition name="modal-fade">
        <div v-if="showDeleteConfirm" class="modal-backdrop" @click.self="showDeleteConfirm = false">
          <div class="confirm-modal">
            <div class="confirm-icon">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--warning)"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            </div>
            <h3 class="confirm-title">Abandon Mission?</h3>
            <p class="confirm-desc">Permanently delete <strong>{{ goalToDelete?.title }}</strong>?</p>
            <div class="confirm-actions">
              <button class="btn-cancel" @click="showDeleteConfirm = false">Keep It</button>
              <button class="btn-delete" @click="executeDelete">Delete</button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <GoalsAddEditModal
      :isOpen="showModal"
      :mode="modalMode"
      :habits="habits"
      :initialData="goalToEdit"
      :saving="saving"
      :error="modalError"
      @close="showModal = false"
      @save="saveFromModal"
    />
  </div>
</template>

<style scoped>
.goals-wrapper { width: 100%; }
.container { width: min(1100px, 92vw); margin: 0 auto; padding: 28px 0; display: grid; gap: 20px; }

/* Header */
.page-header {
  display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 14px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.page-icon   { font-size: 36px; }
.page-title  { margin: 0; font-family: var(--ff-head); font-size: 1.8rem; font-weight: 700; color: var(--text); line-height: 1; }
.page-sub    { margin: 3px 0 0; font-size: .85rem; color: var(--muted); font-weight: 600; }

.btn-new {
  display: flex; align-items: center; gap: 7px;
  padding: 10px 18px; border-radius: 10px;
  background: linear-gradient(135deg, rgba(0,229,160,.15), rgba(59,130,246,.1));
  border: 1px solid rgba(0,229,160,.3); color: var(--accent);
  font-family: var(--ff-head); font-size: .95rem; font-weight: 700;
  cursor: pointer; transition: all .2s; letter-spacing: .3px; text-decoration: none;
}
.btn-new:hover { 
  border-color: rgba(0,229,160,.5); transform: translateY(-1px); box-shadow: 0 4px 16px rgba(0,229,160,.2);
  background: linear-gradient(135deg, rgba(0,229,160,.25), rgba(59,130,246,.15));
}

/* Summary row */
.summary-row { display: flex; gap: 12px; flex-wrap: wrap; }
.summary-card {
  flex: 1; min-width: 100px;
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 16px; border-radius: 14px;
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border);
  transition: all .2s;
}
.summary-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.3); }
.sum-icon { font-size: 22px; }
.sum-val  { font-family: var(--ff-head); font-size: 1.6rem; font-weight: 700; color: var(--text); }
.sum-lbl  { font-size: .72rem; color: var(--muted); font-weight: 700; text-transform: uppercase; letter-spacing: .4px; }
.active-card:hover    { border-color: rgba(0,229,160,.4); }
.completed-card:hover { border-color: rgba(255,215,0,.4); }
.failed-card:hover    { border-color: rgba(239,68,68,.4); }

/* Filter tabs */
.filter-tabs { display: flex; gap: 8px; flex-wrap: wrap; }
.filter-tab {
  padding: 8px 16px; border-radius: 20px;
  border: 1px solid var(--border); background: rgba(255,255,255,.03);
  color: var(--muted); font-family: var(--ff-body); font-size: .83rem; font-weight: 700;
  cursor: pointer; transition: all .15s;
}
.filter-tab:hover { color: var(--text); border-color: var(--border2); }
.filter-tab.is-active { color: var(--text); border-color: var(--border2); background: rgba(255,255,255,.07); }
.active-tab.is-active    { border-color: rgba(0,229,160,.3); color: var(--accent); background: rgba(0,229,160,.08); }
.completed-tab.is-active { border-color: rgba(255,215,0,.3); color: var(--gold); background: rgba(255,215,0,.06); }
.failed-tab.is-active    { border-color: rgba(239,68,68,.3); color: #ff8f8f; background: rgba(239,68,68,.06); }

/* Error */
.error-box {
  padding: 14px 18px; border-radius: 12px;
  background: rgba(239,68,68,.08); border: 1px solid rgba(239,68,68,.2); color: #fca5a5;
}

/* Skeleton */
.skeleton-list { display: flex; flex-direction: column; gap: 12px; }
.skeleton-goal {
  height: 100px; border-radius: 14px;
  background: rgba(255,255,255,.03); animation: shimmer 1.4s ease-in-out infinite;
}
@keyframes shimmer { 0%,100%{opacity:.4} 50%{opacity:.8} }

/* Goals list */
.goals-list { display: flex; flex-direction: column; gap: 12px; }

.goal-card {
  position: relative; overflow: hidden;
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border); border-radius: 14px;
  padding: 20px 24px; transition: all .2s;
}
.goal-card:hover     { border-color: var(--border2); transform: translateY(-1px); box-shadow: 0 8px 24px rgba(0,0,0,.3); }
.card-completed      { border-color: rgba(255,215,0,.2); background: linear-gradient(160deg, rgba(255,215,0,.04), var(--bg2)); }
.card-failed         { border-color: rgba(239,68,68,.2); opacity: .8; }
.card-overdue        { border-color: rgba(255,204,102,.2); }

.goal-top { display: flex; align-items: flex-start; gap: 16px; }
.goal-icon {
  font-size: 28px; flex-shrink: 0; width: 50px; height: 50px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,.04); border-radius: 12px;
  border: 1px solid var(--border);
}
.goal-main { flex: 1; min-width: 0; }
.goal-title { font-family: var(--ff-head); font-size: 1.1rem; font-weight: 700; color: var(--text); }
.goal-desc  { font-size: .85rem; color: var(--muted); margin: 4px 0 10px; line-height: 1.4; }

.goal-tags { display: flex; flex-wrap: wrap; gap: 6px; }
.goal-tag {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: .75rem; font-weight: 700; padding: 4px 10px; border-radius: 20px;
  font-family: var(--ff-head); text-transform: uppercase; letter-spacing: 0.5px;
}
.habit-tag   { background: rgba(59,130,246,.1); border: 1px solid rgba(59,130,246,.2); color: var(--accent-2); }
.solo-tag    { background: rgba(139,92,246,.1); border: 1px solid rgba(139,92,246,.2); color: #c4b5fd; }
.deadline-tag{ background: rgba(255,255,255,.05); border: 1px solid var(--border); color: var(--muted); }
.deadline-tag.overdue { background: rgba(255,204,102,.1); border-color: rgba(255,204,102,.3); color: var(--warning); }

.goal-actions { display: flex; gap: 6px; flex-shrink: 0; margin-left: 10px; }
.action-btn {
  width: 36px; height: 36px; border-radius: 8px;
  border: 1px solid transparent; background: transparent;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  color: var(--muted); transition: all .15s;
}
.edit-btn:hover   { background: rgba(245,158,11,.12); color: var(--gold); border-color: rgba(245,158,11,.3); }
.delete-btn:hover { background: rgba(239,68,68,.12); color: var(--danger); border-color: rgba(239,68,68,.3); }
.retry-btn:hover  { background: rgba(0,229,160,.12); color: var(--accent); border-color: rgba(0,229,160,.3); }
.completed-badge {
  font-size: .75rem; font-weight: 800; padding: 6px 14px; border-radius: 20px;
  background: rgba(255,215,0,.12); border: 1px solid rgba(255,215,0,.25); color: var(--gold);
  font-family: var(--ff-head); text-transform: uppercase; letter-spacing: 0.5px;
}

/* Progress bar */
.goal-progress-wrap {
  display: flex; align-items: center; gap: 12px; margin-top: 16px;
  padding-top: 14px; border-top: 1px solid rgba(255,255,255,.06);
}
.goal-progress-info { display: flex; flex-direction: column; gap: 2px; min-width: 80px; }
.prog-label { font-size: .65rem; text-transform: uppercase; color: var(--muted); font-weight: 700; letter-spacing: .5px; }
.prog-nums  { font-size: .78rem; color: var(--text); font-weight: 700; font-family: monospace; }
.goal-progress-track {
  flex: 1; height: 8px; background: rgba(255,255,255,.07);
  border-radius: 4px; overflow: hidden;
}
.goal-progress-fill {
  height: 100%; border-radius: 4px;
  background: linear-gradient(90deg, var(--accent), var(--accent-2));
  transition: width .5s ease;
}
.fill-complete { background: linear-gradient(90deg, var(--gold), #e6a800); }
.fill-failed   { background: linear-gradient(90deg, var(--danger), #c0392b); }
.prog-pct { font-family: var(--ff-head); font-size: .85rem; font-weight: 700; color: var(--accent); min-width: 40px; text-align: right; }

/* Empty state */
.empty-state {
  text-align: center; padding: 48px 24px;
  background: rgba(255,255,255,.02); border: 1px dashed var(--border);
  border-radius: 16px; color: var(--muted);
}
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-text { margin: 0 0 20px; font-size: .95rem; }

/* Delete confirm */
.modal-backdrop {
  position: fixed; inset: 0; background: rgba(0,0,0,.7); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 9999;
}
.confirm-modal {
  background: var(--card); border: 1px solid var(--border);
  border-radius: 20px; width: 90%; max-width: 380px; padding: 28px; text-align: center;
  box-shadow: 0 20px 40px rgba(0,0,0,0.5);
}
.confirm-icon  { font-size: 40px; margin-bottom: 12px; }
.confirm-title { font-family: var(--ff-head); font-size: 1.3rem; font-weight: 700; color: var(--text); margin: 0 0 8px; }
.confirm-desc  { color: var(--muted); font-size: .95rem; line-height: 1.5; margin: 0 0 22px; }
.confirm-desc strong { color: var(--text); font-weight: 700; }
.confirm-actions { display: flex; gap: 10px; }
.btn-cancel, .btn-delete {
  flex: 1; padding: 12px; border-radius: 10px;
  font-family: var(--ff-head); font-weight: 700; font-size: .95rem; cursor: pointer; transition: all .2s;
}
.btn-cancel { background: transparent; border: 1px solid var(--border2); color: var(--text); }
.btn-cancel:hover { background: rgba(255,255,255,.06); border-color: var(--muted); }
.btn-delete { background: var(--danger); border: none; color: #fff; }
.btn-delete:hover { box-shadow: 0 4px 12px rgba(239,68,68,.3); transform: translateY(-1px); }

.modal-fade-enter-active { transition: all .25s cubic-bezier(.34,1.56,.64,1); }
.modal-fade-leave-active { transition: all .2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .confirm-modal { transform: scale(.95); }

/* Boss Raid Styles */
.card-boss {
  border-color: rgba(239, 68, 68, 0.4) !important;
  background: linear-gradient(160deg, rgba(var(--danger-rgb), 0.1), var(--card)) !important;
  box-shadow: 0 10px 30px rgba(239, 68, 68, 0.1);
}
:root[class*="theme-light"] .card-boss {
  background: linear-gradient(160deg, rgba(239, 68, 68, 0.05), #ffffff) !important;
}
.boss-pulse {
  position: absolute; inset: 0;
  border: 1px solid var(--danger);
  border-radius: 14px;
  animation: bossPulse 2s infinite;
  pointer-events: none;
}
@keyframes bossPulse {
  0% { box-shadow: inset 0 0 10px rgba(239, 68, 68, 0); }
  50% { box-shadow: inset 0 0 25px rgba(239, 68, 68, 0.2); }
  100% { box-shadow: inset 0 0 10px rgba(239, 68, 68, 0); }
}

.boss-tag { background: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.3); color: #ff8f8f; }
.fill-boss { background: linear-gradient(90deg, #ef4444, #991b1b) !important; box-shadow: 0 0 10px rgba(239, 68, 68, 0.5); }
.text-boss { color: #ef4444 !important; text-shadow: 0 0 8px rgba(239, 68, 68, 0.4); }
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 20px;
  }
  .btn-new {
    width: 100%;
    justify-content: center;
    padding: 14px;
  }

  /* Horizontal Summary for Mobile */
  .summary-row {
    display: flex !important;
    flex-wrap: nowrap !important;
    overflow-x: auto;
    padding-bottom: 12px;
    scrollbar-width: none;
    -ms-overflow-style: none;
  }
  .summary-row::-webkit-scrollbar { display: none; }
  .summary-card {
    flex: 0 0 140px !important;
    scroll-snap-align: start;
  }

  /* Horizontal Tabs for Mobile */
  .filter-tabs {
    flex-wrap: nowrap !important;
    overflow-x: auto;
    padding-bottom: 8px;
    scrollbar-width: none;
  }
  .filter-tabs::-webkit-scrollbar { display: none; }
  .filter-tab {
    flex-shrink: 0;
    white-space: nowrap;
  }

  /* 2-Column Grid for Missions (Non-Vertical Stack) */
  .goals-list {
    display: grid !important;
    grid-template-columns: 1fr 1fr !important;
    gap: 12px !important;
  }

  .goal-card {
    padding: 16px !important;
    display: flex;
    flex-direction: column;
  }

  .goal-top {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 12px;
  }

  .goal-icon {
    width: 40px;
    height: 40px;
    margin: 0 auto;
  }

  .goal-title {
    font-size: 0.95rem;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .goal-desc {
    display: none; /* Keep grid clean */
  }

  .goal-tags {
    justify-content: center;
    margin-top: 8px;
  }
  .goal-tag {
    font-size: 0.65rem;
    padding: 2px 6px;
  }

  .goal-actions {
    margin-left: 0;
    margin-top: 12px;
    width: 100%;
    justify-content: center;
    border-top: 1px solid var(--border);
    padding-top: 12px;
  }

  .goal-progress-wrap {
    flex-direction: column;
    gap: 8px;
    align-items: stretch;
  }
  .goal-progress-info {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  .prog-pct {
    text-align: center;
    margin-top: 4px;
  }
}
</style>
