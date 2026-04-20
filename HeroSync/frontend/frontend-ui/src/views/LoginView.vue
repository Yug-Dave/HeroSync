<template>
  <div class="auth-page">

    <!-- ── Left panel: animated SVG scene ── -->
    <div class="auth-panel">
      <div class="panel-glow panel-glow-tl" />
      <div class="panel-glow panel-glow-br" />

      <div class="panel-content">
        <div class="panel-brand">
          <div class="panel-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                 stroke-linecap="round" stroke-linejoin="round" style="width:65%;height:65%;">
              <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
            </svg>
          </div>
          <span class="panel-brand-name"><span class="highlight">Hero</span>Sync</span>
        </div>

        <!-- Animated SVG hero scene -->
        <div class="svg-scene">
          <svg viewBox="0 0 280 280" xmlns="http://www.w3.org/2000/svg" class="hero-svg">
            <!-- Star particles -->
            <circle class="star s1" cx="40"  cy="30"  r="1.5" />
            <circle class="star s2" cx="230" cy="50"  r="1"   />
            <circle class="star s3" cx="260" cy="120" r="1.5" />
            <circle class="star s4" cx="20"  cy="180" r="1"   />
            <circle class="star s5" cx="250" cy="210" r="1.5" />
            <circle class="star s6" cx="60"  cy="240" r="1"   />
            <circle class="star s7" cx="200" cy="260" r="1.5" />
            <circle class="star s8" cx="140" cy="20"  r="1"   />

            <!-- Outer orbit ring -->
            <ellipse class="orbit-ring" cx="140" cy="140" rx="110" ry="110"
                     fill="none" stroke="rgba(0,229,160,0.12)" stroke-width="1" stroke-dasharray="6 4" />

            <!-- Mid orbit ring -->
            <ellipse class="orbit-ring-mid" cx="140" cy="140" rx="75" ry="75"
                     fill="none" stroke="rgba(139,92,246,0.15)" stroke-width="1" stroke-dasharray="4 6" />

            <!-- Orbiting dot 1 (green) -->
            <g class="orbit-dot-1">
              <circle cx="140" cy="30" r="7" fill="rgba(0,229,160,0.9)" />
              <circle cx="140" cy="30" r="12" fill="rgba(0,229,160,0.15)" />
            </g>

            <!-- Orbiting dot 2 (purple) -->
            <g class="orbit-dot-2">
              <circle cx="215" cy="140" r="5" fill="rgba(139,92,246,0.9)" />
              <circle cx="215" cy="140" r="10" fill="rgba(139,92,246,0.15)" />
            </g>

            <!-- Orbiting dot 3 (blue) -->
            <g class="orbit-dot-3">
              <circle cx="65" cy="140" r="6" fill="rgba(59,130,246,0.9)" />
              <circle cx="65" cy="140" r="11" fill="rgba(59,130,246,0.12)" />
            </g>

            <!-- Central glow backdrop -->
            <circle cx="140" cy="140" r="52" fill="rgba(0,229,160,0.06)" />
            <circle cx="140" cy="140" r="38" fill="rgba(0,229,160,0.1)" />

            <!-- Central hero emblem -->
            <g class="emblem-float">
              <circle cx="140" cy="140" r="30" fill="rgba(4,13,9,0.9)"
                      stroke="rgba(0,229,160,0.5)" stroke-width="1.5" />
              <polygon points="140,120 128,150 140,143 152,150"
                       fill="rgba(0,229,160,0.9)" />
            </g>

            <!-- XP floating tags -->
            <g class="xp-tag xp1">
              <rect x="92" y="68" width="44" height="18" rx="9"
                    fill="rgba(0,229,160,0.15)" stroke="rgba(0,229,160,0.4)" stroke-width="1" />
              <text x="114" y="81" text-anchor="middle" fill="rgba(0,229,160,0.9)"
                    font-size="9" font-family="monospace" font-weight="700">+50 XP</text>
            </g>
            <g class="xp-tag xp2">
              <rect x="165" y="90" width="44" height="18" rx="9"
                    fill="rgba(139,92,246,0.15)" stroke="rgba(139,92,246,0.4)" stroke-width="1" />
              <text x="187" y="103" text-anchor="middle" fill="rgba(139,92,246,0.9)"
                    font-size="9" font-family="monospace" font-weight="700">+120 XP</text>
            </g>
            <g class="xp-tag xp3">
              <rect x="70" y="168" width="44" height="18" rx="9"
                    fill="rgba(59,130,246,0.15)" stroke="rgba(59,130,246,0.4)" stroke-width="1" />
              <text x="92" y="181" text-anchor="middle" fill="rgba(59,130,246,0.9)"
                    font-size="9" font-family="monospace" font-weight="700">+80 XP</text>
            </g>

            <!-- Progress bar rows -->
            <g class="stat-bars">
              <rect x="50" y="200" width="180" height="6" rx="3" fill="rgba(255,255,255,0.05)" />
              <rect class="bar bar1" x="50" y="200" width="0" height="6" rx="3"
                    fill="url(#barGrad1)" />

              <rect x="50" y="215" width="180" height="6" rx="3" fill="rgba(255,255,255,0.05)" />
              <rect class="bar bar2" x="50" y="215" width="0" height="6" rx="3"
                    fill="url(#barGrad2)" />

              <rect x="50" y="230" width="180" height="6" rx="3" fill="rgba(255,255,255,0.05)" />
              <rect class="bar bar3" x="50" y="230" width="0" height="6" rx="3"
                    fill="url(#barGrad3)" />
            </g>

            <!-- Stat bar labels -->
            <text x="50" y="198" fill="rgba(255,255,255,0.3)" font-size="7" font-family="monospace">STREAK</text>
            <text x="50" y="213" fill="rgba(255,255,255,0.3)" font-size="7" font-family="monospace">HABITS</text>
            <text x="50" y="228" fill="rgba(255,255,255,0.3)" font-size="7" font-family="monospace">XP</text>

            <defs>
              <linearGradient id="barGrad1" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#00e5a0" />
                <stop offset="100%" stop-color="#1dbd88" />
              </linearGradient>
              <linearGradient id="barGrad2" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#8b5cf6" />
                <stop offset="100%" stop-color="#6d28d9" />
              </linearGradient>
              <linearGradient id="barGrad3" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#3b82f6" />
                <stop offset="100%" stop-color="#1d4ed8" />
              </linearGradient>
            </defs>
          </svg>
        </div>

        <p class="panel-tagline">Build habits. Level up your life.</p>
      </div>
    </div>

    <!-- ── Right panel: login form ── -->
    <div class="auth-form-side">
      <div class="form-container">
        <div class="form-header">
          <h1 class="form-title">Welcome back</h1>
          <p class="form-sub">Sign in to continue your journey</p>
        </div>

        <form class="form" @submit.prevent="onSubmit" novalidate>
          <!-- Email field -->
          <div class="field" :class="{ 'field--error': fieldErrors.email }">
            <label class="label" for="email">Email</label>
            <input
              class="input"
              type="email"
              id="email"
              name="email"
              autocomplete="email"
              v-model.trim="email"
              placeholder="you@example.com"
              @input="clearFieldError('email')"
            />
            <span v-if="fieldErrors.email" class="field-error-msg">{{ fieldErrors.email }}</span>
          </div>

          <!-- Password field -->
          <div class="field" :class="{ 'field--error': fieldErrors.password }">
            <label class="label" for="password">Password</label>
            <input
              class="input"
              type="password"
              id="password"
              name="password"
              autocomplete="current-password"
              v-model="password"
              placeholder="••••••••"
              @input="clearFieldError('password')"
            />
            <span v-if="fieldErrors.password" class="field-error-msg">{{ fieldErrors.password }}</span>
          </div>

          <button class="btn-submit" type="submit" :disabled="loading" id="login-submit-btn">
            <span v-if="loading" class="spinner" />
            <span v-else>Sign In</span>
          </button>

          <!-- Server / general error alert box with Resend Verification support -->
          <div v-if="serverError" class="alert-error" role="alert">
            <svg class="alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
            <div class="alert-content">
              <span>{{ displayErrorMsg }}</span>
              <div v-if="needsVerification" class="resend-action">
                <button type="button" class="btn-resend" @click="doResend" :disabled="resending">
                  <span v-if="resending" class="mini-spinner" />
                  <span v-else>Resend email</span>
                </button>
                <span v-if="resendMsg" class="resend-msg" :class="{ success: resendSuccess }">{{ resendMsg }}</span>
              </div>
            </div>
          </div>

          <p class="form-link">
            New here? <RouterLink to="/signup">Create an account</RouterLink>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { login, resendVerification } from '../api/auth';

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      fieldErrors: { email: '', password: '' },
      serverError: '',
      loading: false,
      resending: false,
      resendMsg: '',
      resendSuccess: false,
    };
  },
  computed: {
    needsVerification() {
      return this.serverError && this.serverError.startsWith('EMAIL_NOT_VERIFIED:');
    },
    displayErrorMsg() {
      if (this.needsVerification) {
        return this.serverError.replace('EMAIL_NOT_VERIFIED:', '').trim();
      }
      return this.serverError;
    }
  },
  methods: {
    /** Clears a per-field error when the user starts correcting it. */
    clearFieldError(field) {
      this.fieldErrors[field] = '';
      this.serverError = '';
    },

    /** Validates all fields client-side before hitting the network. */
    validate() {
      let valid = true;
      this.fieldErrors = { email: '', password: '' };

      if (!this.email) {
        this.fieldErrors.email = 'Email is required.';
        valid = false;
      } else if (!EMAIL_RE.test(this.email)) {
        this.fieldErrors.email = 'Please enter a valid email address.';
        valid = false;
      }

      if (!this.password) {
        this.fieldErrors.password = 'Password is required.';
        valid = false;
      } else if (this.password.length < 6) {
        this.fieldErrors.password = 'Password must be at least 6 characters.';
        valid = false;
      }

      return valid;
    },

    async onSubmit() {
      this.serverError = '';
      this.resendMsg = '';
      if (!this.validate()) return;

      this.loading = true;
      try {
        await login({ email: this.email, password: this.password });
        await this.$router.push('/dashboard');
      } catch (message) {
        // auth.js already extracted a clean string via extractError
        this.serverError = typeof message === 'string'
          ? message
          : 'Login failed. Please check your credentials.';
      } finally {
        this.loading = false;
      }
    },

    async doResend() {
      if (!this.email) return;
      this.resendMsg = '';
      this.resendSuccess = false;
      this.resending = true;
      try {
        await resendVerification(this.email);
        this.resendMsg = 'Email resent. Check your inbox.';
        this.resendSuccess = true;
      } catch (message) {
        this.resendMsg = typeof message === 'string' ? message : 'Failed to resend.';
      } finally {
        this.resending = false;
      }
    }
  },
};
</script>

