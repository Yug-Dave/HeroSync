<script setup>
import { ref } from 'vue';
import Hero from '../components/Hero.vue';
import Features from '../components/Features.vue';
import AppFooter from '../components/AppFooter.vue';

const mobileMenuOpen = ref(false);
</script>

<template>
  <div class="landing-page">
    <nav class="navbar">
      <div class="container nav-content">

        <div class="logo-group">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width:65%;height:65%;">
              <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
            </svg>
          </div>
          <span class="logo-text">HeroSync</span>
        </div>

        <div class="desktop-cta">
          <div class="desktop-nav">
            <a href="#features" class="nav-link">Features</a>
          </div>
          <router-link to="/login" class="nav-link">Log In</router-link>
          <router-link to="/signup" class="btn-primary">Get Started</router-link>
        </div>

        <button class="mobile-toggle" @click="mobileMenuOpen = !mobileMenuOpen">
          <svg v-if="mobileMenuOpen" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-white"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-white"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg>
        </button>
      </div>

      <transition name="fade">
        <div v-if="mobileMenuOpen" class="mobile-menu">
          <a href="#features" class="nav-link" @click="mobileMenuOpen = false">Features</a>
          <div class="mobile-cta-wrapper">
            <router-link to="/login" class="nav-link" @click="mobileMenuOpen = false" style="margin-bottom: 12px; display: block; text-align: center;">Log In</router-link>
            <router-link to="/signup" class="btn-primary full-width" @click="mobileMenuOpen = false">Get Started</router-link>
          </div>
        </div>
      </transition>
    </nav>

    <main>
      <Hero />
      <Features />
    </main>

    <AppFooter />
  </div>
</template>

<style scoped>
.landing-page {
  min-height: 100vh;
  background-color: var(--bg);
  color: var(--text);
  font-family: var(--ff-body);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* --- NAVBAR --- */
.navbar {
  position: sticky;
  top: 0;
  z-index: 50;
  background-color: rgba(7, 10, 16, 0.85);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

/* Logo */
.logo-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  display: flex;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #040d09;
  font-family: var(--ff-display);
  font-size: 10px;
  font-weight: 900;
  box-shadow: 0 0 16px rgba(0,229,160,0.3);
}

.logo-text {
  font-family: var(--ff-display);
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: 1.5px;
  color: var(--text);
}

/* Desktop Nav */
.desktop-nav { display: none; gap: 32px; align-items: center; }
.desktop-cta { display: none; gap: 24px; align-items: center; }

.nav-link {
  color: var(--muted);
  text-decoration: none;
  font-family: var(--ff-head);
  font-size: 1.05rem;
  font-weight: 600;
  transition: color 0.2s;
}
.nav-link:hover { color: var(--accent); }

/* Buttons */
.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--accent), #1dbd88);
  color: #040d09;
  padding: 10px 24px;
  border-radius: 10px;
  font-family: var(--ff-head);
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: 0 4px 16px rgba(0,229,160,0.25);
}
.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,229,160,0.4);
}

/* Mobile Menu */
.mobile-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: var(--text);
}
.icon-white { stroke: var(--text); }

.mobile-menu {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  border-top: 1px solid var(--border);
  background-color: var(--bg2);
}
.full-width { width: 100%; }
.mobile-cta-wrapper { padding-top: 16px; border-top: 1px solid var(--border); }

/* Animation */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* Desktop */
@media (min-width: 768px) {
  .desktop-nav { display: flex; margin-right: 16px; }
  .desktop-cta { display: flex; }
  .mobile-toggle { display: none; }
}
</style>
