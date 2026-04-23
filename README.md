# HeroSync: The Ultimate Gamified Productivity Engine

![Project Status](https://img.shields.io/badge/Status-Production--Ready-00e5a0?style=for-the-badge)
![Tech Stack](https://img.shields.io/badge/Stack-Spring%20%2B%20Vue-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-gray?style=for-the-badge)

HeroSync is a **Full-Stack Gamification Ecosystem** designed to bridge the gap between real-world productivity and RPG engagement. Built with a robust **Spring Boot 3** backend and a reactive **Vue 3** frontend, HeroSync transforms daily habits into epic quests and challenges.

<p align="center">
  <img width="700" alt="HeroSync Dashboard Banner" src="./assets/hero-banner.png" />
</p>

## ✦ Overview
HeroSync allows users to track their habits, set ambitious goals, and face "Boss Battles" that represent their most significant challenges. By completing tasks, users earn Experience Points (XP), level up their hero, and unlock achievements that celebrate their consistency.

## ⚡ Key Features
- **Universal Theme Engine**: Seamless toggle between **Cinema Dark** and **Crystal Light** modes with persistent state management.
- **Dynamic Habit Tracking**: Monitor your daily routines with interactive heatmaps, progress bars, and real-time XP bursts.
- **Goal System & Boss Battles**: Set goals linked to habits. Mark high-priority goals as "Bosses" for greater rewards and a more challenging visual experience.
- **Achievement Vault**: Unlock unique badges based on your performance, streaks, and milestones.
- **Hero Profile**: Customize your 3D avatar (via Avaturn) and watch your hero grow as you gain XP.
- **Production-Ready Docker Pipeline**: Pre-configured Nginx reverse proxy and containerized microservices for instant, secure VPS deployment.

## ⚙ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | ![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=flat-square&logo=vuedotjs&logoColor=4FC08D) ![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat-square&logo=vite&logoColor=white) ![Pinia](https://img.shields.io/badge/Pinia-FFD700?style=flat-square&logo=pinia&logoColor=white) |
| **Backend** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=flat-square&logo=graphql&logoColor=white) ![Java](https://img.shields.io/badge/Java_21-007396?style=flat-square&logo=openjdk&logoColor=white) |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white) ![H2](https://img.shields.io/badge/H2-004088?style=flat-square) |
| **Security** | ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) ![BCrypt](https://img.shields.io/badge/BCrypt-Gray?style=flat-square) |
| **DevOps** | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white) |

## 💠 Architecture
HeroSync follows a **Modular Monolith** pattern with a clean separation of concerns:
- **GraphQL Engine**: For complex, nested data retrieval (Dashboard, Reports).
- **RESTful API**: For standard operations and authentication.
- **Service Layer**: Centralized business logic (XP calculation, Achievement unlocking).
- **Reverse Proxy**: Nginx handles SSL termination and API routing to prevent CORS issues.

```mermaid
graph TD
    subgraph Frontend [Vue 3 / Pinia]
        UI[Tactical Dashboard] --> Store[State Management]
    end

    subgraph ReverseProxy [Nginx]
        Store -- /api --> Proxy[API Gateway]
    end

    subgraph Backend [Spring Boot 3]
        Proxy --> GQL[GraphQL Engine]
        Proxy --> REST[REST Controllers]
        GQL --> Service[Service Layer]
        REST --> Service
        Service --> Repo[JPA Repository]
    end

    subgraph DB [MySQL]
        Repo --> MySQL[(Hero Data)]
    end
```

## 🚀 Quick Start (Recommended)
The fastest way to get HeroSync running is using **Docker**. You don't need to install Java, Node, or MySQL on your host machine.

1. Clone the repo.
2. Run:
   ```bash
   docker compose up -d
   ```
3. Access the dashboard at `http://localhost:80`.

## 🛠️ Manual Development Setup
If you want to modify the code and see changes in real-time without Docker:

#### Prerequisites
- **Java 21**+
- **Node.js 18**+
- **MySQL 8.0**

#### Local Development
1. **Backend**: Navigate to `HeroSync/backend` and run `./mvnw spring-boot:run`.
2. **Frontend**: Navigate to `HeroSync/frontend/frontend-ui`, run `npm install` then `npm run dev`.
3. **Access**: `http://localhost:5173`.

## ⎔ Documentation
Detailed documentation, including the [UML Class Diagram](./Wiki/docs/UML-Class-diagram.md) and [Assignment Breakdown](./Wiki/docs/Assignment-Breakdown.md), can be found in the `/Wiki` directory.

---
*Developed with a focus on Performance, Professional Integrity, and Epic Engagement.*