<style scoped>
.auth-page {
  display: flex;
  min-height: 100dvh;
  background: var(--bg);
}

/* ── Left Panel ── */
.auth-panel {
  width: 42%;
  position: relative;
  overflow: hidden;
  background: linear-gradient(160deg, var(--card2) 0%, var(--bg2) 100%);
  border-right: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
}
.panel-glow {
  position: absolute;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  pointer-events: none;
}
.panel-glow-tl { top: -80px; left: -80px; background: radial-gradient(circle, rgba(0,229,160,0.15) 0%, transparent 70%); }
.panel-glow-br { bottom: -80px; right: -80px; background: radial-gradient(circle, rgba(59,130,246,0.12) 0%, transparent 70%); }

.panel-content {
  position: relative;
  z-index: 1;
  padding: 40px 32px;
  width: 100%;
  max-width: 340px;
}

.panel-brand { 
  display: flex; 
  align-items: center; 
  justify-content: center;
  gap: 14px; 
  margin-bottom: 32px; 
}
.panel-logo {
  width: 56px; height: 56px; border-radius: 14px;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #040d09; display: grid; place-items: center;
  box-shadow: 0 0 24px rgba(0,229,160,0.4);
}
.panel-logo svg {
  width: 60% !important; height: 60% !important;
}
.panel-brand-name {
  font-family: var(--ff-display);
  font-size: 1.15rem;
  font-weight: 800;
  letter-spacing: 2px;
  color: var(--text);
  text-transform: uppercase;
}
.panel-brand-name .highlight { color: var(--accent); }

