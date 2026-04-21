<template>
  <div class="auth-page">
    <div class="auth-panel">
      <div class="panel-glow panel-glow-tl" />
      <div class="panel-glow panel-glow-br" />
      <div class="panel-content">
        <div class="panel-brand">
          <div class="panel-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width:65%;height:65%;">
              <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
            </svg>
          </div>
          <span class="panel-brand-name"><span class="highlight">Hero</span>Sync</span>
        </div>
        <h2 class="panel-headline">Choose Your Path</h2>
        <div class="svg-scene">
          <svg viewBox="0 0 260 220" xmlns="http://www.w3.org/2000/svg" class="hero-svg">
            <!-- Original Premium Stars -->
            <circle class="star s1" cx="20" cy="20" r="1.5" /><circle class="star s2" cx="240" cy="30" r="1" />
            <circle class="star s3" cx="250" cy="100" r="1.5" /><circle class="star s4" cx="10" cy="160" r="1" />
            <circle class="star s5" cx="245" cy="190" r="1.5" /><circle class="star s6" cx="130" cy="10" r="1" />
            <circle class="star s7" cx="30" cy="200" r="1.5" />
            
            <line class="path-line" x1="50" y1="30" x2="210" y2="190" stroke="rgba(0,229,160,0.12)" stroke-width="1" stroke-dasharray="5 4" />
            
            <!-- Tier Nodes -->
            <g class="tier-node node1"><circle cx="50" cy="42" r="14" fill="rgba(0,229,160,0.12)" stroke="rgba(0,229,160,0.5)" stroke-width="1.5" /><circle cx="50" cy="42" r="7" fill="rgba(0,229,160,0.9)" /></g>
            <text x="70" y="38" fill="rgba(0,229,160,0.85)" font-size="9" font-family="monospace" font-weight="700">APPRENTICE</text>
            
            <g class="tier-node node2"><circle cx="80" cy="90" r="14" fill="rgba(59,130,246,0.12)" stroke="rgba(59,130,246,0.5)" stroke-width="1.5" /><circle cx="80" cy="90" r="7" fill="rgba(59,130,246,0.9)" /></g>
            <text x="100" y="86" fill="rgba(59,130,246,0.85)" font-size="9" font-family="monospace" font-weight="700">GUARDIAN</text>
            
            <g class="tier-node node3"><circle cx="130" cy="138" r="14" fill="rgba(139,92,246,0.12)" stroke="rgba(139,92,246,0.5)" stroke-width="1.5" /><circle cx="130" cy="138" r="7" fill="rgba(139,92,246,0.9)" /></g>
            <text x="150" y="134" fill="rgba(139,92,246,0.85)" font-size="9" font-family="monospace" font-weight="700">WARRIOR</text>
            
            <g class="tier-node node4"><circle cx="190" cy="186" r="18" fill="rgba(245,158,11,0.12)" stroke="rgba(245,158,11,0.6)" stroke-width="2" /><circle cx="190" cy="186" r="9" fill="rgba(245,158,11,0.95)" /><polygon points="190,177 185,182 182,178 183,185 197,185 198,178 195,182" fill="rgba(245,158,11,0.9)" /></g>
            <text x="140" y="205" fill="rgba(245,158,11,0.85)" font-size="9" font-family="monospace" font-weight="700">DRAGON SLAYER</text>
            
            <g class="sparkle"><circle cx="210" cy="168" r="3" fill="rgba(245,158,11,0.8)" /><circle cx="220" cy="178" r="2" fill="rgba(245,158,11,0.6)" /></g>
          </svg>
        </div>
        <p class="panel-tagline">Every habit completed brings you closer to greatness.</p>
      </div>
    </div>
    <div class="auth-form-side">
      <div class="form-container">
        <div class="form-header">
          <h1 class="form-title">Create Account</h1>
          <p class="form-sub">Start tracking your habits in minutes</p>
        </div>
        <form class="form" @submit.prevent="onSubmit" novalidate>
          <div class="field"><label class="label">Full Name</label><input class="input" v-model="name" placeholder="Your name" /></div>
          <div class="field"><label class="label">Email</label><input class="input" v-model="email" placeholder="you@example.com" /></div>
          <div class="field"><label class="label">Password</label><input class="input" type="password" v-model="password" placeholder="Min. 6 characters" /></div>
          <div class="field"><label class="label">Confirm Password</label><input class="input" type="password" v-model="confirmPassword" placeholder="Repeat password" /></div>
          <button class="btn-submit" type="submit" :disabled="loading">Create Account</button>
          <div v-if="serverError" class="alert-error"><span>{{ serverError }}</span></div>
          <p class="form-link">Already have an account? <RouterLink to="/login">Sign in</RouterLink></p>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { register, login } from '../api/auth';
