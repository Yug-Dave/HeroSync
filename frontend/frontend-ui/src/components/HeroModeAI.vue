<script setup>
import { ref, onMounted, nextTick, computed, watch } from 'vue';
import { http } from '../api/http';
import { useUserStore } from '../stores/user';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

const userStore = useUserStore();
import ConfirmationModal from './ConfirmationModal.vue';

// ── Companion Config ───────────────────────────────────────
const companions = {
  SYNC:  { label: 'SYNC', type: 'The Strategist',        color: '#3b82f6', initial: 'S', image: '/companion_omega.png' },
  AURA:  { label: 'AURA', type: 'The Empathetic Guide',  color: '#8b5cf6', initial: 'A', image: '/companion_kaelen.png' },
  VOLT:  { label: 'VOLT', type: 'The Motivator',         color: '#f97316', initial: 'V', image: '/companion_rex.png' }
};

// ── State ─────────────────────────────────────────────────
const companion        = computed(() => userStore.companionChoice);
const aiProvider       = computed(() => userStore.aiProvider);
const messages         = computed(() => userStore.currentMessages);
const inputText        = ref('');
const isExpanded       = ref(false);
const isLoading        = ref(false);
const greetingText     = ref('Initializing neural link...');
const chatArea         = ref(null);
const isNewUser        = ref(false);
const onboardingDone   = ref(false);

// ── Dropdown & Modal State ────────────────────────────────
const showBuddyDropdown = ref(false);
const pendingBuddy     = ref(null);
const showConfirmModal  = ref(false);

const providerList = [
  { id: 'GROQ', label: 'Groq', desc: 'Llama 3 (Fastest)' },
  { id: 'GPT',  label: 'GPT-4', desc: 'OpenAI (Smartest)' },
  { id: 'GEMINI', label: 'Gemini', desc: 'Google (Nuanced)' }
];

// ── Computed helpers ──────────────────────────────────────
const currentCompanion = () => companions[companion.value] || companions.SYNC;

const renderMarkdown = (text) => {
  if (!text) return '';
  // Convert markdown to HTML and sanitize
  const rawHtml = marked.parse(text, { breaks: true });
  return DOMPurify.sanitize(rawHtml);
};

// ── API ───────────────────────────────────────────────────
async function aiChat(message, mode) {
  const res = await http.post('/ai/chat', { message, mode });
  return res.data;
}

function scrollToBottom() {
  nextTick(() => {
    if (chatArea.value) chatArea.value.scrollTop = chatArea.value.scrollHeight;
  });
}

// ── Send Greeting Logic ──────────────────────────────────
async function triggerGreeting() {
  if (messages.value.length > 0) return;
  
  isNewUser.value = userStore.totalDone === 0 && userStore.streak === 0;
  const greetMode = isNewUser.value ? 'onboarding' : 'greeting';

  try {
    const res = await aiChat('', greetMode);
    greetingText.value = res.reply;
    userStore.addMessage(companion.value, 'ai', res.reply);
    if (isNewUser.value) isExpanded.value = true;
  } catch (e) {
    greetingText.value = 'Ready to help you level up, hero.';
  }
}

// ── Lifecycle & Watchers ──────────────────────────────────


watch(companion, (newBuddy) => {
  if (newBuddy) {
    if (userStore.currentMessages.length === 0) {
      triggerGreeting();
    }
    scrollToBottom();
  }
}, { immediate: true });

onMounted(async () => {
  if (userStore.initialized) return; // Don't overwrite recent local changes
  try {
    const profileRes = await http.get('/profile/me');
    userStore.updateCompanion(profileRes.data.companionChoice, profileRes.data.aiProvider);
  } catch (e) { console.warn('Could not load profile:', e); }
});

// ── Model Switching ───────────────────────────────────────
async function switchModel(providerId) {
  userStore.updateCompanion(null, providerId);
  try {
    await http.post('/profile/update-companion', {
      companionChoice: userStore.companionChoice,
      aiProvider: providerId
    });
  } catch (e) { console.warn('Failed to persist model switch:', e); }
}

