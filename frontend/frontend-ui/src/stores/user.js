import { defineStore } from 'pinia';
import { http } from '../api/http';

export const useUserStore = defineStore('user', {
  state: () => ({
    name: 'Hero',
    avatar: '',
    streak: 0,
    xp: 0,
    id: null,
    totalDone: 0,
    loading: false,
    initialized: false,
    companionChoice: 'SYNC',
    aiProvider: 'CLAUDE',
    companionChats: {
      SYNC: [],
      AURA: [],
      VOLT: []
    }
  }),

  getters: {
    level: (state) => {
      if (state.xp <= 0) return 1;
      return Math.floor(Math.sqrt(state.xp / 1000)) + 1;
    },
    nextLevelXp: (state) => {
      const currentLevel = Math.floor(Math.sqrt(state.xp / 1000)) + 1;
      return Math.pow(currentLevel, 2) * 1000;
    },
    xpProgress: (state) => {
      const currentLevel = Math.floor(Math.sqrt(state.xp / 1000)) + 1;
      const currentLevelStart = Math.pow(currentLevel - 1, 2) * 1000;
      const nextLevelStart = Math.pow(currentLevel, 2) * 1000;
      const range = nextLevelStart - currentLevelStart;
      const progress = state.xp - currentLevelStart;
      return Math.min(Math.max((progress / range) * 100, 0), 100);
    },
    currentMessages: (state) => {
      if (!state.companionChats) return [];
      return state.companionChats[state.companionChoice] || [];
    }
  },

  actions: {
    async fetchUser() {
      this.loading = true;
      try {
        const res = await http.get('/dashboard');
        const data = res.data;
        this.name = data.userName || 'Hero';
        this.avatar = data.avatar || '';
        this.streak = data.currentStreak || 0;
        this.xp = data.totalXP || 0;
        this.id = data.dashboardId;
        this.totalDone = data.totalHabitsDone || 0;
        this.initialized = true;

        // Load companion + provider preference from profile
        try {
          const profileRes = await http.get('/profile/me');
          this.companionChoice = profileRes.data.companionChoice || 'SYNC';
          this.aiProvider = profileRes.data.aiProvider || 'CLAUDE';
        } catch (profileErr) {
          console.warn('Could not load profile companion settings:', profileErr);
        }
      } catch (error) {
        console.error('Failed to fetch user data:', error);
      } finally {
        this.loading = false;
      }
    },

    updateStats(xp, streak) {
      if (xp !== undefined) this.xp = xp;
      if (streak !== undefined) this.streak = streak;
    },

    updateAvatar(avatar) {
      this.avatar = avatar;
    },

    updateCompanion(companion, provider) {
      if (companion) this.companionChoice = companion;
      if (provider) this.aiProvider = provider;
    },

    addMessage(companion, role, text) {
      if (!this.companionChats) {
        this.companionChats = { SYNC: [], AURA: [], VOLT: [] };
      }
      if (!this.companionChats[companion]) {
        this.companionChats[companion] = [];
      }
      this.companionChats[companion].push({ role, text });
    }
  }
});

