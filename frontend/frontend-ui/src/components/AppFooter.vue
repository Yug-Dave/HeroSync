<script setup>
import { Instagram, Linkedin, Mail } from 'lucide-vue-next';
const currentYear = new Date().getFullYear();
</script>

<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="brand-name"><span class="highlight">Hero</span>Sync</span>
        <span class="brand-copy">© {{ currentYear }} All rights reserved.</span>
      </div>

      <div class="social-links">
        <a href="#" class="social-btn"><Instagram size="18" /></a>
        <a href="#" class="social-btn"><Linkedin size="18" /></a>
        <a href="#" class="social-btn"><Mail size="18" /></a>
      </div>

      <div class="footer-links">
        <a href="#">Privacy Policy</a>
        <a href="#">Terms of Service</a>
        <a href="#">Support</a>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  width: 100%;
  min-height: 100px;
  display: flex;
  align-items: center;
  border-top: 1px solid var(--border);
  background: transparent;
  padding: 0 5vw;
  position: relative;
  z-index: 10;
}
.footer-inner {
  display: flex; 
  justify-content: space-between; 
  align-items: center;
  flex-wrap: wrap; 
  gap: 24px;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 0;
}

/* --- Brand --- */
.footer-brand { display: flex; align-items: center; gap: 12px; }
.brand-name {
  font-family: var(--ff-head); font-weight: 800; font-size: 1.1rem;
  color: var(--text);
  letter-spacing: 1px;
}
.brand-name .highlight { 
  color: var(--accent); 
}
:root:not(.theme-light) .brand-name .highlight {
  text-shadow: 0 0 15px rgba(var(--accent-rgb), 0.3); 
}
.brand-copy { 
  font-size: 0.85rem; 
  color: var(--muted); 
  font-family: var(--ff-body); 
}

/* --- Social --- */
.social-links { display: flex; gap: 16px; }
.social-btn {
  width: 36px; height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--border);
  color: var(--muted);
  display: grid;
  place-items: center;
  transition: all 0.3s ease;
}
.social-btn:hover {
  background: rgba(var(--accent-rgb), 0.1);
  border-color: var(--accent);
  color: var(--accent);
  transform: translateY(-3px);
}

/* --- Links --- */
.footer-links { display: flex; gap: 32px; }
.footer-links a {
  font-size: 0.85rem; 
  color: var(--muted);
  text-decoration: none; 
  transition: all 0.3s ease;
  font-family: var(--ff-head);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1.5px;
}
.footer-links a:hover { 
  color: var(--accent); 
  transform: translateY(-2px); 
}

/* --- Theme Support --- */
.theme-light .footer { 
  border-top-color: var(--border2); 
}
.theme-light .social-btn { background: rgba(0, 0, 0, 0.03); }
.theme-light .brand-name { color: var(--text); }
.theme-light .brand-copy { color: var(--muted); }
.theme-light .footer-links a { color: var(--muted); }
.theme-light .footer-links a:hover { color: var(--accent); }

@media (max-width: 900px) {
  .footer-inner { justify-content: center; text-align: center; }
}

@media (max-width: 768px) {
  .footer { padding: 20px 20px; border-top: 1px solid var(--border); }
  .footer-inner { flex-direction: column; gap: 18px; }
  .footer-brand { order: 3; flex-direction: row; flex-wrap: wrap; justify-content: center; gap: 8px 12px; }
  .brand-name .highlight { text-shadow: none !important; }
  .brand-copy { font-size: 0.75rem; }
  .social-links { order: 1; gap: 10px; }
  .footer-links { 
    order: 2; 
    flex-direction: row; 
    flex-wrap: wrap; 
    justify-content: center; 
    gap: 6px 20px; 
    width: 100%;
  }
  .footer-links a { font-size: 0.7rem; letter-spacing: 0.5px; }
}
</style>
