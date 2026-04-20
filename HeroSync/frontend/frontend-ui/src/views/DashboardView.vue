<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { listGoals, createGoal } from '../api/goals';
import GoalsAddEditModal from '../components/GoalsAddEditModal.vue';
import AddHabitModal from '../components/AddHabitModal.vue';
import DashboardAchievementsPanel from '../components/DashboardAchivementsPanel.vue';
import CharacterCard from '../components/CharacterCard.vue';
import { playSound } from '../utils/config';

const router = useRouter();

// ── State ──────────────────────────────────────────────
const user        = ref({ name: 'Hero', id: 0, streak: 0, xp: 0, avatar: '' });
const heatmap     = ref([]);
const history     = ref([]);
const habits      = ref([]);
const goals       = ref([]);
const showModal   = ref(false);
const isEditing   = ref(false);
const habitToEdit = ref(null);
const showGoalModal = ref(false);
const goalToEdit  = ref(null);
const goalSaving  = ref(false);
const goalError   = ref('');

// Gamification state
const showXpFlash      = ref(false);
const xpFlashAmt       = ref(0);
const xp = computed(() => user.value.xp);
const level = computed(() => Math.max(1, Math.floor(xp.value / 2000) + 1));

// Achievements ref
const achievementsRef = ref(null);

// ── Computed ───────────────────────────────────────────
const completedCount = computed(() => habits.value.filter(h => h.completed).length);
const totalHabits    = computed(() => habits.value.length);

// ── API helpers ────────────────────────────────────────
const API_BASE = '';
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
  const res  = await fetch(`${API_BASE}/graphql`, {
    method: 'POST', headers: getHeaders(), credentials: 'include',
    body: JSON.stringify({ query })
  });
  const json = await res.json();
  if (json.errors) throw new Error(json.errors[0].message);
  return json.data;
}

// ── Data fetching ──────────────────────────────────────
async function fetchInitialData() {
  try {
    const userRes = await fetch(`${API_BASE}/api/dashboard`, {
      headers: { 'Accept': 'application/json' }, credentials: 'include'
    });
    if (userRes.ok) {
      const data = await userRes.json();
      user.value.name   = data.userName;
      user.value.id     = data.dashboardId;
      user.value.avatar = data.avatar || '';
      user.value.xp     = data.totalXP || 0;
      user.value.streak = data.currentStreak || 0;
    }
  } catch (e) { console.warn('Auth check failed'); }
  await fetchHabits();
  await fetchGoals();
  await fetchActivityStats();
}

async function fetchHabits() {
  try {
    const data = await graphqlRequest(`{
      habits { id name description xpValue archived }
    }`);
    const completed = await graphqlRequest(`{
      habitsCompletedToday
    }`);
    const completedIds = new Set(completed.habitsCompletedToday);

    habits.value = data.habits
      .filter(h => !h.archived)
      .map(h => ({
        ...h,
        completed: completedIds.has(Number(h.id))
      }));
  } catch (e) { console.error('Fetch habits failed', e); }
}

async function fetchGoals() {
  try {
    const res = await listGoals();
    goals.value = res || [];
  } catch (e) {
    console.error('Error fetching goals:', e);
  }
}

async function fetchActivityStats() {
  const query = 'query { heatmap { date totalCompletions } activities { date habitId habitName completions xpValue } }';
  try {
    const data = await graphqlRequest(query);
    if (data) {
      heatmap.value = data.heatmap || [];
      history.value = data.activities || [];
      calculateStats();
      mapHabitStatus();
    }
  } catch (e) { console.error('Error fetching stats:', e); }
}

// ── Logic ──────────────────────────────────────────────
function mapHabitStatus() {
  const now   = new Date();
  const today = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
    .toISOString().split('T')[0];
  habits.value.forEach(habit => {
    const match = history.value.find(act =>
      act.date.startsWith(today) && String(act.habitId) === String(habit.id)
    );
    habit.completed = match ? match.completions > 0 : false;
  });
  const total = habits.value.length;
  user.value.totalDone = total === 0 ? 0
    : Math.round((habits.value.filter(h => h.completed).length / total) * 100);
}

