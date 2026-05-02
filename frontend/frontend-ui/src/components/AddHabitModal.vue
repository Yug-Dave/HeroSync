<script setup>
import { ref, watch, computed } from 'vue';

const props = defineProps({
  isOpen:      Boolean,
  editMode:    Boolean,
  initialData: Object
});
const emit = defineEmits(['close', 'save']);

// ── Difficulty tiers (match backend QuestClassifierService) ──────────────
const TIERS = {
  EASY:     { label: 'Easy',     minXp: 30,  maxXp: 60,  mid: 45,  color: '#22c55e', desc: 'Low effort, routine' },
  STANDARD: { label: 'Standard', minXp: 80,  maxXp: 120, mid: 100, color: '#3b82f6', desc: 'Moderate, consistent' },
  HARD:     { label: 'Hard',     minXp: 150, maxXp: 200, mid: 175, color: '#8b5cf6', desc: 'High effort, discipline' },
  ELITE:    { label: 'Elite',    minXp: 250, maxXp: 300, mid: 275, color: '#f59e0b', desc: 'Extreme commitment' },
};

const form      = ref({ name: '', description: '', xpValue: 100 });
const classifying    = ref(false);
const classified     = ref(false);   // true once AI has spoken
const aiTier         = ref(null);    // EASY | STANDARD | HARD | ELITE
const aiReasoning    = ref('');
const overrideWarning = ref('');     // shown when user picks tier above AI suggestion
const classifyDebounce = ref(null);

const activeTierKey = ref('STANDARD');
const activeTier    = computed(() => TIERS[activeTierKey.value]);

// ── Watch modal open ────────────────────────────────────────────────────────
watch(() => props.isOpen, (val) => {
  if (val) {
    if (props.editMode && props.initialData) {
      form.value = { ...props.initialData };
      // Find which tier the existing xpValue falls in
      activeTierKey.value = inferTierKey(props.initialData.xpValue);
    } else {
      form.value = { name: '', description: '', xpValue: 100 };
      activeTierKey.value = 'STANDARD';
    }
    classified.value  = false;
    aiTier.value      = null;
    aiReasoning.value = '';
    overrideWarning.value = '';
  }
});

function inferTierKey(xp) {
  if (xp <= 60)  return 'EASY';
  if (xp <= 120) return 'STANDARD';
  if (xp <= 200) return 'HARD';
  return 'ELITE';
}

// ── Auto-classify on name/desc change ──────────────────────────────────────
watch([() => form.value.name, () => form.value.description], () => {
  if (!form.value.name.trim() || form.value.name.trim().length < 3) return;
  clearTimeout(classifyDebounce.value);
  classifyDebounce.value = setTimeout(runClassify, 900); // 900ms debounce
});

async function runClassify() {
  if (!form.value.name.trim()) return;
  classifying.value = true;
  try {
    const res = await fetch('/api/ai/classify-quest', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ name: form.value.name, description: form.value.description || '' })
    });
    if (!res.ok) throw new Error('classify failed');
    const data = await res.json();
    aiTier.value      = data.difficulty;  // EASY | STANDARD | HARD | ELITE
    aiReasoning.value = data.reasoning;
    classified.value  = true;
    // Auto-set tier and XP to AI suggestion (soft suggestion — user can change)
    selectTier(data.difficulty, true);
    form.value.xpValue = data.suggestedXp;
  } catch (e) {
    // Silent fallback — keep current selection
    classified.value = false;
  } finally {
    classifying.value = false;
  }
}

// ── Tier selection ──────────────────────────────────────────────────────────
function selectTier(tierKey, fromAI = false) {
  const tier = TIERS[tierKey];
  if (!tier) return;
  activeTierKey.value = tierKey;
  form.value.xpValue  = tier.mid;
  if (!fromAI && aiTier.value) {
    // Show warning only if user manually picks higher than AI's suggestion
    const aiIdx   = tierOrder(aiTier.value);
    const userIdx = tierOrder(tierKey);
    overrideWarning.value = userIdx > aiIdx
      ? `SYNC recommends ${TIERS[aiTier.value].label} for this habit. You can override — just be honest with yourself.`
      : '';
  } else {
    overrideWarning.value = '';
  }
}

function tierOrder(key) {
  return { EASY: 0, STANDARD: 1, HARD: 2, ELITE: 3 }[key] ?? 1;
}

// ── Submit ──────────────────────────────────────────────────────────────────
const handleSubmit = () => {
  if (!form.value.name.trim()) return alert('Quest name is required!');
  emit('save', { ...form.value, xpValue: form.value.xpValue });
};
</script>

