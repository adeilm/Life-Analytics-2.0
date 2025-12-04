/**
 * Calendar Domain
 * ────────────────
 * Manages time-based events:
 *   • CalendarEvent – imported from Google Calendar or created manually
 *   • ActivityLog   – logged activities linked to events
 *
 * Subpackages:
 *   • entity/     – JPA entities
 *   • repository/ – Spring Data JPA repositories
 *   • service/    – Business logic, .ics export, sync
 *   • controller/ – REST endpoints under /api/calendar-events, /api/activity-logs
 */
package com.dali.lifeanalytics.calendar;
