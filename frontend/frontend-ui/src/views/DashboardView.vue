<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { listGoals, createGoal } from '../api/goals';
import GoalsAddEditModal from '../components/GoalsAddEditModal.vue';
import AddHabitModal from '../components/AddHabitModal.vue';
import DashboardAchievementsPanel from '../components/DashboardAchivementsPanel.vue';
import CharacterCard from '../components/CharacterCard.vue';
import { playSound } from '../utils/config';
import { useUserStore } from '../stores/user';

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

const userStore = useUserStore();
const companionChoice = computed(() => userStore.companionChoice);
const battlePlan = ref('');

const companionMap = {
  SYNC: { image: '/companion_omega.png', color: '#3b82f6', name: 'SYNC' },
  AURA: { image: '/companion_kaelen.png', color: '#8b5cf6', name: 'AURA' },
  VOLT: { image: '/companion_rex.png', color: '#f97316', name: 'VOLT' }
};

const currentDashboardAvatar = computed(() => companionMap[companionChoice.value]?.image || '/companion_omega.png');
const companionColor = computed(() => companionMap[companionChoice.value]?.color || '#3b82f6');
const companionName = computed(() => companionMap[companionChoice.value]?.name || 'SYNC');

const streakWeather = computed(() => {
  const s = userStore.streak;
  if (s >= 14) return { icon: '🔥', label: 'LEGENDARY',  color: '#f97316', desc: `${s} day inferno` };
  if (s >= 7)  return { icon: '⚡', label: 'BLAZING',    color: '#eab308', desc: `${s} days strong` };
  if (s >= 3)  return { icon: '🌤', label: 'WARMING UP', color: '#3b82f6', desc: `${s} day streak` };
  if (s >= 1)  return { icon: '❄️', label: 'COLD START', color: '#94a3b8', desc: `${s} day — push harder` };
  return              { icon: '💀', label: 'FROZEN',     color: '#64748b', desc: 'No streak — start today' };
});

// ── Computed ───────────────────────────────────────────
const completedCount = computed(() => habits.value.filter(h => h.completed).length);
const totalHabits    = computed(() => habits.value.length);

// ── API helpers ────────────────────────────────────────
const API_BASE = '/api';
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
    const userRes = await fetch(`${API_BASE}/dashboard`, {
      headers: { 'Accept': 'application/json' }, credentials: 'include'
    });
    if (userRes.ok) {
      const data = await userRes.json();
      user.value.name   = data.userName;
      user.value.id     = data.dashboardId;
      user.value.avatar = data.avatar || '';
      user.value.xp     = data.totalXP || 0;
      user.value.streak = data.currentStreak || 0;
      
      // Sync store
      userStore.updateStats(data.totalXP, data.currentStreak);
    }
  } catch (e) { console.warn('Auth check failed'); }
  await fetchHabits();
  await fetchGoals();
  await fetchActivityStats();
}

