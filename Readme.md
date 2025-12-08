# Wellness Tracker 🧘

A simple backend to **correlate sleep and mood with daily habits**.

---

## Tech Stack

### Backend
- **Java 17** + **Spring Boot 3.2.5**
- **MySQL** (relational database)
- **Maven** (build tool)
- *Note: Lombok has been removed for better compatibility.*

### Frontend
- **Python** + **Streamlit** (Simple web UI)

---

## Project Goal

> *"Track my habits, log my sleep/mood/energy, and see how they correlate."*

Simple. Focused. A **personal wellness backend**.

---

## Features

| Module | Description |
|--------|-------------|
| **Habits** | Create habits, log daily completions, track streaks |
| **Health Metrics** | Log sleep, mood, stress, energy levels |
| **Analytics** | Weekly reports, trends, dashboard |

---

## API Endpoints

### Habits

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/habits` | List all habits |
| GET | `/api/habits/{id}` | Get a specific habit |
| POST | `/api/habits` | Create a new habit |
| PUT | `/api/habits/{id}` | Update a habit |
| DELETE | `/api/habits/{id}` | Delete a habit |
| POST | `/api/habits/{id}/logs` | Log habit completion |
| GET | `/api/habits/{id}/logs` | Get habit logs |

### Health Metrics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health-metrics` | List all health metrics |
| GET | `/api/health-metrics/{id}` | Get specific metric |
| POST | `/api/health-metrics` | Log health data |
| PUT | `/api/health-metrics/{id}` | Update metric |
| DELETE | `/api/health-metrics/{id}` | Delete metric |

### Analytics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/habits/weekly` | Weekly habit completion report |
| GET | `/api/analytics/health/trend` | Health trends (sleep, mood, etc.) |
| GET | `/api/analytics/dashboard` | Combined dashboard |

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | API health status |

---

## Data Model

### Habit
```
id, name, description, category, target_per_week, active, created_at
```

### HabitLog
```
id, habit_id, log_date, value, note
```

### HealthMetric
```
id, recorded_at, sleep_hours, mood_score (1-10), stress_level (1-10), energy_level (1-10), note
```

---

## Quick Start

### 1. Setup MySQL Database

```sql
CREATE DATABASE wellness_tracker;
```

### 2. Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wellness_tracker
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

### 4. Run the Frontend

```bash
cd frontend
pip install streamlit requests
streamlit run app.py
```

### 5. Test the API

```bash
# Health check
curl http://localhost:8080/api/health

# Create a habit
curl -X POST http://localhost:8080/api/habits \
  -H "Content-Type: application/json" \
  -d '{"name": "Exercise", "category": "HEALTH", "targetPerWeek": 5}'

# Log health metrics
curl -X POST http://localhost:8080/api/health-metrics \
  -H "Content-Type: application/json" \
  -d '{"sleepHours": 7.5, "moodScore": 8, "stressLevel": 3, "energyLevel": 7}'

# Get dashboard
curl http://localhost:8080/api/analytics/dashboard
```

---

## Project Structure

```
Life Analytics 2.0/
├── backend/                             # Spring Boot Backend
│   ├── src/main/java/com/dali/wellness/
│   │   ├── WellnessTrackerApplication.java
│   │   ├── analytics/
│   │   ├── config/
│   │   └── tracking/
│   └── pom.xml
├── frontend/                            # Streamlit Frontend
│   └── app.py
└── Readme.md
```

---

## Example Workflow

### Daily Wellness Routine

1. **Morning**: Log how you slept
   ```
   POST /api/health-metrics
   { "sleepHours": 7.5, "moodScore": 7, "energyLevel": 8 }
   ```

2. **Throughout day**: Log habit completions
   ```
   POST /api/habits/1/logs
   { "value": 1, "note": "30 min workout" }
   ```

3. **Evening**: Check your dashboard
   ```
   GET /api/analytics/dashboard
   ```
   → See how sleep affects your mood and habits!

---

## Analytics Insights

The dashboard answers:
- ✅ How many habits did I complete this week?
- ✅ What's my average sleep/mood/energy?
- ✅ What's my current habit streak?
- ✅ How does my sleep correlate with mood?

---

## Author

**Dali** – Personal wellness tracking project for university course.

---

*Keep it simple. Track what matters.* 🌟