function calculateStats() {
  if (!heatmap.value.length) { user.value.streak = 0; return; }
  const sorted   = [...heatmap.value].sort((a, b) => new Date(b.date) - new Date(a.date));
  const todayStr = new Date().toISOString().split('T')[0];
  let streak     = 0;
  for (const day of sorted) {
    const ds = day.date.substring(0, 10);
    if (day.totalCompletions > 0) streak++;
    else if (ds !== todayStr) break;
  }
  user.value.streak = streak;
}

// ── Actions ────────────────────────────────────────────
const refreshAchievements = async () => {
  if (achievementsRef.value) await achievementsRef.value.loadAll();
};

const toggleCheck = async (habit, index, e) => {
  const now   = new Date();
  const today = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
    .toISOString().split('T')[0];
  try {
    const res = await fetch(`${API_BASE}/api/activities`, {
      method: 'POST', headers: getHeaders(), credentials: 'include',
      body: JSON.stringify({
        habitId: Number(habit.id), date: today,
        completions: habit.completed ? 1 : 0
      })
    });
    if (!res.ok) { habit.completed = !habit.completed; return; }

    if (habit.completed) {
      xpFlashAmt.value = habit.xpValue || 60;
      showXpFlash.value = true;
      setTimeout(() => { showXpFlash.value = false; }, 1800);
    }
      
    if (e && e.target) {
      const rect = e.target.getBoundingClientRect();
      window.dispatchEvent(new CustomEvent('trigger-xp-burst', {
        detail: { xp: xpFlashAmt.value, x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 }
      }));
    }
    await fetchActivityStats();
    await fetchInitialData();
    window.dispatchEvent(new CustomEvent('refresh-dashboard'));
    playSound('questDone');
    await fetchGoals();
    await refreshAchievements();
  } catch (err) {
    console.error('Toggle failed', err);
    habit.completed = !habit.completed;
  }
};

const openCreateModal = () => {
  isEditing.value = false; habitToEdit.value = null; showModal.value = true;
};
const openCreateGoalModal = () => { showGoalModal.value = true; };