async function fetchBattlePlan(force = false) {
  const today = new Date().toDateString();
  const cached = sessionStorage.getItem('battlePlan');
  const cachedDate = sessionStorage.getItem('battlePlanDate');
  const cachedBuddy = sessionStorage.getItem('battlePlanBuddy');

  // If same day + same buddy + not forced, use cache
  if (!force && cached && cachedDate === today && cachedBuddy === companionChoice.value) {
    battlePlan.value = cached;
    return;
  }

  battlePlan.value = ''; // Trigger loading state
  try {
    const res = await http.post('/ai/chat', { message: '', mode: 'greeting' });
    battlePlan.value = res.data.reply;
    sessionStorage.setItem('battlePlan', res.data.reply);
    sessionStorage.setItem('battlePlanDate', today);
    sessionStorage.setItem('battlePlanBuddy', companionChoice.value);
  } catch (e) {
    battlePlan.value = 'Stay consistent. Every quest counts.';
  }
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
    const res = await fetch(`${API_BASE}/activities`, {
      method: 'POST', headers: getHeaders(), credentials: 'include',
      body: JSON.stringify({
        habitId: Number(habit.id), date: today, habitName: habit.name,
        completions: habit.completed ? 1 : 0, xpValue: habit.xpValue
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

import { watch } from 'vue';
watch(companionChoice, () => {
  fetchBattlePlan(true); // Force refresh on switch
});

const formatDate = (d) => ({
  day: new Date(d).getDate().toString().padStart(2, '0'),
  mon: new Date(d).toLocaleString('default', { month: 'short' })
});
const formatSimpleDate = (d) => d.substring(5);

onMounted(async () => {
  fetchInitialData();
  window.addEventListener('refresh-dashboard', fetchInitialData);
  fetchBattlePlan();
});
</script>

<template>
  <div class="dashboard-wrapper">
    <main class="main">
      <section class="section">
        <div class="container">
          
          <!-- Top Hero & Stats Section -->
          <div class="dashboard-top-section">
            <div class="char-column">
              <CharacterCard 
                :name="user.name"
                :level="level"
                :xp="xp"
                :streak="user.streak"
                :avatarSeed="user.avatar"
                size="lg"
              >
                <div class="hero-stats">
                  <div class="hstat">
                    <div class="hval">{{ completedCount }}/{{ totalHabits }}</div>
                    <div class="hlbl">Quests Done</div>
                  </div>
                </div>
              </CharacterCard>

              <!-- Companion Motivation Card -->
              <div class="motivation-card" :style="{ '--comp-color': companionColor }">
                <div class="motivation-left">
                  <div class="dash-avatar-frame" :style="{ borderColor: companionColor }">
                    <img :src="currentDashboardAvatar" class="dash-avatar" />
                  </div>
                  <div class="comp-status">
                    <span class="comp-dot"></span>
                    System Online
                  </div>
                </div>
                <div class="motivation-right">
                  <div class="motivation-header">
                    <span class="comp-name-label" :style="{ color: companionColor }">
                      {{ companionName }}
                    </span>
                    <span class="motivation-tag">Daily Briefing</span>
                  </div>
                  <p class="motivation-text" v-if="battlePlan">{{ battlePlan }}</p>
                  <p class="motivation-text placeholder" v-else>Initializing neural link...</p>
                  
                  <div class="streak-weather">
                    <span class="weather-icon">{{ streakWeather.icon }}</span>
                    <span class="weather-label" :style="{ color: streakWeather.color }">
                      {{ streakWeather.label }}
                    </span>
                </div>
              </div>
            </div>
            </div>
            <div class="stats-column">
              <div class="stat-grid-mini">
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
            </div>
          </div>

          <!-- Bottom Main Grid -->
          <div class="grid">
            <div class="card span-4">
              <div class="card-inner">
                <div class="section-head">
                  <div class="sec-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/></svg>
                    Active Quests <span class="count-pill">{{ completedCount }}/{{ totalHabits }}</span>
                  </div>
                  <div class="sec-actions">
                    <button class="btn-sm" aria-label="Add New Quest" @click="openCreateModal">
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                      Quest
                    </button>
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
              <div class="card-inner no-scroll">
                <div class="section-head mb-16">
                  <div class="sec-title">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4a2 2 0 01-2-2V5h4"/><path d="M18 9h2a2 2 0 002-2V5h-4"/><path d="M6 5h12v8a6 6 0 01-12 0V5z"/></svg>
                    Achievements
                  </div>
                  <router-link to="/achievements" class="btn-sm ghost">See All</router-link>
                </div>
                <div class="achievements-full-wrap">
                  <DashboardAchievementsPanel ref="achievementsRef" />
                </div>
              </div>
            </div>
          </div>

          <div class="quick-actions">
            <button class="qa-btn primary-qa" aria-label="Create New Quest" @click="openCreateModal">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              New Quest
            </button>
            <button class="qa-btn" aria-label="Create New Goal" @click="openCreateGoalModal">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="2"/></svg>
              New Goal
            </button>
            <router-link to="/achievements" class="qa-btn" aria-label="View Achievements and Badges">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4a2 2 0 01-2-2V5h4"/><path d="M6 5h12v8a6 6 0 01-12 0V5z"/></svg>
              Badges
            </router-link>
            <router-link to="/reports/weekly" class="qa-btn" aria-label="View Weekly Progress Report">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.21 15.89A10 10 0 118 2.83M22 12A10 10 0 0012 2v10z"/></svg>
              Report
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
/* ── Layout ── */
.dashboard-wrapper { width: 100%; }
.container { width: min(1200px, 95vw); margin: 0 auto; padding: 20px 0; }

.dashboard-top-section {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  align-items: stretch;
}
.char-column {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.stats-column {
  width: 520px;
  flex-shrink: 0;
}

/* ── Card Heights & Scrolling ── */
.grid { display: grid; grid-template-columns: repeat(12, 1fr); gap: 20px; }
.grid .card { 
  height: 480px; 
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
.card-inner.no-scroll { overflow: visible; }

.scroll-area { 
  flex: 1; 
  overflow-y: auto; 
  min-height: 0; 
  padding-right: 4px;
}
.scroll-area::-webkit-scrollbar { width: 5px; }
.scroll-area::-webkit-scrollbar-thumb { background: var(--border2); border-radius: 3px; }

.achievements-full-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: visible;
}

/* ── Stat Cards ── */
.stat-grid-mini {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 16px;
  height: 100%;
}
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

/* ── Heatmap ── */
.hm-card { padding: 16px; }
.hm-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.hm-sub  { font-size: .7rem; color: var(--muted); font-weight: 600; }
.hm-grid { display: flex; gap: 5px; flex-wrap: wrap; flex: 1; align-items: center; justify-content: center; }
.day-box { width: 34px; height: 34px; border-radius: 7px; background: rgba(255,255,255,.04); border: 1px solid var(--border); display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: transform .15s; }
.day-box:hover { transform: scale(1.1); }
.day-box .dd { font-size: 10px; font-weight: 700; color: var(--text); line-height: 1; }
.day-box .mm { font-size: 7px; text-transform: uppercase; color: var(--muted); font-weight: 600; }
.day-box.lvl1 { background: var(--hm-lvl1); border-color: var(--hm-lvl1-border); }
.day-box.lvl2 { background: var(--hm-lvl2); border-color: var(--hm-lvl2-border); }
.day-box.lvl3 { background: var(--hm-lvl3); border-color: var(--hm-lvl3-border); }

/* ── Companion Motivation Card ── */
.motivation-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  animation: fadeUp 0.6s ease-out;
}
.motivation-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at left center, var(--comp-color, #3b82f6) 0%, transparent 60%);
  opacity: 0.04;
  pointer-events: none;
}
.motivation-card:hover { border-color: var(--comp-color, var(--border2)); }

.motivation-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}
.dash-avatar-frame {
  width: 88px;
  height: 88px;
  border-radius: 14px;
  border: 2px solid;
  overflow: hidden;
  background: #000;
}
.dash-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center top;
  transform: scale(1.4);
}
.comp-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.68rem;
  color: var(--muted);
  font-family: var(--ff-head);
}
.comp-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: #22c55e;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.motivation-right { flex: 1; min-width: 0; }
.motivation-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.comp-name-label {
  font-family: var(--ff-head);
  font-size: 1rem;
  font-weight: 900;
  letter-spacing: 2px;
}
.motivation-tag {
  font-size: 0.65rem;
  padding: 2px 8px;
  border-radius: 20px;
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--border);
  color: var(--muted);
  font-family: var(--ff-head);
  letter-spacing: 1px;
}
.motivation-text {
  font-size: 0.85rem;
  line-height: 1.6;
  color: var(--text);
  margin: 0 0 12px;
}
.motivation-text.placeholder { color: var(--muted); font-style: italic; }

.streak-weather {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 7px 12px;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border);
  border-radius: 8px;
  width: fit-content;
}
.weather-icon { font-size: 1rem; }
.weather-label {
  font-family: var(--ff-head);
  font-size: 0.72rem;
  font-weight: 800;
  letter-spacing: 1.5px;
}
.weather-desc {
  font-size: 0.7rem;
  color: var(--muted);
}

