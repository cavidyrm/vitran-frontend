# Observability (Phase 12)

## Logging architecture

- Network: `NetworkLogger` + `LoggingSanitizer` in `:core:network`
- App: prefer severity-aware abstraction; avoid `println` in features
- Debug: richer diagnostics when `ApiEnvironments.Local`
- Release: substantially reduced; no sensitive headers/bodies

## Release logging policy

Production must not log credentials, payment metadata, multipart/CSV, PII comment bodies. Query params with `session_id` / referral / authority treated carefully.

## Crash reporting

Vendor-neutral `CrashReporter` in `:core:platform`:

- `recordException`
- `recordNonFatal`
- `setAppContext` (build/env only)
- `setUserContext` — **optional**; default omit UserId/phone/email

**Production binding:** `NoOpCrashReporter` until EXTERNAL provider account is configured. Do not silently add Firebase/Sentry without privacy review.

## Technical breadcrumbs

Safe: route name, HTTP status class, feature tag, correlation id **only if** backend provides one. Do not invent request-id headers.

## Privacy redaction

Never send to crash/logs: phone, email, tokens, API keys, review/comment bodies, payment authority, raw payloads with PII.

## Network diagnostics

Map failures to `AppError`; preserve internal cause for Unexpected without exposing stack traces to UI.

## Database diagnostics

Log schema version / migration failure class — not row contents.

## Deliberately not collected

Continuous `/health` from user devices, analytics event replay queues, personalized Home in crash metadata, Admin datasets.

## Separation from product analytics

Phase 6 marketplace event POSTs ≠ crash/technical telemetry.