const saveHabit = async (habitData) => {
  const safeName = habitData.name.replace(/"/g, '\\"');
  const safeDesc = habitData.description ? habitData.description.replace(/"/g, '\\"') : '';
  try {
    let mutation;
    if (isEditing.value) {
      mutation = `mutation { updateHabit(id: "${habitToEdit.value.id}", input: { name: "${safeName}", description: "${safeDesc}", xpValue: ${habitData.xpValue} }) { id name description xpValue archived } }`;
    } else {
      mutation = `mutation { createHabit(input: { name: "${safeName}", description: "${safeDesc}", xpValue: ${habitData.xpValue} }) { id name description xpValue archived } }`;
    }
    const data = await graphqlRequest(mutation);
    if (isEditing.value) {
      const updated = data.updateHabit;
      const idx = habits.value.findIndex(h => h.id == updated.id);
      if (idx !== -1) habits.value[idx] = { ...updated, completed: habits.value[idx].completed };
    } else {
      habits.value.push({ ...data.createHabit, completed: false });
    }
    showModal.value = false;
  } catch (e) { console.error('Error saving habit:', e); alert('Failed to save.'); }
};

const saveGoal = async (payload) => {
  goalSaving.value = true;
  goalError.value = '';
  try {
    await createGoal(payload);
    showGoalModal.value = false;
    await fetchGoals();
  } catch (e) {
    console.error('Error creating goal:', e);
    goalError.value = 'Failed to create goal.';
  } finally {
    goalSaving.value = false;
  }
};

const formatDate = (d) => ({
  day: new Date(d).getDate().toString().padStart(2, '0'),
  mon: new Date(d).toLocaleString('default', { month: 'short' })
});
const formatSimpleDate = (d) => d.substring(5);

onMounted(() => {
  fetchInitialData();
  window.addEventListener('refresh-dashboard', fetchInitialData);
});
</script>

<template>
  <div class="dashboard-wrapper">
    <main class="main">
      <section class="section">
        <div class="container">
          <CharacterCard 
            :name="user.name"
            :level="level"
            :xp="xp"
            :streak="user.streak"
            :avatarSeed="user.avatar"
            size="lg"
            style="margin-bottom: 20px;"
          >
            <div class="hero-stats">
              <div class="hstat">
                <div class="hval">{{ completedCount }}/{{ totalHabits }}</div>
                <div class="hlbl">Quests Done</div>
              </div>
            </div>
          </CharacterCard>

          <div class="stat-row">
            <div class="stat-card" style="display: flex; flex-direction: column; justify-content: center; gap: 10px; padding: 16px;">
              <div style="display: flex; align-items: center; gap: 12px;">
                <div class="stat-icon fire-icon" style="margin-bottom:0; width: 30px; height: 30px; flex-shrink: 0;"><svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2s-6 5.5-6 10a6 6 0 0012 0C18 7.5 12 2 12 2zm0 14a2 2 0 01-2-2c0-2 2-4.5 2-4.5s2 2.5 2 4.5a2 2 0 01-2 2z"/></svg></div>
                <div style="display: flex; align-items: baseline; gap: 6px;">
                  <div class="sv" style="font-size: 1.5rem; line-height: 1;">{{ user.streak }}</div>
                  <div class="sl">Streak</div>
                </div>
              </div>
              <div style="display: flex; align-items: center; gap: 12px;">
                <div class="stat-icon xp-icon" style="margin-bottom:0; width: 30px; height: 30px; flex-shrink: 0;"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg></div>
                <div style="display: flex; align-items: baseline; gap: 6px;">
                  <div class="sh accent" style="font-size: 1.2rem; margin-top: 0; line-height: 1;">{{ xp.toLocaleString() }}</div>
                  <div class="sl">Total XP</div>
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon check-icon"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg></div>
              <div class="sv">{{ user.totalDone }}%</div><div class="sl">Done Today</div>
              <div class="mini-bar" role="progressbar" :aria-valuenow="user.totalDone" aria-valuemin="0" aria-valuemax="100"><div class="mini-fill" :style="{ width: user.totalDone + '%' }" /></div>
            </div>

            <div class="stat-card">
              <div class="stat-icon" style="background: rgba(255,215,0,0.1); color: var(--gold);"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="2"/></svg></div>
              <div class="sv">{{ goals.filter(g => g.status === 'ACTIVE').length }}</div><div class="sl">Active Missions</div>
              <div class="sh accent" @click="router.push('/goals')" style="cursor:pointer">View All →</div>
            </div>
            <div class="stat-card hm-card">
              <div class="hm-header"><span class="sl">Consistency</span><span class="hm-sub">Last 10 days</span></div>
              <div class="hm-grid">
                <div v-for="day in heatmap" :key="day.date" class="day-box"
                  :class="{ lvl1: day.totalCompletions>0 && day.totalCompletions<3, lvl2: day.totalCompletions>=3 && day.totalCompletions<6, lvl3: day.totalCompletions>=6 }"
                  :title="`${day.date}: ${day.totalCompletions}`">
                  <span class="dd">{{ formatDate(day.date).day }}</span>
                  <span class="mm">{{ formatDate(day.date).mon }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="grid">
            <div class="card span-4">
              <div class="card-inner">
                <div class="section-head">
                  <div class="sec-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/></svg>
                    Active Quests <span class="count-pill">{{ completedCount }}/{{ totalHabits }}</span>
                  </div>
                  <div class="sec-actions">
                    <button class="btn-sm" aria-label="Add New Quest" @click="openCreateModal">+ Quest</button>
                    <button class="btn-sm ghost" aria-label="Manage All Quests" @click="router.push('/habits')">Manage</button>
                  </div>
                </div>
                <div class="scroll-area">
                  <div v-if="habits.length === 0" class="empty-state">
                    <p>No quests yet. Add your first habit!</p>
                    <button class="btn primary" @click="openCreateModal">Add Quest</button>
                  </div>
                  <div v-else class="quests-list">
                    <div v-for="(habit, index) in habits" :key="habit.id" class="quest-row" :class="{ done: habit.completed }">
                      <label class="q-check-wrap">
                        <input type="checkbox" class="q-checkbox" v-model="habit.completed" @change="toggleCheck(habit, index, $event)" />
                        <span class="q-mark"><svg v-if="habit.completed" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5"><polyline points="20 6 9 17 4 12"/></svg></span>
                      </label>
                      <div class="q-rarity" :style="{ background: ['#00e5a0','#3b82f6','#8b5cf6','#f59e0b'][index%4] }" />
                      <div class="q-info">
                        <div class="q-name" :class="{ 'q-done': habit.completed }">{{ habit.name }}</div>
                        <div class="q-desc" v-if="habit.description">{{ habit.description }}</div>
                      </div>
                      <span class="q-xp">+{{ habit.xpValue }} XP</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="card span-4">
              <div class="card-inner">
                <div class="section-head mb-16">
                  <div class="sec-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                    Activity Log
                  </div>
                </div>
                <div class="scroll-area">
                  <table class="table">
                    <thead><tr><th>Date</th><th>Quest</th><th>Status</th><th>Reward</th></tr></thead>
                    <tbody>
                      <tr v-if="history.length===0"><td colspan="4" class="empty-cell">No activity yet.</td></tr>
                      <tr v-for="act in history" :key="act.date+act.habitName">
                        <td class="date-cell">{{ formatSimpleDate(act.date) }}</td>
                        <td>{{ act.habitName }}</td>
                        <td><span v-if="act.completions>0" class="status-done">Done</span><span v-else class="status-miss">Missed</span></td>
                        <td><span class="xp-mini" v-if="act.completions>0">+{{ act.xpValue }} XP</span></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <div class="card span-4">
              <div class="card-inner">
                <div class="section-head mb-16">
                  <div class="sec-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4a2 2 0 01-2-2V5h4"/><path d="M18 9h2a2 2 0 002-2V5h-4"/><path d="M6 5h12v8a6 6 0 01-12 0V5z"/></svg>
                    Achievements
                  </div>
                  <router-link to="/achievements" class="btn-sm ghost">See All</router-link>
                </div>
                <div class="scroll-area">
                  <div class="vertical-stack"><DashboardAchievementsPanel ref="achievementsRef" /></div>
                </div>
              </div>
            </div>
          </div>

          <div class="quick-actions">
            <button class="qa-btn primary-qa" aria-label="Create New Quest" @click="openCreateModal">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>New Quest
            </button>
            <button class="qa-btn" aria-label="Create New Goal" @click="openCreateGoalModal">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="2"/></svg>New Goal
            </button>
            <router-link to="/achievements" class="qa-btn" aria-label="View Achievements and Badges">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4a2 2 0 01-2-2V5h4"/><path d="M6 5h12v8a6 6 0 01-12 0V5z"/></svg>Badges
            </router-link>
            <router-link to="/reports/weekly" class="qa-btn" aria-label="View Weekly Progress Report">
              <span>Report</span>
            </router-link>
          </div>

        </div>
      </section>
    </main>

    <AddHabitModal :isOpen="showModal" :editMode="isEditing" :initialData="habitToEdit" @close="showModal = false" @save="saveHabit" />
    <GoalsAddEditModal :isOpen="showGoalModal" mode="create" :initialData="null" :habits="habits" :saving="goalSaving" :error="goalError" @close="showGoalModal = false" @save="saveGoal" />
  </div>
</template>

<style scoped>
/* ── Box Height Sync & Scrolling ── */
.grid { display: grid; grid-template-columns: repeat(12, 1fr); gap: 20px; }
.grid .card { 
  height: 400px; /* SYNCED HEIGHT */
  display: flex; 
  flex-direction: column; 
}
.card-inner { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  padding: 20px; 
  overflow: hidden; 
}
.scroll-area { 
  flex: 1; 
  overflow-y: auto; 
  min-height: 0; 
  padding-right: 4px;
}
.scroll-area::-webkit-scrollbar { width: 5px; }
.scroll-area::-webkit-scrollbar-thumb { background: var(--border2); border-radius: 3px; }

/* ── Stat Cards ── */
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 20px; }
@media (max-width: 1024px) { .stat-row { grid-template-columns: 1fr 1fr; } }
@media (max-width: 600px)  { .stat-row { grid-template-columns: 1fr; } }

.stat-card {
  background: linear-gradient(160deg, var(--card), var(--bg2));
  border: 1px solid var(--border); border-radius: 14px; padding: 16px;
  display: flex; flex-direction: column; gap: 4px; transition: border-color .2s, transform .2s;
}
.stat-card:hover { border-color: var(--border2); transform: translateY(-2px); }
.stat-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-bottom: 4px; }
.fire-icon  { background: rgba(239,68,68,.12); color: #fca5a5; }
.check-icon { background: rgba(0,229,160,.1); color: var(--accent); }
.xp-icon    { background: rgba(59,130,246,.1); color: var(--accent-2); }
.sv   { font-family: var(--ff-head); font-size: 1.7rem; font-weight: 700; color: var(--text); line-height: 1; }
.sl   { font-size: .72rem; color: var(--muted); font-weight: 600; text-transform: uppercase; letter-spacing: .4px; }
.sh   { font-size: .75rem; font-weight: 700; margin-top: 2px; }
.sh.accent { color: var(--accent); }
.xp-mini { background: rgba(0,229,160,.08); border: 1px solid rgba(0,229,160,.18); color: var(--accent); border-radius: 6px; padding: 2px 6px; font-size: .68rem; font-weight: 700; white-space: nowrap; }
.xp-mini.missed { color: var(--muted); border-color: var(--border); background: transparent; }
.mini-bar  { height: 5px; background: rgba(255,255,255,.07); border-radius: 3px; overflow: hidden; margin-top: 4px; }
.mini-fill { height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2)); border-radius: 3px; transition: width .4s; }


