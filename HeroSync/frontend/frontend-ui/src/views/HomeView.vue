<template>
  <main class="main">
    <section class="section">
      <div class="container">
        <div v-if="error" class="card span-12" style="margin-bottom: 16px;">
          <div class="card-inner">
          <div class="meta mb-12"><span class="badge">Error</span></div>
          <p class="meta">{{ error }}</p>
          </div>
        </div>
   
        <div class="overview-row">

          <!-- Quick actions card -->
          <div class="overview-card">
            <h3>Quick Actions</h3>
            <RouterLink to="/reports" class="action-btn">View Reports</RouterLink>
            <RouterLink to="/achievements" class="action-btn">View Achievements</RouterLink>
            <RouterLink to="/achievements/unlocked" class="action-btn">Unlocked</RouterLink>
          </div>

          <!-- Weekly report overview -->
          <div class="overview-card">
            <div class="meta">Weekly Report</div>
            <h2 class="h2">
              <span v-if="loading">Loading...</span>
              <span v-else>{{ weeklyTotal }}</span>
            </h2>
            <div class="meta">Total completions (this week)</div>
            <div class="progress"><span style="--value:0%;"></span></div>
          </div>

          <!-- Monthly report overview -->
          <div class="overview-card">
            <div class="meta">Monthly Report</div>
            <h2 class="h2">
              <span v-if="loading">Loading...</span>
              <span v-else>{{ monthlyTotal }}</span>
            </h2>
            <div class="meta">Total completions (this month)</div>
            <div class="progress"><span style="--value:0%;"></span></div>
          </div>

          <!-- Achievements overview -->
          <div class="overview-card">
            <div class="meta">Achievements</div>
            <h2 class="h2">
              <span v-if="loading">Loading...</span>
              <span v-else>{{ unlockedCount }}</span>
            </h2>
            <div class="meta">Unlocked achievements</div>
            <div class="progress"><span style="--value:0%;"></span></div>
          </div>

        </div>

        <!-- You can keep a “card grid” section here later for report charts / lists -->
        <div class="grid" style="margin-top: 20px;">
          <div class="card span-12">
            <div class="card-inner">
              <div class="meta mb-12"><span class="badge">Next Step</span></div>
              <p class="meta">
                We will connect these cards to your backend:
                <strong>/reports/weekly</strong>, <strong>/reports/monthly</strong>,
                <strong>/achievements</strong>, <strong>/achievements/unlocked</strong>.
              </p>
            </div>
          </div>
        </div>

      </div>
    </section>
  </main>
</template>

<script setup>
  import { RouterLink } from "vue-router";
  import { onMounted, ref } from "vue";

  import { getWeeklyReport, getMonthlyReport } from "../api/reports";
  import { getUnlockedAchievements } from "../api/achievements";

  const loading = ref(true);
  const error = ref("");

  const weeklyTotal = ref(null);
  const monthlyTotal = ref(null);
  const unlockedCount = ref(null);

  // YYYY-MM-DD
  function formatDate(date) {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  }

  onMounted(async () => {
    try {
      loading.value = true;
      error.value = "";

      const today = new Date();

      // weekly report expects ?date=YYYY-MM-DD
      const weekly = await getWeeklyReport(formatDate(today));
      weeklyTotal.value = weekly.totalCompletions;

      // monthly expects ?year=YYYY&month=M
      const month = today.getMonth() + 1;
      const year = today.getFullYear();
      const monthly = await getMonthlyReport(year, month);
      monthlyTotal.value = monthly.totalCompletions;

      const unlocked = await getUnlockedAchievements();
      unlockedCount.value = unlocked.length;
    } catch (e) {
      console.error(e);
      error.value =
        "Failed to load data from backend. Check backend is running and CORS is enabled.";
    } finally {
      loading.value = false;
    }
  });
</script>
