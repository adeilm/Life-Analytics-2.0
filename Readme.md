# Life Analytics 2.0 – Personal Life OS for Dali

Life Analytics 2.0 is a **personal analytics backend** built for **one user (Dali)** to centralize and analyze daily life data.

**Tech Stack:**
- **Java 17** (Zulu OpenJDK)
- **Spring Boot 3.2.5** + Maven (REST backend)
- **MySQL** (relational database)
- **Gemini API (free tier)** for natural-language → JSON translation
- **Google Calendar integration** (export `.ics`, conflict detection)

The main goal is to centralize **habit, health, study, expense, task, and activity data** into one backend and expose **REST APIs** and **analytics endpoints** that can feed dashboards or future mobile/web apps.

---

## Current Implementation Status ✅

### Fully Implemented Features:

| Module | Status | Description |
|--------|--------|-------------|
| **Habit Tracking** | ✅ Complete | CRUD + daily logs + weekly stats |
| **Health Metrics** | ✅ Complete | Mood, stress, energy, sleep tracking |
| **Calendar Events** | ✅ Complete | Events + ICS export + conflict detection |
| **Activity Logging** | ✅ Complete | Time tracking by category |
| **Task Management** | ✅ Complete | Todos with priority, status, due dates |
| **Expense Tracking** | ✅ Complete | Financial tracking with categories |
| **Course Management** | ✅ Complete | Academic courses + exams |
| **Goal Tracking** | ✅ Complete | Goals with progress entries |
| **AI Intake Module** | ✅ Complete | Daily log ingestion from AI |
| **Analytics Dashboard** | ✅ Complete | Trends, summaries, weekly reports |

---

## 1. Project Goals

- ✅ Build a **clean Spring Boot REST API** with MySQL (Service Web course).
- ✅ Model life data (habits, health, study sessions, events) in a **relational schema**.
- ✅ Provide **analytics endpoints** (time spent, trends, completions).
- ✅ Design the system as **microservice-oriented**, even if the first version is a modular monolith.
- ✅ Integrate **Gemini API (free tier)** for natural-language daily-log ingestion.
- ✅ Integrate with **Google Calendar** for automatic event import/export.

---

## 2. High-Level Concept

> _"A personal life OS for Dali: centralize calendar data and personal logs (habits, health, study, activities), talk to Gemini to log your day in natural language, and get insight endpoints."_

Core ideas:

1. **Tracking**  
   Log what you do: Goals, habits, health metrics, study sessions, activities.

2. **AI Intake (Gemini)**  
   Describe your day in plain language → Gemini converts it to structured JSON → backend ingests it.

3. **Calendar Integration**  
   Import/export events (Google Calendar) and link them to activities/study sessions.

4. **Analytics**  
   Compute aggregated statistics:
   - time spent per activity type
   - study load per day/week/course
   - habit consistency
   - simple health trends

---

## 3. Architecture Overview

### 3.1 Logical Services (microservice-oriented design)

The system is split conceptually into four services:

1. **Tracking Service**
   - Manages:
     - `Habit`, `HabitLog`
     - `HealthMetric`
     - `ActivityLog`
   - Responsible for **writing** most life events.

2. **Calendar Service**
   - Manages:
     - `CalendarEvent`
     - Calendar synchronization (Google Calendar or mocked data)
   - Responsible for importing and exposing calendar-based events.

3. **Analytics Service**
   - Read-oriented service.
   - Combines data from Tracking + Calendar to produce:
     - time-per-activity analytics
     - habit completion rates
     - health trends

4. **Intake Service (Gemini-powered)**
   - Receives natural-language daily logs.
   - Calls Gemini API to convert text → structured JSON.
   - Dispatches parsed data to Tracking and Calendar services.

Implementation strategy:
- **Phase 1:** Modular monolith (one Spring Boot app) with clear packages:  
  `tracking.*`, `calendar.*`, `analytics.*`, `intake.*`
- **Phase 2 (optional):** Extract Calendar into a separate Spring Boot microservice.

---

## 4. Data Model (SQL Version – MySQL)

### 4.1 Main Entities

**Habit**
- `id` (PK)
- `name`
- `category` (e.g. HEALTH, PRODUCTIVITY, STUDY)
- `target_per_week` (int)

**HabitLog**
- `id` (PK)
- `habit_id` (FK → Habit)
- `date` (DATE)
- `value` (e.g. minutes, count)
- `note` (optional)

