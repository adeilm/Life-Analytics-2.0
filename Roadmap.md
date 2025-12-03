# Life Analytics 2.0

A comprehensive life tracking and analytics platform built with Spring Boot and MySQL, designed to help users monitor habits, health metrics, calendar events, and activities with intelligent analytics.

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

- **Habit Tracking**: Create habits, log completions, and track progress
- **Health Metrics**: Monitor mood, stress, energy levels, and other wellness indicators
- **Calendar Integration**: Sync and manage calendar events
- **Activity Logging**: Record and categorize daily activities
- **Analytics**: Generate insights and trends from tracked data

---

## Tech Stack

- **Backend**: Spring Boot (Java 17/21)
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **ORM**: Spring Data JPA
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
mvn clean install
mvn spring-boot:run
```

Verify installation: `GET http://localhost:8080/ping` → should return "OK"

---

## Project Structure

```
com.lifeanalytics
├── tracking
│   ├── entities (Habit, HabitLog, HealthMetric)
│   ├── repositories
│   ├── services
│   └── controllers
├── calendar
│   ├── entities (CalendarEvent, ActivityLog)
│   ├── repositories
│   ├── services
│   └── controllers
└── analytics
    ├── services
    └── controllers
```

---

## Development Roadmap

### Phase 1: Environment Setup ✓
- Install and configure development tools
- Set up MySQL database
- Verify Spring Boot can connect to database

### Phase 2: Define MVP Scope
**Must-Have:**
- Habit tracking with logs
- Health metrics recording
- Calendar events or activity logs

**Nice-to-Have:**
- Both calendar and activity modules
- Advanced features (tags, users, courses)

### Phase 3: Database Schema Design
Design and implement tables:
- `habit` - Habit definitions with targets
- `habit_log` - Individual habit completions
- `health_metric` - Wellness measurements
- `calendar_event` - Time-based events
- `activity_log` - Categorized activities

### Phase 4: Core Tracking Module
**Habits:**
- CRUD operations for habits
- Log habit completions
- Query logs by date range

**Health Metrics:**
- Record metrics (mood, stress, energy)
- Query metrics by date range

### Phase 5: Calendar & Activity Module
- Manage calendar events
- Log activities with duration and type
- Optional: Mock calendar sync endpoint

### Phase 6: Analytics Endpoints
Implement key analytics:
- **Habit Analytics**: Weekly completion rates
- **Time Analysis**: Duration by activity type
- **Health Trends**: Average metrics over time

### Phase 7: Architecture Refinement
- Organize packages by service layer
- Create architecture diagrams
- Optional: Extract calendar as microservice

### Phase 8: Documentation & Testing
- Complete Postman collection
- Write technical report
- Prepare presentation slides

### Phase 9: NoSQL Version (Future)
- Design MongoDB document schema
- Implement aggregation pipelines
- Compare SQL vs NoSQL approaches

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
POST   /api/health-metrics
GET    /api/health-metrics?from=&to=
```

### Calendar Events
```
GET    /api/calendar-events?from=&to=
POST   /api/calendar-events
POST   /api/calendar/sync?from=&to=
```

### Activity Logs
```
GET    /api/activity-logs?from=&to=&type=
POST   /api/activity-logs
```

### Analytics
```
GET    /api/analytics/habits/weekly?habitId=&weekStart=
GET    /api/analytics/time-by-activity?from=&to=
GET    /api/analytics/health/trends?from=&to=
```

---

## Future Work

### Short Term
- Implement actual Google Calendar API integration
- Add user authentication and authorization
- Create web frontend interface

### Long Term
- NoSQL version using MongoDB for time-series data
- AI-powered insights and recommendations
- Mobile application
- Real-time analytics dashboard
- Multi-user support with data privacy

---

## Database Schema

```sql
-- Core tables with relationships
habit (id, name, category, target_per_week, created_at)
habit_log (id, habit_id, completed_at, notes)
health_metric (id, metric_type, value, recorded_at)
calendar_event (id, title, start_time, end_time, description)
activity_log (id, activity_type, duration, started_at, calendar_event_id)
```

**Key Indexes:**
- Date/time columns for efficient range queries
- Foreign keys for referential integrity
- Activity type for aggregation queries

---

## Contributing

This is an academic project. For the Service Web course evaluation, focus on:
1. Stable CRUD operations
2. Clean architecture
3. 2-3 working analytics endpoints
4. Professional documentation

---

## License

Academic project - Nabil OULAHYANE

---

## Notes

- Start simple: habits + health metrics + basic analytics
- Calendar sync can be mocked initially
- Microservice split can remain at design level if time is limited
- A working MVP is better than an incomplete complex system

**Good luck building Life Analytics 2.0! 🚀**