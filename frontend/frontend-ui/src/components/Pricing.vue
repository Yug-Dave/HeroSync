<script setup>
import { Check, X, ShieldCheck } from 'lucide-vue-next';
import { ref } from 'vue';
import AppFooter from './AppFooter.vue';

const isYearly = ref(true);

const plans = [
  {
    name: "Apprentice",
    price: "0",
    desc: "Perfect for starting your journey.",
    features: ["Unlimited Basic Habits", "Core Statistics", "Standard XP Rewards", "1 AI Companion"],
    cta: "Start Free",
    popular: false
  },
  {
    name: "Dragon Slayer",
    price: "9.99",
    desc: "For serious heroes who demand the best.",
    features: ["All 3 AI Companions", "Advanced Flow Analytics", "Custom Quest Creator", "Priority Support", "Rare Avatars"],
    cta: "Ascend Now",
    popular: true
  }
];
</script>

<template>
  <section id="pricing" class="pricing-section">
    <div class="split-container">
      <!-- Left Side: Title & Toggle -->
      <div class="title-side">
        <div class="hero-pill">Investment</div>
        <h2 class="title">Invest in <br/><span class="gradient-text">Your Growth</span></h2>
        <p class="subtitle">Start your hero's journey for free, upgrade when you're ready to dominate.</p>

        <div class="pricing-toggle">
          <span :class="{ active: !isYearly }">Monthly</span>
          <button class="toggle-btn" @click="isYearly = !isYearly">
            <div class="toggle-circle" :style="{ transform: isYearly ? 'translateX(24px)' : 'translateX(0)' }"></div>
          </button>
          <span :class="{ active: isYearly }">Yearly <span class="badge">-20%</span></span>
        </div>
      </div>

      <!-- Right Side: Plan Cards -->
      <div class="content-side">
        <div class="pricing-grid">
          <div v-for="plan in plans" :key="plan.name" 
               class="pricing-card" :class="{ popular: plan.popular }">
            <div v-if="plan.popular" class="popular-tag">Most Heroic</div>
            
            <h3 class="plan-name">{{ plan.name }}</h3>
            <div class="price-box">
              <span class="currency">$</span>
              <span class="val">{{ isYearly ? (parseFloat(plan.price) * 0.8).toFixed(1) : plan.price }}</span>
              <span class="period">/mo</span>
            </div>
            
            <div class="feature-list">
              <div v-for="f in plan.features" :key="f" class="feature-item">
                <Check size="18" class="check" />
                <span>{{ f }}</span>
              </div>
            </div>

            <router-link to="/signup" class="btn-plan" :class="{ primary: plan.popular }">{{ plan.cta }}</router-link>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Footer integrated at the bottom of the section -->
    <div class="footer-wrap">
      <AppFooter />
    </div>
  </section>
</template>

<style scoped>
.pricing-section {
  min-height: 100vh; width: 100%;
  display: flex; flex-direction: column; align-items: center;
  padding: 120px 5vw 0; position: relative;
}

.split-container {
  display: flex; width: 100%; max-width: 1400px; gap: 80px; align-items: center;
  flex: 1; padding-bottom: 60px;
}

/* --- LEFT SIDE --- */
.title-side { flex: 0.8; text-align: left; }

.title { font-family: var(--ff-display); font-size: clamp(2.5rem, 5vw, 4rem); color: var(--text); font-weight: 950; line-height: 1.05; margin-bottom: 24px; letter-spacing: -2px; }
.gradient-text { background: linear-gradient(135deg, var(--accent), var(--accent-2)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.subtitle { color: var(--text-dim); font-size: 1.2rem; line-height: 1.6; margin-bottom: 40px; max-width: 450px; }

.pricing-toggle { display: flex; align-items: center; gap: 16px; font-family: var(--ff-head); font-weight: 800; color: var(--muted); text-transform: uppercase; letter-spacing: 1px; font-size: 0.8rem; }
.pricing-toggle .active { color: var(--text); }
.toggle-btn { width: 56px; height: 32px; background: rgba(255,255,255,0.05); border-radius: 16px; border: 1px solid var(--border); position: relative; cursor: pointer; }
.toggle-circle { position: absolute; top: 4px; left: 4px; width: 22px; height: 22px; background: var(--accent); border-radius: 50%; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.badge { background: rgba(var(--accent-rgb), 0.15); color: var(--accent); font-size: 0.7rem; padding: 4px 8px; border-radius: 10px; margin-left: 5px; }

/* --- RIGHT SIDE --- */
.content-side { flex: 1.2; }

.pricing-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 30px; }

.pricing-card {
  background: rgba(30, 41, 59, 0.4); backdrop-filter: blur(15px);
  border: 1px solid var(--border); border-radius: 32px;
  padding: 48px; display: flex; flex-direction: column; position: relative;
  transition: all 0.4s ease;
}
.pricing-card.popular { border-color: var(--accent); transform: scale(1.05); box-shadow: 0 30px 60px rgba(var(--accent-rgb), 0.15); }

.popular-tag {
  position: absolute; top: 20px; right: 30px;
  background: var(--accent); color: #000;
  font-family: var(--ff-head); font-weight: 800; font-size: 0.75rem;
  padding: 6px 14px; border-radius: 30px; text-transform: uppercase;
}

.plan-name { font-family: var(--ff-head); font-size: 1.6rem; color: var(--text); font-weight: 800; margin-bottom: 20px; }
.price-box { display: flex; align-items: baseline; gap: 4px; margin-bottom: 32px; }
.currency { font-size: 1.5rem; color: var(--muted); font-weight: 600; }
.val { font-size: 4rem; font-weight: 950; color: var(--text); font-family: var(--ff-display); letter-spacing: -2px; }
.period { color: var(--muted); font-weight: 600; }

.feature-list { flex: 1; display: flex; flex-direction: column; gap: 14px; margin-bottom: 40px; }
.feature-item { display: flex; align-items: center; gap: 12px; color: var(--text-dim); font-size: 1.05rem; }
.check { color: var(--accent); }

.btn-plan {
  width: 100%; padding: 18px; border-radius: 14px; text-decoration: none; text-align: center;
  font-family: var(--ff-head); font-weight: 800; font-size: 1.1rem;
  background: rgba(255,255,255,0.05); color: var(--text); border: 1px solid var(--border2);
  transition: all 0.3s;
}
.btn-plan.primary { background: var(--accent); color: #000; border: none; }
.btn-plan:hover { transform: translateY(-3px); filter: brightness(1.1); }

.footer-wrap { width: 100%; margin-top: auto; }

@media (max-width: 1000px) {
  .split-container { flex-direction: column; gap: 40px; align-items: flex-start; }
  .pricing-section { height: auto; padding: 120px 20px 40px; }
  .pricing-card.popular { transform: none; }
}

.theme-light .pricing-card { background: rgba(255, 255, 255, 0.7); border-color: #e2e8f0; }
</style>