**HealthMetric**
- `id` (PK)
- `timestamp` (DATETIME)
- `sleep_hours` (double, nullable)
- `mood_score` (int, 1–5)
- `stress_level` (int, 1–5)
- `energy_level` (int, 1–5)
- `note` (optional)

**CalendarEvent**
- `id` (PK)
- `external_id` (Google Calendar event id or null)
- `title`
- `start_time` (DATETIME)
- `end_time` (DATETIME)
- `source` (e.g. GOOGLE, MANUAL)
- `event_type` (STUDY, WORK, HEALTH, PERSONAL, OTHER)
- `raw_json` (TEXT, optional – original payload for debugging)

**ActivityLog**
- `id` (PK)
- `event_id` (FK → CalendarEvent, nullable)
- `activity_type` (STUDY, WORK, SPORT, REST, OTHER)
- `start_time` (DATETIME)
- `end_time` (DATETIME)
- `duration_minutes` (int, can be computed)
- `tags` (stored as simple string or separate table)
- `note` (optional)

*(Single-user system – personalized for Dali, no multi-user support needed.)*

---

## 5. API Overview (REST Endpoints)

> Note: paths and payloads can be refined during implementation.  
> This is the expected **surface** of the project for testing & presentation.

### 5.1 Tracking Service APIs

#### Habits

- `GET /api/habits` – List all habits
- `GET /api/habits/{id}` – Get a specific habit
- `POST /api/habits` – Create a new habit
- `PUT /api/habits/{id}` – Update a habit
- `DELETE /api/habits/{id}` – Delete a habit
- `POST /api/habits/{id}/logs` – Create a log entry for a habit
- `GET /api/habits/{id}/logs?from=&to=` – Get logs for a habit in a date range

#### Health Metrics

- `POST /api/health-metrics` – Log a new health metric entry
- `GET /api/health-metrics?from=&to=` – List health metrics in range

#### Activity Logs

- `GET /api/activities` – List all activity logs
- `GET /api/activities/today` – Get today's activities
- `GET /api/activities/date/{date}` – Get activities for specific date
- `GET /api/activities/range?start=&end=` – Get activities in date range
- `GET /api/activities/weekly-breakdown` – Get weekly breakdown by category
- `POST /api/activities` – Create a new activity log
- `POST /api/activities/quick` – Quick log (minimal fields)

#### Tasks (NEW ✅)

- `GET /api/tasks` – List all tasks
- `GET /api/tasks/{id}` – Get a specific task
- `POST /api/tasks` – Create a new task
- `PUT /api/tasks/{id}` – Update a task
- `DELETE /api/tasks/{id}` – Delete a task
- `GET /api/tasks/status/{status}` – Get tasks by status
- `GET /api/tasks/priority/{priority}` – Get tasks by priority
- `GET /api/tasks/overdue` – Get overdue tasks

#### Expenses (NEW ✅)

- `GET /api/expenses` – List all expenses
- `GET /api/expenses/{id}` – Get a specific expense
- `POST /api/expenses` – Create a new expense
- `PUT /api/expenses/{id}` – Update an expense
- `DELETE /api/expenses/{id}` – Delete an expense
- `GET /api/expenses/category/{category}` – Get expenses by category
- `GET /api/expenses/range?start=&end=` – Get expenses in date range
- `GET /api/expenses/summary?start=&end=` – Get expense summary by category

#### Courses (NEW ✅)

- `GET /api/courses` – List all courses
- `GET /api/courses/{id}` – Get a specific course
- `POST /api/courses` – Create a new course
- `PUT /api/courses/{id}` – Update a course
- `DELETE /api/courses/{id}` – Delete a course
- `GET /api/courses/{id}/exams` – Get exams for a course

#### Exams (NEW ✅)

- `GET /api/exams` – List all exams
- `GET /api/exams/{id}` – Get a specific exam
- `POST /api/exams` – Create a new exam
- `PUT /api/exams/{id}` – Update an exam
- `DELETE /api/exams/{id}` – Delete an exam
- `GET /api/exams/upcoming` – Get upcoming exams

#### Goals (NEW ✅)

- `GET /api/goals` – List all goals
- `GET /api/goals/{id}` – Get a specific goal
- `POST /api/goals` – Create a new goal
- `PUT /api/goals/{id}` – Update a goal
- `DELETE /api/goals/{id}` – Delete a goal
- `GET /api/goals/{id}/progress` – Get progress entries for a goal
- `POST /api/goals/{id}/progress` – Add progress entry

