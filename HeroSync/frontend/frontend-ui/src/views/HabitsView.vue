<script setup>
import { onMounted, onUnmounted, ref, computed } from 'vue';
import HabitsAddEditModal from '../components/HabitsAddEditModal.vue';
import { http } from '../api/http';
import { createHabit, deleteHabit, listHabits, updateHabit } from '../api/habits';
import { playSound } from '../utils/config';

const habits      = ref([]);
const loading     = ref(true);
const error       = ref('');
const userName    = ref('');
const showModal   = ref(false);
const isEditing   = ref(false);
const habitToEdit = ref(null);
const saving      = ref(false);
const modalError  = ref('');
const searchQuery = ref('');
const showDeleteConfirm = ref(false);
const habitToDelete     = ref(null);

const filteredHabits = computed(() => {
  if (!searchQuery.value.trim()) return habits.value;
  const q = searchQuery.value.toLowerCase();
  return habits.value.filter(h =>
    h.name.toLowerCase().includes(q) || (h.description || '').toLowerCase().includes(q)
  );
});

function getRarity(index) {
  const tiers = ['common', 'rare', 'epic', 'legendary'];
  return tiers[index % 4];
}

// CSRF and Headers
const getHeaders = () => {
  const headers = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
  const getCookie = (name) => {
    if (!document.cookie) return null;
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    return match ? decodeURIComponent(match[2]) : null;
  };
  const csrfToken = getCookie('XSRF-TOKEN');
  if (csrfToken) headers['X-XSRF-TOKEN'] = csrfToken;
  return headers;
};

async function graphqlRequest(query) {
  const res = await fetch('/graphql', {
    method: 'POST',
    headers: getHeaders(),
    credentials: 'include',
    body: JSON.stringify({ query })
  });
  const json = await res.json();
  if (json.errors) throw new Error(json.errors[0].message);
  return json.data;
}

async function load() {
  loading.value = true; error.value = '';
  try { habits.value = await listHabits(); }
  catch (e) { habits.value = []; error.value = 'Failed to load habits. Please try again.'; }
  finally { loading.value = false; }
}
async function toggleComplete(habit, e) {
  try {
    const newVal = !habit.completedToday;
    const action = newVal ? 'complete' : 'uncomplete';
    const query = `mutation { ${action}Habit(id: ${habit.id}) { completedToday } }`;
    const data = await graphqlRequest(query);

    habit.completedToday = data[`${action}Habit`].completedToday;
    
    // Trigger XP Burst if completed
    if (habit.completedToday && e && e.target) {
      const rect = e.target.getBoundingClientRect();
      const x = rect.left + rect.width / 2;
      const y = rect.top + rect.height / 2;
      
      window.dispatchEvent(new CustomEvent('trigger-xp-burst', {
        detail: { xp: habit.xpValue || 60, x, y }
      }));
      // Notify components to refresh
      window.dispatchEvent(new CustomEvent('refresh-dashboard'));
      // Play Sound
      playSound('questDone');
    }
  } catch (err) {
    error.value = 'Failed to update habit.';
  }
}

async function loadProfile() {
  try {
    const res = await http.get('/profile/me');
    userName.value = res.data?.name || 'Hero';
  } catch { userName.value = ''; }
}

function openCreateModal() {
  modalError.value = ''; isEditing.value = false;
  habitToEdit.value = null; showModal.value = true;
}

function openEditModal(h) {
  modalError.value = ''; isEditing.value = true;
  habitToEdit.value = { ...h }; showModal.value = true;
}

function confirmDelete(h) {
  habitToDelete.value = h; showDeleteConfirm.value = true;
}

async function executeDelete() {
  if (!habitToDelete.value) return;
  try { await deleteHabit(habitToDelete.value.id); await load(); }
  catch (e) { alert('Failed to delete. Please try again.'); }
  finally { showDeleteConfirm.value = false; habitToDelete.value = null; }
}

