<script setup>
import { computed } from 'vue';
import { heroConfig } from '../utils/config';

const props = defineProps({
  size: { type: String, default: 'medium' } // small, medium, large
});

const isDark = computed(() => heroConfig.theme === 'dark');

const logoClasses = computed(() => ({
  'app-logo': true,
  [`logo-${props.size}`]: true,
  'logo-dark': isDark.value,
  'logo-light': !isDark.value
}));
</script>

<template>
  <div :class="logoClasses">
    <div class="logo-icon-box">
      <svg viewBox="0 0 24 24" fill="currentColor" class="bolt-svg">
        <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
      </svg>
    </div>
    <div class="logo-text">
      <span class="text-hero">Hero</span><span class="text-sync">Sync</span>
    </div>
  </div>
</template>

<style scoped>
.app-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  user-select: none;
  cursor: pointer;
}

.logo-icon-box {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.bolt-svg {
  width: 60%;
  height: 60%;
}

.logo-text {
  font-family: var(--ff-display);
  font-weight: 800;
  letter-spacing: 1px;
  line-height: 1;
}

/* --- Sizes --- */
.logo-small { gap: 8px; }
.logo-small .logo-icon-box { width: 32px; height: 32px; border-radius: 8px; }
.logo-small .logo-text { font-size: 1rem; }

.logo-medium { gap: 12px; }
.logo-medium .logo-icon-box { width: 42px; height: 42px; border-radius: 12px; }
.logo-medium .logo-text { font-size: 1.4rem; }

.logo-large { gap: 16px; }
.logo-large .logo-icon-box { width: 56px; height: 56px; border-radius: 16px; }
.logo-large .logo-text { font-size: 2rem; }

/* --- Dark Mode Styles --- */
.logo-dark .logo-icon-box {
  background-color: #00e5a0; /* Emerald Cyan */
  color: #000;
  box-shadow: 0 0 20px rgba(0, 229, 160, 0.4);
}
.logo-dark .text-hero { color: #00e5a0; }
.logo-dark .text-sync { color: #ffffff; }

/* --- Light Mode Styles --- */
.logo-light .logo-icon-box {
  background-color: #2563eb; /* Blue */
  color: #ffffff;
  box-shadow: 0 0 20px rgba(37, 99, 235, 0.3);
}
.logo-light .text-hero { color: #2563eb; }
.logo-light .text-sync { color: #000000; }

.app-logo:hover .logo-icon-box {
  transform: scale(1.05);
}
</style>
