# Life Analytics 2.0 – Personal Analytics Workbench

Life Analytics 2.0 is a **personal analytics backend** built with:

- **Spring Boot + Maven**
- **MySQL (SQL version – Service Web project)**
- (Later) **MongoDB (NoSQL project)**
- (Later) **Google Calendar integration**
- (Later) **Data engineering + AI insights layer**

The main goal is to centralize **habit, health, study, and activity data** into one backend and expose **REST APIs** and **analytics endpoints** that can feed dashboards or future mobile/web apps.

---

## 1. Project Goals

- ✅ Build a **clean Spring Boot REST API** with MySQL (Service Web course).
- ✅ Model life data (habits, health, study sessions, events) in a **relational schema**.
- ✅ Provide **analytics endpoints** (time spent, trends, completions).
- ✅ Design the system as **microservice-oriented**, even if the first version is a modular monolith.
- ✅ Prepare a **NoSQL version** (MongoDB) with aggregation pipelines.
- ✅ Integrate with **Google Calendar** for automatic event import.

---

## 2. High-Level Concept

> _“Personal analytics workbench that aggregates calendar data and personal logs (habits, health, study, activities) and exposes insight endpoints.”_

Core ideas:

1. **Tracking**  
   Log what you do: Goals, habits, health metrics, study sessions, activities.

2. **Calendar Integration**  
   Import events (e.g. from Google Calendar) and link them to activities/study sessions.

3. **Analytics**  
   Compute aggregated statistics:
   - time spent per activity type
   - study load per day/week/course
   - habit consistency
   - simple health trends

---

## 3. Architecture Overview

### 3.1 Logical Services (microservice-oriented design)

The system is split conceptually into three services:

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

Implementation strategy:
- **Phase 1:** Modular monolith (one Spring Boot app) with clear packages:  
  `tracking.*`, `calendar.*`, `analytics.*`
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

*(Optional later: `User`, `Course`, `Tag`, etc.)*

---

## 5. API Overview (REST Endpoints)

> Note: paths and payloads can be refined during implementation.  
> This is the expected **surface** of the project for testing & presentation.

### 5.1 Tracking Service APIs

#### Habits

- `GET /api/habits`  
  List all habits.

- `GET /api/habits/{id}`  
  Get a specific habit.

- `POST /api/habits`  
  Create a new habit.

- `PUT /api/habits/{id}`  
  Update a habit.

- `DELETE /api/habits/{id}`  
  Delete a habit.

- `POST /api/habits/{id}/logs`  
  Create a log entry for a habit (date, value, note).

- `GET /api/habits/{id}/logs?from=YYYY-MM-DD&to=YYYY-MM-DD`  
  Get logs for a habit in a date range.

#### Health Metrics

- `POST /api/health-metrics`  
  Log a new health metric entry.

- `GET /api/health-metrics?from=&to=`  
  List health metrics in range.

#### Activity Logs

- `GET /api/activity-logs?from=&to=&type=`  
- `POST /api/activity-logs`  
  Create a new activity log (optionally linked to a calendar event).

---

### 5.2 Calendar Service APIs

- `GET /api/calendar-events?from=&to=`  
  List calendar events in a period.

- `POST /api/calendar-events`  
  Insert a calendar event manually.

- `POST /api/calendar/sync?from=&to=`  
  Trigger synchronization with Google Calendar  
  (first version may read from a local JSON file / mocked source).

---

### 5.3 Analytics Service APIs

- `GET /api/analytics/habits/weekly?habitId=&weekStart=`  
  Returns weekly completion statistics for a habit.

- `GET /api/analytics/time-by-activity?from=&to=`  
  Aggregated duration by `activity_type`.

- `GET /api/analytics/health/trends?from=&to=`  
  Basic trend data (e.g., avg mood/stress per day).

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
- Optionally shows microservice-ready design (clear separation of Tracking / Calendar / Analytics layers).

### 7.2 NoSQL version (MongoDB – separate project)

- Same “Life Analytics 2.0” concept.
- Unified `events` collection storing different event types (habit logs, health logs, calendar events, activity logs).
- Heavy use of **aggregation pipelines**:
  - `$match`, `$group`, `$project`, `$sort`, `$bucket`, etc.
- Good playground for data engineering concepts.

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
