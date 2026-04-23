<script setup>
import { Instagram, Linkedin, Mail } from 'lucide-vue-next';
import AppLogo from './AppLogo.vue';
</script>

<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-grid">
        <div class="brand-col">
          <AppLogo size="medium" style="margin-bottom: 24px" />
          <p class="brand-desc">
            Build better habits, one day at a time. Track your progress, stay motivated, and achieve your goals with HeroSync.
          </p>
          <div class="social-links">
            <a href="#" class="social-btn"><Instagram size="20" /></a>
            <a href="#" class="social-btn"><Linkedin size="20" /></a>
            <a href="#" class="social-btn"><Mail size="20" /></a>
          </div>
        </div>

        <div class="link-col">
          <h4>Product</h4>
          <ul>
            <li><a href="#">Features</a></li>
            <li><a href="#">Pricing</a></li>
            <li><a href="#">How It Works</a></li>
          </ul>
        </div>

        <div class="link-col">
          <h4>Company</h4>
          <ul>
            <li><a href="#">About Us</a></li>
            <li><a href="#">Blog</a></li>
            <li><a href="#">Contact</a></li>
          </ul>
        </div>

        <div class="link-col">
          <h4>Legal</h4>
          <ul>
            <li><a href="#">Privacy Policy</a></li>
            <li><a href="#">Terms of Service</a></li>
          </ul>
        </div>
      </div>
    </div>
    
    <div class="footer-bottom-wrapper">
      <div class="bottom-content">
        <div class="copyright">
          &copy; 2026 HeroSync. All rights reserved.
        </div>
        <div class="bottom-links">
          <a href="#">Privacy</a>
          <a href="#">Terms</a>
          <a href="#">Cookies</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  width: 100%;
  background-color: transparent;
  color: var(--text-dim);
  padding: 80px 0 0;
  border-top: 1px solid var(--border);
}

.container { max-width: 1300px; margin: 0 auto; padding: 0 40px; }

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 60px;
  margin-bottom: 80px;
}

.brand-col { max-width: 360px; }
.brand-desc { font-size: 1rem; line-height: 1.7; margin-bottom: 30px; color: var(--text-dim); }

.social-links { display: flex; gap: 12px; }
.social-btn {
  width: 40px; height: 40px;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border);
  border-radius: 12px;
  display: grid; place-items: center;
  color: var(--text-dim);
  transition: all 0.3s ease;
}
.social-btn:hover { border-color: var(--accent); color: var(--accent); transform: translateY(-3px); }

.link-col h4 { color: var(--text); font-family: var(--ff-head); font-size: 1.1rem; margin-bottom: 24px; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; }
.link-col ul { list-style: none; padding: 0; margin: 0; }
.link-col li { margin-bottom: 12px; }
.link-col a { color: var(--text-dim); text-decoration: none; font-size: 0.95rem; transition: 0.2s; }
.link-col a:hover { color: var(--accent); }

.footer-bottom-wrapper {
  border-top: 1px solid var(--border);
  padding: 24px 0;
}

.bottom-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 40px;
  color: var(--muted);
}

.bottom-links { display: flex; gap: 32px; }
.bottom-links a { color: var(--muted); text-decoration: none; transition: 0.2s; }
.bottom-links a:hover { color: var(--text); }

@media (max-width: 1000px) {
  .footer-grid { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 600px) {
  .footer-grid { grid-template-columns: 1fr; gap: 40px; }
  .bottom-content { flex-direction: column; gap: 20px; text-align: center; }
}
</style>