<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="isOpen" class="modal-backdrop" @click.self="$emit('close')">
        <div class="modal-panel">

          <!-- Header -->
          <div class="modal-header">
            <div class="modal-title-group">
              <div class="modal-icon-wrap">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M14.5 17.5L3 6V3h3l11.5 11.5"/><path d="M13 19l6-6"/></svg>
              </div>
              <h3 class="modal-title">{{ editMode ? 'Edit Quest' : 'New Quest' }}</h3>
            </div>
            <button class="close-btn" @click="$emit('close')">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <!-- Body -->
          <div class="modal-body">
            <!-- Quest Name -->
            <div class="form-group">
              <label class="form-label">Quest Name</label>
              <input v-model="form.name" class="form-input" placeholder="e.g. Morning Run" autofocus />
            </div>

            <!-- Description -->
            <div class="form-group">
              <label class="form-label">Description <span class="optional">(Optional — helps AI classify)</span></label>
              <input v-model="form.description" class="form-input" placeholder="e.g. 30 mins around the park" />
            </div>

            <!-- AI Difficulty Classification -->
            <div class="classify-section">
              <div class="classify-header">
                <label class="form-label">Difficulty & XP</label>
                <div class="classify-status" v-if="classifying">
                  <span class="classify-spinner"/>
                  <span class="classify-text">Analyzing...</span>
                </div>
                <div class="ai-badge" v-else-if="classified">
                  <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
                  AI Classified
                </div>
              </div>

              <!-- Tier buttons -->
              <div class="tier-selector">
                <button
                  v-for="(tier, key) in TIERS" :key="key"
                  type="button"
                  class="tier-btn"
                  :class="{ active: activeTierKey === key, 'ai-pick': aiTier === key }"
                  :style="{ '--t-color': tier.color }"
                  @click="selectTier(key)"
                >
                  <span class="tier-label">{{ tier.label }}</span>
                  <span class="tier-xp">{{ tier.minXp }}–{{ tier.maxXp }}</span>
                  <span v-if="aiTier === key" class="tier-ai-dot" title="AI Recommendation"/>
                </button>
              </div>

              <!-- AI reasoning -->
              <div class="ai-reasoning" v-if="classified && aiReasoning">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="10" rx="2"/><circle cx="12" cy="5" r="2"/><path d="M12 7v4"/></svg>
                {{ aiReasoning }}
              </div>

              <!-- Override warning -->
              <div class="tier-warning" v-if="overrideWarning">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                {{ overrideWarning }}
              </div>
            </div>

            <!-- XP Preview -->
            <div class="xp-preview" :style="{ '--t-color': activeTier.color }">
              <div class="xp-left">
                <span class="xp-label">Reward per completion</span>
                <span class="xp-desc">{{ activeTier.desc }}</span>
              </div>
              <div class="xp-right">
                <span class="xp-amount">+{{ form.xpValue }}</span>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="modal-footer">
            <button class="btn-cancel" @click="$emit('close')">Cancel</button>
            <button class="btn-save" @click="handleSubmit" :disabled="classifying">
              {{ editMode ? 'Save Changes' : 'Create Quest' }}
            </button>
          </div>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-backdrop {
  position: fixed; inset: 0;
  background: rgba(10,15,29,.88); backdrop-filter: blur(14px);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999;
}
.modal-panel {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 28px; width: 90%; max-width: 460px;
  box-shadow: var(--shadow-lg), 0 0 60px rgba(var(--accent-rgb),.06);
  overflow: hidden;
}

/* Header */
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 24px 24px 0;
}
.modal-title-group { display: flex; align-items: center; gap: 12px; }
.modal-icon-wrap {
  width: 36px; height: 36px; border-radius: 10px;
  background: rgba(var(--accent-rgb),.1);
  border: 1px solid rgba(var(--accent-rgb),.2);
  display: flex; align-items: center; justify-content: center;
  color: var(--accent);
}
.modal-title {
  margin: 0;
  font-family: var(--ff-head); font-size: 1.3rem; font-weight: 800; color: var(--text);
}
.close-btn {
  width: 32px; height: 32px; border-radius: 10px;
  background: var(--card2); border: 1px solid var(--border);
  color: var(--muted); cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all .15s;
}
.close-btn:hover { background: var(--border); color: var(--text); }

