# HeroSync - Habit Tracker (Frontend)

HeroSync is a modern, interactive Habit Tracking application designed to help users build consistency. This repository contains the **Frontend** client built with **Vue 3** and **Vite**.

It connects to a **Spring Boot** backend and a **MySQL** database to track habits, calculate streaks, and visualize progress through heatmaps and history logs.

---

## Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Running the Project](#-running-the-project)
- [Project Structure](#-project-structure)
- [Backend Integration](#-backend-integration)
- [Recommended IDE Setup](#-recommended-ide-setup)

---

## Features

* **Interactive Dashboard:** Real-time view of active habits, current streaks, achievements and daily completion rates.
* **Consistency Heatmap:** A GitHub-style contribution graph showing habit activity over the last 10 days.
* **Habit Management:** CRUD operations
* **History Log:** A scrollable, detailed log of all past activities with status indicators.
* **Responsive Design:** Fully optimized layout that adapts to desktop and mobile screens using CSS Grid and Flexbox.

---

## Tech Stack

* **Framework:** [Vue 3](https://vuejs.org/) (Composition API)
* **Build Tool:** [Vite](https://vitejs.dev/)
* **Styling:** Native CSS 3 (Flexbox, CSS Grid, Custom Variables for Dark Mode)
* **API Communication:**
  * **REST API:** For user dashboard statistics.
  * **GraphQL:** For complex data fetching (Habits, Activities, Heatmaps).

---

## Prerequisites

Before running this frontend, you must ensure the backend environment is ready:

1.  **Node.js:** (v22.19.x or higher) - [Download Here](https://nodejs.org/)
2.  **MySQL Server:** Must be installed and running.
  * Database Name: `HeroSync_db`
  * Port: `3306`
3.  **Spring Boot Backend:** The backend application must be running on `localhost:8080`.

> ⚠️ **Note:** If the backend is not running, the frontend will load but will show empty data or "Auth check failed" errors.

---

## Installation

1.  **Clone the repository:**
    ```sh
    git clone <https://github.com/FrankV17/web2-ws25-GlitchGang.git>
    cd HeroSync/frontend/vue-HeroSync
    ```

2.  **Install Dependencies:**
    ```sh
    npm install
    ```

---

## Running the Project

### Step 1: Start the Backend
1.  Open your Backend project in **IntelliJ IDEA**.
2.  Ensure your `application.yaml` connects to your local MySQL (check your password configuration).
3.  Run the Spring Boot application.
4.  Verify it is listening on port `8080`.

### Step 2: Start the Frontend
In your terminal (inside the `HeroSync/frontend/vue-HeroSync` directory), run:

```sh
npm run build
```

### Step 3: Access the App
Open your browser and navigate to:
**http://localhost:5173**

---

## Project Structure

Key files and folders in the application:

```text
HeroSync/
└── frontend/
    └── vue-HeroSync/
        ├── src/
        │   ├── api/                                        # API connection logic
        │   ├── assets/                                     # Global styles and static resources
        │   ├── components/                                 # Reusable UI components
        │   │   ├── icons/                                  # SVG icons
        │   │   ├── AddHabitModal.vue                       # Modal for Creating/Editing habits
        │   │   ├── AppHeader.vue                           # Navigation bar
        │   │   ├── AppFooter.vue                           # Footer section
        │   │   ├── CompletionsTable.vue                    # History log table
        │   │   ├── DashboardAchivementsCard.vue
        │   │   └── ... (other components)
        │   ├── router/                                     # Vue Router configuration
        │   │   └── index.js
        │   ├── views/                                      # Main Page Views (Pages)
        │   │   ├── DashboardView.vue                       # Main dashboard (Habits, Heatmap)
        │   │   ├── HabitsView.vue                          # Dedicated habits page
        │   │   ├── AchievementsView.vue                    # User achievements
        │   │   ├── LoginView.vue                           # Authentication
        │   │   ├── ReportsMonthlyView.vue                  # Analytics
        │   │   └── ... (other views)
        │   ├── App.vue                                     # Root component
        │   └── main.js                                     # Entry point
        ├── public/                                         # Static assets
        └── vite.config.js                                  # Vite configuration
```

## Backend Integration

The frontend communicates with the backend via the following strategies:

1.  **Dashboard Data (REST):**
    * Endpoint: `GET /api/dashboard`
    * Usage: Fetches the User's Name, ID, current Streak count, and Total Completion %.

2.  **Habit Data (GraphQL):**
    * Endpoint: `POST /graphql`
    * Usage: Used for all CRUD operations (Create, Read, Update, Delete) on Habits and fetching the Activity History / Heatmap data.

---

## Recommended IDE Setup

You can use either VS Code or IntelliJ IDEA for frontend development.

* **[VS Code](https://code.visualstudio.com/)** (Recommended)
  * Extension: **[Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar)**
* **[IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/)**
  * The Vue.js plugin is usually bundled. If not, install the **Vue.js** plugin from the marketplace.

### Browser DevTools
* [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd) (Chrome)
* [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/) (Firefox)

---

## Production Build

To build the app for production (minified and optimized):

```sh
npm run build