/* ── AI Companion ── */
.ai-companion-banner.mini {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}
.ai-companion-banner.mini .mascot-svg-wrap {
  width: 70px; height: 70px; flex-shrink: 0;
}
.ai-companion-banner.mini .ai-message h3 {
  margin: 0 0 4px; font-family: var(--ff-head); font-size: 1rem; color: var(--accent); font-weight: 800;
}
.ai-companion-banner.mini .ai-message p {
  margin: 0; color: var(--text-dim); font-size: 0.8rem; line-height: 1.4;
}

/* ── Button Styles ── */
.btn-sm { 
  padding: 8px 14px; 
  border-radius: 10px; 
  border: 1px solid rgba(255,255,255,0.08); 
  background: rgba(255,255,255,0.04); 
  color: var(--text); 
  font-family: var(--ff-body); 
  font-size: 0.75rem; 
  font-weight: 700; 
  cursor: pointer; 
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); 
  display: inline-flex; 
  align-items: center;
  gap: 6px;
}
.btn-sm:hover { 
  background: rgba(255,255,255,0.08); 
  border-color: var(--accent); 
  color: var(--accent);
  transform: translateY(-1px);
}
.btn-sm.ghost { background: transparent; border-color: transparent; opacity: 0.7; }
.btn-sm.ghost:hover { background: rgba(255,255,255,0.05); border-color: rgba(255,255,255,0.1); opacity: 1; }

