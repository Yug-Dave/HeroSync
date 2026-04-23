<script setup>
import { computed } from 'vue';
import { Line } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, LineElement, CategoryScale, LinearScale, PointElement, Filler } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, LineElement, CategoryScale, LinearScale, PointElement, Filler);

const props = defineProps({
  // This is the history array from your API
  history: { type: Array, default: () => [] }
});

const chartData = computed(() => {
  // If history is empty, show the "No Data" placeholder
  if (!props.history || props.history.length === 0) {
    return {
      labels: ['No Data'],
      datasets: [{ data: [0], backgroundColor: 'transparent', borderColor: '#2d2d44' }]
    };
  }

  const recentHistory = props.history.slice(-7);

  const labels = recentHistory.map(item => {
    // Try 'date', then 'createdAt', then default to today if both fail
    const dateVal = item.date || item.createdAt || new Date();
    const date = new Date(dateVal);
    return date.toLocaleDateString('en-US', { weekday: 'short' });
  });

  const dataPoints = recentHistory.map(item => {
    // Some APIs send xp_value or completions. Adjust if needed.
    return item.xpValue || item.xp_value || 0;
  });

  return {
    labels,
    datasets: [
      {
        label: 'XP Gained',
        backgroundColor: 'rgba(0, 229, 160, 0.1)',
        borderColor: '#00e5a0',
        data: dataPoints,
        tension: 0.4,
        fill: true,
        pointRadius: 4,
        pointBackgroundColor: '#00e5a0',
        borderWidth: 2
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      display: false,
      beginAtZero: true
    },
    x: {
      grid: { display: false },
      ticks: { color: '#64748b', font: { size: 10 } }
    }
  },
  plugins: {
    legend: { display: false },
    tooltip: {
      backgroundColor: '#1a1a2e',
      titleColor: '#00e5a0',
      bodyColor: '#fff',
      displayColors: false
    }
  }
};
</script>

<template>
  <div class="chart-container">
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.chart-container {
  height: 180px;
  width: 100%;
}
</style>
