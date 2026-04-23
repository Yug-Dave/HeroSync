<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import Hero from '../components/Hero.vue';
import Features from '../components/Features.vue';
import Methodology from '../components/Methodology.vue';
import DashboardPreview from '../components/DashboardPreview.vue';
import CompanionSlider from '../components/CompanionSlider.vue';
import Pricing from '../components/Pricing.vue';
import AppLogo from '../components/AppLogo.vue';
import { heroConfig } from '../utils/config';

const activeSectionIndex = ref(0);
const isTransitioning = ref(false);
const sections = ['Hero', 'Features', 'Method', 'Preview', 'Companions', 'Pricing'];

const isMobile = ref(false);
const mobileMenuOpen = ref(false);

function checkMobile() {
  isMobile.value = window.innerWidth <= 900;
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value;
}

function goToSection(index) {
  if (index < 0 || index >= sections.length) return;
  
  if (isMobile.value) {
    mobileMenuOpen.value = false;
    const slides = document.querySelectorAll('.slide');
    const target = slides[index];
    if (target) {
      // Force scroll to top of the specific section
      setTimeout(() => {
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        activeSectionIndex.value = index;
      }, 100);
    }
  } else {
    if (isTransitioning.value) return;
    isTransitioning.value = true;
    activeSectionIndex.value = index;
    setTimeout(() => { isTransitioning.value = false; }, 1000);
  }
}

function handleWheel(e) {
  if (isMobile.value || isTransitioning.value) return;
  if (Math.abs(e.deltaY) < 30) return;
  if (e.deltaY > 0) goToSection(activeSectionIndex.value + 1);
  else goToSection(activeSectionIndex.value - 1);
}

onMounted(() => {
  checkMobile();
  window.addEventListener('resize', checkMobile);
  if (!isMobile.value) {
    window.addEventListener('wheel', handleWheel, { passive: false });
    document.body.style.overflow = 'hidden';
  }
});

onUnmounted(() => {
  window.removeEventListener('wheel', handleWheel);
  window.removeEventListener('resize', checkMobile);
  document.body.style.overflow = '';
});

const sectionTransform = computed(() => {
  if (isMobile.value) return 'none';
  return `translateY(-${activeSectionIndex.value * 100}vh)`;
});
const bgParallax = computed(() => `translateY(-${activeSectionIndex.value * 15}vh)`);
</script>

