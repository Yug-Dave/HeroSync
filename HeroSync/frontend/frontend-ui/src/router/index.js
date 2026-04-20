import { createRouter, createWebHistory } from "vue-router";
import { me } from "../api/auth";

import LandingView from "../views/LandingView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "landing",
      component: LandingView
    },

    { path: "/login", name: "login", component: () => import("../views/LoginView.vue") },
    { path: "/signup", name: "signup", component: () => import("../views/SignupView.vue") },
    { path: "/verify-email", name: "verify-email", component: () => import("../views/VerifyEmailView.vue") },

    {
      path: "/dashboard",
      name: "dashboard",
      component: () => import("../views/DashboardView.vue"),
      meta: { requiresAuth: true }
    },

    {
      path: "/profile",
      name: "profile",
      component: () => import("../views/ProfileView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/settings",
      name: "settings",
      component: () => import("../views/SettingsView.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/habits",
      name: "habits",
      component: () => import("../views/HabitsView.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/goals",
      name: "goals",
      component: () => import("../views/GoalsView.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/achievements",
      name: "achievements",
      component: () => import("../views/AchievementsView.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/reports/weekly",
      name: "reports-weekly",
      component: () => import("../views/ReportsWeeklyView.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/reports/monthly",
      name: "reports-monthly",
      component: () => import("../views/ReportsMonthlyView.vue"),
      meta: { requiresAuth: true },
    },

  ],
});

router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.matched.some((r) => r.meta.requiresAuth);
  if (!requiresAuth) return next();

  try {
    await me();
    next();
  } catch (e) {
    next({ name: "landing" });
  }
});

export default router;
