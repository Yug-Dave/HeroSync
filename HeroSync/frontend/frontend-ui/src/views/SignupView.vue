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

        <h2 class="panel-headline">Choose Your Path</h2>

        <!-- Animated SVG scene (signup variant) -->
        <div class="svg-scene">
          <svg viewBox="0 0 260 220" xmlns="http://www.w3.org/2000/svg" class="hero-svg">
            <!-- Star particles -->
            <circle class="star s1" cx="20"  cy="20"  r="1.5" />
            <circle class="star s2" cx="240" cy="30"  r="1"   />
            <circle class="star s3" cx="250" cy="100" r="1.5" />
            <circle class="star s4" cx="10"  cy="160" r="1"   />
            <circle class="star s5" cx="245" cy="190" r="1.5" />
            <circle class="star s6" cx="130" cy="10"  r="1"   />
            <circle class="star s7" cx="30"  cy="200" r="1.5" />

            <!-- Tier path connector line -->
            <line class="path-line" x1="50" y1="30" x2="210" y2="190"
                  stroke="rgba(0,229,160,0.12)" stroke-width="1" stroke-dasharray="5 4" />

            <!-- Tier node 1 – Apprentice (green) -->
            <g class="tier-node node1">
              <circle cx="50" cy="42" r="14" fill="rgba(0,229,160,0.12)" stroke="rgba(0,229,160,0.5)" stroke-width="1.5" />
              <circle cx="50" cy="42" r="7"  fill="rgba(0,229,160,0.9)" />
              <circle cx="50" cy="42" r="20" fill="rgba(0,229,160,0.04)" />
            </g>
            <text x="70" y="38" fill="rgba(0,229,160,0.85)" font-size="9" font-family="monospace" font-weight="700">APPRENTICE</text>
            <text x="70" y="50" fill="rgba(255,255,255,0.3)" font-size="7.5" font-family="monospace">Level 1 · Begin</text>

            <!-- Tier node 2 – Guardian (blue) -->
            <g class="tier-node node2">
              <circle cx="80" cy="90" r="14" fill="rgba(59,130,246,0.12)" stroke="rgba(59,130,246,0.5)" stroke-width="1.5" />
              <circle cx="80" cy="90" r="7"  fill="rgba(59,130,246,0.9)" />
              <circle cx="80" cy="90" r="20" fill="rgba(59,130,246,0.04)" />
            </g>
            <text x="100" y="86" fill="rgba(59,130,246,0.85)" font-size="9" font-family="monospace" font-weight="700">GUARDIAN</text>
            <text x="100" y="98" fill="rgba(255,255,255,0.3)" font-size="7.5" font-family="monospace">Level 5 · Discipline</text>

            <!-- Tier node 3 – Warrior (purple) -->
            <g class="tier-node node3">
              <circle cx="130" cy="138" r="14" fill="rgba(139,92,246,0.12)" stroke="rgba(139,92,246,0.5)" stroke-width="1.5" />
              <circle cx="130" cy="138" r="7"  fill="rgba(139,92,246,0.9)" />
              <circle cx="130" cy="138" r="20" fill="rgba(139,92,246,0.04)" />
            </g>
            <text x="150" y="134" fill="rgba(139,92,246,0.85)" font-size="9" font-family="monospace" font-weight="700">WARRIOR</text>
            <text x="150" y="146" fill="rgba(255,255,255,0.3)" font-size="7.5" font-family="monospace">Level 10 · Mastery</text>

            <!-- Tier node 4 – Dragon Slayer (gold) -->
            <g class="tier-node node4">
              <circle cx="190" cy="186" r="18" fill="rgba(245,158,11,0.12)" stroke="rgba(245,158,11,0.6)" stroke-width="2" />
              <circle cx="190" cy="186" r="9"  fill="rgba(245,158,11,0.95)" />
              <circle cx="190" cy="186" r="26" fill="rgba(245,158,11,0.05)" />
              <!-- Crown detail on gold tier -->
              <polygon points="190,177 185,182 182,178 183,185 197,185 198,178 195,182"
                       fill="rgba(245,158,11,0.9)" />
            </g>
            <text x="140" y="205" fill="rgba(245,158,11,0.85)" font-size="9" font-family="monospace" font-weight="700">DRAGON SLAYER</text>
            <text x="140" y="216" fill="rgba(255,255,255,0.3)" font-size="7.5" font-family="monospace">Level 15 · Legendary</text>

            <!-- Animated sparkle near gold tier -->
            <g class="sparkle">
              <circle cx="210" cy="168" r="3" fill="rgba(245,158,11,0.8)" />
              <circle cx="220" cy="178" r="2" fill="rgba(245,158,11,0.6)" />
              <circle cx="216" cy="160" r="1.5" fill="rgba(245,158,11,0.5)" />
            </g>
          </svg>
        </div>

        <p class="panel-tagline">Every habit completed brings you closer to greatness.</p>
      </div>
    </div>

    <!-- ── Right panel: signup form ── -->
    <div class="auth-form-side">
      <div class="form-container">
        <div class="form-header">
          <h1 class="form-title">Create Account</h1>
          <p class="form-sub">Start tracking your habits in minutes</p>
        </div>

        <form class="form" @submit.prevent="onSubmit" novalidate>
          <!-- Full Name -->
          <div class="field" :class="{ 'field--error': fieldErrors.name }">
            <label class="label" for="name">Full Name</label>
            <input
              class="input"
              type="text"
              id="name"
              name="name"
              autocomplete="name"
              v-model.trim="name"
              placeholder="Your name"
              @input="clearFieldError('name')"
            />
            <span v-if="fieldErrors.name" class="field-error-msg">{{ fieldErrors.name }}</span>
          </div>

          <!-- Email -->
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

          <!-- Password -->
          <div class="field" :class="{ 'field--error': fieldErrors.password }">
            <label class="label" for="password">Password</label>
            <input
              class="input"
              type="password"
              id="password"
              name="password"
              autocomplete="new-password"
              v-model="password"
              placeholder="Min. 6 characters"
              @input="clearFieldError('password')"
            />
            <span v-if="fieldErrors.password" class="field-error-msg">{{ fieldErrors.password }}</span>
          </div>

          <!-- Confirm Password -->
          <div class="field" :class="{ 'field--error': fieldErrors.confirmPassword }">
            <label class="label" for="confirm">Confirm Password</label>
            <input
              class="input"
              type="password"
              id="confirm"
              name="confirm"
              autocomplete="new-password"
              v-model="confirmPassword"
              placeholder="Repeat password"
              @input="clearFieldError('confirmPassword')"
            />
            <span v-if="fieldErrors.confirmPassword" class="field-error-msg">{{ fieldErrors.confirmPassword }}</span>
          </div>

          <!-- Terms checkbox -->
          <label class="terms-row" :class="{ 'terms-row--error': fieldErrors.terms }">
            <input type="checkbox" v-model="termsAccepted" @change="clearFieldError('terms')" />
            <span>I agree to the Terms &amp; Privacy Policy</span>
          </label>
          <span v-if="fieldErrors.terms" class="field-error-msg terms-error">{{ fieldErrors.terms }}</span>

          <!-- Success message replacing the form -->
          <div v-if="successMsg" class="alert-success" role="alert">
            <svg class="alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            <div>
              <p class="success-title">Account created!</p>
              <p class="success-desc">{{ successMsg }}</p>
            </div>
          </div>

          <button v-if="!successMsg" class="btn-submit" type="submit" :disabled="loading" id="signup-submit-btn">
            <span v-if="loading" class="spinner" />
            <span v-else>Create Account</span>
          </button>

          <!-- Server / general error alert box -->
          <div v-if="serverError" class="alert-error" role="alert">
            <svg class="alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
            <span>{{ serverError }}</span>
          </div>

          <p class="form-link">Already have an account? <RouterLink to="/login">Sign in</RouterLink></p>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '../api/auth';

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default {
  name: 'SignupView',
  data() {
    return {
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
      termsAccepted: false,
      fieldErrors: { name: '', email: '', password: '', confirmPassword: '', terms: '' },
      serverError: '',
      successMsg: '',
      loading: false,
    };
  },
  methods: {
    /** Clears a per-field error as soon as the user corrects the input. */
    clearFieldError(field) {
      this.fieldErrors[field] = '';
      this.serverError = '';
    },

    /** Client-side validation for all signup fields. */
    validate() {
      let valid = true;
      this.fieldErrors = { name: '', email: '', password: '', confirmPassword: '', terms: '' };

      if (!this.name || this.name.length < 2) {
        this.fieldErrors.name = 'Full name must be at least 2 characters.';
        valid = false;
      }

      if (!this.email) {
        this.fieldErrors.email = 'Email is required.';
        valid = false;
      } else if (!EMAIL_RE.test(this.email)) {
        this.fieldErrors.email = 'Please enter a valid email address (e.g. user@example.com).';
        valid = false;
      }

      if (!this.password) {
        this.fieldErrors.password = 'Password is required.';
        valid = false;
      } else if (this.password.length < 6) {
        this.fieldErrors.password = 'Password must be at least 6 characters.';
        valid = false;
      }

      if (!this.confirmPassword) {
        this.fieldErrors.confirmPassword = 'Please confirm your password.';
        valid = false;
      } else if (this.password !== this.confirmPassword) {
        this.fieldErrors.confirmPassword = 'Passwords do not match.';
        valid = false;
      }

      if (!this.termsAccepted) {
        this.fieldErrors.terms = 'You must accept the Terms & Privacy Policy to continue.';
        valid = false;
      }

      return valid;
    },

    async onSubmit() {
      this.serverError = '';
      this.successMsg = '';
      if (!this.validate()) return;

      this.loading = true;
      try {
        await register({ name: this.name, email: this.email, password: this.password });
        this.successMsg = 'We sent a verification link to ' + this.email + '. Please check your inbox and click the link to continue.';
        // clear form fields to prevent double submission
        this.name = '';
        this.email = '';
        this.password = '';
        this.confirmPassword = '';
        this.termsAccepted = false;
      } catch (message) {
        this.serverError = typeof message === 'string'
          ? message
          : 'Sign up failed. The email may already be registered.';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.auth-page { display: flex; min-height: 100dvh; background: var(--bg); }

/* ── Left Panel ── */
.auth-panel {
  width: 42%; position: relative; overflow: hidden;
  background: linear-gradient(160deg, var(--card2) 0%, var(--bg2) 100%);
  border-right: 1px solid var(--border);
  display: flex; align-items: center; justify-content: center;
}
.panel-glow { position: absolute; width: 300px; height: 300px; border-radius: 50%; pointer-events: none; }
.panel-glow-tl { top: -80px; left: -80px; background: radial-gradient(circle, rgba(0,229,160,0.14) 0%, transparent 70%); }
.panel-glow-br { bottom: -80px; right: -80px; background: radial-gradient(circle, rgba(245,158,11,0.12) 0%, transparent 70%); }

.panel-content { position: relative; z-index: 1; padding: 32px; width: 100%; max-width: 340px; }

.panel-brand { 
  display: flex; 
  align-items: center; 
  justify-content: center;
  gap: 14px; 
  margin-bottom: 24px; 
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
.panel-brand-name { font-family: var(--ff-display); font-size: 1.15rem; font-weight: 800; letter-spacing: 2px; color: var(--text); text-transform: uppercase; }
.panel-brand-name .highlight { color: var(--accent); }
.panel-headline   { font-family: var(--ff-head); font-size: 1.4rem; font-weight: 700; color: var(--text); margin: 0 0 20px; text-align: center; }

/* ── SVG Scene ── */
.svg-scene { width: 100%; margin-bottom: 16px; }
.hero-svg  { width: 100%; height: auto; overflow: visible; }

/* Star twinkling */
.star { fill: rgba(255,255,255,0.6); animation: twinkle 3s ease-in-out infinite; }
.s1 { animation-delay: 0s;   } .s2 { animation-delay: 0.5s; } .s3 { animation-delay: 1s;   }
.s4 { animation-delay: 1.5s; } .s5 { animation-delay: 2s;   } .s6 { animation-delay: 2.5s; }
.s7 { animation-delay: 3s;   }

@keyframes twinkle {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50%       { opacity: 1;   transform: scale(1.6); }
}

/* Tier nodes pulsing */
.tier-node { animation: pulse-node 3s ease-in-out infinite; }
.node1 { animation-delay: 0s;   }
.node2 { animation-delay: 0.75s; }
.node3 { animation-delay: 1.5s; }
.node4 { animation-delay: 2.25s; }

@keyframes pulse-node {
  0%, 100% { transform: scale(1); }
  50%       { transform: scale(1.08); }
}

/* Gold tier sparkle */
.sparkle { animation: sparkle-anim 2s ease-in-out infinite; }
@keyframes sparkle-anim {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50%       { opacity: 1;   transform: scale(1.3); }
}

/* Path line drawing */
.path-line { stroke-dasharray: 200; stroke-dashoffset: 200; animation: draw-path 2s ease-out 0.3s forwards; }
@keyframes draw-path { to { stroke-dashoffset: 0; } }

.panel-tagline { font-size: 0.85rem; color: var(--muted); font-style: italic; margin: 0; text-align: center; }

/* ── Right Form Side ── */
.auth-form-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 24px; overflow-y: auto; }
.form-container { width: 100%; max-width: 400px; }
.form-header    { margin-bottom: 24px; }
.form-title     { margin: 0 0 6px; font-family: var(--ff-head); font-size: 1.9rem; font-weight: 700; color: var(--text); }
.form-sub       { margin: 0; color: var(--muted); font-size: 0.88rem; }

.form  { display: flex; flex-direction: column; gap: 14px; }
.field { display: flex; flex-direction: column; gap: 5px; }

.field--error .input {
  border-color: var(--danger);
  box-shadow: 0 0 0 2px rgba(239,68,68,0.2);
}
.field-error-msg {
  font-size: 0.78rem;
  color: var(--danger);
  margin-top: 2px;
}

.terms-row { display: flex; align-items: flex-start; gap: 10px; font-size: 0.85rem; color: var(--muted); cursor: pointer; }
.terms-row input { margin-top: 2px; accent-color: var(--accent); }
.terms-row--error { color: var(--danger); }
.terms-error { margin-top: -8px; }

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

.spinner { width: 18px; height: 18px; border-radius: 50%; border: 2px solid rgba(0,0,0,0.2); border-top-color: #040d09; animation: spin 0.7s linear infinite; }

/* Server success alert box */
.alert-success {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  background: rgba(0,229,160,0.08);
  border: 1px solid rgba(0,229,160,0.3);
  color: var(--accent);
  text-align: left;
  animation: fadeUp 0.4s ease;
}
.alert-success .alert-icon { width: 22px; height: 22px; flex-shrink: 0; }
.success-title { margin: 0 0 4px; font-weight: 700; font-family: var(--ff-head); font-size: 1.05rem; }
.success-desc { margin: 0; font-size: 0.85rem; color: var(--muted); line-height: 1.5; }

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
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
.alert-icon { flex-shrink: 0; width: 18px; height: 18px; margin-top: 1px; }

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%       { transform: translateX(-4px); }
  60%       { transform: translateX(4px); }
}

.form-link { font-size: 0.88rem; color: var(--muted); margin: 0; }
.form-link a { color: var(--accent); text-decoration: none; font-weight: 600; }
.form-link a:hover { text-decoration: underline; }

@media (max-width: 768px) { .auth-panel { display: none; } }
</style>