<template>
  <div class="landing-page" :class="[`theme-${heroConfig.theme}`, { 'mobile-snap': isMobile, 'menu-open': mobileMenuOpen }]">
    <div class="grain-overlay"></div>
    
    <!-- Premium Header -->
    <header class="main-header" :class="{ 'scrolled': activeSectionIndex > 0 || isMobile }">
      <div class="nav-container">
        <a href="#" class="logo-link" @click.prevent="goToSection(0)">
          <AppLogo size="medium" />
        </a>

        <nav class="desktop-nav">
          <a v-for="(s, i) in sections.slice(1)" :key="s" 
             href="#" :class="{ active: activeSectionIndex === i + 1 }"
             @click.prevent="goToSection(i + 1)">
            {{ s }}
          </a>
        </nav>

        <div class="header-actions">
          <button class="theme-toggle" @click="heroConfig.theme = heroConfig.theme === 'light' ? 'dark' : 'light'">
            <span v-if="heroConfig.theme === 'light'">🌙</span>
            <span v-else>☀️</span>
          </button>
          <router-link to="/login" class="nav-btn-link hide-mobile">Log In</router-link>
          <router-link to="/signup" class="btn-epic-small hide-mobile">Get Started</router-link>

          <!-- Mobile Burger -->
          <button class="burger-btn" @click="toggleMobileMenu">
            <span class="line"></span>
            <span class="line"></span>
            <span class="line"></span>
          </button>
        </div>
      </div>
    </header>

    <!-- Mobile Navigation Overlay -->
    <transition name="fade">
      <div v-if="mobileMenuOpen" class="mobile-nav-overlay" @click="mobileMenuOpen = false">
        <div class="mobile-menu" @click.stop>
          <div class="menu-header">
            <AppLogo size="small" />
            <button class="close-btn" @click="mobileMenuOpen = false">×</button>
          </div>
          <nav class="menu-links">
            <a v-for="(s, i) in sections" :key="s" 
               href="#" :class="{ active: activeSectionIndex === i }"
               @click.prevent="goToSection(i)">
              {{ s }}
            </a>
            <hr class="menu-divider" />
            <router-link to="/login" class="menu-btn-secondary">Log In</router-link>
            <router-link to="/signup" class="menu-btn-primary">Get Started</router-link>
          </nav>
        </div>
      </div>
    </transition>

    <!-- Side Nav Dots (Desktop only) -->
    <div class="side-nav" v-if="!isMobile">
      <button v-for="(s, i) in sections" :key="s" 
              class="nav-dot-container" :class="{ active: activeSectionIndex === i }"
              @click="goToSection(i)">
        <span class="nav-dot-label">{{ s }}</span>
        <span class="nav-dot"></span>
      </button>
    </div>

    <div class="scroll-progress" v-if="!isMobile"><div class="progress-fill" :style="{ width: `${((activeSectionIndex + 1) / sections.length) * 100}%` }"></div></div>

    <div class="viewport">
      <main class="slider" :style="{ transform: sectionTransform }">
        <section class="slide" :class="{ active: activeSectionIndex === 0 || isMobile }"><Hero @learn-more="goToSection(2)" /></section>
        <section class="slide" :class="{ active: activeSectionIndex === 1 || isMobile }"><Features /></section>
        <section class="slide" :class="{ active: activeSectionIndex === 2 || isMobile }"><Methodology /></section>
        <section class="slide" :class="{ active: activeSectionIndex === 3 || isMobile }"><DashboardPreview /></section>
        <section class="slide" :class="{ active: activeSectionIndex === 4 || isMobile }"><CompanionSlider /></section>
        <section class="slide" :class="{ active: activeSectionIndex === 5 || isMobile }"><Pricing /></section>
      </main>
    </div>
  </div>
</template>

<style scoped>
.landing-page {
  height: 100vh; width: 100vw; overflow: hidden; position: relative;
  background-color: var(--bg); color: var(--text); font-family: var(--ff-body);
}

.landing-page.mobile-snap {
  height: 100vh;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
  -webkit-overflow-scrolling: touch;
}

.landing-page.theme-dark {
  background: radial-gradient(ellipse at bottom, #0d162a 0%, #070a10 100%);
}
.landing-page.theme-light {
  background: #f8fafc !important;
}


/* --- HEADER --- */
.main-header {
  position: fixed; top: 0; left: 0; right: 0; height: 100px; z-index: 500;
  display: flex; align-items: center; transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}
.main-header.scrolled {
  height: 80px; backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border);
  background: rgba(var(--bg-rgb), 0.8);
}

