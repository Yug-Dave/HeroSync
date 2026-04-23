<template>
  <div class="auth-page">
    <div class="auth-panel">
      <div class="panel-glow panel-glow-tl" />
      <div class="panel-glow panel-glow-br" />
      <div class="panel-content">
        <div class="panel-brand">
          <AppLogo size="medium" />
        </div>
        <div class="svg-scene">
          <svg viewBox="0 0 280 280" xmlns="http://www.w3.org/2000/svg" class="hero-svg">
            <circle class="star s1" cx="40"  cy="30"  r="1.5" />
            <circle class="star s2" cx="230" cy="50"  r="1"   />
            <circle class="star s3" cx="260" cy="120" r="1.5" />
            <circle class="star s4" cx="20"  cy="180" r="1"   />
            <circle class="star s5" cx="250" cy="210" r="1.5" />
            <circle class="star s6" cx="60"  cy="240" r="1"   />
            <circle class="star s7" cx="200" cy="260" r="1.5" />
            <circle class="star s8" cx="140" cy="20"  r="1"   />
            
            <ellipse class="orbit-ring" cx="140" cy="140" rx="110" ry="110" fill="none" stroke="rgba(0,229,160,0.12)" stroke-width="1" stroke-dasharray="6 4" />
            <ellipse class="orbit-ring-mid" cx="140" cy="140" rx="75" ry="75" fill="none" stroke="rgba(139,92,246,0.15)" stroke-width="1" stroke-dasharray="4 6" />
            
            <g class="orbit-dot-1"><circle cx="140" cy="30" r="7" fill="rgba(0,229,160,0.9)" /><circle cx="140" cy="30" r="12" fill="rgba(0,229,160,0.15)" /></g>
            <g class="orbit-dot-2"><circle cx="215" cy="140" r="5" fill="rgba(139,92,246,0.9)" /><circle cx="215" cy="140" r="10" fill="rgba(139,92,246,0.15)" /></g>
            <g class="orbit-dot-3"><circle cx="65" cy="140" r="6" fill="rgba(59,130,246,0.9)" /><circle cx="65" cy="140" r="11" fill="rgba(59,130,246,0.12)" /></g>
            
            <circle cx="140" cy="140" r="52" fill="rgba(0,229,160,0.06)" />
            <circle cx="140" cy="140" r="38" fill="rgba(0,229,160,0.1)" />
            
            <g class="emblem-float">
              <circle cx="140" cy="140" r="30" fill="rgba(4,13,9,0.9)" stroke="rgba(0,229,160,0.5)" stroke-width="1.5" />
              <polygon points="140,120 128,150 140,143 152,150" fill="rgba(0,229,160,0.9)" />
            </g>

            <g class="xp-tag xp1"><rect x="92" y="68" width="44" height="18" rx="9" fill="rgba(0,229,160,0.15)" stroke="rgba(0,229,160,0.4)" stroke-width="1" /><text x="114" y="81" text-anchor="middle" fill="rgba(0,229,160,0.9)" font-size="9" font-family="monospace" font-weight="700">+50 XP</text></g>
            <g class="xp-tag xp2"><rect x="165" y="90" width="44" height="18" rx="9" fill="rgba(139,92,246,0.15)" stroke="rgba(139,92,246,0.4)" stroke-width="1" /><text x="187" y="103" text-anchor="middle" fill="rgba(139,92,246,0.9)" font-size="9" font-family="monospace" font-weight="700">+120 XP</text></g>
            
            <g class="stat-bars">
              <rect x="50" y="210" width="180" height="6" rx="3" fill="rgba(255,255,255,0.05)" />
              <rect class="bar bar1" x="50" y="210" width="0" height="6" rx="3" fill="#00e5a0" />
              <rect x="50" y="225" width="180" height="6" rx="3" fill="rgba(255,255,255,0.05)" />
              <rect class="bar bar2" x="50" y="225" width="0" height="6" rx="3" fill="#8b5cf6" />
            </g>
          </svg>
        </div>
        <p class="panel-tagline">Build habits. Level up your life.</p>
      </div>
    </div>

    <div class="auth-form-side">
      <div class="form-container">
        <div class="form-header">
          <div class="mobile-logo"><AppLogo size="medium" /></div>
          <h1 class="form-title">Welcome back</h1>
          <p class="form-sub">Sign in to continue your journey</p>
        </div>
        <form class="form" @submit.prevent="onSubmit">
          <div class="field">
            <label class="label">Email</label>
            <input class="input" type="email" v-model="email" placeholder="you@example.com" />
          </div>
          <div class="field">
            <label class="label">Password</label>
            <input class="input" type="password" v-model="password" placeholder="••••••••" />
          </div>
          <button class="btn-submit" type="submit" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span v-else>Sign In</span>
          </button>
          <div v-if="serverError" class="alert-error">{{ serverError }}</div>
          <p class="form-link">New here? <RouterLink to="/signup">Create an account</RouterLink></p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '../api/auth';
