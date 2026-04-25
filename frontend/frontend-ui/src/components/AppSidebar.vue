<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { logout } from '../api/auth';
import { getAvatarImageUrl } from '../utils/avatar';
import { heroConfig } from '../utils/config';
import { useUserStore } from '../stores/user';
import AppLogo from './AppLogo.vue';

const props = defineProps({ isOpen: Boolean });
const emit  = defineEmits(['close']);
const route  = useRoute();
const router = useRouter();
const userStore = useUserStore();

const tier = computed(() => {
  const level = userStore.level;
  if (level >= 4) return { label: 'Dragon Slayer', cls: 'tier-legendary', color: 'var(--accent)' };
  if (level >= 3) return { label: 'Warrior',       cls: 'tier-epic',      color: 'var(--accent-2)' };
  if (level >= 2) return { label: 'Guardian',      cls: 'tier-rare',      color: 'var(--accent-2)' };
  return                             { label: 'Apprentice',    cls: 'tier-common',    color: 'var(--accent)' };
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

function toggleTheme() {
  heroConfig.theme = heroConfig.theme === 'light' ? 'dark' : 'light';
}

async function handleLogout() {
  try { await logout(); } catch (e) {}
  window.location.href = '/';
}

onMounted(async () => {
  if (!userStore.initialized) {
    await userStore.fetchUser();
  }
  window.addEventListener('refresh-dashboard', refreshUser);
});

onUnmounted(() => {
  window.removeEventListener('refresh-dashboard', refreshUser);
});

async function refreshUser() {
  await userStore.fetchUser();
}
</script>

<template>
  <transition name="overlay-fade">
    <div v-if="isOpen" class="sidebar-overlay" @click="emit('close')" />
  </transition>

  <aside class="sidebar" :class="{ 'sidebar-open': isOpen }">
    <div class="sidebar-brand">
      <AppLogo size="small" />
    </div>

    <nav class="sidebar-nav">
      <button
        v-for="item in navItems" :key="item.to"
        class="nav-item"
        :class="{ active: isActive(item.to) }"
        @click="navigate(item.to)"
      >
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

      <button class="nav-item logout-btn" @click="handleLogout" style="margin-top: auto;">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        <span class="nav-label">Logout</span>
      </button>

      <div class="nav-item theme-switch-item" @click="toggleTheme">
        <svg v-if="heroConfig.theme === 'light'" class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707m12.728 0l-.707-.707M6.343 6.343l-.707-.707M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z"/></svg>
        <svg v-else class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
        <span class="nav-label">{{ heroConfig.theme === 'light' ? 'Light Mode' : 'Dark Mode' }}</span>
        <div class="mini-switch" :class="{ on: heroConfig.theme === 'light' }"></div>
      </div>
    </nav>

    <div class="sidebar-footer">
      <div class="char-mini">
        <div class="char-aura" :style="{ background: `radial-gradient(circle, ${tier.color}22 0%, transparent 70%)` }" />
        <div class="char-avatar" :style="{ borderColor: tier.color, boxShadow: `0 0 16px ${tier.color}44` }">
          <img :src="getAvatarImageUrl(userStore.avatar)" class="mini-avatar-img" alt="Avatar" loading="lazy" />
        </div>
        <div class="char-info">
          <div class="char-name">{{ userStore.name }}</div>
          <div class="char-rank" :style="{ color: tier.color }">{{ tier.label }}</div>
          <div class="char-xp-wrap">
            <div class="char-xp-bar">
              <div class="char-xp-fill" :style="{ width: userStore.xpProgress + '%', background: tier.color }" />
            </div>
            <span class="char-level" :style="{ color: tier.color }">Lv {{ userStore.level }}</span>
          </div>
        </div>
      </div>
      <div class="sidebar-chips">
        <span class="chip streak-chip" v-if="userStore.streak > 0">⚡ {{ userStore.streak }}d</span>
        <span class="chip xp-chip">🏆 {{ userStore.xp.toLocaleString() }} XP</span>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); z-index: 199; }
.sidebar { width: var(--sidebar-w); min-height: 100dvh; background: var(--bg2); border-right: 1px solid var(--border); display: flex; flex-direction: column; position: sticky; top: 0; height: 100dvh; flex-shrink: 0; overflow: hidden; transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.sidebar-brand { display: flex; align-items: center; gap: 10px; padding: 24px 20px; border-bottom: 1px solid var(--border); }
.sidebar-nav { flex: 1; padding: 14px 10px; display: flex; flex-direction: column; gap: 2px; overflow-y: auto; overflow-x: hidden; }
.nav-item { position: relative; display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 12px; border: none; background: transparent; color: var(--muted); font-family: var(--ff-body); font-size: 0.9rem; font-weight: 500; cursor: pointer; text-align: left; transition: all 0.2s; width: 100%; }
.nav-item:hover { background: var(--surface); color: var(--text); }
.nav-item.active { background: rgba(var(--accent-rgb), 0.1); color: var(--accent); font-weight: 600; border: 1px solid rgba(var(--accent-rgb), 0.2); }
.nav-icon { width: 18px; height: 18px; }
.nav-active-bar { position: absolute; right: -1px; top: 50%; transform: translateY(-50%); width: 3px; height: 60%; background: var(--accent); border-radius: 2px 0 0 2px; }
.theme-switch-item { margin-top: 4px; border-top: 1px solid var(--border); padding-top: 14px !important; }
.mini-switch { width: 32px; height: 16px; border-radius: 10px; background: var(--border2); position: relative; transition: background 0.3s; }
.mini-switch::after { content: ''; position: absolute; left: 2px; top: 2px; width: 12px; height: 12px; border-radius: 50%; background: #fff; transition: transform 0.3s; }
.mini-switch.on { background: var(--accent); }
.mini-switch.on::after { transform: translateX(16px); }
.sidebar-footer { padding: 20px; border-top: 1px solid var(--border); display: flex; flex-direction: column; gap: 12px; }
.char-mini { display: flex; align-items: center; gap: 12px; padding: 14px; border-radius: 16px; background: var(--card); border: 1px solid var(--border); overflow: hidden; }
.char-avatar { width: 36px; height: 36px; border-radius: 50%; border: 2px solid; display: grid; place-items: center; font-weight: 700; color: var(--text); background: var(--surface); }
.mini-avatar-img { width: 100%; height: 100%; object-fit: contain; }
.char-info { flex: 1; min-width: 0; }
.char-name { font-size: 0.85rem; font-weight: 700; color: var(--text); }
.char-rank { font-size: 0.7rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
.char-xp-wrap { display: flex; align-items: center; gap: 6px; margin-top: 4px; }
.char-xp-bar { flex: 1; height: 4px; background: var(--border); border-radius: 2px; overflow: hidden; }
.char-xp-fill { height: 100%; border-radius: 2px; transition: width 0.5s ease; }
.char-level { font-size: 0.7rem; font-weight: 700; }
.sidebar-chips { display: flex; gap: 8px; }
.chip { font-size: 0.7rem; font-weight: 700; padding: 4px 10px; border-radius: 20px; }
.streak-chip { background: rgba(239, 68, 68, 0.1); color: #fca5a5; }
.xp-chip { background: rgba(0, 229, 160, 0.1); color: var(--accent); }

@media (max-width: 1023px) {
  .sidebar { position: fixed; z-index: 200; transform: translateX(-100%); box-shadow: var(--shadow-lg); }
  .sidebar.sidebar-open { transform: translateX(0); }
}
</style>
