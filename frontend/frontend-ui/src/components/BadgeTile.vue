<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  ruleType: { type: String, default: '' },
  threshold: { type: Number, default: 1 },
  unlocked: { type: Boolean, default: false },
  unlockedAt: { type: String, default: null },
  size: { type: String, default: 'md' }, // 'sm', 'md', 'lg'
  xpReward: { type: Number, default: 0 },
  iconCode: { type: String, default: '' }
});

const rarity = computed(() => {
  if (props.threshold >= 100) return { cls: 'legendary', color: '#f59e0b', label: 'Legendary' };
  if (props.threshold >= 50)  return { cls: 'epic',      color: '#8b5cf6', label: 'Epic' };
  if (props.threshold >= 10)  return { cls: 'rare',      color: '#3b82f6', label: 'Rare' };
  return                      { cls: 'common',    color: '#00e5a0', label: 'Common' };
});

const badgeIcon = computed(() => {
  // Use explicit iconCode if provided
  if (props.iconCode) return props.iconCode;
  
  const r = (props.ruleType || '').toUpperCase();
  if (r.includes('STREAK'))  return 'flame';
  if (r.includes('TOTAL'))   return 'star';
  if (r.includes('PERFECT')) return 'diamond';
  if (r.includes('DAILY'))   return 'sun';
  if (r.includes('WEEK'))    return 'calendar';
  return 'award';
});

function formatDate(dateString) {
  if (!dateString) return '';
  return String(dateString).replace('T', ' ').slice(0, 10);
}
</script>

<template>
  <div 
    class="badge-tile"
    :class="[`size-${size}`, { 'is-locked': !unlocked, [`tier-${rarity.cls}`]: unlocked }]"
  >
    <div class="badge-icon-box">
      <div class="badge-emoji" v-if="unlocked">
        <svg v-if="badgeIcon === 'flame'" class="glow-icon glow-red" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c-2.2-4.5-1-7.5-1-7.5s3 2 4 5c2 3.5 1 7 1 7a5 5 0 1 1-6.5-2z"/></svg>
        <svg v-else-if="badgeIcon === 'star'" class="glow-icon glow-gold" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
        <svg v-else-if="badgeIcon === 'diamond' || badgeIcon === 'shield'" class="glow-icon glow-purple" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 22 8.5 22 15.5 12 22 2 15.5 2 8.5 12 2"/></svg>
        <svg v-else-if="badgeIcon === 'sun' || badgeIcon === 'bolt'" class="glow-icon glow-warning" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
        <svg v-else-if="badgeIcon === 'calendar'" class="glow-icon glow-blue" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <svg v-else-if="badgeIcon === 'leaf' || badgeIcon === 'brain'" class="glow-icon glow-green" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 3.5 1 9.8a7 7 0 0 1-9 8.2z"/><path d="M12 11.5a4.5 4.5 0 1 1-4.5-4.5A4.5 4.5 0 0 1 12 11.5z"/></svg>
        <svg v-else class="glow-icon glow-gold" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>
      </div>
      <div class="badge-emoji" v-else>
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
      </div>
      <div v-if="xpReward > 0" class="reward-tag">+{{ xpReward }} XP</div>
    </div>
    
    <div class="badge-content">
      <h4 class="badge-title">{{ title }}</h4>
      <p class="badge-desc" v-if="size !== 'sm'">{{ description }}</p>
    </div>
    
    <div class="badge-meta" v-if="unlocked && size !== 'sm'">
      <div class="badge-rarity">{{ rarity.label }}</div>
      <div class="badge-date">{{ formatDate(unlockedAt) }}</div>
    </div>
  </div>
</template>

<style scoped>
.badge-tile {
  display: flex; gap: 16px; align-items: center;
  padding: 16px; border-radius: 16px;
  background: rgba(255,255,255,0.02);
  border: 1px solid var(--border);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative; overflow: hidden;
}

/* Unlocked Hover & Effects */
.badge-tile:not(.is-locked):hover {
  transform: translateY(-3px) scale(1.02);
  border-color: var(--border2);
  box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}

/* Locked state */
.is-locked { opacity: 0.6; filter: grayscale(100%); }
.is-locked .badge-emoji { opacity: 0.5; font-size: 24px; }

