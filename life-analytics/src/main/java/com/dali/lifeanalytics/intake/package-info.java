/**
 * Intake Domain (Gemini-powered)
 * ───────────────────────────────
 * Receives natural-language daily logs, converts them to structured JSON
 * via Gemini API, and dispatches to Tracking/Calendar services.
 *
 * Subpackages:
 *   • dto/        – Request DTOs for daily-log JSON schema
 *   • service/    – Gemini API client, JSON validation, dispatch logic
 *   • controller/ – REST endpoint POST /api/intake/daily-log
 */
package com.dali.lifeanalytics.intake;