/* ── SVG Scene ── */
.svg-scene {
  width: 100%;
  margin-bottom: 24px;
}
.hero-svg {
  width: 100%;
  height: auto;
  overflow: visible;
}

/* Star twinkling */
.star { fill: rgba(255,255,255,0.6); animation: twinkle 3s ease-in-out infinite; }
.s1  { animation-delay: 0s;    }
.s2  { animation-delay: 0.4s;  }
.s3  { animation-delay: 0.8s;  }
.s4  { animation-delay: 1.2s;  }
.s5  { animation-delay: 1.6s;  }
.s6  { animation-delay: 2.0s;  }
.s7  { animation-delay: 2.4s;  }
.s8  { animation-delay: 2.8s;  }

@keyframes twinkle {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50%       { opacity: 1;   transform: scale(1.6); }
}

/* Orbit rings rotating in opposite directions */
.orbit-ring     { animation: spin-cw  20s linear infinite; transform-origin: 140px 140px; }
.orbit-ring-mid { animation: spin-ccw 14s linear infinite; transform-origin: 140px 140px; }

@keyframes spin-cw  { to { transform: rotate(360deg); } }
@keyframes spin-ccw { to { transform: rotate(-360deg); } }

/* Orbiting dots */
.orbit-dot-1 { animation: orbit1 8s linear  infinite; transform-origin: 140px 140px; }
.orbit-dot-2 { animation: orbit2 12s linear infinite; transform-origin: 140px 140px; }
.orbit-dot-3 { animation: orbit3 10s linear infinite; transform-origin: 140px 140px; }

