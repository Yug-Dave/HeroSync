<script setup>
import { ref, watch, computed } from "vue";

const props = defineProps({
  isOpen: { type: Boolean, default: false },
  habits: { type: Array, default: () => [] }, // [{ id, name, ... }]
});

const emit = defineEmits(["close", "save"]);

const form = ref({
  title: "",
  description: "",
  habitId: "",       // "" means solo goal
  targetCount: "",   // only for habit-linked
  deadline: "",      // yyyy-mm-dd
});

const isHabitLinked = computed(() => String(form.value.habitId || "").trim() !== "");

watch(
  () => props.isOpen,
  (open) => {
    if (!open) return;
    // reset on open
    form.value = {
      title: "",
      description: "",
      habitId: "",
      targetCount: "",
      deadline: "",
    };
  }
);

function handleSubmit() {
  const title = (form.value.title || "").trim();
  if (!title) {
    alert("Title is required");
    return;
  }

  const payload = {
    title,
    description: (form.value.description || "").trim() || null,
    deadline: (form.value.deadline || "").trim() || null,
  };

  if (isHabitLinked.value) {
    const habitIdNum = Number(form.value.habitId);
    const targetNum = Number(form.value.targetCount);

    if (!Number.isFinite(habitIdNum) || habitIdNum <= 0) {
      alert("Please select a valid habit");
      return;
    }
    if (!Number.isFinite(targetNum) || targetNum <= 0) {
      alert("Target count must be a positive number");
      return;
    }

    payload.habitId = habitIdNum;
    payload.targetCount = targetNum;
  } else {
    // solo goal: do not send habitId/targetCount
    payload.habitId = null;
    payload.targetCount = null;
  }

  emit("save", payload);
}
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3>New Goal</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <div class="form-group">
          <label>Goal Title</label>
          <input v-model="form.title" placeholder="e.g. Run 50km this month" autofocus />
        </div>

        <div class="form-group">
          <label>Description (Optional)</label>
          <input v-model="form.description" placeholder="e.g. Build stamina and consistency" />
        </div>

        <div class="form-group">
          <label>Link to Habit (Optional)</label>
          <select v-model="form.habitId">
            <option value="">No (Solo Goal)</option>
            <option v-for="h in habits" :key="h.id" :value="String(h.id)">
              {{ h.name }}
            </option>
          </select>
        </div>

        <div v-if="isHabitLinked" class="form-group">
          <label>Target Count</label>
          <input
            v-model="form.targetCount"
            type="number"
            min="1"
            placeholder="e.g. 30"
          />
        </div>

        <div class="form-group">
          <label>Deadline (Optional)</label>
          <input v-model="form.deadline" type="date" />
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn ghost" @click="$emit('close')">Cancel</button>
        <button class="btn primary" @click="handleSubmit">Create Goal</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--card);
  padding: 20px;
  border-radius: 12px;
  width: 420px;
  border: 1px solid var(--border);
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.modal-header h3 { margin: 0; }

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--muted);
  cursor: pointer;
}

.form-group { margin-bottom: 12px; }

.form-group label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--muted);
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
}
</style>
