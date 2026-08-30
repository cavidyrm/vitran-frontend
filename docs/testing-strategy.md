# Testing Strategy (Phase 12)

## Test pyramid

1. Domain unit tests (roles, publication, entitlements, RBAC)
2. Mapper / DTO ↔ Domain / Entity tests
3. Network contract tests (`MockEngine`)
4. Database / DAO / migration tests
5. Repository cache + Flow tests
6. Use-case + ViewModel tests (`runTest`)
7. Concurrency (session refresh)
8. Targeted Compose UI tests
9. Platform smoke (manual / CI compile)
10. Release smoke checklist

Coverage % is diagnostic only — not Definition of Done.

## Critical paths

App startup, session restore, register/login/verify, token refresh concurrency, Home, shop/product browse, search, wishlist/follow/favorites, Product Contact, seller role transition, seller shop/product multipart, subscription + payment handoff/reconcile, referral credit, Admin RBAC, moderation, CMS HTML safety, offline cache reads.

## Network tests

Paths, methods, query encoding, auth modes, bodies, multipart fields, error mappings. Never hit production backend in unit tests.

## Database tests

Empty + network success, populated + refresh success/failure, offline with/without cache, invalidation, schema creation.

## Migration tests

v1: fresh create. Harness ready for vN → vN+1 without unexpected loss.

## Repository / use-case / ViewModel / Flow

Existing feature tests retained; cache Flow tests assert DB emission before/after refresh and that errors do not destroy cached streams.

## Compose tests

Prioritize: auth-required action, offline cached screen, pagination error, seller pending approval, payment awaiting verification, Admin permission gating, HTML content.

## Platform tests

Android EncryptedSharedPreferences smoke where feasible; iOS Keychain path documented; Desktop secure storage when implemented; Wasm OPFS optional smoke.

## Release smoke

See [release-readiness.md](release-readiness.md). Sandbox payment only; isolated Admin test accounts.

## CI matrix

| Job | Scope |
|-----|--------|
| PR | commonTest (cores/features), Android compile, Wasm compile, Desktop compile when hosts allow |
| main | Wasm distribution + Docker deploy |
| macOS (when available) | iOS framework compile |

## What cannot be automated here

Store policy forms, live App/Universal Link association, real payment provider, production crash-provider dashboards, notarization, marketing screenshots.
