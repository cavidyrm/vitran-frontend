# ADR 0008: Multiplatform file selection and upload

## Status

Accepted — Phase 8; picker implementation updated 2026-04 (FileKit)

## Context

Seller product create/update require multipart image uploads across Android, iOS, Desktop, and Web/Wasm. Platform file types must not enter Domain or common business APIs. The original HostedImagePicker bind on Android/iOS/Wasm was never wired, so gallery pick did not work there.

## Decision

1. Platform file types (`Uri`, `File`, `NSURL`, browser `File`) stay in platform source sets.
2. Shared `SelectedFile` + suspending `readBytes()` in `:core:platform` — ByteArray-backed for modest product images.
3. `ImagePicker` interface + DI; cancel returns an empty list. Runtime implementation is `FileKitImagePicker` ([FileKit](https://klibs.io/project/vinceglb/FileKit) `filekit-dialogs`), which owns native pickers. Domain still only sees `SelectedFile`.
4. Android: `FileKit.init(activity)` from `BindFileKit()`. Desktop JVM: `FileKit.init(appId = "com.vitran.shop")`. iOS / JS / Wasm need no extra init.
5. Ktor multipart encoding lives in seller Data (`SellerProductApi`).
6. No background / offline upload queue; no automatic retry of multipart mutations.
7. HTTP logging must not dump binary multipart bodies.
8. `FileSaver` stays the existing platform implementations (not FileKit). Profile avatar uses `POST /auth/profile/avatar` (multipart `image`) after gallery pick; response `avatar_url` updates profile state.

## Alternatives

- Streaming-only abstraction everywhere — deferred; overkill for ≤5 product images (revisit Phase 11 taxonomy import).
- Hand-rolled Photo Picker / PHPicker / `<input>` bind per target — replaced by FileKit after HostedImagePicker stayed unbound.

## Consequences

Positive: one common pick API; testable `ImagePicker` fakes; Domain stays clean.

Negative: FileKit is an extra dependency; ByteArray memory limits for large future imports.