// ── Buddy Switching Logic ─────────────────────────────────
function requestBuddySwitch(buddyId) {
  if (buddyId === companion.value) return;
  pendingBuddy.value = buddyId;
  showConfirmModal.value = true;
  showBuddyDropdown.value = false;
}

async function confirmBuddySwitch() {
  const newBuddy = pendingBuddy.value;
  showConfirmModal.value = false;
  
  try {
    // Save to backend FIRST to ensure DB is source of truth
    await http.post('/profile/update-companion', {
      companionChoice: newBuddy,
      aiProvider: userStore.aiProvider
    });
    // Then update store
    userStore.updateCompanion(newBuddy, null);
  } catch (e) { 
    console.error('Failed to persist buddy switch:', e); 
    // Even if it fails, we keep the store update for UI responsiveness
    userStore.updateCompanion(newBuddy, null);
  }
}

// ── Send Message ──────────────────────────────────────────
async function sendMessage() {
  const text = inputText.value.trim();
  if (!text || isLoading.value) return;

  userStore.addMessage(companion.value, 'user', text);
  inputText.value = '';
  isLoading.value = true;
  scrollToBottom();

  try {
    let mode;
    if (isNewUser.value && !onboardingDone.value) {
      mode = 'onboarding';
      onboardingDone.value = true;
    } else {
      mode = 'chat';
    }

    const res = await aiChat(text, mode);
    userStore.addMessage(companion.value, 'ai', res.reply);
  } catch (e) {
    userStore.addMessage(companion.value, 'ai', 'Neural link interrupted. Try again.');
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
}

async function deployQuest() {
  isExpanded.value = true;
  isLoading.value  = true;
  scrollToBottom();
  try {
    const res = await aiChat('', 'coaching');
    userStore.addMessage(companion.value, 'ai', res.reply);
  } catch (e) {
    userStore.addMessage(companion.value, 'ai', 'Ready to help you build your next quest.');
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
}

function handleKey(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
}
</script>

<template>
  <div v-if="userStore.initialized" class="hero-ai-sidebar" :class="{ 'is-expanded': isExpanded }" :style="{ '--companion-color': currentCompanion().color }">
    
    <!-- ── COLLAPSED VERTICAL TAB ── -->
    <div v-if="!isExpanded" class="sidebar-collapsed" @click="isExpanded = true">
      <div class="tab-avatar" :style="{ borderColor: currentCompanion().color }">
        <img :src="currentCompanion().image" :alt="currentCompanion().label" class="sidebar-avatar-img" />
      </div>
      <div class="vertical-label">Chat with your AI companion</div>
      <div class="pulse-ring" :style="{ borderColor: currentCompanion().color }"></div>
    </div>

    <!-- ── EXPANDED CHAT PANEL ── -->
    <div v-else class="sidebar-expanded">
      <header class="sidebar-header" :style="{ borderBottomColor: currentCompanion().color + '33' }">
        <div class="companion-box" @click="showBuddyDropdown = !showBuddyDropdown">
          <div class="companion-avatar sm" :style="{ borderColor: currentCompanion().color }">
            <img :src="currentCompanion().image" :alt="currentCompanion().label" class="sidebar-avatar-img" />
          </div>
          <div class="header-meta">
            <div class="companion-name-wrap">
              <span class="companion-name" :style="{ color: currentCompanion().color }">{{ currentCompanion().label }}</span>
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" class="chevron-icon" :class="{ rotated: showBuddyDropdown }">
                <path d="M6 9l6 6 6-6"/>
              </svg>
            </div>
            <div class="companion-status">System Online</div>
          </div>

          <!-- Custom Dropdown -->
          <Transition name="dropdown">
            <div v-if="showBuddyDropdown" class="buddy-picker-menu">
              <div v-for="(buddy, id) in companions" :key="id" 
                   class="buddy-option" 
                   :class="{ active: id === companion }"
                   @click.stop="requestBuddySwitch(id)">
                <div class="opt-avatar" :style="{ borderColor: buddy.color }">
                  <img :src="buddy.image" class="opt-img" />
                </div>
                <div class="opt-info">
                  <div class="opt-name">{{ buddy.label }}</div>
                  <div class="opt-type">{{ buddy.type }}</div>
                </div>
              </div>
            </div>
          </Transition>
        </div>
        
        <div class="header-actions">
          <button class="action-icon-btn" @click="deployQuest" title="Deploy Quest">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
          </button>
          <button class="close-btn" @click="isExpanded = false">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
      </header>

      <div class="chat-messages" ref="chatArea">
        <div v-if="messages.length === 0" class="chat-greeting">
          <div class="greeting-bubble" :style="{ borderLeftColor: currentCompanion().color }" v-html="renderMarkdown(greetingText)">
          </div>
        </div>
        
        <div v-for="(msg, i) in messages" :key="i" class="message-row" :class="msg.role">
          <div v-if="msg.role === 'ai'" class="chat-msg-avatar" :style="{ borderColor: currentCompanion().color }">
            <img :src="currentCompanion().image" :alt="currentCompanion().label" class="sidebar-avatar-img" />
          </div>
          <div class="bubble" :class="msg.role" :style="msg.role === 'ai' ? { borderLeftColor: currentCompanion().color } : {}" 
               v-html="msg.role === 'ai' ? renderMarkdown(msg.text) : msg.text">
          </div>
        </div>

        <div v-if="isLoading" class="message-row ai">
          <div class="bubble ai loading-bubble" :style="{ borderLeftColor: currentCompanion().color }">
            <span class="dot"></span><span class="dot"></span><span class="dot"></span>
          </div>
        </div>
      </div>

      <footer class="sidebar-footer">
        <!-- AI Engine Selector -->
        <div class="model-selector">
          <button v-for="p in providerList" :key="p.id"
                  class="model-chip"
                  :class="{ active: p.id === aiProvider }"
                  @click="switchModel(p.id)">
            {{ p.label }}
          </button>
        </div>

        <div class="input-container">
          <input
            v-model="inputText"
            class="sidebar-input"
            :placeholder="`Message ${currentCompanion().label}...`"
            @keydown="handleKey"
            :disabled="isLoading"
          />
          <button
            class="sidebar-send"
            :style="{ background: currentCompanion().color }"
            @click="sendMessage"
            :disabled="isLoading || !inputText.trim()"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
          </button>
        </div>
      </footer>
    </div>
    
    <!-- Confirmation Modal for Buddy Switching -->
    <ConfirmationModal
      :show="showConfirmModal"
      title="Neural Realignment"
      :message="`Are you sure you want to switch your active buddy to ${pendingBuddy}? This will change your chat persona and dashboard style.`"
      confirmText="Synchronize"
      :accentColor="pendingBuddy ? companions[pendingBuddy].color : 'var(--accent)'"
      @confirm="confirmBuddySwitch"
      @cancel="showConfirmModal = false"
    />
  </div>
</template>

<style scoped>
@media (min-width: 769px) {
  .hero-ai-sidebar {
    position: sticky;
    top: 0;
    height: 100vh;
    z-index: 100;
    background: rgba(13, 17, 23, 0.8);
    backdrop-filter: blur(20px);
    border-left: 1px solid rgba(255, 255, 255, 0.05);
    width: 60px;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    flex-direction: column;
    flex-shrink: 0;
    overflow: hidden;
  }
  .hero-ai-sidebar.is-expanded {
    width: 320px;
    box-shadow: -20px 0 50px rgba(0,0,0,0.5);
    background: var(--bg2);
    overflow: visible;
  }
}

@media (max-width: 768px) {
  .hero-ai-sidebar {
    position: fixed;
    right: 0;
    bottom: 0;
    top: auto;
    height: auto;
    width: 60px;
    border-left: none;
    background: transparent;
    backdrop-filter: none;
    pointer-events: none;
    box-shadow: none;
    z-index: 1000;
  }
  .hero-ai-sidebar.is-expanded {
    top: 0; left: 0; right: 0; bottom: 0;
    height: 100vh;
    width: 100%;
    pointer-events: all;
    background: var(--bg) !important;
    backdrop-filter: blur(20px) !important;
    z-index: 2000;
  }
}

@media (min-width: 769px) {
  .sidebar-collapsed {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0;
    cursor: pointer;
    position: relative;
    pointer-events: all;
  }
  .tab-avatar {
    height: 36px;
    width: 36px;
    border-radius: 10px;
    border: 1.5px solid;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 30px;
    transition: transform 0.2s;
    overflow: hidden;
  }
}

@media (max-width: 768px) {
  .sidebar-collapsed {
    background: var(--bg2);
    border-radius: 50% 0 0 50%;
    height: 70px;
    width: 50px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--border2);
    border-right: none;
    box-shadow: -4px 0 15px rgba(0,0,0,0.3);
    overflow: hidden;
    pointer-events: all;
  }
  .tab-avatar { 
    margin: 0;
    width: 100%; 
    height: 100%; 
    border: none; 
    border-radius: 0;
  }
  .vertical-label, .pulse-ring { display: none; }
}

.tab-avatar { overflow: hidden; }
.sidebar-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.5);
  object-position: center top;
  transform-origin: center top;
}
.sidebar-collapsed:hover .tab-avatar { transform: scale(1.1); }
.tab-initial { font-family: var(--ff-head); font-weight: 900; font-size: 0.9rem; }