.deploy-btn {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #000; border: none; padding: 10px 24px; border-radius: 12px;
  font-family: var(--ff-head); font-weight: 800; cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 4px 15px rgba(0, 229, 160, 0.3);
  font-size: 0.9rem;
  flex: 0 0 auto;
}
.deploy-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(0, 229, 160, 0.4); }

.quick-actions { 
  display: flex; 
  gap: 16px; 
  margin-top: 24px; 
  flex-wrap: wrap; 
}
.qa-btn { 
  flex: 1; 
  min-width: 120px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  gap: 10px; 
  padding: 15px; 
  border-radius: 14px; 
  background: rgba(255,255,255,0.03); 
  border: 1px solid rgba(255,255,255,0.06); 
  color: var(--text-dim); 
  font-weight: 700; 
  font-size: 0.9rem;
  text-decoration: none; 
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.qa-btn:hover {
  background: rgba(255,255,255,0.06);
  border-color: rgba(255,255,255,0.15);
  color: var(--text);
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}
.qa-btn.primary-qa { 
  background: rgba(0, 229, 160, 0.05); 
  border-color: rgba(0, 229, 160, 0.15); 
  color: var(--accent); 
}
.qa-btn.primary-qa:hover {
  background: rgba(0, 229, 160, 0.1);
  border-color: var(--accent);
  box-shadow: 0 10px 20px rgba(0, 229, 160, 0.1);
}

/* ── Tables & Lists ── */
.table { width: 100%; border-collapse: collapse; }
.table th { text-align: left; font-size: .75rem; color: var(--muted); padding-bottom: 8px; }
.table td { padding: 10px 0; border-bottom: 1px solid rgba(255,255,255,0.05); font-size: .85rem; }
.status-done { color: var(--accent); font-weight: 700; }
.status-miss { color: var(--danger); font-weight: 700; }
.xp-mini { background: rgba(0,229,160,.08); border: 1px solid rgba(0,229,160,.18); color: var(--accent); border-radius: 6px; padding: 2px 6px; font-size: .68rem; font-weight: 700; }

.section-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-shrink: 0; }
.sec-title { display: flex; align-items: center; gap: 8px; font-family: var(--ff-head); font-size: 1rem; font-weight: 700; color: var(--text); }
.count-pill { background: rgba(0,229,160,.1); border: 1px solid rgba(0,229,160,.22); color: var(--accent); border-radius: 20px; padding: 1px 8px; font-size: .72rem; font-weight: 800; }
.quests-list { display: flex; flex-direction: column; gap: 8px; }
.quest-row { display: flex; align-items: center; gap: 10px; padding: 11px 14px; background: rgba(255,255,255,.02); border: 1px solid var(--border); border-radius: 10px; }
.quest-row.done { opacity: .55; }