/* ── Heatmap (ORIGINAL STYLE) ── */
.hm-card { padding: 16px; }
.hm-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.hm-sub  { font-size: .7rem; color: var(--muted); font-weight: 600; }
.hm-grid { display: flex; gap: 5px; flex-wrap: wrap; }
.day-box { width: 34px; height: 34px; border-radius: 7px; background: rgba(255,255,255,.04); border: 1px solid var(--border); display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: transform .15s; }
.day-box:hover { transform: scale(1.1); }
.day-box .dd { font-size: 10px; font-weight: 700; color: var(--text); line-height: 1; }
.day-box .mm { font-size: 7px; text-transform: uppercase; color: var(--muted); font-weight: 600; }
.day-box.lvl1 { background: #064e3b; border-color: #059669; }
.day-box.lvl2 { background: #10b981; border-color: #10b981; }
.day-box.lvl3 { background: var(--accent); border-color: var(--accent); }
.day-box.lvl3 .dd, .day-box.lvl3 .mm { color: #062015; }

/* ── Quest Buttons (ORIGINAL STYLE) ── */
.btn-sm { padding: 5px 12px; border-radius: 8px; border: 1px solid var(--border2); background: rgba(255,255,255,.04); color: var(--text); font-family: var(--ff-body); font-size: .8rem; font-weight: 700; cursor: pointer; transition: all .15s; text-decoration: none; display: inline-flex; align-items: center; }
.btn-sm:hover { background: rgba(255,255,255,.08); border-color: var(--accent); color: var(--accent); }
.btn-sm.ghost { background: transparent; border-color: transparent; }
.btn-sm.ghost:hover { background: rgba(255,255,255,.05); border-color: var(--border); }

/* ── Rest of Original UI ── */
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 20px; }
.stat-card { background: linear-gradient(160deg, var(--card), var(--bg2)); border: 1px solid var(--border); border-radius: 14px; padding: 16px; display: flex; flex-direction: column; gap: 4px; }
.sv { font-family: var(--ff-head); font-size: 1.7rem; font-weight: 700; color: var(--text); line-height: 1; }
.sl { font-size: .72rem; color: var(--muted); font-weight: 600; text-transform: uppercase; letter-spacing: .4px; }
.section-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-shrink: 0; }
.sec-title { display: flex; align-items: center; gap: 8px; font-family: var(--ff-head); font-size: 1rem; font-weight: 700; color: var(--text); }
.count-pill { background: rgba(0,229,160,.1); border: 1px solid rgba(0,229,160,.22); color: var(--accent); border-radius: 20px; padding: 1px 8px; font-size: .72rem; font-weight: 800; }
.quests-list { display: flex; flex-direction: column; gap: 8px; }
.quest-row { display: flex; align-items: center; gap: 10px; padding: 11px 14px; background: rgba(255,255,255,.02); border: 1px solid var(--border); border-radius: 10px; }
.quest-row.done { opacity: .55; }

/* 1. The container */
.q-check-wrap {
  flex-shrink: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 20px;
  height: 20px;
  outline: none; /* Kill focus ring on the label */
  -webkit-tap-highlight-color: transparent; /* Kill mobile tap blue box */
}

/* 2. The hidden input */
.q-checkbox {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
  appearance: none;
  -webkit-appearance: none;
  margin: 0;
}

/* 3. The Custom Circle */
.q-mark {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: #062015;
  background: transparent;
  box-shadow: none !important; /* Ensure no glow */
  outline: none !important;    /* Ensure no focus ring */
}

/* 4. Ensure focus state on the hidden input doesn't show on the circle */
.q-checkbox:focus + .q-mark,
.q-checkbox:active + .q-mark {
  outline: none !important;
  box-shadow: none !important;
}

/* 5. The checked state */
.q-checkbox:checked + .q-mark {
  background: var(--accent);
  border-color: var(--accent);
}

.q-rarity { width: 3px; height: 32px; border-radius: 2px; }
.q-xp { background: rgba(0,229,160,.08); border: 1px solid rgba(0,229,160,.18); color: var(--accent); border-radius: 8px; padding: 2px 8px; font-size: .72rem; font-weight: 700; }
.table { width: 100%; border-collapse: collapse; }
.table th { text-align: left; font-size: .75rem; color: var(--muted); padding-bottom: 8px; }
.table td { padding: 10px 0; border-bottom: 1px solid rgba(255,255,255,0.05); font-size: .85rem; }
.status-done { color: var(--accent); font-weight: 700; }
.status-miss { color: var(--danger); font-weight: 700; }
.quick-actions { display: flex; gap: 12px; margin-top: 20px; flex-wrap: wrap; }
.qa-btn { flex: 1; min-width: 110px; display: flex; align-items: center; justify-content: center; gap: 8px; padding: 13px; border-radius: 12px; background: rgba(255,255,255,.03); border: 1px solid var(--border); color: var(--text); font-weight: 600; text-decoration: none; }
.qa-btn.primary-qa { background: linear-gradient(135deg, rgba(0,229,160,.14), rgba(59,130,246,.09)); border-color: rgba(0,229,160,.28); color: var(--accent); }
.xp-toast { position: fixed; top: 80px; right: 20px; background: linear-gradient(135deg, var(--accent), var(--accent-2)); color: #040d09; font-weight: 800; padding: 10px 20px; border-radius: 12px; z-index: 999; }
</style>