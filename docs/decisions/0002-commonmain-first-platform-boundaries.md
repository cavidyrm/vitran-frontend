# ADR 0002: commonMain-first with platform boundaries

## Status

Accepted — Phase 1

## Context

VitranShop targets Android, iOS, JVM Desktop, JS, and Wasm from shared code. Platform-specific code already exists for web navigation (`BrowserNavigation`), media (`NetworkImageUrl`, `LoopingNetworkVideo`), and Ktor engines (Coil).

We must decide how to handle capabilities that differ by OS: secure storage, file picking, sharing, notifications, payment URLs, analytics export downloads.

## Decision

1. **`commonMain` is the default** for domain, ViewModels, repositories, DTOs, mappers, and Ktor API definitions.
2. **Platform source sets are exceptional** — only when an OS API is genuinely required.
3. **Replaceable capabilities use interface + DI** (registered in platform Koin modules):
   - `SecureStorage` (tokens — Phase 3)
   - `ShareManager`, `ExternalUrlLauncher`
   - `FilePicker` / upload abstractions for multipart
   - `NotificationManager`, `DeviceInfo`
4. **`expect/actual` only when** a small primitive is needed in common code and a DI service would be artificial (e.g. web compose resource URL init already in project).

## Consequences

| Capability | Approach |
|------------|----------|
| Token storage | `:core:platform` `SecureStorage`; never Room or plain prefs for tokens |
| Product/category image upload | Shared `UploadPayload` / stream abstraction; platform converts Uri/NSURL/File |
| Analytics CSV export | Download via Ktor in common; platform save/share via interface |
| Payment callback / external checkout URL | `ExternalUrlLauncher` |
| Referral share sheet | `ShareManager` |
| Web SPA routing | Existing `BrowserNavigation` expect/actual |

**Positive:** Maximum code reuse; clear test seams with fakes.

**Negative:** Initial DI setup for platform modules (Phase 2–3); must resist putting Android `Context` in domain.

## Related

- [build-configuration.md](../build-configuration.md)
- [dependency-rules.md](../dependency-rules.md)