/* Body */
.modal-body { padding: 24px; display: flex; flex-direction: column; gap: 18px; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-label {
  font-size: .78rem; font-weight: 700; color: var(--text-dim);
  text-transform: uppercase; letter-spacing: .6px;
}
.optional { font-weight: 400; text-transform: none; color: var(--muted); font-size: .72rem; letter-spacing: 0; }
.form-input {
  width: 100%; padding: 12px 16px;
  background: var(--bg); border: 1px solid var(--border);
  border-radius: 12px; color: var(--text);
  font-family: var(--ff-body); font-size: 1rem;
  transition: border-color .2s;
  box-sizing: border-box;
}
.form-input:focus { outline: none; border-color: var(--accent); background: var(--card2); }
.form-input::placeholder { color: var(--muted); }

/* Classify section */
.classify-section { display: flex; flex-direction: column; gap: 10px; }
.classify-header { display: flex; align-items: center; justify-content: space-between; }
.classify-status { display: flex; align-items: center; gap: 6px; }
.classify-spinner {
  width: 12px; height: 12px; border: 2px solid var(--border);
  border-top-color: var(--accent); border-radius: 50%;
  animation: spin .7s linear infinite; flex-shrink: 0;
}
@keyframes spin { to { transform: rotate(360deg); } }
.classify-text { font-size: .72rem; color: var(--muted); font-family: var(--ff-head); letter-spacing: .5px; }
.ai-badge {
  display: flex; align-items: center; gap: 5px;
  background: rgba(var(--accent-rgb),.1);
  border: 1px solid rgba(var(--accent-rgb),.25);
  color: var(--accent);
  font-family: var(--ff-head); font-size: .65rem; font-weight: 800;
  letter-spacing: 1px; padding: 3px 9px; border-radius: 20px;
}

/* Tier buttons */
.tier-selector { display: flex; gap: 8px; }
.tier-btn {
  flex: 1; padding: 10px 6px;
  border-radius: 12px;
  background: var(--card2); border: 1px solid var(--border);
  cursor: pointer; transition: all .2s;
  display: flex; flex-direction: column; align-items: center; gap: 3px;
  position: relative;
}
.tier-btn:hover { border-color: var(--t-color); background: rgba(var(--t-color), .05); }
.tier-btn.active {
  background: rgba(var(--t-color), .12);
  border-color: var(--t-color);
  box-shadow: 0 0 14px color-mix(in srgb, var(--t-color) 30%, transparent);
  transform: translateY(-2px);
}
.tier-btn.ai-pick::after {
  content: 'AI';
  position: absolute; top: -7px; right: 4px;
  font-family: var(--ff-head); font-size: .58rem; font-weight: 900;
  background: var(--t-color); color: #000; padding: 1px 5px; border-radius: 20px;
  letter-spacing: .5px;
}
.tier-label {
  font-family: var(--ff-head); font-size: .82rem; font-weight: 800;
  color: var(--text); text-transform: uppercase; letter-spacing: .5px;
}
.tier-btn.active .tier-label { color: var(--t-color); }
.tier-xp { font-size: .65rem; color: var(--muted); font-family: var(--ff-head); font-weight: 600; }
.tier-btn.active .tier-xp { color: var(--t-color); opacity: .8; }
.tier-ai-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: var(--t-color); margin-top: 2px;
  box-shadow: 0 0 6px var(--t-color);
}

/* AI reasoning */
.ai-reasoning {
  display: flex; align-items: flex-start; gap: 7px;
  background: rgba(var(--accent-rgb),.05);
  border: 1px solid rgba(var(--accent-rgb),.12);
  border-radius: 10px; padding: 10px 12px;
  font-size: .78rem; color: var(--text-dim); line-height: 1.45;
}
.ai-reasoning svg { flex-shrink: 0; margin-top: 1px; color: var(--accent); }

/* Override warning */
.tier-warning {
  display: flex; align-items: flex-start; gap: 7px;
  background: rgba(245,158,11,.06);
  border: 1px solid rgba(245,158,11,.2);
  border-radius: 10px; padding: 10px 12px;
  font-size: .78rem; color: #d97706; line-height: 1.4;
}
.tier-warning svg { flex-shrink: 0; margin-top: 1px; }

/* XP Preview */
.xp-preview {
  display: flex; align-items: center; justify-content: space-between;
  background: color-mix(in srgb, var(--t-color, var(--accent)) 8%, var(--card2));
  border: 1px solid color-mix(in srgb, var(--t-color, var(--accent)) 25%, var(--border));
  border-radius: 16px; padding: 16px 20px;
  transition: all .3s;
}
.xp-left { display: flex; flex-direction: column; gap: 2px; }
.xp-label { font-size: .8rem; color: var(--muted); font-weight: 600; }
.xp-desc  { font-size: .72rem; color: var(--muted); opacity: .7; }
.xp-right {
  display: flex; align-items: center; gap: 6px;
  font-family: var(--ff-head); font-size: 1.4rem; font-weight: 900;
  color: var(--t-color, var(--accent));
}

/* Footer */
.modal-footer { display: flex; gap: 12px; padding: 0 24px 24px; }
.btn-cancel, .btn-save {
  flex: 1; padding: 14px; border-radius: 14px;
  font-family: var(--ff-head); font-size: 1rem; font-weight: 700;
  cursor: pointer; transition: all .2s; letter-spacing: .5px;
}
.btn-cancel { background: var(--card2); border: 1px solid var(--border); color: var(--text); }
.btn-cancel:hover { background: var(--border); }
.btn-save {
  background: var(--accent); border: none; color: #000;
  box-shadow: 0 4px 16px rgba(var(--accent-rgb),.3);
}
.btn-save:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 22px rgba(var(--accent-rgb),.4); }
.btn-save:disabled { opacity: .5; cursor: not-allowed; transform: none; }
:global(.theme-light) .btn-save { color: #fff; }

/* Transition */
.modal-fade-enter-active { transition: all .3s cubic-bezier(.34,1.56,.64,1); }
.modal-fade-leave-active { transition: all .2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .modal-panel { transform: scale(.92) translateY(20px); }
</style>
