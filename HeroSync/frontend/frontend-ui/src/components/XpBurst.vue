<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const bursts = ref([]);
let idCounter = 0;

function triggerBurst(e) {
  const { xp, x, y } = e.detail;
  const id = idCounter++;
  
  // Generate random particles
  const particles = Array.from({ length: 6 }).map((_, i) => ({
    id: `${id}-p-${i}`,
    angle: Math.random() * Math.PI * 2,
    distance: 40 + Math.random() * 40,
    delay: Math.random() * 0.2
  }));

  bursts.value.push({ id, xp, x, y, particles });
  
  setTimeout(() => {
    bursts.value = bursts.value.filter(b => b.id !== id);
  }, 2000);
}

onMounted(() => {
  window.addEventListener('trigger-xp-burst', triggerBurst);
});

onUnmounted(() => {
  window.removeEventListener('trigger-xp-burst', triggerBurst);
});
</script>

<template>
  <Teleport to="body">
    <div class="xp-burst-container">
      <div 
        v-for="b in bursts" 
        :key="b.id" 
        class="xp-burst-origin"
        :style="{ left: b.x + 'px', top: b.y + 'px' }"
      >
        <!-- Floating Text -->
        <div class="xp-burst-anim">
          <span class="xp-text">+{{ b.xp }} XP</span>
        </div>
        
        <!-- Particles -->
        <div 
          v-for="p in b.particles" 
          :key="p.id"
          class="particle"
          :style="{
            '--tx': `${Math.cos(p.angle) * p.distance}px`,
            '--ty': `${Math.sin(p.angle) * p.distance}px`,
            'animation-delay': `${p.delay}s`
          }"
        ></div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.xp-burst-container {
  position: fixed; inset: 0; pointer-events: none; z-index: 9999;
}
.xp-burst-origin {
  position: absolute; transform: translate(-50%, -50%);
}

.xp-burst-anim {
  animation: floatUp 1.8s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
}

.xp-text {
  font-family: var(--ff-head); font-size: 1.6rem; font-weight: 900;
  color: var(--accent); white-space: nowrap;
  text-shadow: 0 0 12px rgba(0,229,160,0.8), 0 2px 5px rgba(0,0,0,0.8);
}

.particle {
  position: absolute; top: 50%; left: 50%; width: 6px; height: 6px;
  background: var(--gold); border-radius: 50%;
  box-shadow: 0 0 8px var(--gold); opacity: 0;
  animation: explode 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
}

@keyframes floatUp {
  0% { transform: scale(0.5); opacity: 0; }
  10% { transform: translateY(-20px) scale(1.2); opacity: 1; }
  20% { transform: translateY(-30px) scale(1); opacity: 1; }
  80% { transform: translateY(-80px) scale(1); opacity: 1; }
  100% { transform: translateY(-100px) scale(0.9); opacity: 0; }
}

@keyframes explode {
  0% { transform: translate(-50%, -50%) scale(0); opacity: 1; }
  100% { transform: translate(calc(-50% + var(--tx)), calc(-50% + var(--ty))) scale(1.5); opacity: 0; }
}
</style>
