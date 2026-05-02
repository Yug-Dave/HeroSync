<template>
  <div class="auth-page">
    <div class="auth-panel">
      <div class="panel-glow panel-glow-tl" />
      <div class="panel-glow panel-glow-br" />
      <div class="panel-content">
        <div class="panel-brand">
          <AppLogo size="medium" />
        </div>
        <h2 class="panel-headline">Choose Your Path</h2>
        <div class="svg-scene">
          <svg viewBox="0 0 260 220" xmlns="http://www.w3.org/2000/svg" class="hero-svg">
            <circle class="star s1" cx="20" cy="20" r="1.5" /><circle class="star s2" cx="240" cy="30" r="1" />
            <circle class="star s3" cx="250" cy="100" r="1.5" /><circle class="star s4" cx="10" cy="160" r="1" />
            <circle class="star s5" cx="245" cy="190" r="1.5" /><circle class="star s6" cx="130" cy="10" r="1" />
            <circle class="star s7" cx="30" cy="200" r="1.5" />
            <line class="path-line" x1="50" y1="30" x2="210" y2="190" stroke="rgba(0,229,160,0.12)" stroke-width="1" stroke-dasharray="5 4" />
            
            <g class="tier-node node1"><circle cx="50" cy="42" r="14" fill="rgba(0,229,160,0.12)" stroke="rgba(0,229,160,0.5)" stroke-width="1.5" /><circle cx="50" cy="42" r="7" fill="rgba(0,229,160,0.9)" /></g>
            <text x="70" y="38" fill="rgba(0,229,160,0.85)" font-size="9" font-family="monospace" font-weight="700">APPRENTICE</text>
            
            <g class="tier-node node2"><circle cx="80" cy="90" r="14" fill="rgba(59,130,246,0.12)" stroke="rgba(59,130,246,0.5)" stroke-width="1.5" /><circle cx="80" cy="90" r="7" fill="rgba(59,130,246,0.9)" /></g>
            <text x="100" y="86" fill="rgba(59,130,246,0.85)" font-size="9" font-family="monospace" font-weight="700">GUARDIAN</text>
            
            <g class="tier-node node3"><circle cx="130" cy="138" r="14" fill="rgba(139,92,246,0.12)" stroke="rgba(139,92,246,0.5)" stroke-width="1.5" /><circle cx="130" cy="138" r="7" fill="rgba(139,92,246,0.9)" /></g>
            <text x="150" y="134" fill="rgba(139,92,246,0.85)" font-size="9" font-family="monospace" font-weight="700">WARRIOR</text>
            
            <g class="tier-node node4"><circle cx="190" cy="186" r="18" fill="rgba(245,158,11,0.12)" stroke="rgba(245,158,11,0.6)" stroke-width="2" /><circle cx="190" cy="186" r="9" fill="rgba(245,158,11,0.95)" /></g>
            <text x="140" y="205" fill="rgba(245,158,11,0.85)" font-size="9" font-family="monospace" font-weight="700">DRAGON SLAYER</text>
          </svg>
        </div>
        <p class="panel-tagline">Every habit completed brings you closer to greatness.</p>
      </div>
    </div>
    <div class="auth-form-side">
      <div class="form-container">
        <div class="form-header">
          <div class="mobile-logo"><AppLogo size="medium" /></div>
          <h1 class="form-title">Create Account</h1>
          <p class="form-sub">Start tracking your habits in minutes</p>
        </div>
        <form class="form" @submit.prevent="onSubmit">
          <div class="field"><label class="label">Full Name</label><input class="input" v-model="name" placeholder="Your name" /></div>
          <div class="field"><label class="label">Email</label><input class="input" v-model="email" placeholder="you@example.com" /></div>
          <div class="field"><label class="label">Password</label><input class="input" type="password" v-model="password" placeholder="Min. 6 characters" /></div>
          <div class="field"><label class="label">Confirm Password</label><input class="input" type="password" v-model="confirmPassword" placeholder="Repeat password" /></div>
          <button class="btn-submit" type="submit" :disabled="loading">Create Account</button>
          <div v-if="serverError" class="alert-error">{{ serverError }}</div>
          <p class="form-link">Already have an account? <RouterLink to="/login">Sign in</RouterLink></p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { register, login } from '../api/auth';
import AppLogo from '../components/AppLogo.vue';

const name = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const loading = ref(false);
const serverError = ref('');

async function onSubmit() {
  if (password.value !== confirmPassword.value) { serverError.value = "Passwords do not match."; return; }
  loading.value = true;
  try {
    await register({ name: name.value, email: email.value, password: password.value });
    await login({ email: email.value, password: password.value });
    // Signal the dashboard to always show the tour for new signups
    localStorage.setItem('hs_new_signup', '1');
    localStorage.removeItem('hs_tour_done');
    window.location.href = "/dashboard";
  } catch (err) { serverError.value = "Registration failed."; }
  finally { loading.value = false; }
}
</script>

