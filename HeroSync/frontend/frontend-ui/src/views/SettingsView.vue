<script setup>
import { ref, onMounted } from 'vue';
import { http } from '../api/http';
import { getAvatarImageUrl, compressAvatarUrl } from '../utils/avatar';
import AvaturnCreator from '../components/AvaturnCreator.vue';
import { heroConfig } from '../utils/config';

const activeTab = ref('hero'); // hero, interface, quests, security
const loading   = ref(false);
const message   = ref('');
const messageType = ref('success');

const user = ref({
  name: '',
  email: '',
  bio: '',
  avatar: ''
});

const showRPM = ref(false);

const onAvatarCreated = async (payload) => {
  // Update local state - Support new Combo format for 2D previews
  if (payload && payload.model) {
    user.value.avatar = payload.model + '|||' + (payload.image || '');
  } else {
    user.value.avatar = compressAvatarUrl(payload);
  }
  
  showRPM.value = false;
  
  // AUTO-SAVE: Trigger an immediate save so the user doesn't have to click the button
  await saveProfile(true); 
};

async function loadData() {
  loading.value = true;
  try {
    const res = await http.get('/api/profile/me');
    user.value = res.data;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

async function saveProfile(isAutoSave = false) {
  loading.value = true;
  message.value = '';
  try {
    await http.put('/api/profile/me', {
      name: user.value.name,
      bio: user.value.bio,
      avatar: user.value.avatar
    });
    showMsg(isAutoSave ? '3D Avatar Synced!' : 'Hero Registry updated successfully!');
    // Force the dashboard to refresh its data
    window.dispatchEvent(new CustomEvent('refresh-dashboard'));
  } catch (e) {
    showMsg('Failed to update Registry.', 'error');
  } finally {
    loading.value = false;
  }
}

function saveConfig() {
  showMsg('Interface modulators synced!');
}

function showMsg(txt, type = 'success') {
  message.value = txt;
  messageType.value = type;
  setTimeout(() => message.value = '', 3000);
}

onMounted(loadData);

import { onBeforeRouteLeave } from 'vue-router';
onBeforeRouteLeave(() => {
  showRPM.value = false; // Force close avatar creator before leaving
});
</script>

<template>
  <div class="settings-page">
    <div class="container">
      
      <!-- Page Header -->
      <header class="settings-header">
        <div class="header-content">
          <div class="page-icon">
            <svg class="glow-icon glow-blue" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
          </div>
          <div class="title-group">
            <h1>Command Center</h1>
            <p>Configure your heroic interface and quest parameters</p>
          </div>
        </div>
      </header>

      <div class="settings-grid">
        <!-- Sidebar Navigation -->
        <aside class="settings-nav">
          <button 
            v-for="tab in [
              { id: 'hero', label: 'Hero Registry', icon: 'user' },
              { id: 'interface', label: 'Interface Modulators', icon: 'monitor' },
              { id: 'quests', label: 'Quest Parameters', icon: 'sword' },
              { id: 'security', label: 'Vault & Rites', icon: 'lock' }
            ]" 
            :key="tab.id"
            class="nav-btn"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <span class="btn-glow" v-if="activeTab === tab.id"></span>
            <div class="btn-content">
              <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path v-if="tab.icon === 'user'" d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle v-if="tab.icon === 'user'" cx="12" cy="7" r="4"/>
                <rect v-if="tab.icon === 'monitor'" x="2" y="3" width="20" height="14" rx="2" ry="2"/><line v-if="tab.icon === 'monitor'" x1="8" y1="21" x2="16" y2="21"/><line v-if="tab.icon === 'monitor'" x1="12" y1="17" x2="12" y2="21"/>
                <path v-if="tab.icon === 'sword'" d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path v-if="tab.icon === 'sword'" d="M13 19l6-6"/>
                <rect v-if="tab.icon === 'lock'" x="3" y="11" width="18" height="11" rx="2" ry="2"/><path v-if="tab.icon === 'lock'" d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              {{ tab.label }}
            </div>
          </button>
        </aside>

        <!-- Main Settings Panel -->
        <main class="settings-panel card">
          <transition name="fade-slide" mode="out-in">
            
            <!-- Hero Registry -->
            <div v-if="activeTab === 'hero'" class="tab-content" key="hero">
              <h2 class="section-title">Hero Registry</h2>
              <p class="section-desc">Manage your identity across the realm</p>
              
              <div class="form-stack">
                <div class="avatar-edit">
                  <div class="current-avatar">
                    <img :src="getAvatarImageUrl(user.avatar)" alt="Avatar" />
                  </div>
                  <div class="avatar-controls">
                    <label class="field-label">Avatar Seed</label>
                    <div class="input-row">
                      <input v-model="user.avatar" class="input" placeholder="Enter a name or word..." />
                      <button class="btn ghost" @click="user.avatar = Math.random().toString(36).substring(7)">Randomize</button>
                      <button class="btn ghost rpm-btn" @click="showRPM = true">Design 3D</button>
                    </div>
                  </div>
                </div>

                <div class="input-group">
                  <label class="field-label">Display Name</label>
                  <input v-model="user.name" class="input" placeholder="Your hero name..." />
                </div>

                <div class="input-group">
                  <label class="field-label">Hero Bio</label>
                  <textarea v-model="user.bio" class="input textarea" placeholder="Tell your story..."></textarea>
                </div>

                <div class="action-footer">
                  <button class="btn primary" :disabled="loading" @click="saveProfile(false)">
                    {{ loading ? 'Updating...' : 'Save Attributes' }}
                  </button>
                </div>
              </div>
            </div>

            <!-- Interface Modulators -->
            <div v-else-if="activeTab === 'interface'" class="tab-content" key="interface">
              <h2 class="section-title">Interface Modulators</h2>
              <p class="section-desc">Customize your sensory experience</p>

              <div class="setting-item">
                <div class="item-info">
                  <h3>Immersion Mode</h3>
                  <p>Enable high-fidelity animations and visual effects</p>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="heroConfig.immersionMode" />
                  <span class="slider"></span>
                </label>
              </div>

              <div class="setting-item">
                <div class="item-info">
                  <h3>Chrono Theme</h3>
                  <p>Select your interface aesthetic</p>
                </div>
                <select v-model="heroConfig.theme" class="input select-input">
                  <option value="cyber-dark">Cyber-Dark (Default)</option>
                  <option value="mythic-blue">Mythic Blue</option>
                  <option value="emerald-forest">Emerald Forest</option>
                </select>
              </div>

              <div class="setting-item">
                <div class="item-info">
                  <h3>Acoustic Feedback</h3>
                  <p>Play celebratory chimes on quest completion</p>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="heroConfig.soundEffects" />
                  <span class="slider"></span>
                </label>
              </div>

              <div class="action-footer">
                <button class="btn primary" @click="saveConfig">Save Preferences</button>
              </div>
            </div>

            <!-- Quest Parameters -->
            <div v-else-if="activeTab === 'quests'" class="tab-content" key="quests">
              <h2 class="section-title">Quest Parameters</h2>
              <p class="section-desc">Fine-tune your heroic schedule</p>

              <div class="setting-item">
                <div class="item-info">
                  <h3>Realm Reset Time</h3>
                  <p>When should your daily quests reset?</p>
                </div>
                <input type="time" v-model="heroConfig.dailyReset" class="input time-input" />
              </div>

              <div class="setting-item">
                <div class="item-info">
                  <h3>Weekend Grace</h3>
                  <p>Streaks won't break if missions are missed on Sat/Sun</p>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="heroConfig.weekendGrace" />
                  <span class="slider"></span>
                </label>
              </div>

              <div class="action-footer">
                <button class="btn primary" @click="saveConfig">Sync Parameters</button>
              </div>
            </div>

            <!-- Vault & Rites -->
            <div v-else-if="activeTab === 'security'" class="tab-content" key="security">
              <h2 class="section-title">Vault & Rites</h2>
              <p class="section-desc">Manage your credentials and journey status</p>

              <div class="security-card">
                <div class="input-group">
                  <label class="field-label">Registered Email</label>
                  <input :value="user.email" readonly class="input readonly" />
                </div>
                <button class="btn ghost mt-12">Change Password</button>
              </div>

              <div class="danger-zone">
                <h3 class="danger-title">End Journey</h3>
                <p>Permanently erase your hero's record. This action is irreversible.</p>
                <button class="btn-danger">Delete Account</button>
              </div>
            </div>

          </transition>

          <!-- Toast Message -->
          <transition name="toast">
            <div v-if="message" class="toast-msg" :class="messageType">
              {{ message }}
            </div>
          </transition>
        </main>
      </div>
    </div>
  </div>

  <AvaturnCreator 
    v-if="showRPM" 
    @close="showRPM = false" 
    @avatar-created="onAvatarCreated" 
  />
</template>

<style scoped>
.settings-page { min-height: 100vh; padding-bottom: 60px; }

/* Header */
.settings-header { margin-bottom: 30px; padding-top: 20px; }
.header-content { display: flex; align-items: center; gap: 20px; }
.title-group h1 { font-family: var(--ff-display); font-size: 1.8rem; margin: 0; color: #fff; }
.title-group p { color: var(--muted); margin: 5px 0 0; font-size: 0.95rem; }

/* Grid Layout */
.settings-grid {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 30px;
  align-items: start;
}

@media (max-width: 900px) {
  .settings-grid { grid-template-columns: 1fr; }
}

/* Nav Sidebar */
.settings-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-btn {
  position: relative;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px 20px;
  color: var(--text-dim);
  font-family: var(--ff-head);
  font-weight: 700;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s;
  overflow: hidden;
}

.nav-btn:hover { background: rgba(255,255,255,0.06); color: #fff; }
.nav-btn.active { border-color: var(--accent); color: var(--accent); background: rgba(0,229,160,0.05); }

.btn-content { position: relative; z-index: 2; display: flex; align-items: center; gap: 12px; }
.btn-glow {
  position: absolute; inset: 0;
  background: radial-gradient(circle at left, rgba(0,229,160,0.1) 0%, transparent 70%);
  animation: glowMove 3s infinite;
}

/* Panel Content */
.settings-panel { padding: 40px; min-height: 500px; position: relative; }
.tab-content { width: 100%; }

.section-title { font-family: var(--ff-display); font-size: 1.4rem; color: #fff; margin: 0 0 8px; }
.section-desc { color: var(--muted); margin-bottom: 40px; font-size: 0.95rem; }

/* Forms */
.form-stack { display: flex; flex-direction: column; gap: 24px; max-width: 600px; }
.input-group { display: flex; flex-direction: column; gap: 8px; }
.field-label { font-family: var(--ff-head); font-weight: 700; color: var(--text-dim); text-transform: uppercase; font-size: 0.8rem; letter-spacing: 1px; }

.input {
  background: rgba(0,0,0,0.2);
  border: 1px solid var(--border);
  padding: 12px 16px;
  border-radius: 10px;
  color: #fff;
  font-family: var(--ff-body);
  transition: border-color 0.2s;
}
.input:focus { outline: none; border-color: var(--accent); background: rgba(0,0,0,0.3); }
.textarea { min-height: 100px; resize: vertical; }
.readonly { color: var(--muted); cursor: not-allowed; }

/* Avatar Edit */
.avatar-edit { display: flex; align-items: center; gap: 30px; margin-bottom: 10px; }
.current-avatar {
  width: 100px; height: 100px; border-radius: 20px;
  background: rgba(255,255,255,0.05); border: 2px solid var(--accent);
  display: flex; align-items: center; justify-content: center;
  padding: 10px; box-shadow: 0 0 20px rgba(0,229,160,0.15);
}
.current-avatar img { width: 100%; height: 100%; }
.avatar-controls { flex: 1; }
.input-row { display: flex; gap: 10px; margin-top: 8px; }

/* Setting Items (Switches) */
.setting-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 0; border-bottom: 1px solid var(--border);
}
.item-info h3 { font-family: var(--ff-head); font-size: 1.1rem; margin: 0; color: #fff; }
.item-info p { color: var(--muted); margin: 4px 0 0; font-size: 0.85rem; }

/* Toggle Switch */
.switch { position: relative; display: inline-block; width: 44px; height: 24px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider {
  position: absolute; cursor: pointer; inset: 0;
  background-color: rgba(255,255,255,0.1); transition: .4s; border-radius: 24px;
}
.slider:before {
  position: absolute; content: ""; height: 18px; width: 18px; left: 3px; bottom: 3px;
  background-color: #fff; transition: .4s; border-radius: 50%;
}
input:checked + .slider { background-color: var(--accent); box-shadow: 0 0 10px rgba(0,229,160,0.4); }
input:checked + .slider:before { transform: translateX(20px); }

/* Danger Zone */
.danger-zone { margin-top: 60px; padding: 24px; border: 1px dashed rgba(239,68,68,0.3); border-radius: 14px; background: rgba(239,68,68,0.02); }
.danger-title { color: var(--danger); font-family: var(--ff-head); margin: 0 0 8px; }
.danger-zone p { font-size: 0.85rem; color: var(--muted); margin-bottom: 20px; }
.btn-danger {
  padding: 10px 20px; border-radius: 10px; border: 1px solid var(--danger);
  background: transparent; color: var(--danger); font-family: var(--ff-head);
  font-weight: 700; cursor: pointer; transition: all 0.2s;
}
.btn-danger:hover { background: var(--danger); color: #fff; box-shadow: 0 0 20px rgba(239,68,68,0.3); }

.rpm-btn {
  color: #fff; background: linear-gradient(135deg, rgba(59,130,246,0.3), rgba(139,92,246,0.3));
  border-color: rgba(139,92,246,0.5); font-weight: 700;
}
.rpm-btn:hover { border-color: #8b5cf6; box-shadow: 0 0 15px rgba(139,92,246,0.2); }

/* Footer */
.action-footer { margin-top: 40px; display: flex; justify-content: flex-end; }

/* Toast */
.toast-msg {
  position: absolute; bottom: 20px; right: 20px;
  padding: 12px 24px; border-radius: 10px;
  background: var(--bg2); border: 1px solid var(--accent);
  color: var(--accent); font-family: var(--ff-head); font-weight: 700;
  box-shadow: 0 10px 30px rgba(0,0,0,0.4);
}
.toast-msg.error { border-color: var(--danger); color: var(--danger); }

/* Transitions */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.25s; }
.fade-slide-enter-from { opacity: 0; transform: translateX(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(-10px); }

.toast-enter-active { animation: toastIn 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.toast-leave-active { animation: toastIn 0.3s reverse; }
@keyframes toastIn { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

@keyframes glowMove {
  0%, 100% { opacity: 0.5; transform: scaleX(1); }
  50% { opacity: 0.8; transform: scaleX(1.2); }
}
</style>