import AppLogo from '../components/AppLogo.vue';

const router = useRouter();
const email = ref('');
const password = ref('');
const loading = ref(false);
const serverError = ref('');

async function onSubmit() {
  loading.value = true;
  try {
    await login({ email: email.value, password: password.value });
    window.location.href = "/dashboard";
  } catch (err) {
    serverError.value = typeof err === 'string' ? err : "Login failed.";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-page { display: flex; min-height: 100dvh; background: var(--bg); }
.auth-panel { width: 42%; position: relative; overflow: hidden; background: linear-gradient(160deg, var(--card2) 0%, var(--bg2) 100%); border-right: 1px solid var(--border); display: flex; align-items: center; justify-content: center; }
.panel-glow { position: absolute; width: 280px; height: 280px; border-radius: 50%; pointer-events: none; }
.panel-glow-tl { top: -80px; left: -80px; background: radial-gradient(circle, rgba(var(--accent-rgb), 0.15) 0%, transparent 70%); }
.panel-glow-br { bottom: -80px; right: -80px; background: radial-gradient(circle, rgba(var(--accent-2-rgb), 0.12) 0%, transparent 70%); }
.panel-content { position: relative; z-index: 1; padding: 40px; width: 100%; max-width: 360px; }
.panel-brand { display: flex; align-items: center; justify-content: center; margin-bottom: 32px; }

/* SVG Scene Animations */
.svg-scene { width: 100%; margin-bottom: 24px; }
.hero-svg { width: 100%; height: auto; overflow: visible; }

.star { fill: rgba(255,255,255,0.6); animation: twinkle 3s ease-in-out infinite; }
.s1 { animation-delay: 0s; } .s2 { animation-delay: 0.4s; } .s3 { animation-delay: 0.8s; } .s4 { animation-delay: 1.2s; }
.s5 { animation-delay: 1.6s; } .s6 { animation-delay: 2.0s; } .s7 { animation-delay: 2.4s; } .s8 { animation-delay: 2.8s; }
@keyframes twinkle { 0%, 100% { opacity: 0.2; transform: scale(1); } 50% { opacity: 1; transform: scale(1.6); } }

.orbit-ring { animation: spin-cw 20s linear infinite; transform-origin: 140px 140px; }
.orbit-ring-mid { animation: spin-ccw 14s linear infinite; transform-origin: 140px 140px; }
@keyframes spin-cw { to { transform: rotate(360deg); } }
@keyframes spin-ccw { to { transform: rotate(-360deg); } }

.orbit-dot-1 { animation: spin-cw 8s linear infinite; transform-origin: 140px 140px; }
.orbit-dot-2 { animation: spin-ccw 12s linear infinite; transform-origin: 140px 140px; }
.orbit-dot-3 { animation: spin-cw 10s linear infinite; transform-origin: 140px 140px; }

.emblem-float { animation: float 4s ease-in-out infinite; transform-origin: 140px 140px; }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }

.xp-tag { animation: rise 6s ease-in-out infinite; }
.xp1 { animation-delay: 0s; } .xp2 { animation-delay: 3s; }
@keyframes rise { 0% { opacity: 0; transform: translateY(0); } 15% { opacity: 1; } 80% { opacity: 0; transform: translateY(-30px); } 100% { opacity: 0; } }

.bar1 { animation: fill-bar1 2s ease-out forwards; }
.bar2 { animation: fill-bar2 2s ease-out 0.5s forwards; }
@keyframes fill-bar1 { from { width: 0; } to { width: 140px; } }
@keyframes fill-bar2 { from { width: 0; } to { width: 110px; } }

/* Light Theme Overrides for SVG */
.theme-light .star { fill: rgba(37, 99, 235, 0.4); }
.theme-light .orbit-ring { stroke: rgba(37, 99, 235, 0.15); }
.theme-light .orbit-ring-mid { stroke: rgba(59, 130, 246, 0.15); }
.theme-light .emblem-float circle { fill: #ffffff; stroke: #2563eb; }
.theme-light .emblem-float polygon { fill: #2563eb; }
.theme-light .stat-bars rect:first-child { fill: rgba(0,0,0,0.05); }

.panel-tagline { text-align: center; color: var(--muted); font-style: italic; font-size: 0.9rem; }

/* Auth Form Styles */
.auth-form-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 24px; }
.form-container { width: 100%; max-width: 400px; }
.form-header { margin-bottom: 32px; }
.form-title { margin: 0 0 8px; font-family: var(--ff-head); font-size: 2rem; font-weight: 700; color: var(--text); }
.form-sub { margin: 0; color: var(--muted); font-size: 0.9rem; }
.form { display: flex; flex-direction: column; gap: 18px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.label { color: var(--text-dim); font-size: 0.85rem; font-weight: 600; }
.input { width: 100%; padding: 14px; background: var(--card); border: 1px solid var(--border); border-radius: 12px; color: var(--text); transition: all 0.2s; }
.input:focus { border-color: var(--accent); outline: none; background: var(--card2); }
.btn-submit { width: 100%; padding: 14px; border-radius: 12px; border: none; background: linear-gradient(135deg, var(--accent), #1dbd88); color: #000; font-family: var(--ff-head); font-size: 1.1rem; font-weight: 800; cursor: pointer; transition: all 0.2s; box-shadow: 0 5px 20px rgba(var(--accent-rgb), 0.2); display: flex; align-items: center; justify-content: center; }
.btn-submit:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(var(--accent-rgb), 0.3); }


.alert-error { padding: 12px; background: rgba(var(--danger-rgb), 0.1); border: 1px solid rgba(var(--danger-rgb), 0.2); border-radius: 10px; color: var(--danger); font-size: 0.85rem; }
.form-link { font-size: 0.9rem; color: var(--muted); margin-top: 24px; text-align: center; }
.form-link a { color: var(--accent); text-decoration: none; font-weight: 700; }
.spinner { width: 20px; height: 20px; border: 3px solid rgba(0,0,0,0.1); border-top-color: #000; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.mobile-logo { display: none; margin-bottom: 24px; }
@media (max-width: 850px) { 
  .auth-panel { display: none; } 
  .auth-form-side { 
    background: var(--bg); /* Ensure solid background */
    background-image: 
                radial-gradient(circle at top right, rgba(var(--accent-rgb), 0.12) 0%, transparent 50%),
                radial-gradient(circle at bottom left, rgba(var(--accent-2-rgb), 0.1) 0%, transparent 50%);
    padding: 30px 20px; /* Reduced padding to eliminate gaps */
    width: 100%;
  }
  .mobile-logo { display: flex; justify-content: center; transform: scale(1.1); margin-bottom: 24px; }
  .form-header { text-align: center; }
  .form-container { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1); width: 100%; }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
