# Life Analytics 2.0

A comprehensive life tracking and analytics platform built with Spring Boot and MySQL, designed to help users monitor habits, health metrics, calendar events, tasks, expenses, and activities with intelligent analytics.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Development Roadmap](#development-roadmap)
- [API Endpoints](#api-endpoints)
- [Future Work](#future-work)

---

## Overview

Life Analytics 2.0 is a Service Web project that provides REST APIs for tracking and analyzing personal data across multiple domains:

- **Habit Tracking**: Create habits, log completions, and track progress ✅
- **Health Metrics**: Monitor mood, stress, energy levels, and other wellness indicators ✅
- **Calendar Integration**: Sync and manage calendar events with ICS export ✅
- **Activity Logging**: Record and categorize daily activities ✅
- **Task Management**: Track todos with priorities and due dates ✅
- **Expense Tracking**: Monitor spending by category ✅
- **Course & Exam Management**: Academic tracking ✅
- **Goal Tracking**: Set and track personal goals ✅
- **AI Intake**: Process daily logs from AI assistants ✅
- **Analytics**: Generate insights and trends from tracked data ✅

---

## Tech Stack

- **Backend**: Spring Boot 3.2.5 (Java 17)
- **Java**: Zulu OpenJDK 17 (LTS)
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **ORM**: Spring Data JPA with Jakarta EE
- **API Testing**: Postman
- **IDE**: VS Code with Java Extension Pack

---

## Getting Started

### Prerequisites

1. **JDK 17 or 21** - Verify with `java -version`
2. **Maven** - Verify with `mvn -v`
3. **MySQL 8.0+** and MySQL Workbench
4. **Postman** for API testing
5. **VS Code** with extensions:
   - Java Extension Pack
   - Spring Boot Tools
   - Maven for Java

### Database Setup

```sql
CREATE DATABASE life_analytics_db;
```

### Application Configuration

Create `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/life_analytics_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```

### Running the Application

```bash
# Set Java 17 (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Zulu\zulu-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Build and run
mvn clean install
mvn spring-boot:run
```

Verify installation: `GET http://localhost:8080/ping` → should return "OK"

---

## Project Structure

```
com.dali.lifeanalytics
├── tracking/
│   ├── entity/
│   │   ├── Habit, HabitLog
│   │   ├── HealthMetric
│   │   ├── Task          ← NEW
│   │   ├── Expense       ← NEW
│   │   ├── Course, Exam  ← NEW
│   │   └── Goal, GoalProgress ← NEW
│   ├── repository/
│   ├── service/
│   └── controller/
├── calendar/
│   ├── CalendarEvent
│   ├── ActivityLog
│   ├── CalendarExportService  ← NEW (ICS export)
│   └── controllers
├── analytics/
│   ├── dto/ (DashboardDto, etc.)
│   ├── AnalyticsService
│   └── AnalyticsController
└── intake/                    ← NEW MODULE
    ├── dto/ (DailyLogDto, IntakeResultDto)
    ├── IntakeService
    └── IntakeController
```

---

## Development Roadmap

### Phase 1: Environment Setup ✅ COMPLETE
- Install and configure development tools
- Set up MySQL database
- Verify Spring Boot can connect to database

### Phase 2: Define MVP Scope ✅ COMPLETE
**Implemented:**
- Habit tracking with logs ✅
- Health metrics recording ✅
- Calendar events with ICS export ✅
- Activity logs ✅
- Task management ✅
- Expense tracking ✅
- Course & Exam management ✅
- Goal tracking ✅
- AI Intake module ✅

### Phase 3: Database Schema Design ✅ COMPLETE
Implemented tables:
- `habit`, `habit_log` - Habit tracking
- `health_metric` - Wellness measurements
- `calendar_event` - Time-based events
- `activity_log` - Categorized activities
- `task` - Todo items with status/priority ✅ NEW
- `expense` - Financial tracking ✅ NEW
- `course`, `exam` - Academic tracking ✅ NEW
- `goal`, `goal_progress` - Goal tracking ✅ NEW

### Phase 4: Core Tracking Module ✅ COMPLETE
**Habits:** CRUD + logs + date range queries ✅
**Health Metrics:** Record + query by date range ✅
**Tasks:** CRUD + status/priority filtering ✅
**Expenses:** CRUD + category filtering + summaries ✅

### Phase 5: Calendar & Activity Module ✅ COMPLETE
- Manage calendar events ✅
- ICS export endpoint ✅
- Conflict detection ✅
- Log activities with duration and type ✅

### Phase 6: Analytics Endpoints ✅ COMPLETE
- **Habit Analytics**: Weekly completion rates ✅
- **Time Analysis**: Duration by activity type ✅
- **Health Trends**: Average metrics over time ✅
- **Dashboard**: Combined analytics view ✅

### Phase 7: AI Intake Module ✅ COMPLETE
- Process daily log JSON from AI ✅
- Create entities from structured data ✅
- Schema endpoint for AI reference ✅

### Phase 8: Upgrade to Modern Stack ✅ COMPLETE
- Java 11 → Java 17 ✅
- Spring Boot 2.7.18 → 3.2.5 ✅
- javax.* → jakarta.* migration ✅
- Java 17 features (records, text blocks) ✅

### Phase 9: Documentation & Testing
- Complete Postman collection
- Write technical report
- Prepare presentation slides

---

## API Endpoints

### Habits
```
GET    /api/habits
GET    /api/habits/{id}
POST   /api/habits
PUT    /api/habits/{id}
DELETE /api/habits/{id}
POST   /api/habits/{id}/logs
GET    /api/habits/{id}/logs?from=&to=
```

### Health Metrics
```
GET    /api/health-metrics
POST   /api/health-metrics
GET    /api/health-metrics?from=&to=
```

### Tasks (NEW ✅)
```
GET    /api/tasks
GET    /api/tasks/{id}
POST   /api/tasks
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}
GET    /api/tasks/status/{status}
GET    /api/tasks/priority/{priority}
GET    /api/tasks/overdue
```

### Expenses (NEW ✅)
```
GET    /api/expenses
GET    /api/expenses/{id}
POST   /api/expenses
PUT    /api/expenses/{id}
DELETE /api/expenses/{id}
GET    /api/expenses/category/{category}
GET    /api/expenses/range?start=&end=
GET    /api/expenses/summary?start=&end=
```

### Courses & Exams (NEW ✅)
```
GET    /api/courses
POST   /api/courses
GET    /api/courses/{id}/exams
GET    /api/exams
POST   /api/exams
GET    /api/exams/upcoming
```

### Goals (NEW ✅)
```
GET    /api/goals
POST   /api/goals
GET    /api/goals/{id}/progress
POST   /api/goals/{id}/progress
```

### Calendar Events
```
GET    /api/calendar/events?from=&to=
POST   /api/calendar/events
GET    /api/calendar/events/export?from=&to=    ← ICS export (NEW ✅)
POST   /api/calendar/events/check-conflicts     ← Conflict detection (NEW ✅)
POST   /api/calendar/events/safe                ← Safe create (NEW ✅)
```

### Activity Logs
```
GET    /api/activities
GET    /api/activities/today
GET    /api/activities/date/{date}
POST   /api/activities
POST   /api/activities/quick
GET    /api/activities/weekly-breakdown
```

### AI Intake (NEW ✅)
```
POST   /api/intake/daily-log    ← Process AI-generated daily log
GET    /api/intake/schema       ← Get expected JSON schema
```

### Analytics
```
GET    /api/analytics/habits/weekly?habitId=&weekStart=
GET    /api/analytics/time-by-activity?from=&to=
GET    /api/analytics/health/trends?from=&to=
GET    /api/analytics/dashboard                  ← Full dashboard (NEW ✅)
```

---

## Future Work

### Short Term (Next Steps)
- [ ] Create Postman collection for all endpoints
- [ ] Write technical report (compte rendu)
- [ ] Prepare presentation slides
- [ ] Add sample data for demo
- [ ] Implement actual Google Calendar API integration

### Medium Term
- [ ] Add user authentication and authorization
- [ ] Create web frontend interface
- [ ] Add data validation and error handling improvements

### Long Term
- [ ] NoSQL version using MongoDB for time-series data
- [ ] AI-powered insights and recommendations
- [ ] Mobile application
- [ ] Real-time analytics dashboard
- [ ] Multi-user support with data privacy

---

## Database Schema

```sql
-- Core tables (IMPLEMENTED ✅)
habit (id, name, category, target_per_week, created_at)
habit_log (id, habit_id, log_date, value, note, created_at)
health_metric (id, recorded_at, sleep_hours, mood_score, stress_level, energy_level, note)
calendar_event (id, title, start_time, end_time, category, completed, ...)
activity_log (id, activity, category, log_date, duration_minutes, start_time, end_time, ...)

-- New tables (IMPLEMENTED ✅)
task (id, title, description, status, priority, due_date, category, created_at)
expense (id, amount, category, description, note, date, created_at)
course (id, code, name, semester, credits, professor, created_at)
exam (id, course_id, name, date, score, max_score, notes)
goal (id, title, description, category, target_value, current_value, target_date, status)
goal_progress (id, goal_id, value, note, recorded_at)
```

**Key Indexes:**
- Date/time columns for efficient range queries
- Foreign keys for referential integrity
- Category/status columns for filtering queries

---

## Contributing

This is an academic project. For the Service Web course evaluation, focus on:
1. ✅ Stable CRUD operations - DONE
2. ✅ Clean architecture - DONE
3. ✅ Multiple working analytics endpoints - DONE
4. Professional documentation - IN PROGRESS

---

## License

Academic project - Nabil OULAHYANE / Dali

---

## Notes

**Completed:**
- ✅ Habits + health metrics + basic analytics
- ✅ Calendar with ICS export and conflict detection
- ✅ Task management with priorities
- ✅ Expense tracking with categories
- ✅ Course and exam management
- ✅ Goal tracking with progress
- ✅ AI intake module for daily logs
- ✅ Upgraded to Java 17 + Spring Boot 3.2.5

**Next Steps:**
- Create Postman collection
- Write technical report
- Prepare demo presentation

**Project is FEATURE COMPLETE for course submission! 🎉**