<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from './components/AppHeader.vue';
import AppSidebar from './components/AppSidebar.vue';
import AppFooter from './components/AppFooter.vue';
import XpBurst from './components/XpBurst.vue';
import { applyConfig } from './utils/config';

const route = useRoute();
const sidebarOpen = ref(false);

const publicPages = ['/', '/login', '/signup'];
const showNav    = computed(() => !publicPages.includes(route.path));
const showFooter = computed(() => !publicPages.includes(route.path));

onMounted(() => {
  applyConfig();
});
</script>

<template>
  <div class="app-root">
    <a href="#main-content" class="skip-link">Skip to main content</a>
    <!-- Sidebar (auth pages hidden) -->
    <AppSidebar
      v-if="showNav"
      :isOpen="sidebarOpen"
      @close="sidebarOpen = false"
    />

    <!-- Main body -->
    <div class="app-body">
      <!-- Slim top bar (always shown when logged in, mainly for mobile hamburger) -->
      <AppHeader
        v-if="showNav"
        :sidebarOpen="sidebarOpen"
        @toggleSidebar="sidebarOpen = !sidebarOpen"
      />

      <!-- Page content -->
      <main id="main-content" class="main-content">
        <!-- Transitions removed for high stability -->
        <router-view />
      </main>

      <AppFooter v-if="showFooter" />
      <XpBurst />
    </div>
  </div>
</template>

<style>
html { scroll-behavior: smooth; }
body {
  margin: 0;
  overflow-x: hidden;
}

.app-root {
  display: flex;
  min-height: 100dvh;
}

.app-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* ── Page Transitions removed ── */
</style>