.q-check-wrap { flex-shrink: 0; cursor: pointer; display: flex; align-items: center; justify-content: center; position: relative; width: 20px; height: 20px; }
.q-checkbox { position: absolute; opacity: 0; width: 0; height: 0; }
.q-mark { width: 20px; height: 20px; border-radius: 50%; border: 2px solid rgba(255, 255, 255, 0.15); display: flex; align-items: center; justify-content: center; transition: all 0.2s ease; }
.q-checkbox:checked + .q-mark { background: var(--hm-lvl3); border-color: var(--hm-lvl3-border); }
.q-rarity { width: 3px; height: 32px; border-radius: 2px; }
.q-xp { background: rgba(0,229,160,.08); border: 1px solid rgba(0,229,160,.18); color: var(--accent); border-radius: 8px; padding: 2px 8px; font-size: .72rem; font-weight: 700; }

.mini-bar { height: 5px; background: rgba(255,255,255,.07); border-radius: 3px; overflow: hidden; margin-top: 4px; }
.mini-fill { height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2)); border-radius: 3px; transition: width .4s; }

/* ── Responsive ── */
@media (max-width: 1100px) {
  .dashboard-top-section { flex-direction: column; }
  .stats-column { width: 100%; }
}

@media (max-width: 1024px) { 
  .grid .span-4 { grid-column: span 12 !important; } 
} 

@media (max-width: 768px) {
  .dashboard-top-section { gap: 16px; }
  
  .ai-companion-banner.mini {
    flex-direction: row !important;
    text-align: left !important;
    padding: 16px !important;
    gap: 16px !important;
    flex-wrap: wrap;
    align-items: center;
    border-radius: 20px !important;
  }
  .ai-companion-banner.mini .mascot-svg-wrap { 
    width: 64px !important; 
    height: 64px !important; 
    flex-shrink: 0;
    background: var(--bg);
    border: 1px solid var(--border);
    border-radius: 50%;
    padding: 8px;
  }
  .ai-companion-banner.mini .ai-message {
    flex: 1;
    min-width: 140px;
  }
  .ai-companion-banner.mini .ai-message h3 { 
    font-size: 0.95rem !important; 
    color: var(--accent) !important;
    margin-bottom: 2px !important;
  }
  .ai-companion-banner.mini .ai-message p { 
    font-size: 0.75rem !important; 
    line-height: 1.3 !important;
    color: var(--text-dim) !important;
  }
  .deploy-btn { 
    width: 100% !important; 
    margin-top: 4px !important;
    padding: 10px !important;
    font-size: 0.8rem !important;
    border-radius: 12px !important;
    background: rgba(0, 229, 160, 0.05) !important;
    border: 1px solid rgba(0, 229, 160, 0.15) !important;
    color: var(--accent) !important;
    box-shadow: none !important;
    height: auto !important;
  }

  /* Horizontal Scrolling Stats on Mobile */
  .stat-grid-mini {
    display: flex !important;
    flex-direction: row !important;
    overflow-x: auto;
    gap: 16px;
    padding-bottom: 12px;
    scroll-snap-type: x mandatory;
    scrollbar-width: none;
  }
  .stat-grid-mini::-webkit-scrollbar { display: none; }
  .stat-grid-mini .stat-card {
    flex: 0 0 240px;
    scroll-snap-align: center;
    height: auto;
  }

  .grid .card { 
    height: auto !important; 
    min-height: 420px !important;
    border-radius: 24px;
  }
  .scroll-area { max-height: 400px; }

  .quick-actions { 
    display: grid; 
    grid-template-columns: 1fr 1fr; 
    gap: 12px; 
    margin-top: 10px;
  }
  .qa-btn { padding: 14px; font-size: 0.85rem; }
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes floatAvatar {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}
</style>