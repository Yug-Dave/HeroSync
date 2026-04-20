<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import { AvaturnSDK } from '@avaturn/sdk';

const emit = defineEmits(['avatar-created', 'close']);
const sdkContainer = ref(null);
let sdk = null;

onMounted(() => {
  sdk = new AvaturnSDK();
  // Using the subdomain provided by the user
  sdk.init(sdkContainer.value, {
    url: 'https://herosync.avaturn.dev',
  }).then(() => {
    sdk.on('export', (data) => {
      console.log('Avaturn Export Data:', data);
      const avatarUrl = data.httpURL || data.dataURL || data.url;
      const imageUrl = data.image; 
      if (avatarUrl) {
        emit('avatar-created', { model: avatarUrl, image: imageUrl });
      }
    });
  }).catch((err) => {
    console.error("Failed to initialize Avaturn:", err);
  });
});

onUnmounted(() => {
  // Nuclear cleanup for navigation stability
  if (sdk) {
    try {
      // Unsubscribe from all events
      if (typeof sdk.off === 'function') sdk.off('export');
      // Destroy SDK instance if supported
      if (typeof sdk.destroy === 'function') sdk.destroy();
    } catch (e) {
      console.warn("SDK cleanup skipped:", e);
    }
  }
  // Force clear the container to remove the iframe from DOM
  if (sdkContainer.value) {
    sdkContainer.value.innerHTML = '';
  }
});

</script>

<template>
  <div class="rpm-modal-backdrop" @click.self="emit('close')">
    <div class="rpm-modal-box">
      <div class="rpm-header">
        <h3>Create Your 3D Avatar</h3>
        <button class="close-btn" @click="emit('close')" aria-label="Close">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
      <div class="iframe-container" ref="sdkContainer">
        <!-- Avaturn SDK injects the iframe directly into this div -->
      </div>
    </div>
  </div>
</template>

<style scoped>
.rpm-modal-backdrop {
  position: fixed; inset: 0; background: rgba(0,0,0,0.8); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; z-index: 99999;
}
.rpm-modal-box {
  background: var(--card2); border: 1px solid var(--border2);
  width: 95%; max-width: 1000px; height: 90vh; border-radius: 20px; overflow: hidden;
  display: flex; flex-direction: column; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5);
}
.rpm-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; border-bottom: 1px solid var(--border);
  background: linear-gradient(to right, rgba(0,229,160,0.05), transparent);
}
.rpm-header h3 { margin: 0; font-family: var(--ff-head); font-size: 1.2rem; color: #fff; }
.close-btn {
  width: 32px; height: 32px; border-radius: 8px; background: rgba(255,255,255,0.05); border: 1px solid var(--border);
  color: var(--muted); display: grid; place-items: center; cursor: pointer; transition: all 0.2s;
}
.close-btn:hover { background: rgba(239,68,68,0.1); color: var(--danger); border-color: rgba(239,68,68,0.3); }

.iframe-container { flex: 1; width: 100%; position: relative; background: #fff; }
</style>
