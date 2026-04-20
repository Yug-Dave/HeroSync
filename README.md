# HeroSync: The Ultimate Gamified Productivity Engine

![Project Status](https://img.shields.io/badge/Status-Production--Ready-00e5a0?style=for-the-badge)
![Tech Stack](https://img.shields.io/badge/Stack-Spring%20%2B%20Vue-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-gray?style=for-the-badge)

HeroSync is a **Full-Stack Gamification Ecosystem** designed to bridge the gap between real-world productivity and RPG engagement. Built with a robust **Spring Boot 3** backend and a reactive **Vue 3** frontend, HeroSync transforms daily habits into epic quests and challenges.

## ✦ Overview
HeroSync allows users to track their habits, set ambitious goals, and face "Boss Battles" that represent their most significant challenges. By completing tasks, users earn Experience Points (XP), level up their hero, and unlock achievements that celebrate their consistency.

<p align="center">
  <img width="700" alt="image" src="https://github.com/user-attachments/assets/51a6c035-4c02-4462-9df5-1071e374bcdb" />
</p>


## ⚡ Key Features
- **Dynamic Habit Tracking**: Monitor your daily routines with interactive heatmaps and progress bars.
- **Goal System & Boss Battles**: Set goals linked to habits. Mark high-priority goals as "Bosses" for greater rewards and a more challenging visual experience.
- **Achievement Vault**: Unlock unique badges based on your performance, streaks, and milestones.
- **Hero Profile**: Customize your 3D avatar (via Avaturn) and watch your hero grow as you gain XP.
- **Deep Analytics**: Weekly and monthly reports provide insights into your productivity patterns.

## ⚙ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | ![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=flat-square&logo=vuedotjs&logoColor=4FC08D) ![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat-square&logo=vite&logoColor=white) ![Pinia](https://img.shields.io/badge/Pinia-FFD700?style=flat-square&logo=pinia&logoColor=white) |
| **Backend** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=flat-square&logo=graphql&logoColor=white) ![Java](https://img.shields.io/badge/Java_21-007396?style=flat-square&logo=openjdk&logoColor=white) |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white) ![H2](https://img.shields.io/badge/H2-004088?style=flat-square) |
| **Security** | ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) ![BCrypt](https://img.shields.io/badge/BCrypt-Gray?style=flat-square) |
| **Design** | ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white) ![Glassmorphism](https://img.shields.io/badge/Glassmorphism-00e5a0?style=flat-square) |

## 💠 Architecture
HeroSync follows a **Modular Monolith** pattern with a clean separation of concerns:
- **GraphQL Engine**: For complex, nested data retrieval (Dashboard, Reports).
- **RESTful API**: For standard operations and authentication.
- **Service Layer**: Centralized business logic (XP calculation, Achievement unlocking).
- **Security**: Robust session management and CSRF protection.

```mermaid
graph TD
    subgraph Frontend [Vue 3 / Pinia]
        UI[Tactical Dashboard] --> Store[State Management]
    end

    subgraph Backend [Spring Boot 3]
        Store -- API --> GQL[GraphQL Engine]
        Store -- API --> REST[REST Controllers]
        GQL --> Service[Service Layer]
        REST --> Service
        Service --> Repo[JPA Repository]
    end

    subgraph DB [MySQL]
        Repo --> MySQL[(Hero Data)]
    end
```

## ⎆ Getting Started

### Prerequisites
- **Java 21** or higher
- **Node.js 18** or higher
- **MySQL 8.0**
- **Docker** (Optional, for easy database setup)

### Backend Setup
1. Navigate to `HeroSync/backend`.
2. Configure your database in `src/main/resources/application.yaml` or set environment variables:
   - `DB_USERNAME`: your_username
   - `DB_PASSWORD`: your_password
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup
1. Navigate to `HeroSync/frontend/frontend-ui`.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
4. Access the app at `http://localhost:5173`.

## ⎔ Documentation
Detailed documentation, including the [UML Class Diagram](./Wiki/docs/UML-Class-diagram.md) and [Assignment Breakdown](./Wiki/docs/Assignment-Breakdown.md), can be found in the `/Wiki` directory.

---
*Developed with a focus on Performance, Professional Integrity, and Epic Engagement.*
