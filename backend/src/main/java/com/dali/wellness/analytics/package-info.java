/**
 * Analytics Domain
 * ─────────────────
 * Read-only service that aggregates data from Tracking and Calendar
 * to produce insights:
 *   • Weekly habit completion rates
 *   • Time spent per activity type
 *   • Health trends over time
 *
 * Subpackages:
 *   • dto/        – Response DTOs for aggregated statistics
 *   • service/    – Query logic (SQL GROUP BY, etc.)
 *   • controller/ – REST endpoints under /api/analytics/*
 */
package com.dali.wellness.analytics;
