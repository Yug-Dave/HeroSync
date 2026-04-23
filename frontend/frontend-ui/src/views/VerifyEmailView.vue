<template>
  <div class="verify-page">
    <div class="verify-card">
      <!-- Animated status icon -->
      <div class="icon-wrap" :class="status">
        <svg v-if="status === 'loading'" class="spinner-svg" viewBox="0 0 50 50">
          <circle class="path" cx="25" cy="25" r="20" fill="none" stroke-width="4" />
        </svg>
        <svg v-else-if="status === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <circle cx="12" cy="12" r="10" />
          <line x1="12" y1="8" x2="12" y2="12" />
          <line x1="12" y1="16" x2="12.01" y2="16" />
        </svg>
      </div>

      <h1 class="title">{{ title }}</h1>
      <p class="subtitle">{{ subtitle }}</p>

      <!-- Resend form (shown on error) -->
      <div v-if="status === 'error'" class="resend-section">
        <p class="resend-label">Didn't get the email? Request a new link:</p>
        <form class="resend-form" @submit.prevent="doResend" novalidate>
          <input
            class="input"
            type="email"
            v-model.trim="resendEmail"
            placeholder="your@email.com"
            autocomplete="email"
          />
          <button class="btn-resend" type="submit" :disabled="resending">
            <span v-if="resending" class="mini-spinner" />
            <span v-else>Resend Email</span>
          </button>
        </form>
        <p v-if="resendMsg" class="resend-msg" :class="{ success: resendSuccess }">{{ resendMsg }}</p>
      </div>

      <RouterLink v-if="status === 'success'" class="btn-login" to="/login">
        Go to Login
      </RouterLink>
      <RouterLink v-if="status === 'error'" class="btn-back" to="/login">
        Back to Login
      </RouterLink>
    </div>
  </div>
</template>

<script>
import { verifyEmail, resendVerification } from '../api/auth';

export default {
  name: 'VerifyEmailView',
  data() {
    return {
      status: 'loading',   // 'loading' | 'success' | 'error'
      title: 'Verifying your email…',
      subtitle: 'Please wait a moment.',
      resendEmail: '',
      resending: false,
      resendMsg: '',
      resendSuccess: false,
    };
  },
  async created() {
    const token = this.$route.query.token;
    if (!token) {
      this.status   = 'error';
      this.title    = 'Invalid Link';
      this.subtitle = 'No verification token was found in this link.';
      return;
    }
    try {
      await verifyEmail(token);
      this.status   = 'success';
      this.title    = 'Email Verified!';
      this.subtitle = 'Your account is confirmed. You can now log in and start your journey.';
    } catch (message) {
      this.status   = 'error';
      this.title    = 'Link Expired or Invalid';
      this.subtitle = typeof message === 'string'
        ? message
        : 'This verification link is no longer valid. Please request a new one below.';
    }
  },
  methods: {
    async doResend() {
      this.resendMsg     = '';
      this.resendSuccess = false;
      if (!this.resendEmail) {
        this.resendMsg = 'Please enter your email address.';
        return;
      }
      this.resending = true;
      try {
        await resendVerification(this.resendEmail);
        this.resendMsg     = 'Verification email sent! Check your inbox (and spam folder).';
        this.resendSuccess = true;
      } catch (message) {
        this.resendMsg = typeof message === 'string' ? message : 'Could not send email. Please try again.';
      } finally {
        this.resending = false;
      }
    },
  },
};
</script>

<style scoped>
.verify-page {
  min-height: 100dvh;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.verify-card {
  width: 100%;
  max-width: 440px;
  background: var(--card2);
  border: 1px solid var(--border);
  border-radius: 20px;
  padding: 48px 36px;
  text-align: center;
  animation: fadeUp 0.4s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* Status icon */
.icon-wrap {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  margin: 0 auto 24px;
}
.icon-wrap svg { width: 34px; height: 34px; }

.icon-wrap.loading {
  background: rgba(0,229,160,0.1);
  border: 1px solid rgba(0,229,160,0.3);
  color: var(--accent);
}
.icon-wrap.success {
  background: rgba(0,229,160,0.12);
  border: 1px solid rgba(0,229,160,0.4);
  color: var(--accent);
}
.icon-wrap.error {
  background: rgba(239,68,68,0.08);
  border: 1px solid rgba(239,68,68,0.35);
  color: var(--danger);
}

/* SVG spinner */
.spinner-svg {
  animation: spin-icon 1.2s linear infinite;
}
.spinner-svg .path {
  stroke: var(--accent);
  stroke-linecap: round;
  stroke-dasharray: 80;
  stroke-dashoffset: 60;
  animation: dash 1.5s ease-in-out infinite;
}
@keyframes spin-icon { to { transform: rotate(360deg); } }
@keyframes dash {
  0%   { stroke-dashoffset: 80; }
  50%  { stroke-dashoffset: 20; }
  100% { stroke-dashoffset: 80; }
}

.title    { font-family: var(--ff-head); font-size: 1.6rem; font-weight: 700; color: var(--text); margin: 0 0 10px; }
.subtitle { color: var(--muted); font-size: 0.92rem; line-height: 1.6; margin: 0 0 28px; }

/* Resend section */
.resend-section  { margin-bottom: 20px; }
.resend-label    { font-size: 0.85rem; color: var(--muted); margin: 0 0 12px; }
.resend-form     { display: flex; gap: 8px; }
.resend-form .input { flex: 1; }
.btn-resend {
  padding: 11px 16px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, var(--accent), #1dbd88);
  color: #040d09;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: opacity 0.2s;
}
.btn-resend:disabled { opacity: 0.7; cursor: not-allowed; }
.mini-spinner {
  width: 14px; height: 14px; border-radius: 50%;
  border: 2px solid rgba(0,0,0,0.2); border-top-color: #040d09;
  animation: spin 0.7s linear infinite;
}
.resend-msg { font-size: 0.82rem; color: var(--danger); margin: 10px 0 0; }
.resend-msg.success { color: var(--accent); }

/* CTA buttons */
.btn-login, .btn-back {
  display: inline-block;
  padding: 13px 32px;
  border-radius: 10px;
  font-family: var(--ff-head);
  font-weight: 700;
  font-size: 0.95rem;
  text-decoration: none;
  transition: all 0.2s;
}
.btn-login {
  background: linear-gradient(135deg, var(--accent), #1dbd88);
  color: #040d09;
  box-shadow: 0 4px 20px rgba(0,229,160,0.25);
}
.btn-login:hover { box-shadow: 0 6px 28px rgba(0,229,160,0.4); transform: translateY(-1px); }
.btn-back {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--muted);
}
.btn-back:hover { background: rgba(255,255,255,0.05); }
</style>
