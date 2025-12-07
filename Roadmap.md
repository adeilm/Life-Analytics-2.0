# Wellness Tracker - Development Roadmap

## Project Overview

**Goal**: A backend to correlate sleep and mood with daily habits.

**Domain**: Personal wellness tracking

---

## Current Status ✅

### Implemented

| Feature | Status | Description |
|---------|--------|-------------|
| Habits | ✅ Done | CRUD + daily logging |
| Habit Logs | ✅ Done | Track completions with notes |
| Health Metrics | ✅ Done | Sleep, mood, stress, energy |
| Weekly Report | ✅ Done | Habit completion stats |
| Health Trends | ✅ Done | Average metrics over time |
| Dashboard | ✅ Done | Combined overview |

---

## Tech Stack

- **Java 17** (Zulu OpenJDK)
- **Spring Boot 3.2.5**
- **MySQL 8.0**
- **Maven**

---

## API Summary

### Core Endpoints (15 total)

**Habits (7)**
- `GET /api/habits` - List habits
- `GET /api/habits/{id}` - Get habit
- `POST /api/habits` - Create habit
- `PUT /api/habits/{id}` - Update habit
- `DELETE /api/habits/{id}` - Delete habit
- `POST /api/habits/{id}/logs` - Log completion
- `GET /api/habits/{id}/logs` - Get logs

**Health Metrics (5)**
- `GET /api/health-metrics` - List all
- `GET /api/health-metrics/{id}` - Get one
- `POST /api/health-metrics` - Create
- `PUT /api/health-metrics/{id}` - Update
- `DELETE /api/health-metrics/{id}` - Delete

**Analytics (3)**
- `GET /api/analytics/habits/weekly` - Weekly report
- `GET /api/analytics/health/trend` - Health trends
- `GET /api/analytics/dashboard` - Dashboard

---

## Presentation Demo Flow

### Scenario: "Does Sleep Affect My Mood?"

**Step 1**: Create habits
```bash
POST /api/habits
{ "name": "Exercise", "category": "HEALTH", "targetPerWeek": 5 }
```

**Step 2**: Log health data over several days
```bash
POST /api/health-metrics
{ "sleepHours": 6.0, "moodScore": 5, "energyLevel": 4 }  # Bad sleep day

POST /api/health-metrics
{ "sleepHours": 8.0, "moodScore": 8, "energyLevel": 8 }  # Good sleep day
```

**Step 3**: Log habit completions
```bash
POST /api/habits/1/logs
{ "value": 1, "note": "Morning run" }
```

**Step 4**: View analytics
```bash
GET /api/analytics/dashboard
```
→ Shows correlation between sleep and mood!

---

## Future Enhancements (Parked)

These features are designed but not active:
- 📅 Calendar integration & Google sync
- 💰 Expense tracking
- 📚 Course & exam management
- ✅ Task management
- 🎯 Goal tracking with progress
- 🤖 AI intake for natural language logging

*Will implement when project matures.*

---

## Files Structure

```
life-analytics/
├── src/main/java/com/dali/lifeanalytics/
│   ├── LifeAnalyticsApplication.java
│   │
│   ├── tracking/           # Core domain
│   │   ├── entity/
│   │   │   ├── Habit.java
│   │   │   ├── HabitLog.java
│   │   │   └── HealthMetric.java
│   │   ├── repository/
│   │   ├── service/
│   │   └── controller/
│   │
│   ├── analytics/          # Reports & trends
│   │   ├── AnalyticsController.java
│   │   ├── AnalyticsService.java
│   │   └── dto/
│   │
│   └── config/             # Health check
│
└── pom.xml
```

---

## Key Points for Presentation

1. **Simple Domain**: Habits + Health = Wellness correlation
2. **Clean REST API**: Standard CRUD + analytics
3. **Spring Boot 3.x**: Modern Java 17 with Jakarta EE
4. **MySQL**: Relational data with JPA
5. **Analytics**: Real insights from data

---

## Quick Commands

```bash
# Compile
mvn clean compile

# Run
mvn spring-boot:run

# Test health
curl http://localhost:8080/api/health
```

---

*Keep it simple. Ship it.* 🚀