async function saveHabit(formData) {
  modalError.value = ''; saving.value = true;
  try {
    if (isEditing.value && habitToEdit.value?.id != null) {
      await updateHabit(habitToEdit.value.id, { 
        name: formData.name, 
        description: formData.description || null,
        xpValue: formData.xpValue
      });
    } else {
      await createHabit({ 
        name: formData.name, 
        description: formData.description || null,
        xpValue: formData.xpValue
      });
    }
    showModal.value = false; await load();
  } catch (e) { modalError.value = 'Failed to save. Please try again.'; }
  finally { saving.value = false; }
}

onMounted(async () => { 
  await Promise.all([loadProfile(), load()]); 
  window.addEventListener('refresh-dashboard', load);
});
onUnmounted(() => {
  window.removeEventListener('refresh-dashboard', load);
});
</script>

<template>
  <div class="habits-wrapper">
    <main class="main">
      <section class="section">
        <div class="container">

          <!-- Page Header -->
          <div class="page-header">
            <div class="header-left">
              <div class="page-icon">
                <svg class="glow-icon glow-blue" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M16 16l4 4"/><path d="M19 21l2-2"/></svg>
              </div>
              <div>
                <h1 class="page-title">Quest Board</h1>
                <p class="page-sub">{{ userName ? `${userName}'s` : 'Your' }} active habits</p>
              </div>
            </div>
            <button class="btn-new" aria-label="Create New Quest" @click="openCreateModal">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              New Quest
            </button>
          </div>

          <!-- Stats Bar -->
          <div class="stats-bar">
            <div class="stat-pill">
              <span class="stat-pill-icon"><svg class="glow-icon glow-blue" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M16 16l4 4"/><path d="M19 21l2-2"/></svg></span>
              <span class="stat-pill-val">{{ habits.length }}</span>
              <span class="stat-pill-lbl">Total Quests</span>
            </div>
            <div class="stat-pill">
              <span class="stat-pill-icon"><svg class="glow-icon glow-red" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg></span>
              <span class="stat-pill-val">+{{ habits.reduce((acc, h) => acc + (h.xpValue || 60), 0) }}</span>
              <span class="stat-pill-lbl">XP / Day</span>
            </div>
            <div class="stat-pill">
              <span class="stat-pill-icon"><svg class="glow-icon glow-green" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg></span>
              <span class="stat-pill-val">{{ habits.filter(h => !h.archived).length }}</span>
              <span class="stat-pill-lbl">Active</span>
            </div>
          </div>

          <!-- Search -->
          <div class="search-wrap">
            <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            <input
              v-model="searchQuery"
              class="search-input"
              placeholder="Search quests…"
            />
          </div>

          <!-- Loading / Error -->
          <div v-if="loading" class="skeleton-list">
            <div v-for="n in 4" :key="n" class="skeleton-row"></div>
          </div>
          <div v-else-if="error" class="error-box">{{ error }}</div>

          <!-- Quest List -->
          <div v-else class="quest-list">
            <div v-if="filteredHabits.length === 0" class="empty-state">
              <div class="empty-icon">
                <svg class="glow-icon glow-blue" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M16 16l4 4"/><path d="M19 21l2-2"/></svg>
              </div>
              <p class="empty-text">No quests found. Create your first habit!</p>
              <button class="btn-new" @click="openCreateModal">+ New Quest</button>
            </div>

            <div
              v-for="(h, index) in filteredHabits" :key="h.id"
              class="quest-card"
              :class="`tier-${getRarity(index)}`"
            >
              <div class="quest-rarity-stripe" :class="`stripe-${getRarity(index)}`"></div>
              
              <div class="quest-badge-icon">
                <svg class="glow-icon glow-gold" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/></svg>
              </div>

              <div class="quest-body">
                <div class="quest-name">{{ h.name }}</div>
                <div class="quest-desc">{{ h.description || 'No description' }}</div>
              </div>

              <div class="quest-rewards">
                <span class="xp-tag">+{{ h.xpValue }} XP</span>
              </div>

              <div class="quest-actions">
                <button class="action-btn edit-btn" aria-label="Edit Quest" title="Edit Quest" @click="openEditModal(h)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                </button>
                <button class="action-btn delete-btn" aria-label="Abandon Quest" title="Abandon Quest" @click="confirmDelete(h)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
              </div>
            </div>
          </div>

        </div>
      </section>
    </main>

    <!-- Delete Confirm Modal -->
    <Teleport to="body">
      <transition name="modal-fade">
        <div v-if="showDeleteConfirm" class="modal-backdrop" @click.self="showDeleteConfirm = false">
          <div class="confirm-modal">
            <div class="confirm-icon">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: #f59e0b;"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            </div>
            <h3 class="confirm-title">Abandon Quest?</h3>
            <p class="confirm-desc">
              Permanently remove <strong>{{ habitToDelete?.name }}</strong>? This cannot be undone.
            </p>
            <div class="confirm-actions">
              <button class="btn-cancel" @click="showDeleteConfirm = false">Keep It</button>
              <button class="btn-delete" @click="executeDelete">Abandon</button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <HabitsAddEditModal
      :isOpen="showModal"
      :editMode="isEditing"
      :initialData="habitToEdit"
      :saving="saving"
      :error="modalError"
      @close="showModal = false"
      @save="saveHabit"
    />
  </div>
