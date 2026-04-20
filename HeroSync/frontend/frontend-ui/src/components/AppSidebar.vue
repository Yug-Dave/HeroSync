<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { http } from '../api/http';

const props = defineProps({ isOpen: Boolean });
const emit  = defineEmits(['close']);
const route  = useRoute();
const router = useRouter();

const user = ref({ name: 'Hero', level: 1, xp: 0, streak: 0, xpProgress: 0, avatar: '' });

const tier = computed(() => {
  if (user.value.level >= 4) return { label: 'Dragon Slayer', cls: 'tier-legendary', color: '#f59e0b' };
  if (user.value.level >= 3) return { label: 'Warrior',       cls: 'tier-epic',      color: '#8b5cf6' };
  if (user.value.level >= 2) return { label: 'Guardian',      cls: 'tier-rare',      color: '#3b82f6' };
  return                             { label: 'Apprentice',    cls: 'tier-common',    color: '#00e5a0' };
});

const navItems = [
  { to: '/dashboard',       label: 'Dashboard',   icon: 'grid'    },
  { to: '/habits',          label: 'Quests',       icon: 'sword'   },
  { to: '/goals',           label: 'Missions',     icon: 'target'  },
  { to: '/achievements',    label: 'Badges',       icon: 'trophy'  },
  { to: '/reports/weekly',  label: 'Reports',      icon: 'chart'   },
  { to: '/profile',         label: 'Profile',      icon: 'user'    },
  { to: '/settings',        label: 'Settings',     icon: 'settings'},
];

function isActive(path) {
  if (path === '/dashboard') return route.path === '/dashboard';
  return route.path.startsWith(path);
}

function navigate(to) {
  router.push(to);
  emit('close');
}

import { onMounted, onUnmounted } from 'vue';
import { logout } from '../api/auth';
import { getAvatarImageUrl } from '../utils/avatar';

async function handleLogout() {
  try {
    await logout();
  } catch (e) {} // ignore errors on logout
  window.location.href = '/';
}

async function fetchDash() {
  try {
    const res = await fetch('/api/dashboard', { headers: { Accept: 'application/json' }, credentials: 'include' });
    if (res.ok) {
      const d = await res.json();
      user.value.name   = d.userName || 'Hero';
      user.value.avatar = d.avatar || '';
      user.value.streak = d.currentStreak || 0;
      user.value.xp     = d.totalXP || 0;
      user.value.level  = Math.max(1, Math.floor(user.value.xp / 2000) + 1);
      user.value.xpProgress = ((user.value.xp % 2000) / 2000) * 100;
    }
  } catch {}
}

onMounted(async () => {
  await fetchDash();
  window.addEventListener('refresh-dashboard', fetchDash);
  window.addEventListener('profile-updated', fetchDash);
});

onUnmounted(() => {
  window.removeEventListener('refresh-dashboard', fetchDash);
  window.removeEventListener('profile-updated', fetchDash);
});
</script>

<template>
  <!-- Overlay (mobile) -->
  <transition name="overlay-fade">
    <div v-if="isOpen" class="sidebar-overlay" @click="emit('close')" />
  </transition>

  <!-- Sidebar -->
  <aside class="sidebar" :class="{ 'sidebar-open': isOpen }">
    <!-- Brand -->
    <div class="sidebar-brand">
      <div class="brand-logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width:65%;height:65%;">
          <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
        </svg>
      </div>
      <span class="brand-name"><span class="highlight">Hero</span>Sync</span>
    </div>

    <!-- Nav -->
    <nav class="sidebar-nav">
      <button
        v-for="item in navItems" :key="item.to"
        class="nav-item"
        :class="{ active: isActive(item.to) }"
        @click="navigate(item.to)"
      >
        <!-- Icons -->
        <svg v-if="item.icon === 'grid'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/></svg>
        <svg v-if="item.icon === 'sword'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/><path d="M2 2l20 20"/></svg>
        <svg v-if="item.icon === 'target'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
        <svg v-if="item.icon === 'trophy'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4a2 2 0 0 1-2-2V5h4"/><path d="M18 9h2a2 2 0 0 0 2-2V5h-4"/><path d="M12 17v4"/><path d="M8 21h8"/><path d="M6 5h12v8a6 6 0 0 1-12 0V5z"/></svg>
        <svg v-if="item.icon === 'chart'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/><line x1="2" y1="20" x2="22" y2="20"/></svg>
        <svg v-if="item.icon === 'user'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        <svg v-if="item.icon === 'settings'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>

        <span class="nav-label">{{ item.label }}</span>
        <span v-if="isActive(item.to)" class="nav-active-bar" />
      </button>

      <!-- Logout Button -->
      <button class="nav-item logout-btn" @click="handleLogout" style="margin-top: auto;">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        <span class="nav-label">Logout</span>
      </button>
    </nav>

    <!-- Character Mini Card -->
    <div class="sidebar-footer">
      <div class="char-mini" :class="tier.cls">
        <div class="char-aura" :style="{ background: `radial-gradient(circle, ${tier.color}22 0%, transparent 70%)` }" />
        <div class="char-avatar" :style="{ borderColor: tier.color, boxShadow: `0 0 16px ${tier.color}55` }">
          <img v-if="user.avatar" :src="getAvatarImageUrl(user.avatar)" class="mini-avatar-img" alt="Avatar" loading="lazy" />
          <span v-else>{{ user.name.charAt(0).toUpperCase() }}</span>
        </div>
        <div class="char-info">
          <div class="char-name">{{ user.name }}</div>
          <div class="char-rank" :style="{ color: tier.color }">{{ tier.label }}</div>
          <div class="char-xp-wrap">
            <div class="char-xp-bar">
              <div class="char-xp-fill" :style="{ width: user.xpProgress + '%', background: tier.color }" />
            </div>
            <span class="char-level" :style="{ color: tier.color }">Lv {{ user.level }}</span>
          </div>
        </div>
      </div>

      <div class="sidebar-chips">
        <span class="chip streak-chip" v-if="user.streak > 0">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm-1 14.5v-5l-3 3 1.5-7L13 10l-1.5-3 5 4.5-4 1 1.5 4H11z"/></svg>
          {{ user.streak }}d streak
        </span>
        <span class="chip xp-chip">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
          {{ user.xp.toLocaleString() }} XP
        </span>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.6);
  backdrop-filter: blur(4px); z-index: 199;
}
.overlay-fade-enter-active, .overlay-fade-leave-active { transition: opacity 0.25s; }
.overlay-fade-enter-from, .overlay-fade-leave-to { opacity: 0; }

