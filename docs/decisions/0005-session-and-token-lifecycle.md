# ADR 0005 — Session and Token Lifecycle

**Status:** Accepted  
**Date:** 2026-08-29  
**Phase:** 3 — Session, Authentication & Account

## Context

VitranShop needs production-quality authentication: secure credential storage, proactive token refresh, single-flight concurrency, and clear separation between transport (`:core:network`) and session ownership (`:core:session`). Phase 2 established Ktor + `ApiRequestExecutor` but used a stub `SessionReader`.

## Decision

1. **Session owns credentials** — `SessionRepository` is the only write path for tokens. Features call `establishSession()` / never hold tokens in ViewModels.

2. **Platform secure storage** — `SecureSessionStorage` in `:core:platform` with platform actuals. Desktop/Web use honest in-memory fallbacks until OS-backed storage is added.

3. **Refresh in session, HTTP in network** — `TokenRefreshRemoteDataSource` interface in session; `KtorTokenRefreshRemoteDataSource` in network to avoid Gradle cycles.

4. **SessionAuthPlugin** — Separate Ktor plugin (not `HttpRequestRetry`) handles token attach, proactive expiry refresh, and 401 single-retry. Refresh requests use `SkipSessionAuthKey`.

5. **AuthMode.Optional mutation policy** — GET/HEAD may retry after refresh failure; mutating methods do not silently replay without token.

6. **Feature modules** — `:feature:auth` and `:feature:account` own repositories/ViewModels; screens stay in `:shared`.

7. **UserRole.Unknown** — Backend role strings map to sealed `UserRole`; unknown values never crash mappers.

8. **Verification state** — `AuthFlowStateHolder` holds in-memory challenges; navigation carries phone only, never `tempToken`.

## Consequences

### Positive

- Single HttpClient stack; no parallel networking
- Testable refresh concurrency (mutex + double-check)
- Clear module boundaries for Phase 4+ features

### Negative / follow-ups

- Web/Wasm has no persistent session until storage strategy is chosen
- JVM desktop uses dev-only in-memory storage
- KVault requires ≥1.12.0 for `iosSimulatorArm64` (resolved in Phase 3)

## Alternatives considered

| Alternative | Rejected because |
|-------------|------------------|
| AuthRepository owns refresh | Duplicates session logic; breaks single source of truth |
| JWT decode for roles | Backend roles authoritative; claims may lag |
| Merge refresh into HttpRequestRetry | Different semantics (auth vs transport retry) |
| Pass tempToken in navigation | Security / URL leakage risk |

## References

- [session-authentication.md](../session-authentication.md)
- [0004-ktor-network-stack.md](0004-ktor-network-stack.md)
- Postman: `docs/postman/vitran-api.postman_collection.json`