.nav-container { width: min(1400px, 92%); margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.logo-link { text-decoration: none; }

.desktop-nav { display: flex; gap: 32px; }
.desktop-nav a {
  text-decoration: none; color: var(--text-dim); font-family: var(--ff-head); font-weight: 700;
  font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1.5px; transition: all 0.3s; position: relative;
}
.desktop-nav a.active { color: var(--accent); }

.header-actions { display: flex; align-items: center; gap: 24px; }
.theme-toggle { background: rgba(255,255,255,0.05); border: 1px solid var(--border); width: 44px; height: 44px; border-radius: 12px; cursor: pointer; color: var(--text); display: flex; align-items: center; justify-content: center; transition: all 0.3s; }
.nav-btn-link { text-decoration: none; color: var(--text); font-weight: 800; font-size: 0.95rem; }
.btn-epic-small {
  background: linear-gradient(135deg, var(--accent), #1dbd88); color: #000;
  padding: 12px 24px; border-radius: 12px; text-decoration: none; font-family: var(--ff-head); font-weight: 800;
  box-shadow: 0 5px 15px rgba(var(--accent-rgb), 0.2); transition: all 0.3s;
}

/* --- BURGER --- */
.burger-btn { display: none; flex-direction: column; gap: 6px; background: transparent; border: none; cursor: pointer; padding: 4px; }
.burger-btn .line { width: 28px; height: 2px; background: var(--text); border-radius: 2px; transition: 0.3s; }

/* --- MOBILE NAV OVERLAY --- */
.mobile-nav-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.8); backdrop-filter: blur(8px); z-index: 1000; display: flex; justify-content: flex-end; }
.mobile-menu { width: 80%; max-width: 320px; height: 100%; background: var(--bg2); border-left: 1px solid var(--border); padding: 30px; display: flex; flex-direction: column; box-shadow: -10px 0 30px rgba(0,0,0,0.5); }
.menu-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; }
.close-btn { background: transparent; border: none; color: var(--text); font-size: 2rem; cursor: pointer; }
.menu-links { display: flex; flex-direction: column; gap: 20px; }
.menu-links a { text-decoration: none; color: var(--text); font-family: var(--ff-head); font-size: 1.5rem; font-weight: 700; text-transform: uppercase; letter-spacing: 2px; transition: 0.3s; }
.menu-links a.active { color: var(--accent); }
.menu-divider { width: 100%; border: none; border-top: 1px solid var(--border); margin: 10px 0; }
.menu-btn-primary { background: var(--accent); color: #000; padding: 16px; border-radius: 12px; text-align: center; text-decoration: none; font-weight: 800; font-family: var(--ff-head); font-size: 1.1rem; }
.menu-btn-secondary { border: 1px solid var(--border); color: var(--text); padding: 16px; border-radius: 12px; text-align: center; text-decoration: none; font-weight: 800; font-family: var(--ff-head); font-size: 1.1rem; }

/* --- SIDE NAV --- */
.side-nav {
  position: fixed; right: 40px; top: 50%; transform: translateY(-50%);
  z-index: 100; display: flex; flex-direction: column; gap: 24px;
}
.nav-dot-container {
  background: transparent; border: none; padding: 0; display: flex; align-items: center; justify-content: flex-end;
  cursor: pointer; position: relative;
}
.nav-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--text-dim); opacity: 0.3; transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.nav-dot-label {
  position: absolute; right: 28px; font-family: var(--ff-head); font-size: 0.75rem; font-weight: 800;
  text-transform: uppercase; color: var(--accent); opacity: 0; transform: translateX(10px);
  pointer-events: none; transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); letter-spacing: 2px;
}
.nav-dot-container:hover .nav-dot { opacity: 1; transform: scale(1.6); }
.nav-dot-container:hover .nav-dot-label { opacity: 1; transform: translateX(0); }
.nav-dot-container.active .nav-dot { background: var(--accent); opacity: 1; width: 12px; height: 12px; box-shadow: 0 0 15px var(--accent); }

/* --- PROGRESS --- */
.scroll-progress { position: fixed; top: 0; left: 0; width: 100%; height: 3px; z-index: 600; background: rgba(255,255,255,0.05); }
.progress-fill { height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2)); transition: width 0.8s ease; }

/* --- VIEWPORT --- */
.viewport { height: 100vh; width: 100vw; overflow: hidden; position: relative; z-index: 1; }
.mobile-snap .viewport { height: auto; overflow: visible; }

.slider { 
  display: flex; 
  flex-direction: column; 
  transition: transform 1s cubic-bezier(0.7, 0, 0.3, 1); 
  will-change: transform; 
  align-items: stretch;
}
.mobile-snap .slider { display: block; transform: none !important; }

.slide { height: 100vh; width: 100%; flex-shrink: 0; transition: opacity 0.8s, filter 0.8s, transform 0.8s; }
.slide:not(.active) { opacity: 0.2; filter: blur(10px); transform: scale(0.95); }

.mobile-snap .slide {
  min-height: 100vh;
  height: auto; /* Allow height to expand based on content */
  width: 100%;
  scroll-snap-align: start;
  scroll-snap-stop: always;
  opacity: 1 !important;
  filter: none !important;
  transform: none !important;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 0;
}

@media (max-width: 900px) {
  .desktop-nav { display: none; }
  .hide-mobile { display: none; }
  .burger-btn { display: flex; }
  .main-header { height: 80px; }
  .nav-container { width: 92%; }
}

/* Animations */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
