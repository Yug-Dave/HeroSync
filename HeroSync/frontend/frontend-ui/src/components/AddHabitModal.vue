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
  position: fixed; inset: 0;
  background: rgba(10, 15, 29, 0.85); backdrop-filter: blur(12px);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999;
}
.modal-panel {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 28px; width: 90%; max-width: 440px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

/* Header */
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 24px 24px 0;
}
.modal-title-group { display: flex; align-items: center; gap: 12px; }
.modal-icon { font-size: 24px; }
.modal-title {
  margin: 0;
  font-family: var(--ff-head); font-size: 1.4rem; font-weight: 800; color: var(--text);
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
.modal-body { padding: 24px; display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-label {
  font-size: .8rem; font-weight: 700; color: var(--text-dim);
  text-transform: uppercase; letter-spacing: .6px;
}
.optional { font-weight: 400; text-transform: none; color: var(--muted); font-size: 0.75rem; }
.form-input {
  width: 100%; padding: 12px 16px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 12px; color: var(--text);
  font-family: var(--ff-body); font-size: 1rem;
  transition: border-color .2s;
}
.form-input:focus {
  outline: none;
  border-color: var(--accent);
  background: var(--card2);
}
.form-input::placeholder { color: var(--muted); }
.form-input:disabled { opacity: .5; }

/* Tier Selector */
.tier-selector { display: flex; gap: 10px; }
.tier-btn {
  flex: 1; padding: 12px; border-radius: 12px;
  background: var(--card2); border: 1px solid var(--border);
  color: var(--muted); font-family: var(--ff-head); font-weight: 700;
  font-size: .9rem; cursor: pointer; transition: all 0.2s;
  text-transform: uppercase; letter-spacing: .5px;
}
.tier-btn:hover { background: var(--border); color: var(--text); }
.tier-btn.active {
  background: rgba(var(--accent-rgb), 0.1);
  border-color: var(--accent);
  color: var(--accent);
  box-shadow: 0 0 15px rgba(var(--accent-rgb), 0.2);
  transform: translateY(-2px);
}

/* XP preview */
.xp-preview {
  display: flex; align-items: center; justify-content: space-between;
  background: var(--card2); border: 1px solid var(--border);
  border-radius: 16px; padding: 16px;
  margin-top: 8px;
}
.xp-label  { font-size: .9rem; color: var(--muted); font-weight: 600; }
.xp-amount { 
  display: flex; align-items: center; gap: 8px;
  font-family: var(--ff-head); font-size: 1.3rem; font-weight: 800; color: var(--accent);
}
.xp-icon { color: var(--accent); }

/* Footer */
.modal-footer {
  display: flex; gap: 12px;
  padding: 0 24px 24px;
}
.btn-cancel, .btn-save {
  flex: 1; padding: 14px; border-radius: 14px;
  font-family: var(--ff-head); font-size: 1.1rem; font-weight: 700;
  cursor: pointer; transition: all 0.2s; letter-spacing: .5px;
}
.btn-cancel {
  background: var(--card2); border: 1px solid var(--border); color: var(--text);
}
.btn-cancel:hover { background: var(--border); }
.btn-save {
  background: var(--accent) !important;
  border: none !important; color: #fff !important;
}
.btn-save:hover { background: var(--accent-2) !important; transform: translateY(-3px); box-shadow: 0 8px 20px rgba(var(--accent-rgb), 0.3) !important; }

/* Transition */
.modal-fade-enter-active { transition: all .3s cubic-bezier(.34,1.56,.64,1); }
.modal-fade-leave-active { transition: all .2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .modal-panel { transform: scale(.9) translateY(20px); }
</style>
