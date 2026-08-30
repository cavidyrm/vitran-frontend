# Seller Analytics & Placement Boosts (Phase 10)

Phase 10 implements **CSV export**, **raw file download**, **FileSaver**, and **placement-boost transport** inside `:feature:seller`. Seller analytics **dashboard JSON is not implemented** (Postman has no response example; backend source was not available). Compose screens are deferred.

**Contract branch:** UNRESOLVED schemas were not invented.

## 1. Seller Analytics ownership

Packages in `:feature:seller`:

| Package | Role |
|---------|------|
| `feature/seller/analytics/` | Export-only domain/data/presentation |
| `feature/seller/boost/` | Placement boost create + empty/unmapped list |

No new Gradle module. Distinct from Phase 6 `feature/engagement/analytics`.

## 2. Phase 6 event tracking vs Phase 10 seller analytics

| Phase 6 | Phase 10 |
|---------|----------|
| Customer event **ingestion** (`ShopAnalyticsApi`, `UserEventApi`) | Seller **reporting/export** |
| `promotion_impression` / `promotion_click` | Not emitted from boost create |

Creating a boost is not a promotion impression.

## 3. Analytics endpoint

| Method | Path | Status |
|--------|------|--------|
| GET | `/api/v1/seller/shops/{shopId}/analytics?period=` | **Unresolved contract — not implemented** |
| GET | `/api/v1/seller/shops/{shopId}/analytics/export?period=` | **Implemented** (raw CSV) |

Both require `AuthMode.Required`.

## 4. Analytics response contract

**UNRESOLVED — NOT INVENTED.** Postman description mentions `available_metrics` / `locked_metrics` with **no saved example**. Sibling Vitran client guessed nullable metric fields — **not copied**.

`SellerAnalyticsViewModel` dashboard state is `ContractUnresolved`. No fake views/clicks/charts.

## 5. `available_metrics`

**Open.** Not mapped.

## 6. `locked_metrics`

**Open.** Not mapped. Locked ≠ zero is documented for when the schema is verified.

## 7. Metric typing

**Open.** No metric Domain types.

## 8. Analytics periods

Postman-proven query values only:

| Domain | Query |
|--------|-------|
| `AnalyticsPeriod.SevenDays` | `7d` |
| `AnalyticsPeriod.ThirtyDays` | `30d` |

Complete allowed set **Open**. `90d` / `today` / `1y` **not** added. Mapping lives in Data (`toQueryValue()`).

## 9. Shop scoping

All analytics/export/boosts are scoped by Phase 5/7 `ShopId`. Shop list comes from `SellerShopRepository`. Switching shop cancels in-flight export.

## 10. PlanCapabilities integration

Reuse Phase 9 `PlanCapabilities.advancedAnalytics` (`features.advanced_analytics` Boolean, missing → `false`). UX precheck only. **No plan-slug checks** (`business`).

Server export success is accepted even if local capability is stale `false`.

## 11. Locked metric upsell

No locked metric cards (schema unresolved). ViewModel can emit `OpenPlans(shopId)` for Phase 9 upgrade (`Route.StorePlanUpgrade`). No second purchase flow.

## 12. Analytics caching

No dashboard cache. CSV bytes are **not** stored in `SellerAnalyticsStateStore`. Store only records export-attempt keys and **clears on logout**.

## 13. Refresh / cancellation

Dashboard GET is not implemented. Export uses a cancellable `Job`. Shop/period change cancels in-flight export. `CancellationException` is not mapped to `AppError`.

## 14. CSV export

`ExportSellerAnalyticsUseCase` → `SellerAnalyticsRepository.exportAnalytics` → `SellerAnalyticsApi` → `FileDownloadExecutor`. Columns **Open**. Bytes passed through unchanged. Not parsed.

## 15. Raw download networking

`FileDownloadExecutor` in `:core:network` is generic (not analytics-specific). Success: HTTP 2xx, not HTML, opaque `ByteArray`. Non-2xx: JSON envelope via `ApiErrorMapper` (never saved as `.csv`). Memory: ByteArray (bounded tradeoff, not unlimited).

## 16. FileSaver architecture

`:core:platform` `FileSaver` / `FileSaveResult` (`Saved` / `Cancelled` / `Failed`). Domain does not depend on it. Presentation (`SellerAnalyticsViewModel`) saves after download.

## 17. Export behavior by platform