</template>

<style scoped>
.habits-wrapper { width: 100%; }
.container { width: min(1100px, 92vw); margin: 0 auto; padding: 28px 0; }

/* Header */
.page-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.page-icon { font-size: 36px; }
.page-title {
  margin: 0; font-family: var(--ff-head); font-size: 1.8rem; font-weight: 700;
  color: var(--text); line-height: 1;
}
.page-sub { margin: 3px 0 0; font-size: .85rem; color: var(--muted); font-weight: 600; }

.btn-new {
  display: flex; align-items: center; gap: 7px;
  padding: 10px 18px; border-radius: 10px;
  background: linear-gradient(135deg, rgba(0,229,160,.15), rgba(59,130,246,.1));
  border: 1px solid rgba(0,229,160,.3); color: var(--accent);
  font-family: var(--ff-head); font-size: .95rem; font-weight: 700;
  cursor: pointer; transition: all .2s; letter-spacing: .3px;
}
.btn-new:hover {
  background: linear-gradient(135deg, rgba(0,229,160,.25), rgba(59,130,246,.15));
  border-color: rgba(0,229,160,.5); transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(0,229,160,.2);
}

/* Stats bar */
.stats-bar {
  display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap;
}
.stat-pill {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,255,255,.03); border: 1px solid var(--border);
  border-radius: 10px; padding: 10px 16px; flex: 1; min-width: 100px;
}
.stat-pill-icon { font-size: 18px; }
.stat-pill-val {
  font-family: var(--ff-head); font-size: 1.1rem; font-weight: 700; color: var(--text);
}
.stat-pill-lbl { font-size: .75rem; color: var(--muted); font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }

