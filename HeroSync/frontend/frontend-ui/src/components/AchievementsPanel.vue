<template>
  <div class="wrap">
    <div class="toolbar">
      <div class="left">
        <h2 class="heading">Overview</h2>
        <p class="sub">Evaluate to refresh unlocked achievements.</p>
      </div>

      <div class="actions">
        <button class="btn ghost" @click="loadAll" :disabled="loading">Reload</button>
        <button class="btn primary" @click="runEvaluation" :disabled="loading">
          {{ loading ? "Working..." : "Evaluate" }}
        </button>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="loading && achievements.length === 0" class="skeletonGrid">
      <div class="skeletonCard" v-for="n in 4" :key="n"></div>
    </div>

    <div v-else class="grid">
      <AchievementCard
        v-for="a in achievements"
        :key="a.id"
        :achievement="a"
        :unlockedAt="unlockedMap[a.id] || ''"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import AchievementCard from "./AchievementsCard.vue";
import { getAllAchievements, getUnlockedAchievements, evaluateAchievements } from "../api/achievements";

const achievements = ref([]);
const unlockedMap = ref({});
const loading = ref(false);
const error = ref("");

async function loadAll() {
  loading.value = true;
  error.value = "";
  try {
    const [all, unlocked] = await Promise.all([getAllAchievements(), getUnlockedAchievements()]);
    achievements.value = all;

    const map = {};
    for (const ua of unlocked) map[ua.achievement.id] = ua.unlockedAt;
    unlockedMap.value = map;
  } catch (e) {
    console.error(e);
    error.value = "Failed to load achievements. Check backend/CORS/session.";
  } finally {
    loading.value = false;
  }
}

async function runEvaluation() {
  loading.value = true;
  error.value = "";
  try {
    await evaluateAchievements();
    await loadAll();
  } catch (e) {
    console.error(e);
    error.value = "Evaluation failed. Check backend logs.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadAll);
</script>

<style scoped>
.wrap{ display:grid; gap:16px; }

.toolbar{
  display:flex;
  align-items:flex-end;
  justify-content:space-between;
  gap:14px;
  flex-wrap:wrap;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(17,24,39,0.35);
  border: 1px solid rgba(255,255,255,0.08);
}
.left{ display:grid; gap:6px; }
.heading{ margin:0; color:#fff; font-size:1.05rem; }
.sub{ margin:0; color: rgba(226,232,240,0.75); font-size:0.95rem; }

.actions{ display:flex; gap:10px; }

.btn{
  height: 40px;
  padding: 0 14px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.10);
  background: rgba(15,23,42,0.35);
  color: rgba(226,232,240,0.95);
  font-weight: 700;
  cursor:pointer;
}
.btn:hover{ filter: brightness(1.06); }
.btn:disabled{ opacity:.6; cursor:not-allowed; }

.btn.primary{
  border-color: rgba(52,211,153,0.30);
  background: rgba(52,211,153,0.14);
  color: #a7f3d0;
}
.btn.ghost{
  background: rgba(15,23,42,0.25);
}

.error{
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(239,68,68,0.08);
  border: 1px solid rgba(239,68,68,0.20);
  color: #fecaca;
}

.grid{
  display:grid;
  gap: 14px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}
@media (max-width: 900px){
  .grid{ grid-template-columns: 1fr; }
}

/* skeleton */
.skeletonGrid{
  display:grid;
  gap:14px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}
@media (max-width: 900px){
  .skeletonGrid{ grid-template-columns: 1fr; }
}
.skeletonCard{
  height: 140px;
  border-radius: 18px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.08);
  position: relative;
  overflow:hidden;
}
.skeletonCard::after{
  content:"";
  position:absolute;
  inset:0;
  transform: translateX(-100%);
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.06), transparent);
  animation: shimmer 1.2s infinite;
}
@keyframes shimmer{ 100%{ transform: translateX(100%);} }
</style>