.vertical-label {
  writing-mode: vertical-rl;
  text-orientation: mixed;
  font-family: var(--ff-head);
  font-weight: 800;
  font-size: 0.75rem;
  color: var(--muted);
  letter-spacing: 2px;
  opacity: 0.6;
  margin-top: auto;
  margin-bottom: 20px;
}

.pulse-ring {
  position: absolute;
  top: 20px;
  width: 36px;
  height: 36px;
  border: 1px solid;
  border-radius: 10px;
  animation: tabPulse 2s infinite;
  pointer-events: none;
}
@keyframes tabPulse {
  0% { transform: scale(1); opacity: 0.6; }
  100% { transform: scale(1.6); opacity: 0; }
}

/* ── Expanded State ── */
.sidebar-expanded {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 320px;
  animation: fadeIn 0.3s forwards;
}

@media (max-width: 768px) {
  .sidebar-expanded {
    width: 100%;
  }
}

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.sidebar-header {
  position: relative; /* Anchor for dropdown */
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid;
  z-index: 60;
}

.companion-box { display: flex; align-items: center; gap: 10px; }
.companion-avatar.sm {
  width: 32px; height: 32px; border-radius: 8px; border: 1px solid;
  display: flex; align-items: center; justify-content: center;
  overflow: hidden;
}
.companion-initial.sm { font-size: 0.7rem; font-weight: 800; }

