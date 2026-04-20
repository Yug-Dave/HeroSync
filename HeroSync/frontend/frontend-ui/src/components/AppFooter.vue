<template>
  <footer class="footer">
    <div class="container footer-inner">
      <div class="footer-brand">
        <span class="brand-name"><span class="highlight">Hero</span>Sync</span>
        <span class="brand-copy">© 2026 All rights reserved.</span>
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
  border-top: 1px solid rgba(255,255,255,.06);
  background: var(--bg, #0b0f14);
  padding: 0 20px;
}
.footer-inner {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 0; flex-wrap: wrap; gap: 12px;
}
.footer-brand { display: flex; align-items: center; gap: 10px; }
.brand-name {
  font-family: var(--ff-head); font-weight: 700; font-size: .95rem;
  color: var(--text);
}
.brand-name .highlight { color: var(--accent); }
.brand-copy { font-size: .8rem; color: var(--muted); }
.footer-links { display: flex; gap: 18px; }
.footer-links a {
  font-size: .8rem; color: var(--muted);
  text-decoration: none; transition: color .2s;
}
.footer-links a:hover { color: var(--accent); }
</style>
