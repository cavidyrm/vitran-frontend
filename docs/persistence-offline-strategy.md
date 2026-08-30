# Persistence & Offline Strategy (Phase 12)

## 1. Persistence goals

Provide durable **offline-capable reads** for public reference and marketplace snapshots so cold start and network failure remain useful. Server remains authoritative. Offline writes are out of scope.

## 2. Room / KMP decision

| Choice | Value |
|--------|--------|
| Technology | **Room 3.0** (`androidx.room3`) |
| Codegen | KSP (Kotlin-matched version) |
| Drivers | Bundled SQLite (Android/iOS/JVM); `WebWorkerSQLiteDriver` + OPFS (Wasm) |

## 3. Configured target support

| Target | Persistence |
|--------|-------------|
| Android | Room + bundled SQLite |
| iOS | Room + bundled SQLite |
| Desktop (JVM) | Room + bundled SQLite |
| Wasm | Room + WebWorker + OPFS (requires COOP/COEP) |
| JS (browser lib) | Same as Wasm when executable; otherwise N/A |

If OPFS/worker fails on Wasm: fall back to empty DB path → network/memory behavior with clear offline errors when no cache.

## 4. Database ownership

Module: `:core:database`

Owns: `@Database`, entities, DAOs, transactions, schema version, schema export, `DatabaseFactory` contract.

Feature repositories own Entity ↔ Domain mapping and refresh/invalidation policy.

## 5. Database factory

```text
DatabaseFactory (common)
  ├── androidMain — Context + BundledSQLiteDriver
  ├── iosMain — path + BundledSQLiteDriver
  ├── jvmMain — user home / app data path
  └── wasmJsMain — WebWorkerSQLiteDriver + worker URL
```

Platform DI provides `VitranDatabase`. Repositories stay common; no `expect class *Repository`.

## 6. Entity / Domain / DTO separation

| Layer | Role |
|-------|------|
| Remote DTO | Transport only |
| `*Entity` | Room tables |
| Domain | Feature models (`City`, `PlanCapabilities`, `HtmlContent`, …) |

Domain never imports Room. DTOs are never stored as entities blindly.

## 7. Cached features

Cities, Taxonomy, Plans, Static Pages, anonymous Home (city-keyed), public Shop detail, public Product detail.

## 8. Deliberately non-persisted

Tokens, temp tokens, OTP, passwords, Shop API keys, payment URLs/authority, Admin users/queues, Seller analytics, Boosts, search/catalog pages, engagement mutation queues, referral history, image binaries.

## 9. Source-of-truth architecture

```text
UI → ViewModel → Repository Flow → DAO → Domain
Repository.refresh() → API → map → transactional upsert → Flow emits
```

Composables never observe DAOs.

## 10. Cities cache policy

- **SoT (read):** DB after first successful sync
- **Refresh:** first meaningful use / session + `forceRefresh` + Admin city mutations
- **Offline:** return cache; if empty → network/offline error
- **Invalidation:** Admin create/update/delete → `invalidateCities()` / refresh
- **User scope:** public (survives logout)

## 11. Taxonomy cache policy

- Normalized categories / attributes / values
- Transactional full replace on refresh/import
- Admin mutations invalidate public taxonomy
- Offline: last tree; empty → error

## 12. Plans cache policy

- Public catalog only; features JSON column remapped to `PlanCapabilities`
- Refresh on plans/subscription screens + Admin plan CRUD
- Does **not** invalidate `ShopSubscription`

## 13. CMS cache policy

- Persist slug, title, raw body; Domain sanitizes at read (`HtmlContent`)
- Refresh on open + Admin CMS invalidate via `ContentCacheInvalidator`
- Do not store sanitized-only if policy may evolve (store raw + sanitize on map)

## 14. Home cache policy

- Anonymous/public snapshot keyed by `cityId` (and verified params)
- Do **not** persist personalized authenticated sections by default
- `/me/home/feed` not cached (contract unresolved)
- Refresh when Home opens

## 15. Shop / Product detail cache policy

- Key by id (+ slug where useful)
- Refresh on detail open
- Authoritative 404 → eviction
- Seller projections not mixed into public tables
- Image URLs only

## 16. User-scoped data

Default: none in Room. If added later: key by `UserId`, clear on logout/account switch.

## 17. Logout / account-switch

Public caches remain. Session credentials cleared via `SecureSessionStorage`. In-memory StateStores cleared via `SessionInvalidationListener`.

## 18. Offline read behavior

Show last-known cache when present. Non-blocking stale indication only where UI already supports it. No fake business placeholders.

## 19. Offline mutation policy

**No offline mutation queue.** Mutations offline → clear retryable network error. Never queue payments, Admin, Boosts, deletes, analytics replay.

## 20. Invalidation

| Mutation | Cache action |
|----------|--------------|
| Admin city CRUD | Cities refresh/invalidate |
| Admin taxonomy | Taxonomy invalidate |
| Admin plans | Plans refresh |
| Admin CMS | Content invalidate |
| Admin/seller product/shop confirm/edit | Public shop/product invalidate |

## 21. Database versioning

Schema version **1**. Exported JSON under `core/database/schemas/`.

## 22. Migrations

Every version change requires explicit `Migration` **or** documented destructive strategy for cache-only tables. Silent global destructive migration is forbidden for non-cache data (none today).

## 23. Corruption / recovery

Do not delete DB on every exception. Cache-only recreation is allowed after confirmed corruption/migration failure; document in diagnostics. Session credentials unaffected.

## 24. Testing

DAO CRUD, transactions, Flow, offline empty/populated, invalidation, logout public-cache survival, schema v1 creation, future migration harness.
