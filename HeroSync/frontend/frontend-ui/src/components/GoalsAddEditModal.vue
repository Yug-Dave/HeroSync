<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  isOpen:      { type: Boolean, default: false },
  mode:        { type: String,  default: 'create' },
  habits:      { type: Array,   default: () => [] },
  initialData: { type: Object,  default: null },
  saving:      { type: Boolean, default: false },
  error:       { type: String,  default: '' },
});
const emit = defineEmits(['close', 'save']);

const form = ref({ 
  title: '', 
  description: '', 
  habitId: '', 
  targetCount: '', 
  deadline: '',
  isBoss: false,
  bossType: 'DRAGON'
});

const isCreate = computed(() => props.mode === 'create');
const isEdit   = computed(() => props.mode === 'edit');
const isRetry  = computed(() => props.mode === 'retry');
const isHabitLinked = computed(() => String(form.value.habitId || '').trim() !== '');

const currentTarget  = computed(() => Number(props.initialData?.targetCount ?? 0));
const currentProgress= computed(() => Number(props.initialData?.progressCount ?? 0));
const retryMinTarget = computed(() => Math.max(currentTarget.value + 1, currentProgress.value + 1));

const habitNameMap = computed(() => {
  const map = new Map();
  for (const h of props.habits || []) map.set(String(h.id), h.name);
  return map;
});
const habitLabel = computed(() => {
  const id = props.initialData?.habitId;
  if (id == null) return 'Solo Goal';
  return habitNameMap.value.get(String(id)) || 'Unknown Habit';
});

const modeIcon = computed(() => props.mode);
const modeTitle = computed(() => ({ create: 'New Goal', edit: 'Edit Goal', retry: 'Retry Goal' }[props.mode] || 'Goal'));
const modeHint = computed(() => ({
  create: 'Habit & target cannot be changed after creation.',
  edit:   'Habit association and target are locked. Delete and recreate to change habit.',
  retry:  'Target can only be increased. Title is locked. Progress resumes from where you left off.'
}[props.mode] || ''));

watch(() => props.isOpen, (open) => {
  if (!open) return;
  if (isCreate.value) { 
    form.value = { title: '', description: '', habitId: '', targetCount: '', deadline: '', isBoss: false, bossType: 'DRAGON' }; 
    return; 
  }
  const g = props.initialData || {};
  form.value = {
    title:       g.title ?? '',
    description: g.description ?? '',
    habitId:     g.habitId != null ? String(g.habitId) : '',
    targetCount: g.targetCount != null ? String(g.targetCount) : '',
    deadline:    g.deadline ?? '',
    isBoss:      g.isBoss ?? false,
    bossType:    g.bossType ?? 'DRAGON'
  };
});

function close() { if (props.saving) return; emit('close'); }

