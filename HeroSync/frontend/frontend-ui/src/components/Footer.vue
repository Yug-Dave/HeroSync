<script setup>
import { Instagram, Linkedin, Mail } from 'lucide-vue-next';
</script>

<template>
  <footer class="footer">
    <div class="container">

      <div class="footer-grid">

        <div class="brand-col">
          <div class="logo-group">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width:65%;height:65%;">
                <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
              </svg>
            </div>
            <span class="logo-text"><span class="highlight">Hero</span>Sync</span>
          </div>
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
            <li><a href="#">Testimonials</a></li>
            <li><a href="#">FAQ</a></li>
          </ul>
        </div>

        <div class="link-col">
          <h4>Company</h4>
          <ul>
            <li><a href="#">About Us</a></li>
            <li><a href="#">Blog</a></li>
            <li><a href="#">Careers</a></li>
            <li><a href="#">Press Kit</a></li>
            <li><a href="#">Contact</a></li>
          </ul>
        </div>

        <div class="link-col">
          <h4>Legal</h4>
          <ul>
            <li><a href="#">Privacy Policy</a></li>
            <li><a href="#">Terms of Service</a></li>
            <li><a href="#">Cookie Policy</a></li>
            <li><a href="#">GDPR</a></li>
            <li><a href="#">Licenses</a></li>
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
  background-color: #05091a;
  color: #9ca3af;
  padding: 60px 0 0;
  border-top: 1px solid #1f2937;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 40px;
  margin-bottom: 60px;
  width: 100%;
}

.brand-col {
  max-width: 320px;
}

.logo-group { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.logo-icon {
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  inline-size:34px;
  block-size:34px;
  border-radius:10px;
  display:grid;
  place-items:center;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color:#071216; font-weight:800; box-shadow: var(--shadow);
}
.logo-text { font-size: 1.5rem; font-weight: 700; color: white; }
.logo-text .highlight { color: var(--accent); }

.brand-desc {
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 24px;
  color: #9ca3af;
}

.social-links { display: flex; gap: 12px; }
.social-btn {
  width: 36px; height: 36px;
  background: #111827;
  border: 1px solid #1f2937;
  border-radius: 8px;
  display: grid; place-items: center;
  color: #9ca3af;
  transition: 0.2s;
}
.social-btn:hover { border-color: #22d3ee; color: #22d3ee; }

.link-col h4 { color: white; font-size: 1rem; margin-bottom: 20px; font-weight: 600; }
.link-col ul { list-style: none; padding: 0; margin: 0; }
.link-col li { margin-bottom: 12px; }
.link-col a { color: #9ca3af; text-decoration: none; font-size: 0.9rem; transition: 0.2s; }
.link-col a:hover { color: #22d3ee; }

.bottom-links { display: flex; gap: 24px; }
.bottom-links a { color: #9ca3af; text-decoration: none; transition: 0.2s; }
.bottom-links a:hover { color: white; }

.footer-bottom-wrapper {
  width: 80%;
  border-top: 1px solid #1f2937;
  background-color: #05091a;
  padding: 12px 0;
  margin: auto;
  display: flex;
  justify-content: center;
}

.bottom-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;

  width: 100%;
  max-width: 900px;
  padding: 0 20px;
}

/* Responsive */
@media (max-width: 900px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
}

@media (max-width: 600px) {
  .footer-grid {
    grid-template-columns: 1fr;
    text-align: left;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  .brand-col { max-width: 100%; }
}
</style>
