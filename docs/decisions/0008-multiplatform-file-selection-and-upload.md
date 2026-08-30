# ADR 0008: Multiplatform file selection and upload

## Status

Accepted — Phase 8

## Context

Seller product create/update require multipart image uploads across Android, iOS, Desktop, and Web/Wasm. Platform file types must not enter Domain or common business APIs.

## Decision

1. Platform file types (`Uri`, `File`, `NSURL`, browser `File`) stay in platform source sets.
2. Shared `SelectedFile` + suspending `readBytes()` in `:core:platform` — ByteArray-backed for modest product images.
3. `ImagePicker` interface + DI; cancel returns empty list.
4. Ktor multipart encoding lives in seller Data (`SellerProductApi`).
5. No background / offline upload queue; no automatic retry of multipart mutations.
6. HTTP logging must not dump binary multipart bodies.

## Alternatives

- Streaming-only abstraction everywhere — deferred; overkill for ≤5 product images (revisit Phase 11 taxonomy import).
- Third-party filekit — not added; keep dependency surface small.

## Consequences

Positive: testable fakes; Domain stays clean; one HttpClient.

Negative: Hosted pickers need UI bind on Android/iOS/Wasm; ByteArray memory limits for large future imports.
