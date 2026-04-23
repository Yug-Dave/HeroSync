<template>
  <article class="card">
    <div class="row">
      <div class="text">
        <h3 class="title">{{ achievement.title }}</h3>
        <p class="desc">{{ achievement.description }}</p>
      </div>

      <span class="badge" :class="unlockedAt ? 'ok' : 'locked'">
        {{ unlockedAt ? "Unlocked" : "Locked" }}
      </span>
    </div>

    <div class="meta">
      <div class="pill">
        <span class="k">Rule</span>
        <span class="v">{{ achievement.ruleType }}</span>
      </div>

      <div class="pill">
        <span class="k">Threshold</span>
        <span class="v">{{ achievement.threshold }}</span>
      </div>

      <div class="pill" :class="unlockedAt ? 'pillOk' : 'pillMuted'">
        <span class="k">Unlocked at</span>
        <span class="v">{{ unlockedAt ? short(unlockedAt) : "—" }}</span>
      </div>
    </div>
  </article>
</template>

<script setup>
defineProps({
  achievement: { type: Object, required: true },
  unlockedAt: { type: String, default: "" },
});

function short(s) {
  return String(s).replace("T", " ").slice(0, 19);
}
</script>

<style scoped>
.card{
  background: rgba(17, 24, 39, 0.45);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 18px;
  padding: 16px;
  box-shadow: 0 10px 26px rgba(0,0,0,0.25);
}
.row{
  display:flex;
  align-items:flex-start;
  justify-content:space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.text{ min-width:0; }
.title{
  margin:0;
  color:#fff;
  font-size: 1.05rem;
  letter-spacing: -0.01em;
}
.desc{
  margin:8px 0 0;
  color: rgba(226,232,240,0.8);
  line-height:1.45;
  font-size:0.95rem;
}
.badge{
  flex-shrink:0;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 700;
  border: 1px solid transparent;
}
.badge.ok{
  background: rgba(52,211,153,0.12);
  border-color: rgba(52,211,153,0.22);
  color: #a7f3d0;
}
.badge.locked{
  background: rgba(148,163,184,0.10);
  border-color: rgba(148,163,184,0.16);
  color: rgba(226,232,240,0.85);
}
.meta{
  display:flex;
  gap:10px;
  flex-wrap:wrap;
}
.pill{
  display:flex;
  align-items:center;
  gap:8px;
  padding: 8px 10px;
  border-radius: 12px;
  background: rgba(15,23,42,0.35);
  border: 1px solid rgba(255,255,255,0.07);
}
.k{
  color: rgba(226,232,240,0.68);
  font-size:0.85rem;
}
.v{
  color:#fff;
  font-weight:700;
  font-size:0.9rem;
}
.pillOk{
  background: rgba(52,211,153,0.08);
  border-color: rgba(52,211,153,0.16);
}
.pillMuted{
  background: rgba(148,163,184,0.06);
  border-color: rgba(148,163,184,0.12);
}
</style>
