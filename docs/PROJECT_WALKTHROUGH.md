# Project Walkthrough & Learning Guide 🎓

This document explains every file in the `life-analytics` project to help you understand the structure of a Spring Boot application.

---

## 1. High-Level Architecture 🏗️

This project follows the standard **Layered Architecture** common in Spring Boot:

1.  **Controller Layer** (`controller/`): The "front desk". It receives HTTP requests (GET, POST, etc.) from the user (or Postman) and returns JSON responses. It doesn't do logic; it just delegates to the Service.
2.  **Service Layer** (`service/`): The "brain". It contains the business logic (e.g., "calculate streak", "validate input"). It calls the Repository to get data.
3.  **Repository Layer** (`repository/`): The "librarian". It talks directly to the database (MySQL) to save, find, or delete data.
4.  **Entity Layer** (`entity/`): The "data shape". These are Java classes that map directly to database tables.

---

## 2. Configuration Files ⚙️

### `pom.xml`
**"Project Object Model"** - The recipe for Maven.
- **What it does**: Lists all the libraries (dependencies) the project needs.
- **Key Dependencies**:
    - `spring-boot-starter-web`: Allows us to build REST APIs.
    - `spring-boot-starter-data-jpa`: Allows us to talk to the database easily.
    - `mysql-connector-j`: The driver to connect to MySQL.
    - `lombok`: A helper library that auto-generates getters, setters, and constructors (so we don't have to write them).

### `src/main/resources/application.properties`
**The Control Panel.**
- **What it does**: Stores configuration settings.
- **Key Settings**:
    - `spring.datasource.url`: Where the database is located.
    - `spring.datasource.username/password`: Login credentials.
    - `spring.jpa.hibernate.ddl-auto=update`: Automatically creates/updates database tables to match your Java entities.

---

## 3. The Entry Point 🚪

### `src/main/java/com/dali/wellness/WellnessTrackerApplication.java`
**The Main Class.**
- **What it does**: This is where the application starts.
- **Key Annotation**: `@SpringBootApplication`. This tells Spring: "This is a Spring app, please scan all the files in this package and wire them together."

---

## 4. The Tracking Module (Core Logic) 🧠

This module handles the raw data: Habits and Health Metrics.

### Entities (The Database Tables)
- **`tracking/entity/Habit.java`**: Represents a habit (e.g., "Run 5km"). Maps to the `habit` table.
- **`tracking/entity/HabitLog.java`**: Represents a single completion of a habit (e.g., "Ran 5km on Monday"). Maps to the `habit_log` table.
- **`tracking/entity/HealthMetric.java`**: Represents a daily health check-in (Sleep, Mood, Stress). Maps to the `health_metric` table.

### Repositories (The Database Access)
*These interfaces extend `JpaRepository`, which gives us magic methods like `.save()`, `.findAll()`, and `.deleteById()` for free!*
- **`tracking/repository/HabitRepository.java`**: Talk to the `habit` table.
- **`tracking/repository/HabitLogRepository.java`**: Talk to the `habit_log` table. Contains custom queries like `countByHabitIdAndDateRange`.
- **`tracking/repository/HealthMetricRepository.java`**: Talk to the `health_metric` table.

### Services (The Business Logic)
- **`tracking/service/HabitService.java`**: Logic for creating and managing habits.
- **`tracking/service/HabitLogService.java`**: Logic for logging a habit. It checks if the habit exists before logging.
- **`tracking/service/HealthMetricService.java`**: Logic for saving health data.

### Controllers (The API Endpoints)
- **`tracking/controller/HabitController.java`**:
    - `GET /api/habits`: List habits.
    - `POST /api/habits`: Create a habit.
    - `POST /api/habits/{id}/logs`: Log a habit.
- **`tracking/controller/HealthMetricController.java`**:
    - `POST /api/health-metrics`: Save today's stats.
    - `GET /api/health-metrics`: See history.

---

## 5. The Analytics Module (The Insights) 📊

This module doesn't save new data; it reads existing data and calculates interesting stats.

### The Logic
- **`analytics/AnalyticsService.java`**: The heavy lifter.
    - It asks the Repositories for raw data.
    - It calculates averages, streaks, and completion rates.
    - It packages the results into a simple `Map<String, Object>` (a flexible key-value store) to send back as JSON.

### The API
- **`analytics/AnalyticsController.java`**:
    - `GET /api/analytics/dashboard`: Returns the full dashboard data.
    - `GET /api/analytics/habits/weekly`: Returns the weekly habit report.

---

## 6. Other Files 📂

- **`config/HealthCheckController.java`**: A simple "Are you alive?" endpoint (`GET /api/health`). Useful to test if the server is running.
- **`package-info.java`**: Just documentation files for Java packages. You can ignore these.

---

## How to Read the Code (Suggested Order)

1.  Start with **`Habit.java`** (Entity) to see what data we are storing.
2.  Look at **`HabitRepository.java`** to see how we access it.
3.  Look at **`HabitService.java`** to see the logic.
4.  Look at **`HabitController.java`** to see how it's exposed to the world.
5.  Finally, look at **`AnalyticsService.java`** to see how we combine everything together.
