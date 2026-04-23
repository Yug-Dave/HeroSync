# HeroSync - Backend (Spring Boot)

The backend for **HeroSync** is a robust REST and GraphQL API built with **Spring Boot**. It handles data persistence, business logic, authentication, and habit tracking calculations.

It connects to a **MySQL** database for production data and uses **H2** for automated testing.

---

## Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Configuration](#-configuration-important)
- [Running the Application](#-running-the-application)
- [Project Structure](#-project-structure)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)

---

## Features

* **API Management:**
  * **REST API:** Provides endpoints for Dashboard statistics and lightweight data fetching.
  * **GraphQL API:** Handles complex queries and mutations for Habits, Activities, and History Logs.
* **Database Integration:** 
  * **Spring Data JPA** (Hibernate) to manage MySQL relationships.
  * **H2** for automated testing.
* **Business Logic:**
  * **Streaks & Heatmaps:** Algorithms to calculate user consistency and visual activity data.
  * **Achievements:** Logic to unlock badges based on user progress.
  * **Reports:** Weekly and Monthly progress aggregation.

---

## Tech Stack

* **Language:** Java (JDK 21)
* **Framework:** Spring Boot 3.5.5
* **Database:** MySQL 8.0 (Production), H2 (Testing)
* **ORM:** Hibernate / Spring Data JPA
* **API:** Spring Web (REST) & Spring GraphQL
* **Tools:** Maven, Lombok

---

## Prerequisites

1.  **Java Development Kit (JDK):** Version 17 or higher.
2.  **MySQL Server:** Ensure it is running on port `3306`.
3.  **Maven:** (Optional, if using the wrapper provided in the project).

---

## Configuration (Important)

The application uses **Environment Variables** to secure database credentials. You must configure these before running.

### 1. Database Setup (Optional)
Open MySQL Workbench and run:
```sql
CREATE DATABASE HeroSync_db;
```

### 2. Environment Variables

In IntelliJ (Run Configurations) or your OS environment, set the following:

| Variable | Description | Default (if unset) |
| :--- | :--- | :--- |
| `DB_USERNAME` | Your MySQL Username | `root` |
| `DB_PASSWORD` | **REQUIRED** - Your MySQL Root Password | *None* |

> **Note:** The `application.yaml` is configured to create tables automatically (`ddl-auto: update`) when the app starts.

---

## Running the Application

### Option 1: Using IntelliJ IDEA

1.  Open the `HeroSync/backend` folder in IntelliJ.
2.  Wait for Maven to download dependencies.
3.  Open `src/main/java/de/fhdo/HeroSync/HeroSyncApplication.java`.
4.  Click the **Green Play Button** (Run).

### Option 2: Using Terminal (Maven Wrapper)

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
./mvnw.cmd spring-boot:run
```
The server will start at: http://localhost:8080

---

## Project Structure

The code is organized by layer (Controller-Service-Repository pattern):

```text
backend/
├── src/main/java/de/fhdo/HeroSync/
│   ├── client/                             # External API clients (if any)
│   ├── config/                             # Security, CORS, and App configuration
│   ├── controller/                         # REST Controllers & GraphQL Resolvers
│   ├── dto/                                # Data Transfer Objects (API responses)
│   ├── entity/                             # Database Models (User, Habit, Activity)
│   ├── repository/                         # JPA Interfaces for Database Access
│   ├── service/                            # Business Logic Layer
│   │   ├── AchievementService.java
│   │   ├── ActivityService.java
│   │   ├── AuthService.java
│   │   ├── DashboardService.java
│   │   ├── HabitService.java
│   │   └── ReportService.java
│   └── HeroSyncApplication.java            # Main Entry Point
└── src/main/resources/
    ├── graphql/                            # GraphQL Schema files (.graphqls)
    ├── application.yaml                    # Main Configuration
    └── static/                             # Static resources
```
## API Documentation
> **Note:** You cannot access without user login
### 1. GraphQL Interface (GraphiQL)
The backend includes an interactive GraphQL playground.
* **URL:** `http://localhost:8080/graphiql`
* **Usage:** You can write queries to test your backend manually.

**Example Query:**
```graphql
query {
  habits {
    id
    name
    description
  }
}
```

### 2. REST Endpoints

* GET /api/dashboard - Returns main user stats.
* (Add other REST endpoints here as you build them)

## Testing

The project uses **JUnit 5** and **Mockito**. Tests are configured to use an in-memory **H2 Database** so they don't interfere with your real MySQL data.

To run tests:

```bash
# Run all tests
./mvnw test
```