function submit() {
  if (isCreate.value) {
    const title = (form.value.title || '').trim();
    if (!title) return alert('Title is required.');
    const payload = {
      title, description: (form.value.description || '').trim() || null,
      deadline: (form.value.deadline || '').trim() || null, habitId: null, targetCount: null,
      isBoss: form.value.isBoss, bossType: form.value.isBoss ? form.value.bossType : null
    };
    if (isHabitLinked.value) {
      const hid = Number(form.value.habitId);
      const tgt = Number(form.value.targetCount);
      if (!Number.isFinite(hid) || hid <= 0) return alert('Select a valid habit.');
      if (!Number.isFinite(tgt) || tgt <= 0) return alert('Target must be a positive number.');
      payload.habitId = hid; payload.targetCount = tgt;
    }
    return emit('save', payload);
  }
  if (isEdit.value) {
    const title = (form.value.title || '').trim();
    if (!title) return alert('Title is required.');
    return emit('save', { title, description: (form.value.description || '').trim() || null, deadline: (form.value.deadline || '').trim() || null });
  }
  if (isRetry.value) {
    const tgt = Number(form.value.targetCount);
    if (!Number.isFinite(tgt)) return alert('Target must be a number.');
    if (tgt < retryMinTarget.value) return alert(`Target must be at least ${retryMinTarget.value}.`);
    return emit('save', { description: (form.value.description || '').trim() || null, targetCount: tgt, deadline: (form.value.deadline || '').trim() || null });
  }
}
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="isOpen" class="modal-backdrop" @click.self="close">
        <div class="modal-panel">
          <!-- Header -->
          <div class="modal-header">
            <div class="modal-title-group">
              <span class="modal-icon-emoji">
                <svg v-if="modeIcon === 'create'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--accent)"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
                <svg v-else-if="modeIcon === 'edit'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--gold)"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>
                <svg v-else-if="modeIcon === 'retry'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:var(--warning)"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
                <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
              </span>
              <h3 class="modal-title">{{ modeTitle }}</h3>
            </div>
            <button class="close-btn" @click="close" :disabled="saving">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <!-- Hint -->
          <div class="hint-box">{{ modeHint }}</div>

          <!-- Error -->
          <div v-if="error" class="error-box">{{ error }}</div>

          <!-- Form -->
          <div class="modal-body">
            <!-- Title -->
            <div class="form-group">
              <label class="form-label">Title</label>
              <input v-model="form.title" class="form-input" placeholder="Goal title" :disabled="saving || isRetry" />
            </div>

            <!-- Description -->
            <div class="form-group">
              <label class="form-label">Description <span class="optional">(Optional)</span></label>
              <input v-model="form.description" class="form-input" placeholder="What does success look like?" :disabled="saving" />
            </div>

            <!-- Habit select -->
            <div class="form-group">
              <label class="form-label">Linked Quest</label>
              <select v-if="isCreate" v-model="form.habitId" class="form-input form-select" :disabled="saving">
                <option value="">No (Solo Goal)</option>
                <option v-for="h in habits" :key="h.id" :value="String(h.id)">{{ h.name }}</option>
              </select>
              <input v-else class="form-input" :value="habitLabel" disabled />
            </div>

            <!-- Target count -->
            <div class="form-group" v-if="isCreate ? isHabitLinked : (initialData && initialData.habitId != null)">
              <label class="form-label">
                Target Count
                <span class="optional" v-if="isRetry">(min {{ retryMinTarget }})</span>
              </label>
              <input
                v-if="isCreate"    v-model="form.targetCount" class="form-input" type="number" min="1" placeholder="e.g. 30" :disabled="saving" />
              <input
                v-else-if="isEdit" v-model="form.targetCount" class="form-input" type="number" disabled />
              <input
                v-else             v-model="form.targetCount" class="form-input" type="number" :min="retryMinTarget" :disabled="saving" />
            </div>

            <!-- Deadline -->
            <div class="form-group">
              <label class="form-label">Deadline</label>
              <input v-model="form.deadline" class="form-input" type="date" :disabled="saving || isCreate" />
              <p v-if="isCreate" class="field-note">Deadline can be set after creation by editing the goal.</p>
            </div>

            <!-- Boss Summoning -->
            <div class="form-group boss-toggle-group" v-if="isCreate">
              <div class="boss-header">
                <label class="form-label">Summon Boss</label>
                <label class="switch">
                  <input type="checkbox" v-model="form.isBoss" />
                  <span class="slider"></span>
                </label>
              </div>
              <p class="field-note">Bosses are epic missions with unique UI and higher rewards.</p>
              
              <transition name="fade">
                <div v-if="form.isBoss" class="boss-selector">
                  <label class="form-label">Select Entity</label>
                  <div class="boss-options">
                    <button 
                      v-for="b in ['DRAGON', 'GOLEM', 'PHANTOM']" 
                      :key="b"
                      class="boss-opt-btn"
                      :class="{ active: form.bossType === b }"
                      @click="form.bossType = b"
                    >
                      {{ b }}
                    </button>
                  </div>
                </div>
              </transition>
            </div>
          </div>

          <!-- Footer -->
          <div class="modal-footer">
            <button class="btn-cancel" @click="close" :disabled="saving">Cancel</button>
            <button class="btn-save" @click="submit" :disabled="saving">
              {{ saving ? 'Saving…' : isCreate ? 'Create Goal' : isEdit ? 'Save Changes' : 'Retry Goal' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.modal-backdrop {
  position: fixed; inset: 0; background: rgba(0,0,0,.65); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 9999;
}
.modal-panel {
  background: var(--card2, #1a2436); border: 1px solid rgba(255,255,255,.1);
  border-radius: 20px; width: 90%; max-width: 480px;
  max-height: 90vh; overflow-y: auto;
  box-shadow: 0 24px 50px rgba(0,0,0,.6);
}
.modal-header {
  display: flex; align-items: center; justify-content: space-between; padding: 20px 22px 0;
}
.modal-title-group { display: flex; align-items: center; gap: 10px; }
.modal-icon-emoji  { font-size: 22px; }
.modal-title       { margin: 0; font-family: var(--ff-head); font-size: 1.2rem; font-weight: 700; color: #fff; }
.close-btn {
  width: 30px; height: 30px; border-radius: 8px;
  background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.08);
  color: var(--muted); cursor: pointer;
  display: flex; align-items: center; justify-content: center; transition: all .15s;
}
.close-btn:hover { background: rgba(255,255,255,.12); color: #fff; }

.hint-box {
  margin: 12px 22px 0; padding: 10px 14px; border-radius: 10px;
  background: rgba(255,255,255,.04); border: 1px solid rgba(255,255,255,.08);
  font-size: .78rem; color: var(--muted); line-height: 1.5;
}
.error-box {
  margin: 10px 22px 0; padding: 10px 14px; border-radius: 10px;
  background: rgba(239,68,68,.08); border: 1px solid rgba(239,68,68,.2);
  color: #fca5a5; font-size: .85rem;
}

.modal-body { padding: 16px 22px; display: flex; flex-direction: column; gap: 14px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: .78rem; font-weight: 700; color: var(--muted); text-transform: uppercase; letter-spacing: .4px; display: flex; gap: 6px; align-items: center; }
.optional   { font-weight: 400; text-transform: none; color: #4b6279; font-size: .72rem; }
.form-input {
  width: 100%; padding: 11px 14px;
  background: rgba(255,255,255,.04); border: 1px solid rgba(255,255,255,.1);
  border-radius: 10px; color: var(--text); font-family: var(--ff-body); font-size: .9rem;
  transition: border-color .2s, box-shadow .2s;
}
.form-input:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 3px rgba(0,229,160,.15); }
.form-input::placeholder { color: #4b6279; }
.form-input:disabled { opacity: .5; }
.form-select { cursor: pointer; }
.form-select option { background: #1a2436; }
.field-note { font-size: .72rem; color: #4b6279; margin: 4px 0 0; }

.modal-footer { display: flex; gap: 10px; padding: 4px 22px 22px; }
.btn-cancel, .btn-save {
  flex: 1; padding: 12px; border-radius: 10px;
  font-family: var(--ff-head); font-size: 1rem; font-weight: 700;
  cursor: pointer; transition: all .2s; letter-spacing: .3px;
}
.btn-cancel { background: transparent; border: 1px solid rgba(255,255,255,.12); color: var(--text); }
.btn-cancel:hover { background: rgba(255,255,255,.06); }
.btn-save { background: linear-gradient(135deg, var(--accent), #1f9e71); border: none; color: #062015; }
.btn-save:hover:not(:disabled) { box-shadow: 0 4px 16px rgba(0,229,160,.35); transform: translateY(-1px); }
.btn-save:disabled { opacity: .5; cursor: not-allowed; }

.modal-fade-enter-active { transition: all .25s cubic-bezier(.34,1.56,.64,1); }
.modal-fade-leave-active { transition: all .2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .modal-panel { transform: scale(.95) translateY(10px); }

/* Boss Styles */
.boss-toggle-group { margin-top: 10px; padding-top: 15px; border-top: 1px dashed rgba(255,255,255,.1); }
.boss-header { display: flex; justify-content: space-between; align-items: center; }
.boss-selector { margin-top: 15px; }
.boss-options { display: flex; gap: 8px; margin-top: 8px; }
.boss-opt-btn {
  flex: 1; padding: 8px; border-radius: 8px; background: rgba(0,0,0,.2); border: 1px solid rgba(255,255,255,.1);
  color: var(--muted); font-size: .75rem; font-family: var(--ff-head); font-weight: 700; cursor: pointer;
}
.boss-opt-btn.active { border-color: var(--danger); color: var(--danger); background: rgba(239,68,68,.1); }

/* Switch for Boss */
.switch { position: relative; display: inline-block; width: 36px; height: 20px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; inset: 0; background: rgba(255,255,255,.1); transition: .4s; border-radius: 20px; }
.slider:before { position: absolute; content: ""; height: 14px; width: 14px; left: 3px; bottom: 3px; background: #fff; transition: .4s; border-radius: 50%; }
input:checked + .slider { background: var(--danger); }
input:checked + .slider:before { transform: translateX(16px); }

.fade-enter-active, .fade-leave-active { transition: opacity .2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
