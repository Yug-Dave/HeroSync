<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  isOpen:      Boolean,
  editMode:    Boolean,
  initialData: Object
});

const emit = defineEmits(['close', 'save']);

const tiers = [
  { name: 'Common', xp: 60, color: 'var(--tier-common)' },
  { name: 'Elite',  xp: 100, color: 'var(--tier-rare)' },
  { name: 'Epic',   xp: 150, color: 'var(--tier-epic)' },
];

const form = ref({ name: '', description: '', xpValue: 60 });

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    form.value = props.editMode && props.initialData
      ? { ...props.initialData }
      : { name: '', description: '', xpValue: 60 };
  }
});

const handleSubmit = () => {
  if (!form.value.name.trim()) return alert('Quest name is required!');
  emit('save', form.value);
};
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="isOpen" class="modal-backdrop" @click.self="$emit('close')">
        <div class="modal-panel">
          <!-- Header -->
          <div class="modal-header">
            <div class="modal-title-group">
              <span class="modal-icon">{{ editMode ? '✏️' : '⚔️' }}</span>
              <h3 class="modal-title">{{ editMode ? 'Edit Quest' : 'New Quest' }}</h3>
            </div>
            <button class="close-btn" @click="$emit('close')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <!-- Body -->
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Quest Name</label>
              <input
                v-model="form.name"
                class="form-input"
                placeholder="e.g. Morning Run"
                autofocus
              />
            </div>
            <div class="form-group">
              <label class="form-label">Description <span class="optional">(Optional)</span></label>
              <input
                v-model="form.description"
                class="form-input"
                placeholder="e.g. 30 mins around the park"
              />
            </div>
            <!-- Tier Selector -->
            <div class="form-group">
              <label class="form-label">Quest Tier</label>
              <div class="tier-selector">
                <button
                  v-for="t in tiers" :key="t.name"
                  type="button"
                  class="tier-btn"
                  :class="{ active: form.xpValue === t.xp }"
                  :style="{ '--t-color': t.color }"
                  @click="form.xpValue = t.xp"
                >
                  {{ t.name }}
                </button>
              </div>
            </div>

            <!-- XP preview -->
            <div class="xp-preview">
              <span class="xp-label">Reward per completion</span>
              <span class="xp-amount">
                +{{ form.xpValue }} XP
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="xp-icon"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
              </span>
            </div>
          </div>

          <!-- Footer -->
          <div class="modal-footer">
            <button class="btn-cancel" @click="$emit('close')">Cancel</button>
            <button class="btn-save" @click="handleSubmit">
              {{ editMode ? 'Save Changes' : 'Create Quest' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.modal-backdrop {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.65); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999;
}
.modal-panel {
  background: var(--card2, #1a2436);
  border: 1px solid rgba(255,255,255,.1);
  border-radius: 20px; width: 90%; max-width: 440px;
  box-shadow: 0 24px 50px rgba(0,0,0,.6);
  overflow: hidden;
}

/* Header */
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 22px 0;
}
.modal-title-group { display: flex; align-items: center; gap: 10px; }
.modal-icon { font-size: 22px; }
.modal-title {
  margin: 0;
  font-family: var(--ff-head); font-size: 1.2rem; font-weight: 700; color: #fff;
}
.close-btn {
  width: 30px; height: 30px; border-radius: 8px;
  background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.08);
  color: var(--muted); cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all .15s;
}
.close-btn:hover { background: rgba(255,255,255,.12); color: #fff; }

/* Body */
.modal-body { padding: 20px 22px; display: flex; flex-direction: column; gap: 14px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label {
  font-size: .8rem; font-weight: 700; color: var(--muted);
  text-transform: uppercase; letter-spacing: .4px;
}
.optional { font-weight: 400; text-transform: none; color: #4b6279; }
.form-input {
  width: 100%; padding: 11px 14px;
  background: rgba(255,255,255,.04);
  border: 1px solid rgba(255,255,255,.1);
  border-radius: 10px; color: var(--text);
  font-family: var(--ff-body); font-size: .95rem;
  transition: border-color .2s, box-shadow .2s;
}
.form-input:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(0,229,160,.15);
}
.form-input::placeholder { color: #4b6279; }
.form-input:disabled { opacity: .6; }

/* Tier Selector */
.tier-selector { display: flex; gap: 8px; }
.tier-btn {
  flex: 1; padding: 10px; border-radius: 10px;
  background: rgba(255,255,255,.03); border: 1px solid var(--border);
  color: var(--muted); font-family: var(--ff-head); font-weight: 700;
  font-size: .85rem; cursor: pointer; transition: all .2s;
  text-transform: uppercase; letter-spacing: .5px;
}
.tier-btn:hover { background: rgba(255,255,255,.06); color: var(--text); }
.tier-btn.active {
  background: rgba(var(--t-color), .1);
  border-color: var(--t-color);
  color: var(--t-color);
  box-shadow: 0 0 12px var(--t-color);
  transform: translateY(-1px);
}
/* Color overrides for variables if they are hex */
.tier-btn.active[style*="common"] { border-color: #8fa8c2; color: #8fa8c2; box-shadow: 0 0 12px rgba(143,168,194,.3); }
.tier-btn.active[style*="rare"]   { border-color: #3b82f6; color: #3b82f6; box-shadow: 0 0 12px rgba(59,130,246,.3); }
.tier-btn.active[style*="epic"]   { border-color: #8b5cf6; color: #8b5cf6; box-shadow: 0 0 12px rgba(139,92,246,.3); }

/* XP preview */
.xp-preview {
  display: flex; align-items: center; justify-content: space-between;
  background: rgba(0,229,160,.08); border: 1px solid rgba(0,229,160,.2);
  border-radius: 12px; padding: 12px 16px;
  margin-top: 8px;
}
.xp-label  { font-size: .85rem; color: #8fa8c2; font-weight: 600; }
.xp-amount { 
  display: flex; align-items: center; gap: 6px;
  font-family: var(--ff-head); font-size: 1.1rem; font-weight: 800; color: var(--accent);
  text-shadow: 0 0 10px rgba(0,229,160,0.3);
}
.xp-icon { color: #f97316; filter: drop-shadow(0 0 4px rgba(249,115,22,0.4)); }

/* Footer */
.modal-footer {
  display: flex; gap: 10px;
  padding: 0 22px 22px;
}
.btn-cancel, .btn-save {
  flex: 1; padding: 12px; border-radius: 10px;
  font-family: var(--ff-head); font-size: 1rem; font-weight: 700;
  cursor: pointer; transition: all .2s; letter-spacing: .3px;
}
.btn-cancel {
  background: transparent; border: 1px solid rgba(255,255,255,.12); color: var(--text);
}
.btn-cancel:hover { background: rgba(255,255,255,.06); }
.btn-save {
  background: linear-gradient(135deg, var(--accent), #1f9e71);
  border: none; color: #062015;
}
.btn-save:hover { box-shadow: 0 4px 16px rgba(0,229,160,.35); transform: translateY(-1px); }

/* Transition */
.modal-fade-enter-active { transition: all .25s cubic-bezier(.34,1.56,.64,1); }
.modal-fade-leave-active { transition: all .2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .modal-panel { transform: scale(.95) translateY(10px); }
.modal-fade-leave-to .modal-panel   { transform: scale(.96); }
</style>