/* Search */
.search-wrap {
  position: relative; margin-bottom: 24px;
}
.search-icon {
  position: absolute; left: 14px; top: 50%; transform: translateY(-50%);
  color: var(--muted); pointer-events: none;
}
.search-input {
  width: 100%; padding: 11px 14px 11px 40px;
  background: rgba(255,255,255,.03); border: 1px solid var(--border);
  border-radius: 10px; color: var(--text);
  font-family: var(--ff-body); font-size: .9rem;
  transition: border-color .2s;
}
.search-input:focus { outline: none; border-color: var(--accent); background: rgba(255,255,255,.05); }
.search-input::placeholder { color: #4b6279; }

/* Skeleton */
.skeleton-list { display: flex; flex-direction: column; gap: 12px; }
.skeleton-row {
  height: 72px; border-radius: 12px;
  background: rgba(255,255,255,.03);
  animation: shimmer 1.4s ease-in-out infinite;
}
@keyframes shimmer { 0%,100%{opacity:.4} 50%{opacity:.8} }

/* Error */
.error-box {
  padding: 14px 18px; border-radius: 12px;
  background: rgba(239,68,68,.08); border: 1px solid rgba(239,68,68,.2);
  color: #fca5a5; font-size: .9rem;
}

/* Quest list */
.quest-list { display: flex; flex-direction: column; gap: 12px; }

.quest-card {
  position: relative;
  display: flex; align-items: center; gap: 14px;
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border); border-radius: 14px;
  padding: 16px 18px 16px 24px; transition: all .2s;
  overflow: hidden;
}
.quest-card:hover {
  border-color: var(--border2); transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(0,0,0,.3);
}

.quest-rarity-stripe {
  position: absolute; left: 0; top: 0; bottom: 0; width: 4px;
}
.stripe-common { background: rgba(0,229,160,.6); }
.stripe-rare   { background: rgba(59,130,246,.6); }
.stripe-epic   { background: rgba(139,92,246,.6); }
.stripe-legendary { background: rgba(245,158,11,.6); }

/* Tiers hover */
.quest-card.tier-common:hover { border-color: rgba(0,229,160,.3); }
.quest-card.tier-rare:hover   { border-color: rgba(59,130,246,.3); }
.quest-card.tier-epic:hover   { border-color: rgba(139,92,246,.3); }
.quest-card.tier-legendary:hover { border-color: rgba(245,158,11,.4); }

.quest-badge-icon {
  width: 44px; height: 44px; border-radius: 10px;
  background: rgba(255,255,255,.05); border: 1px solid var(--border);
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; flex-shrink: 0;
}
.quest-body { flex: 1; min-width: 0; }
.quest-name { font-weight: 700; font-size: 1rem; color: var(--text); font-family: var(--ff-head); }
.quest-desc { font-size: .8rem; color: var(--muted); margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.quest-rewards { flex-shrink: 0; }
.xp-tag {
  background: rgba(0,229,160,.1); border: 1px solid rgba(0,229,160,.2);
  color: var(--accent); border-radius: 8px;
  padding: 4px 10px; font-family: var(--ff-head); font-size: .8rem; font-weight: 700;
}

.quest-actions { display: flex; gap: 6px; flex-shrink: 0; margin-left: 8px; }
.action-btn {
  width: 36px; height: 36px; border-radius: 8px;
  border: 1px solid transparent; background: transparent;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  color: var(--muted); transition: all .15s;
}
.edit-btn:hover   { background: rgba(245,158,11,.12); color: var(--gold); border-color: rgba(245,158,11,.3); }
.delete-btn:hover { background: rgba(239,68,68,.12); color: var(--danger); border-color: rgba(239,68,68,.3); }

/* Empty state */
.empty-state {
  text-align: center; padding: 48px 24px;
  background: rgba(255,255,255,.02); border: 1px dashed var(--border);
  border-radius: 16px; color: var(--muted);
}
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-text { margin: 0 0 20px; font-size: .95rem; }

/* Delete confirm modal */
.modal-backdrop {
  position: fixed; inset: 0; background: rgba(0,0,0,.7); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 9999;
}
.confirm-modal {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 20px; width: 90%; max-width: 380px;
  padding: 28px; text-align: center;
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
.btn-delete { background: linear-gradient(135deg, #ef4444, #dc2626); border: none; color: #fff; }
.btn-delete:hover { box-shadow: 0 4px 12px rgba(239,68,68,.3); transform: translateY(-1px); }

.modal-fade-enter-active { transition: all .25s cubic-bezier(.34,1.56,.64,1); }
.modal-fade-leave-active { transition: all .2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .confirm-modal { transform: scale(.95); }
</style>
