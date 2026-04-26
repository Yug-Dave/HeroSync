import { reactive, watch } from 'vue';

const DEFAULT_CONFIG = {
  notifications: true,
  theme: 'dark',
  soundEffects: true,
  dailyReset: '00:00',
  immersionMode: true,
  weekendGrace: false
};

const saved = localStorage.getItem('hero_config');
export const heroConfig = reactive(saved ? { ...DEFAULT_CONFIG, ...JSON.parse(saved) } : DEFAULT_CONFIG);

export function applyConfig(companion = 'SYNC') {
  const root = document.documentElement;
  const body = document.body;

  // Theme
  body.classList.remove('theme-light');
  if (heroConfig.theme === 'light') {
    body.classList.add('theme-light');
    
    // Companion-specific colors for Light Mode
    const companionColors = {
      SYNC: { hex: '#2563eb', rgb: '37, 99, 235' },   // Deep Blue
      AURA: { hex: '#7c3aed', rgb: '124, 58, 237' },  // Deep Purple
      VOLT: { hex: '#ea580c', rgb: '234, 88, 12' }    // Deep Orange
    };
    
    const colors = companionColors[companion] || companionColors.SYNC;
    // Apply to body to override .theme-light specificity
    body.style.setProperty('--accent', colors.hex);
    body.style.setProperty('--accent-rgb', colors.rgb);
    body.style.setProperty('--hm-lvl3', colors.hex);
    body.style.setProperty('--hm-lvl3-border', colors.hex);
  } else {
    // Revert to Dark Mode Default (Green)
    // Clear both root and body to ensure no previous session leaks
    root.style.removeProperty('--accent');
    root.style.removeProperty('--accent-rgb');
    root.style.removeProperty('--hm-lvl3');
    root.style.removeProperty('--hm-lvl3-border');
    
    body.style.removeProperty('--accent');
    body.style.removeProperty('--accent-rgb');
    body.style.removeProperty('--hm-lvl3');
    body.style.removeProperty('--hm-lvl3-border');
  }

  // Immersion (Animations)
  if (heroConfig.immersionMode === false) {
    body.classList.add('no-animations');
  } else {
    body.classList.remove('no-animations');
  }
}

// Watch for changes and save (App.vue handles the application logic)
watch(heroConfig, () => {
  localStorage.setItem('hero_config', JSON.stringify(heroConfig));
}, { deep: true });

// Sound Utility
const sounds = {
  questDone: 'https://assets.mixkit.co/active_storage/sfx/2013/2013-preview.mp3', // Generic chime
  levelUp: 'https://assets.mixkit.co/active_storage/sfx/1435/1435-preview.mp3'    // Fanfare
};

export function playSound(type) {
  if (!heroConfig.soundEffects) return;
  const audio = new Audio(sounds[type]);
  audio.volume = 0.4;
  audio.play().catch(() => {}); // Browser might block auto-play
}