.header-meta { display: flex; flex-direction: column; cursor: pointer; }
.companion-name-wrap { display: flex; align-items: center; gap: 6px; }
.companion-name { font-family: var(--ff-head); font-weight: 800; font-size: 0.85rem; }
.chevron-icon { color: var(--muted); transition: transform 0.3s ease; }
.chevron-icon.rotated { transform: rotate(180deg); }
.companion-status { font-size: 0.65rem; color: var(--accent); font-weight: 600; opacity: 0.8; }

/* ── Buddy Dropdown ── */
.buddy-picker-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--bg2);
  border: 1px solid var(--border2);
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  padding: 8px;
  z-index: 1000;
  box-shadow: 0 20px 40px rgba(0,0,0,0.5);
  backdrop-filter: blur(15px);
  margin-top: -1px;
}
.buddy-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.buddy-option:hover { background: rgba(255,255,255,0.05); }
.buddy-option.active { background: rgba(var(--accent-rgb), 0.1); }

.opt-avatar {
  width: 36px; height: 36px; border-radius: 8px; border: 1.5px solid;
  overflow: hidden; flex-shrink: 0;
}
.opt-img {
  width: 100%; height: 100%; object-fit: cover;
  transform: scale(1.5); object-position: center top;
}
.opt-name { font-weight: 800; font-size: 0.8rem; color: var(--text); }
.opt-type { font-size: 0.65rem; color: var(--muted); }

/* ── Model Selector ── */
.model-selector {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
  padding: 0 4px;
}
.model-chip {
  flex: 1;
  padding: 6px 4px;
  border-radius: 8px;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border);
  color: var(--muted);
  font-size: 0.65rem;
  font-family: var(--ff-head);
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}
.model-chip:hover { border-color: var(--border2); color: var(--text); }
.model-chip.active {
  background: rgba(var(--accent-rgb), 0.1);
  border-color: var(--companion-color);
  color: var(--companion-color);
}