---

### 5.2 Calendar Service APIs

- `GET /api/calendar/events?from=&to=` – List calendar events in a period
- `POST /api/calendar/events` – Insert a calendar event manually
- `GET /api/calendar/events/export?from=&to=` – **Export to ICS format (NEW ✅)**
- `POST /api/calendar/events/check-conflicts` – **Check for conflicts (NEW ✅)**
- `POST /api/calendar/events/safe` – **Create event only if no conflicts (NEW ✅)**

---

### 5.3 AI Intake Service APIs (NEW ✅)

- `POST /api/intake/daily-log` – Process AI-generated daily log JSON
- `GET /api/intake/schema` – Get expected JSON schema for AI

**Example Daily Log JSON:**
```json
{
  "date": "2025-12-05",
  "health": {
    "sleepHours": 7.5,
    "moodScore": 7,
    "stressLevel": 4,
    "energyLevel": 8
  },
  "activities": [
    { "type": "EXERCISE", "durationMinutes": 60 }
  ],
  "habits": [
    { "habitName": "Exercise", "value": 1 }
  ],
  "expenses": [
    { "amount": 12.50, "category": "FOOD" }
  ],
  "tasks": [
    { "title": "Complete report", "priority": "HIGH" }
  ]
}
```

---

### 5.4 Analytics Service APIs

### 5.4 Analytics Service APIs

- `GET /api/analytics/habits/weekly?habitId=&weekStart=` – Weekly completion stats
- `GET /api/analytics/time-by-activity?from=&to=` – Aggregated duration by activity type
- `GET /api/analytics/health/trends?from=&to=` – Health trend data
- `GET /api/analytics/dashboard` – **Full dashboard with all metrics (NEW ✅)**

You can start with simple SQL `GROUP BY` queries and enrich later.

---

## 6. Workflow Scenarios

### 6.1 Log a habit and see its weekly performance

1. Create a habit (`POST /api/habits`).
2. Log daily progress (`POST /api/habits/{id}/logs`).
3. Query weekly stats (`GET /api/analytics/habits/weekly?habitId=...`).

### 6.2 Log study sessions via calendar events

1. Sync or create study events via calendar:
   - `POST /api/calendar/sync` (or `POST /api/calendar-events` manually)
2. For each study event, create an ActivityLog (`POST /api/activity-logs`) with `activity_type=STUDY`.
3. Query study time per day or per period:
   - `GET /api/analytics/time-by-activity?from=&to=`

### 6.3 Track health metrics over time

1. Log health metrics (`POST /api/health-metrics`).
2. Query trends:
   - `GET /api/analytics/health/trends?from=&to=`

---

## 7. Relationship to Courses and Future Work

### 7.1 Service Web (SQL version)

- Uses **Spring Boot + Maven + REST + MySQL**.
- Demonstrates:
  - CRUD endpoints
  - Parameter passing (`PathVariable`, `RequestParam`)
  - JSON input/output
  - DB access via JPA
  - Testing with Postman
- Optionally shows microservice-ready design (clear separation of Tracking / Calendar / Analytics / Intake layers).

### 7.2 Gemini Integration

- Use **Gemini free-tier API** to convert natural-language daily descriptions into structured JSON.
- Backend validates and ingests the JSON via `/api/intake/daily-log`.
- Keep API key in environment variable (`GEMINI_API_KEY`), never commit to repo.

### 7.3 Future extensions

- Google Calendar real integration (OAuth 2.0).
- Kafka / message queue between services.
- Frontend dashboard (React / Vue / Flutter).
- AI layer for:
  - anomaly detection (stress spikes, burnout risk)
  - recommendation (when to study, when to rest).

---

## 8. Evaluation / What to Expect

By the time the project is “done” for the course, you should have:

- Running **Spring Boot app** with MySQL.
- At least:
  - 2–3 fully working domains (**Habits**, **Health**, **Calendar** or **Activity**).
  - 1–2 analytics endpoints per domain.
- A **Postman collection** demonstrating all endpoints.
- A short **report** (compte rendu) explaining:
  - context & goals
  - schema design
  - REST design
  - analytics logic
- Slides showing:
  - architecture diagram
  - data model diagram
  - demo flow
  - possible future extensions.