| Target | Implementation | Runtime verification |
|--------|----------------|----------------------|
| Android | `HostedFileSaver` (bind SAF/`CreateDocument` when UI exists) | Not runtime-tested (screens deferred) |
| iOS | `HostedFileSaver` | Compiles; not runtime-tested |
| Desktop | `JvmFileSaver` (AWT `FileDialog.SAVE`) | Not runtime-tested this phase |
| JS | `BrowserFileSaver` (Blob + object URL, revoke) | Not runtime-tested |
| Wasm | `HostedFileSaver` | Compiles; not runtime-tested |

No `WRITE_EXTERNAL_STORAGE`. No FileKit.

## 18. Export security

- CSV bodies not logged (`LoggingSanitizer` redacts `text/csv` / attachment).
- `Content-Disposition` filename sanitized (`sanitizeDownloadFileName`); fallback `vitran-shop-{shopId}-analytics-{period}.csv`.
- Path traversal (`../`, `\`, control chars) stripped.

## 19. Boost ownership

`SellerBoostRepository` / `SellerBoostApi` — **not** merged into `SellerShopRepository`. Distinct from plan `ranking_boost` (`RankingBoostLevel`).

## 20. Shop-level vs Product-level Boost

`BoostTarget.Shop` omits `product_id`. `BoostTarget.Product(ProductId)` sends `product_id`. Product still shop-scoped in the URL.

## 21. Boost request

Verified Postman shape: optional `product_id`, `days`, `price_paid` (`Long`). `explicitNulls = false` omits null `product_id`.

## 22. Boost response

Verified create fields: `id`, `shop_id`, `days` → `CreatedBoost`. No `started_at` / `expires_at` / status.

## 23. Active Boost list

Empty `boosts: []` → `ActiveBoosts.Empty`. Non-empty items: `ActiveBoosts.Unmapped(count)` — **no invented PlacementBoost fields**. Transport may hold `List<JsonElement>` only at DTO boundary.

## 24. Duration contract

Example `days = 7` is payload shape. Allowed set **Open**. UI does not offer invented chips.

## 25. Boost pricing contract

**UNRESOLVED — NO CLIENT INVENTION.** No `GET /boost-prices`. Postman `50000` is **not** policy. `CreateBoostViewModel.submit()` **never** calls the repository/use case. `CreatePlacementBoostUseCase` exists for tests/future wiring and treats `pricePaid` as an opaque Long.

Backend may currently trust client `price_paid` without server-side price validation — **security gap**, not fixed in the client.

## 26. Non-idempotent mutation rules

POST `/boosts` is not auto-retried (`HttpRequestRetry` GET/HEAD only). Repository `tryLock` prevents duplicate in-flight creates. No offline queue.

## 27. Seller Shop/Product integration

Shop selection via Phase 7 `SellerShopRepository`. Product `ProductId` reused from Phase 5/8. Publication eligibility **Open** — not invented. Compose product picker deferred.

## 28. Public marketplace invalidation

`CreatePlacementBoostUseCase` calls `ShopPublicCacheInvalidator.invalidate(shopId)` after success. Does **not** re-sort Home/browse. No `PromotionChanged` event type (reuse existing invalidator).

## 29. Security / privacy

- Analytics/CSV not dumped to logs.
- User-scoped stores implement `SessionInvalidationListener`.
- Shop/product IDs from seller selection, not typed by the user (UI deferred).
- Boost POST never blindly retried.

## 30. Tests

See `:core:network` `FileDownloadExecutorTest`, `LoggingSanitizerTest`; `:core:platform` `DownloadFileNamesTest`; `:feature:seller` analytics/boost API, use-case, and ViewModel tests.

Highlights: locked schema not invented; raw CSV bypasses envelope; malicious filename sanitization; Boost POST no auto-retry; CreateBoost VM never POSTs.

## 31. API gaps

See [api-gaps.md](api-gaps.md) Gap 42.

## 32. Phase 11 readiness

Reuse: `AppError`/`AppResult`, session, `ShopId`/`ProductId`/`UserId`, `PlanCapabilities`, pagination, `FileDownloadExecutor`/`FileSaver`, UDF, Koin.

Do **not** reuse: Seller Analytics DTOs as Admin analytics; Seller Boost DTOs as Admin promotions; public Plan DTOs as Admin Plan mutation DTOs; Seller Shop/Product DTOs as Admin projections.
