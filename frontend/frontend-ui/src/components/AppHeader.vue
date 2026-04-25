<script setup>
import { ref, onMounted } from 'vue';
import { http } from '../api/http';
import { getAvatarImageUrl } from '../utils/avatar';
import { useUserStore } from '../stores/user';

const props = defineProps({ sidebarOpen: Boolean });
const emit  = defineEmits(['toggleSidebar']);

const userStore = useUserStore();
const showLogoutModal = ref(false);

/** Calls the Spring Security logout endpoint, then redirects to the landing page. */
const confirmLogout = async () => {
  showLogoutModal.value = false;
  try { await http.post('/auth/logout'); } catch { /* session may already be invalid */ }
  finally { window.location.href = '/'; }
};

onMounted(async () => {
  if (!userStore.initialized) {
    await userStore.fetchUser();
  }
});
</script>

<template>
  <header class="header">
    <div class="nav">
      <button class="hamburger" @click="emit('toggleSidebar')" aria-label="Toggle navigation">
        <span class="ham-bar" :class="{ open: sidebarOpen }" />
        <span class="ham-bar" :class="{ open: sidebarOpen }" />
        <span class="ham-bar" :class="{ open: sidebarOpen }" />
      </button>

      <router-link to="/dashboard" class="brand header-brand">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width:65%;height:65%;">
            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
          </svg>
        </div>
        <span class="brand-text"><span class="highlight">Hero</span>Sync</span>
      </router-link>

      <div class="header-actions">
        <div class="header-user" v-if="userStore.avatar">
          <img :src="getAvatarImageUrl(userStore.avatar)" class="header-avatar" alt="User Avatar" />
        </div>
        <button class="icon-btn" title="Logout" @click="showLogoutModal = true">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
      </div>
    </div>
  </header>

  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="showLogoutModal" class="modal-backdrop" @click.self="showLogoutModal = false">
        <div class="modal-box">
          <div class="modal-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
          </div>
          <h3 class="modal-title">Log Out</h3>
          <p class="modal-desc">Abandoning your quest? Come back soon, hero.</p>
          <div class="modal-actions">
            <button class="btn-cancel" @click="showLogoutModal = false">Stay</button>
            <button class="btn-confirm" @click="confirmLogout">Logout</button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.header {
  height: var(--header-h);
  background: var(--card);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  position: sticky; top: 0; z-index: 100;
  display: flex; align-items: center; padding: 0 16px;
}
.nav { display: flex; align-items: center; gap: 12px; width: 100%; }

.hamburger {
  display: flex; flex-direction: column; justify-content: center; gap: 5px;
  width: 36px; height: 36px; padding: 6px; background: none; border: none;
  cursor: pointer; border-radius: 8px; transition: background 0.2s;
}
.hamburger:hover { background: rgba(255,255,255,0.06); }
.ham-bar {
  display: block; width: 18px; height: 2px;
  background: var(--text-dim); border-radius: 2px;
  transition: all 0.25s ease; transform-origin: center;
}
.ham-bar.open:nth-child(1) { transform: translateY(7px) rotate(45deg); }
.ham-bar.open:nth-child(2) { opacity: 0; transform: scaleX(0); }
.ham-bar.open:nth-child(3) { transform: translateY(-7px) rotate(-45deg); }

.header-brand { display: flex; align-items: center; gap: 8px; text-decoration: none; color: var(--text); flex: 1; }
.logo { width: 28px; height: 28px; border-radius: 7px; display: grid; place-items: center;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #040d09; font-family: var(--ff-display); font-size: 8px; font-weight: 900; }
.brand-text {
  font-family: var(--ff-display);
  font-size: 1.15rem;
  font-weight: 800;
  color: var(--text);
  letter-spacing: 1px;
}
.brand-text .highlight { color: var(--accent); }
.header-actions { display: flex; align-items: center; gap: 8px; margin-left: auto; }
.header-user { width: 32px; height: 32px; border-radius: 50%; overflow: hidden; background: rgba(255,255,255,0.05); border: 1px solid var(--border); }
.header-avatar { width: 100%; height: 100%; object-fit: contain; }

.icon-btn {
  width: 34px; height: 34px; border-radius: 8px; border: 1px solid var(--border);
  background: rgba(255,255,255,0.03); color: var(--muted); cursor: pointer;
  display: grid; place-items: center; transition: all 0.18s;
}
.icon-btn:hover { background: rgba(239,68,68,0.1); color: var(--danger); border-color: rgba(239,68,68,0.3); }

@media (min-width: 1024px) {
  .header { display: none; }
}

.modal-backdrop {
  position: fixed; inset: 0; background: rgba(0,0,0,0.65); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 9999;
}
.modal-box {
  background: var(--card2); border: 1px solid var(--border2);
  width: 90%; max-width: 360px; border-radius: 20px; padding: 28px; text-align: center;
}
.modal-icon {
  width: 50px; height: 50px; margin: 0 auto 14px;
  background: rgba(239,68,68,0.1); color: var(--danger);
  border-radius: 50%; display: grid; place-items: center;
}
.modal-title { margin: 0 0 8px; font-family: var(--ff-head); font-size: 1.3rem; font-weight: 700; color: var(--text); }
.modal-desc  { margin: 0 0 22px; color: var(--muted); font-size: 0.9rem; }
.modal-actions { display: flex; gap: 10px; }
.btn-cancel, .btn-confirm {
  flex: 1; padding: 11px; border-radius: 10px; font-weight: 700; cursor: pointer;
  font-size: 0.9rem; font-family: var(--ff-body); transition: all 0.2s;
}
.btn-cancel  { background: transparent; border: 1px solid var(--border2); color: var(--text); }
.btn-cancel:hover { background: rgba(255,255,255,0.06); }
.btn-confirm { background: var(--danger); border: none; color: #fff; }
.btn-confirm:hover { background: #dc2626; }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.2s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style>