/* Icon Box */
.badge-icon-box {
  width: 54px; height: 54px; border-radius: 14px;
  background: rgba(255,255,255,0.05);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; position: relative; border: 1px solid transparent;
}
.badge-emoji { font-size: 28px; z-index: 1; }

/* Tiers */
.tier-common .badge-icon-box {
  border-color: rgba(0,229,160,0.3); background: rgba(0,229,160,0.05);
  box-shadow: 0 0 15px rgba(0,229,160,0.1);
}
.tier-rare .badge-icon-box {
  border-color: rgba(59,130,246,0.4); background: rgba(59,130,246,0.1);
  box-shadow: 0 0 15px rgba(59,130,246,0.2);
}
.tier-epic .badge-icon-box {
  border-color: rgba(139,92,246,0.5); background: rgba(139,92,246,0.15);
  box-shadow: 0 0 20px rgba(139,92,246,0.3);
}
.tier-epic .badge-emoji { animation: float 3s infinite ease-in-out; }

.tier-legendary .badge-icon-box {
  border-color: rgba(245,158,11,0.6); background: rgba(245,158,11,0.15);
  box-shadow: 0 0 25px rgba(245,158,11,0.4);
}
.tier-legendary { background: linear-gradient(135deg, rgba(245,158,11,0.05), transparent); }
.tier-legendary .badge-emoji { animation: float 2.5s infinite ease-in-out, pulse-glow 2s infinite alternate; }

/* Content */
.badge-content { flex: 1; min-width: 0; }
.badge-title { font-family: var(--ff-head); font-size: 1.05rem; font-weight: 700; color: #fff; margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.badge-desc { font-size: 0.85rem; color: var(--muted); margin: 4px 0 0; line-height: 1.3; }

/* Meta */
.badge-meta { text-align: right; display: flex; flex-direction: column; gap: 4px; flex-shrink: 0; }
.badge-rarity { font-family: var(--ff-head); font-size: 0.75rem; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; }
.badge-date { font-family: monospace; font-size: 0.75rem; color: var(--muted); }

.tier-common .badge-rarity { color: var(--accent); }
.tier-rare .badge-rarity { color: #60a5fa; }
.tier-epic .badge-rarity { color: #a78bfa; }
.tier-legendary .badge-rarity { color: var(--gold); }

/* Sizes */
.size-sm { padding: 10px; gap: 10px; border-radius: 12px; }
.size-sm .badge-icon-box { width: 36px; height: 36px; border-radius: 10px; }
.size-sm .badge-emoji { font-size: 20px; }
.size-sm .badge-title { font-size: 0.9rem; }

.size-lg { padding: 24px; gap: 20px; flex-direction: column; text-align: center; }
.size-lg .badge-icon-box { width: 80px; height: 80px; border-radius: 20px; margin: 0 auto; }
.size-lg .badge-emoji { font-size: 40px; }
.size-lg .badge-title { font-size: 1.2rem; white-space: normal; }
.size-lg .badge-meta { text-align: center; align-items: center; }

.glow-blue { color: #3b82f6; filter: drop-shadow(0 0 5px rgba(59,130,246,0.5)); }
.glow-green { color: #10b981; filter: drop-shadow(0 0 5px rgba(16,185,129,0.5)); }

.reward-tag {
  position: absolute;
  top: -8px;
  right: -8px;
  background: var(--accent);
  color: #111;
  font-family: var(--ff-head);
  font-size: 0.65rem;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 6px;
  box-shadow: 0 4px 8px rgba(0,229,160,0.3);
  z-index: 2;
  white-space: nowrap;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}
@keyframes pulse-glow {
  0% { filter: drop-shadow(0 0 2px rgba(245,158,11,0.5)); }
  100% { filter: drop-shadow(0 0 10px rgba(245,158,11,0.8)); }
}
@media (max-width: 500px) {
  .badge-tile {
    flex-direction: column;
    text-align: center;
    padding: 20px;
    gap: 12px;
  }
  .badge-content {
    width: 100%;
  }
  .badge-title {
    white-space: normal;
    font-size: 1rem;
  }
  .badge-meta {
    text-align: center;
    align-items: center;
    width: 100%;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    padding-top: 10px;
  }
}
</style>
