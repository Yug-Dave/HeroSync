<script setup>
defineProps({
  show: Boolean,
  title: String,
  message: String,
  confirmText: { type: String, default: 'Confirm' },
  cancelText: { type: String, default: 'Cancel' },
  accentColor: { type: String, default: 'var(--accent)' }
});

const emit = defineEmits(['confirm', 'cancel']);
</script>

<template>
  <Transition name="modal">
    <div v-if="show" class="modal-backdrop" @click="emit('cancel')">
      <div class="modal-card" @click.stop :style="{ '--modal-accent': accentColor }">
        <div class="modal-glow"></div>
        
        <div class="modal-header">
          <div class="modal-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </div>
          <h3>{{ title }}</h3>
        </div>

        <div class="modal-body">
          <p>{{ message }}</p>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="emit('cancel')">{{ cancelText }}</button>
          <button class="btn-confirm" @click="emit('confirm')">
            {{ confirmText }}
            <span class="btn-shimmer"></span>
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  z-index: 2000;
  display: grid;
  place-items: center;
  padding: 20px;
}

.modal-card {
  width: min(450px, 100%);
  background: var(--bg2);
  border: 1px solid var(--border2);
  border-radius: 24px;
  padding: 32px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.6);
}

.modal-glow {
  position: absolute;
  top: -50px; left: -50px;
  width: 150px; height: 150px;
  background: radial-gradient(circle, var(--modal-accent) 0%, transparent 70%);
  opacity: 0.15;
  pointer-events: none;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.modal-icon-wrap {
  width: 48px; height: 48px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--border);
  display: grid; place-items: center;
  color: var(--modal-accent);
}

.modal-header h3 {
  font-family: var(--ff-display);
  font-size: 1.25rem;
  margin: 0;
  color: var(--text);
  letter-spacing: 0.5px;
}

.modal-body p {
  color: var(--text-dim);
  line-height: 1.6;
  margin: 0 0 32px;
  font-size: 1rem;
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 12px 24px;
  border-radius: 12px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--muted);
  font-family: var(--ff-head);
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text);
}

.btn-confirm {
  position: relative;
  padding: 12px 28px;
  border-radius: 12px;
  border: none;
  background: var(--modal-accent);
  color: #000;
  font-family: var(--ff-head);
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px -5px var(--modal-accent);
}

.btn-shimmer {
  position: absolute;
  top: 0; left: -100%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  30% { left: 150%; }
  100% { left: 150%; }
}

/* Theme Support */
:deep(.theme-light) .modal-card {
  background: #ffffff;
  border-color: #e2e8f0;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1);
}

:deep(.theme-light) .modal-icon-wrap {
  background: rgba(var(--accent-rgb), 0.05);
}

:deep(.theme-light) .btn-confirm {
  color: #fff;
}

/* Transitions */
.modal-enter-active, .modal-leave-active { transition: opacity 0.3s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }

.modal-enter-active .modal-card { animation: zoomIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.modal-leave-active .modal-card { animation: zoomIn 0.2s reverse ease-in; }

@keyframes zoomIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
</style>
