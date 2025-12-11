# 📅 Google Calendar Integration Guide

To integrate your habits and tasks with Google Calendar, we need to connect your backend to the Google Calendar API.

## Phase 1: Google Cloud Setup (Required)
Before writing code, you need to register the app with Google.

1.  **Go to Google Cloud Console**: [https://console.cloud.google.com/](https://console.cloud.google.com/)
2.  **Create a Project**: Name it "Life Analytics".
3.  **Enable API**:
    *   Go to "APIs & Services" > "Library".
    *   Search for **"Google Calendar API"**.
    *   Click **Enable**.
4.  **Configure OAuth Consent**:
    *   Go to "APIs & Services" > "OAuth consent screen".
    *   Choose **External** (since it's a personal app).
    *   Fill in required fields (App name, email).
    *   **Scopes**: Add `.../auth/calendar` (See, edit, share, and permanently delete all the calendars you can access using Google Calendar).
    *   **Test Users**: Add your own Gmail address.
5.  **Create Credentials**:
    *   Go to "Credentials" > "Create Credentials" > **OAuth client ID**.
    *   Application type: **Desktop app**.
    *   Name: "Spring Boot Backend".
    *   Click Create.
6.  **Download JSON**:
    *   Download the JSON file.
    *   Rename it to `credentials.json`.
    *   Place it in the `backend/src/main/resources/` folder.

---

## Phase 2: Backend Implementation

### 1. Add Dependencies
We need to add the Google Client libraries to `pom.xml`.

```xml
<dependency>
    <groupId>com.google.api-client</groupId>
    <artifactId>google-api-client</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>com.google.oauth-client</groupId>
    <artifactId>google-oauth-client-jetty</artifactId>
    <version>1.34.1</version>
</dependency>
<dependency>
    <groupId>com.google.apis</groupId>
    <artifactId>google-api-services-calendar</artifactId>
    <version>v3-rev20220715-2.0.0</version>
</dependency>
```

### 2. Create `GoogleCalendarService`
This service will handle the authentication flow (opening a browser window to log you in) and sending data to Google.

### 3. Update Data Model
Since Habits are recurring (e.g., "Run 3x a week"), we need to decide **when** to schedule them.
*   **Option A**: Add a `preferredTime` to the Habit entity (e.g., "08:00").
*   **Option B**: Create a new `Task` entity for one-off deadlines.

---

## Phase 3: Frontend Integration

1.  Add a **"Sync to Calendar"** button in the UI.
2.  When clicked, the backend will:
    *   Check if authorized.
    *   Create events in your real Google Calendar for the upcoming week based on your active habits.

---

## Next Steps
Would you like me to:
1.  **Add the dependencies** to `pom.xml` now?
2.  **Create the `Task` entity** so you can track deadlines as requested?