export default {
  data() { return { name: '', email: '', password: '', confirmPassword: '', loading: false, serverError: '' }; },
  methods: {
    async onSubmit() {
      if (this.password !== this.confirmPassword) { this.serverError = "Passwords do not match."; return; }
      this.loading = true;
      try {
        await register({ name: this.name, email: this.email, password: this.password });
        await login({ email: this.email, password: this.password });
        window.location.href = "/dashboard";
      } catch (err) { this.serverError = "Registration failed."; }
      finally { this.loading = false; }
    }
  }
};
</script>

<style scoped>
.auth-page { display: flex; min-height: 100dvh; background: var(--bg); }
.auth-panel { width: 42%; position: relative; overflow: hidden; background: linear-gradient(160deg, var(--card2) 0%, var(--bg2) 100%); border-right: 1px solid var(--border); display: flex; align-items: center; justify-content: center; }
.panel-glow { position: absolute; width: 300px; height: 300px; border-radius: 50%; pointer-events: none; }
.panel-glow-tl { top: -80px; left: -80px; background: radial-gradient(circle, rgba(0,229,160,0.14) 0%, transparent 70%); }
.panel-glow-br { bottom: -80px; right: -80px; background: radial-gradient(circle, rgba(245,158,11,0.12) 0%, transparent 70%); }
.panel-content { position: relative; z-index: 1; padding: 32px; width: 100%; max-width: 340px; }
.panel-brand { display: flex; align-items: center; justify-content: center; gap: 14px; margin-bottom: 24px; }
.panel-logo { width: 56px; height: 56px; border-radius: 14px; background: linear-gradient(135deg, var(--accent), var(--accent-2)); color: #040d09; display: grid; place-items: center; box-shadow: 0 0 24px rgba(0,229,160,0.4); }
.panel-brand-name { font-family: var(--ff-display); font-size: 1.15rem; font-weight: 800; letter-spacing: 2px; color: var(--text); text-transform: uppercase; }
.panel-brand-name .highlight { color: var(--accent); }
.panel-headline { font-family: var(--ff-head); font-size: 1.4rem; font-weight: 700; color: var(--text); margin: 0 0 20px; text-align: center; }
.svg-scene { width: 100%; margin-bottom: 16px; }
.hero-svg { width: 100%; height: auto; overflow: visible; }

/* RE-ANIMATING STARS */
.star { fill: rgba(255,255,255,0.6); animation: twinkle 3s ease-in-out infinite; }
.s1 { animation-delay: 0s; } .s2 { animation-delay: 0.5s; } .s3 { animation-delay: 1s; }
.s4 { animation-delay: 1.5s; } .s5 { animation-delay: 2s; } .s6 { animation-delay: 2.5s; }
.s7 { animation-delay: 3s; }
@keyframes twinkle { 0%, 100% { opacity: 0.2; transform: scale(1); } 50% { opacity: 1; transform: scale(1.6); } }

/* RE-ANIMATING NODES */
.tier-node { animation: pulse-node 3s ease-in-out infinite; }
@keyframes pulse-node { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.08); } }
.sparkle { animation: sparkle-anim 2s ease-in-out infinite; }
@keyframes sparkle-anim { 0%, 100% { opacity: 0.4; transform: scale(1); } 50% { opacity: 1; transform: scale(1.3); } }
.path-line { stroke-dasharray: 200; stroke-dashoffset: 200; animation: draw-path 2s ease-out 0.3s forwards; }
@keyframes draw-path { to { stroke-dashoffset: 0; } }

/* FORM STYLES */
.auth-form-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 24px; overflow-y: auto; }
.form-container { width: 100%; max-width: 400px; }
.form-header { margin-bottom: 24px; }
.form-title { margin: 0 0 6px; font-family: var(--ff-head); font-size: 1.9rem; font-weight: 700; color: var(--text); }
.form-sub { margin: 0; color: var(--muted); font-size: 0.88rem; }
.form { display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 5px; }
.label { display: block; color: var(--muted); font-size: 0.85rem; font-weight: 500; margin-bottom: 0.3rem; }
.input { width: 100%; padding: 13px; background: var(--card); border: 1px solid var(--border); border-radius: 10px; color: var(--text); transition: all 0.2s; }
.input:focus { border-color: var(--accent); outline: none; }
.btn-submit { width: 100%; padding: 13px; border-radius: 10px; border: none; background: linear-gradient(135deg, var(--accent), #1dbd88); color: #040d09; font-family: var(--ff-head); font-size: 1rem; font-weight: 700; letter-spacing: 0.4px; cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 20px rgba(0,229,160,0.25); }
.alert-error { margin-top: 1rem; padding: 12px; background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.35); border-radius: 10px; color: var(--danger); font-size: 0.875rem; }
.form-link { font-size: 0.88rem; color: var(--muted); margin-top: 1rem; text-align: center; }
.form-link a { color: var(--accent); text-decoration: none; font-weight: 600; }
@media (max-width: 768px) { .auth-panel { display: none; } }
</style>