/* ── Sidebar ── */
.sidebar {
  width: var(--sidebar-w);
  min-height: 100dvh;
  background: linear-gradient(180deg, var(--card) 0%, var(--bg2) 100%);
  border-right: 1px solid var(--border);
  display: flex; flex-direction: column;
  position: sticky; top: 0; height: 100dvh;
  flex-shrink: 0; overflow: hidden;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ── Brand ── */
.sidebar-brand {
  display: flex; align-items: center; gap: 10px;
  padding: 20px 18px 18px;
  border-bottom: 1px solid var(--border);
}
.brand-logo {
  width: 34px; height: 34px; border-radius: 9px; flex-shrink: 0;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #040d09; display: grid; place-items: center;
  font-family: var(--ff-display); font-size: 9px; font-weight: 900;
  letter-spacing: 0.5px; box-shadow: 0 0 16px rgba(0,229,160,0.3);
}
.brand-name {
  font-family: var(--ff-display);
  font-weight: 800;
  font-size: 1.1rem;
  letter-spacing: 1px;
  color: var(--text);
}
.brand-name .highlight { color: var(--accent); }

/* ── Nav ── */
.sidebar-nav {
  flex: 1; padding: 14px 10px; display: flex; flex-direction: column; gap: 2px;
  overflow-y: auto; overflow-x: hidden;
}
.nav-item {
  position: relative; display: flex; align-items: center; gap: 12px;
  padding: 10px 12px; border-radius: 10px; border: none;
  background: transparent; color: var(--muted);
  font-family: var(--ff-body); font-size: 0.88rem; font-weight: 500;
  cursor: pointer; text-align: left; transition: all 0.18s; width: 100%;
}
.nav-item:hover { background: rgba(255,255,255,0.05); color: var(--text); }
.nav-item.logout-btn:hover { background: rgba(239,68,68,0.15); color: #ef4444; }
.nav-item.active {
  background: rgba(0,229,160,0.08);
  color: var(--accent);
  font-weight: 600;
  border: 1px solid rgba(0,229,160,0.15);
}
.nav-icon { width: 18px; height: 18px; flex-shrink: 0; }
.nav-label { flex: 1; }
.nav-active-bar {
  position: absolute; right: -1px; top: 50%; transform: translateY(-50%);
  width: 3px; height: 60%; background: var(--accent);
  border-radius: 2px 0 0 2px;
  box-shadow: 0 0 8px rgba(0,229,160,0.6);
}

/* ── Footer ── */
.sidebar-footer {
  padding: 14px 12px 18px; border-top: 1px solid var(--border);
  display: flex; flex-direction: column; gap: 10px;
}
.char-mini {
  position: relative; display: flex; align-items: center; gap: 10px;
  padding: 12px; border-radius: 12px;
  background: rgba(255,255,255,0.03); border: 1px solid var(--border);
  overflow: hidden;
}
.char-aura {
  position: absolute; inset: 0; pointer-events: none;
}
.char-avatar {
  width: 36px; height: 36px; border-radius: 50%; flex-shrink: 0;
  border: 2px solid; display: grid; place-items: center;
  font-family: var(--ff-head); font-size: 1rem; font-weight: 700; color: #fff;
  background: rgba(255,255,255,0.06); position: relative; z-index: 1;
}
.mini-avatar-img { width: 100%; height: 100%; object-fit: contain; }
.char-info { flex: 1; min-width: 0; position: relative; z-index: 1; }
.char-name { font-size: 0.82rem; font-weight: 700; color: var(--text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.char-rank { font-family: var(--ff-head); font-size: 0.72rem; font-weight: 700; letter-spacing: 0.4px; }
.char-xp-wrap { display: flex; align-items: center; gap: 6px; margin-top: 5px; }
.char-xp-bar { flex: 1; height: 4px; background: rgba(255,255,255,0.07); border-radius: 2px; overflow: hidden; }
.char-xp-fill { height: 100%; border-radius: 2px; transition: width 0.5s ease; }
.char-level { font-family: var(--ff-head); font-size: 0.68rem; font-weight: 700; white-space: nowrap; }

.sidebar-chips { display: flex; gap: 6px; flex-wrap: wrap; }
.chip {
  display: inline-flex; align-items: center; gap: 5px;
  font-size: 0.72rem; font-weight: 600; padding: 4px 10px; border-radius: 999px;
  font-family: var(--ff-body);
}
.streak-chip { background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.25); color: #fca5a5; }
.xp-chip { background: rgba(0,229,160,0.08); border: 1px solid rgba(0,229,160,0.22); color: var(--accent); }

/* ── Mobile: hidden by default, slide in ── */
@media (max-width: 1023px) {
  .sidebar {
    position: fixed; top: 0; left: 0; z-index: 200;
    transform: translateX(-100%);
    box-shadow: var(--shadow-lg);
  }
  .sidebar.sidebar-open { transform: translateX(0); }
}
</style>