<style scoped>
.auth-page { display: flex; min-height: 100dvh; background: var(--bg); transition: background 0.3s ease; }
.auth-panel { width: 42%; position: relative; overflow: hidden; background: linear-gradient(160deg, var(--card2) 0%, var(--bg2) 100%); border-right: 1px solid var(--border); display: flex; align-items: center; justify-content: center; }
.panel-glow { position: absolute; width: 300px; height: 300px; border-radius: 50%; pointer-events: none; }
.panel-glow-tl { top: -80px; left: -80px; background: radial-gradient(circle, rgba(var(--accent-rgb), 0.14) 0%, transparent 70%); }
.panel-glow-br { bottom: -80px; right: -80px; background: radial-gradient(circle, rgba(var(--accent-2-rgb), 0.12) 0%, transparent 70%); }
.panel-content { position: relative; z-index: 1; padding: 32px; width: 100%; max-width: 360px; }
.panel-brand { display: flex; align-items: center; justify-content: center; margin-bottom: 32px; }

/* SVG Animations */
.svg-scene { width: 100%; margin-bottom: 16px; }
.hero-svg { width: 100%; height: auto; overflow: visible; }

.star { fill: rgba(255,255,255,0.6); animation: twinkle 3s ease-in-out infinite; }
.s1 { animation-delay: 0s; } .s2 { animation-delay: 0.5s; } .s3 { animation-delay: 1s; } .s4 { animation-delay: 1.5s; }
.s5 { animation-delay: 2s; } .s6 { animation-delay: 2.5s; } .s7 { animation-delay: 3s; }
@keyframes twinkle { 0%, 100% { opacity: 0.2; transform: scale(1); } 50% { opacity: 1; transform: scale(1.6); } }

.tier-node { animation: pulse-node 3s ease-in-out infinite; transform-origin: center; }
@keyframes pulse-node { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.08); } }

.path-line { stroke-dasharray: 400; stroke-dashoffset: 400; animation: draw-path 2s ease-out 0.3s forwards; }
@keyframes draw-path { to { stroke-dashoffset: 0; } }

/* Light Theme Overrides for SVG */
.theme-light .star { fill: rgba(37, 99, 235, 0.4); }
.theme-light .path-line { stroke: rgba(37, 99, 235, 0.15); }
.theme-light .tier-node circle:first-child { fill: rgba(37, 99, 235, 0.1); }
.theme-light .node1 text, .theme-light .node2 text, .theme-light .node3 text, .theme-light .node4 text { fill: rgba(0,0,0,0.6) !important; }

.panel-headline { font-family: var(--ff-head); font-size: 1.4rem; font-weight: 700; color: var(--text); margin: 0 0 20px; text-align: center; }
.panel-tagline { text-align: center; color: var(--muted); font-style: italic; font-size: 0.9rem; }

/* Auth Form Styles */
.auth-form-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 24px; overflow-y: auto; }
.form-container { width: 100%; max-width: 400px; }
.form-header { margin-bottom: 32px; }
.form-title { margin: 0 0 8px; font-family: var(--ff-head); font-size: 2rem; font-weight: 700; color: var(--text); }
.form-sub { margin: 0; color: var(--muted); font-size: 0.9rem; }
.form { display: flex; flex-direction: column; gap: 18px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.label { color: var(--text-dim); font-size: 0.85rem; font-weight: 600; }
.input { width: 100%; padding: 14px; background: var(--card); border: 1px solid var(--border); border-radius: 12px; color: var(--text); transition: all 0.2s; }
.input:focus { border-color: var(--accent); outline: none; background: var(--card2); }
.btn-submit { width: 100%; padding: 14px; border-radius: 12px; border: none; background: linear-gradient(135deg, var(--accent), #1dbd88); color: #000; font-family: var(--ff-head); font-size: 1.1rem; font-weight: 800; cursor: pointer; transition: all 0.2s; box-shadow: 0 5px 20px rgba(var(--accent-rgb), 0.2); }
.btn-submit:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(var(--accent-rgb), 0.3); }


.alert-error { padding: 12px; background: rgba(var(--danger-rgb), 0.1); border: 1px solid rgba(var(--danger-rgb), 0.2); border-radius: 10px; color: var(--danger); font-size: 0.85rem; }
.form-link { font-size: 0.9rem; color: var(--muted); margin-top: 24px; text-align: center; }
.form-link a { color: var(--accent); text-decoration: none; font-weight: 700; }
.mobile-logo { display: none; margin-bottom: 24px; }
@media (max-width: 850px) { 
  .auth-panel { display: none; } 
  .auth-form-side { 
    background: var(--bg); /* Ensure solid background */
    background-image: 
                radial-gradient(circle at top right, rgba(var(--accent-rgb), 0.12) 0%, transparent 50%),
                radial-gradient(circle at bottom left, rgba(var(--accent-2-rgb), 0.1) 0%, transparent 50%);
    padding: 30px 20px;
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
