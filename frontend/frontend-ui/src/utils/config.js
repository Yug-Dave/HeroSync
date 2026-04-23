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

export function applyConfig() {
  const root = document.documentElement;
  const body = document.body;

  // Theme
  body.classList.remove('theme-light');
  if (heroConfig.theme === 'light') body.classList.add('theme-light');

  // Immersion (Animations)
  if (heroConfig.immersionMode === false) {
    body.classList.add('no-animations');
  } else {
    body.classList.remove('no-animations');
  }
}

// Watch for changes and save/apply
watch(heroConfig, () => {
  localStorage.setItem('hero_config', JSON.stringify(heroConfig));
  applyConfig();
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
