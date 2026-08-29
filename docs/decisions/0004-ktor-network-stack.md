# ADR 0004: Ktor Client Network Stack

## Status

Accepted — Phase 2

## Context

VitranShop needs a Kotlin Multiplatform HTTP client for REST APIs documented in the Postman collection. The app targets Android, iOS, Desktop (JVM), JS, and WasmJS. Phase 1 established module ownership (`:core:network`) and API environment configuration but deferred transport implementation.

Requirements:

- Shared client configuration in `commonMain`
- kotlinx.serialization for JSON DTOs matching backend snake_case fields
- Envelope-based response parsing with structured errors
- DI-friendly singleton HttpClient testable with MockEngine
- No coupling of Domain or Presentation to Ktor types

## Decision

Use **Ktor Client 3.1.3** with **kotlinx.serialization** as the shared networking stack in `:core:network`.

Key implementation choices:

1. **`expectSuccess = false`** — manual status + envelope evaluation preserves error bodies
2. **`ApiRequestExecutor`** — transport-only execution returning `AppResult<T>`
3. **`AppError` / `AppResult`** in `:core:domain` — Ktor exceptions never escape infrastructure
4. **Platform engines** per target source set (Android, Darwin, Java, JS)
5. **Koin `networkModule`** — singleton HttpClient, Json, executor, HealthApi
6. **`SessionReader`** integration via auth header plugin (stub in Phase 2)

## Alternatives considered

| Alternative | Why not |
|-------------|---------|
| Retrofit / OkHttp (Android-only) | Not multiplatform |
| Custom curl/native per platform | Duplicates config, breaks commonMain-first strategy |
| KMP HTTP without serialization plugin | Manual JSON parsing error-prone for large API surface |
| Global `object NetworkClient` | Prevents MockEngine injection in tests |
| Exposing `ApiEnvelope` to Domain | Violates Clean Architecture boundaries |

## Consequences

### Positive

- One HTTP stack across all KMP targets
- Feature APIs follow a consistent executor pattern
- MockEngine tests cover transport without backend dependency
- Phase 3 auth (token attach, refresh) plugs into existing plugin/DI extension points

### Negative / trade-offs

- `:core:network` now depends on `:core:domain` and `:core:session` (dependency graph update documented)
- JS/Wasm browser networking subject to CORS and engine limitations
- Ktor plugin API changes require care on upgrades

### Multiplatform implications

- All transport code in `commonMain` except platform engine dependencies
- Use `kotlinx.io.IOException` (not `java.io`) in common code
- Web targets share `ktor-client-js` engine dependency

## Related

- [docs/networking.md](../networking.md)
- [docs/api-contract.md](../api-contract.md)
- ADR 0002: commonMain-first platform boundaries