.dropdown-enter-active, .dropdown-leave-active { transition: all 0.3s ease; }
.dropdown-enter-from, .dropdown-leave-to { opacity: 0; transform: translateY(-10px); }

.header-actions { display: flex; gap: 6px; }
.action-icon-btn, .close-btn {
  background: var(--bg2); border: 1px solid var(--border); border-radius: 6px;
  width: 28px; height: 28px; display: flex; align-items: center; justify-content: center;
  color: var(--muted); cursor: pointer; transition: all 0.2s;
}
.action-icon-btn:hover, .close-btn:hover { color: var(--text); border-color: var(--border2); }

/* ── Messages ── */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.chat-messages::-webkit-scrollbar { width: 4px; }
.chat-messages::-webkit-scrollbar-thumb { background: var(--border2); border-radius: 2px; }

.chat-greeting { margin-bottom: 10px; }
.greeting-bubble {
  background: rgba(255,255,255,0.03);
  border-left: 3px solid;
  padding: 12px;
  border-radius: 4px 12px 12px 12px;
  font-size: 0.8rem;
  line-height: 1.5;
  color: var(--text-dim);
}

.message-row { display: flex; margin-bottom: 12px; gap: 8px; }
.message-row.user { justify-content: flex-end; }
.chat-msg-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid;
  overflow: hidden;
  flex-shrink: 0;
  margin-top: 4px;
  box-shadow: 0 0 10px rgba(0,0,0,0.5);
}
.bubble {
  max-width: 90%;
  padding: 10px 14px;
  font-size: 0.82rem;
  line-height: 1.5;
  word-break: break-word;
}
.bubble.ai {
  background: rgba(255,255,255,0.04);
  border-left: 3px solid;
  border-radius: 2px 12px 12px 12px;
  color: var(--text);
}
.bubble.user {
  background: var(--accent);
  color: #000;
  font-weight: 600;
  border-radius: 12px 2px 12px 12px;
}

/* ── Markdown Styles ── */
.bubble :deep(p) { margin: 0 0 10px 0; }
.bubble :deep(p:last-child) { margin-bottom: 0; }
.bubble :deep(ul), .bubble :deep(ol) { margin: 10px 0; padding-left: 20px; }
.bubble :deep(li) { margin-bottom: 5px; }
.bubble :deep(strong), .bubble :deep(b) { font-weight: 800; color: var(--text); }
.theme-light .bubble :deep(strong) { color: #000; }
.bubble :deep(code) { 
  background: rgba(0,0,0,0.3); 
  padding: 2px 4px; 
  border-radius: 4px; 
  font-family: monospace;
}
.bubble :deep(h1), .bubble :deep(h2), .bubble :deep(h3) {
  font-family: var(--ff-head);
  margin: 15px 0 10px 0;
  font-size: 1rem;
}

/* ── Footer / Input ── */
.sidebar-footer { padding: 16px; border-top: 1px solid var(--border); }
.input-container { display: flex; gap: 8px; position: relative; }
.sidebar-input {
  flex: 1;
  background: var(--bg2);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 10px 14px;
  color: var(--text);
  font-family: var(--ff-body);
  font-size: 0.8rem;
  transition: border-color 0.2s;
}
.sidebar-input:focus { outline: none; border-color: var(--companion-color); }
.sidebar-send {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;
}
.sidebar-send:hover:not(:disabled) { transform: translateY(-1px); }
.sidebar-send:disabled { opacity: 0.5; cursor: not-allowed; }

/* Loading dots */
.loading-bubble { display: flex; gap: 5px; align-items: center; width: 60px; justify-content: center; }
.dot {
  width: 4px; height: 4px; border-radius: 50%;
  background: var(--companion-color);
  animation: dotBounce 1.2s infinite ease-in-out;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes dotBounce {
  0%, 80%, 100% { transform: scale(0.7); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* ── Mobile ── */
@media (max-width: 768px) {
  .hero-ai-sidebar.is-expanded { width: 100%; }
}
</style>