@keyframes orbit1 { to { transform: rotate(360deg); } }
@keyframes orbit2 { to { transform: rotate(-360deg); } }
@keyframes orbit3 { 0% { transform: rotate(0); } 100% { transform: rotate(360deg); } }

/* Central emblem bobbing */
.emblem-float { animation: float 4s ease-in-out infinite; transform-origin: 140px 140px; }
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(-6px); }
}

/* Floating XP tags */
.xp-tag { animation: rise 6s ease-in-out infinite; }
.xp1    { animation-delay: 0s; }
.xp2    { animation-delay: 2s; }
.xp3    { animation-delay: 4s; }

@keyframes rise {
  0%   { opacity: 0;   transform: translateY(0); }
  15%  { opacity: 1; }
  70%  { opacity: 0.8; transform: translateY(-18px); }
  100% { opacity: 0;   transform: translateY(-28px); }
}

/* Progress bars filling up */
.bar1 { animation: fill-bar1 3s ease-out 0.5s forwards; }
.bar2 { animation: fill-bar2 3s ease-out 1.0s forwards; }
.bar3 { animation: fill-bar3 3s ease-out 1.5s forwards; }

@keyframes fill-bar1 { to { width: 144px; } }
@keyframes fill-bar2 { to { width: 108px; } }
@keyframes fill-bar3 { to { width: 162px; } }

.panel-tagline { font-size: 0.9rem; color: var(--muted); font-style: italic; margin: 0; text-align: center; }

/* ── Right Form Side ── */
.auth-form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
}
.form-container { width: 100%; max-width: 400px; }
.form-header    { margin-bottom: 28px; }
.form-title     { margin: 0 0 6px; font-family: var(--ff-head); font-size: 2rem; font-weight: 700; color: var(--text); }
.form-sub       { margin: 0; color: var(--muted); font-size: 0.9rem; }

.form  { display: flex; flex-direction: column; gap: 18px; }
.field { display: flex; flex-direction: column; gap: 6px; }

.field--error .input {
  border-color: var(--danger);
  box-shadow: 0 0 0 2px rgba(239,68,68,0.2);
}
.field-error-msg {
  font-size: 0.78rem;
  color: var(--danger);
  margin-top: 2px;
}

.btn-submit {
  width: 100%; padding: 13px; border-radius: 10px; border: none;
  background: linear-gradient(135deg, var(--accent), #1dbd88);
  color: #040d09; font-family: var(--ff-head); font-size: 1rem; font-weight: 700;
  letter-spacing: 0.4px; cursor: pointer; transition: all 0.2s;
  box-shadow: 0 4px 20px rgba(0,229,160,0.25);
  display: flex; align-items: center; justify-content: center;
}
.btn-submit:hover:not(:disabled) { box-shadow: 0 6px 28px rgba(0,229,160,0.4); transform: translateY(-1px); }
.btn-submit:disabled { opacity: 0.7; cursor: not-allowed; }

.spinner {
  width: 18px; height: 18px; border-radius: 50%;
  border: 2px solid rgba(0,0,0,0.2); border-top-color: #040d09;
  animation: spin 0.7s linear infinite;
}

/* Server error alert box */
.alert-error {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(239,68,68,0.08);
  border: 1px solid rgba(239,68,68,0.35);
  color: var(--danger);
  font-size: 0.875rem;
  line-height: 1.4;
  animation: shake 0.3s ease;
}
.alert-content { display: flex; flex-direction: column; gap: 8px; }
.alert-icon {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  margin-top: 1px;
}

.resend-action { display: flex; align-items: center; gap: 10px; margin-top: 4px; }
.btn-resend {
  background: rgba(239,68,68,0.15);
  color: var(--danger);
  border: 1px solid rgba(239,68,68,0.3);
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}
.btn-resend:hover:not(:disabled) { background: rgba(239,68,68,0.25); }
.btn-resend:disabled { opacity: 0.7; cursor: not-allowed; }
.mini-spinner {
  width: 12px; height: 12px; border-radius: 50%;
  border: 2px solid rgba(239,68,68,0.3); border-top-color: var(--danger);
  animation: spin 0.7s linear infinite;
}
.resend-msg { font-size: 0.75rem; }
.resend-msg.success { color: var(--accent); }

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%       { transform: translateX(-4px); }
  60%       { transform: translateX(4px); }
}

.form-link { font-size: 0.88rem; color: var(--muted); margin: 0; }
.form-link a { color: var(--accent); text-decoration: none; font-weight: 600; }
.form-link a:hover { text-decoration: underline; }

@media (max-width: 768px) {
  .auth-panel    { display: none; }
  .auth-form-side { padding: 60px 24px; }
}
</style>
