# Public Marketplace Integration (Phase 5)

Phase 5 connects existing shopper marketplace UI to Vitran public APIs via `:feature:marketplace` and `:feature:home`. UI visuals are unchanged; Phase 6 engagement actions remain deferred.

## 1. Scope

| In scope | Out of scope (Phase 6+) |
|----------|-------------------------|
| Public shops list/browse/detail/slug | Follow / favorite buttons |
| Public products list/search/detail | Product reviews UI |
| Cursor pagination + search results screen | Contact seller, `POST /events` |
| Home envelope (`GET /home`, optional auth) | Cart / checkout / payment |
| Categories merchant grids + product rows | Typed catalog search response |

## 2. Gradle modules

| Module | Package root | Depends on |
|--------|--------------|------------|
| `:feature:marketplace` | `com.vitran.shop.feature.marketplace` | `:core:domain`, `:core:network`, `:feature:location`, `:feature:taxonomy` |
| `:feature:home` | `com.vitran.shop.feature.home` | `:core:domain`, `:core:network`, `:core:session`, `:feature:location`, `:feature:taxonomy` |

Registered in `settings.gradle.kts`, `shared/build.gradle.kts`, and `VitranKoin.kt` (`marketplaceModule`, `homeModule`).

## 3. Shop domain (`shop/domain`)

| Type | File |
|------|------|
| `ShopId`, `ShopSlug`, `parseShopNavigationKey` | `ShopIdentifiers.kt` |
| `ShopPlanSummary` | `ShopPlanSummary.kt` |
| `ShopSummary` | `ShopSummary.kt` |
| `ShopDetails` | `ShopDetails.kt` |
| `ShopListQuery`, `ShopBrowseQuery` | `ShopQueries.kt` |
| `ShopRepository` | `repository/ShopRepository.kt` |

`ShopFilter` (city by id/slug) lives in `common/domain/filter/MarketplaceFilters.kt`.

## 4. Product domain (`product/domain`)

| Type | File |
|------|------|
| `ProductId` | `ProductIdentifiers.kt` |
| `ProductImage` | `ProductImage.kt` |
| `ProductSummary` | `ProductSummary.kt` |
| `ProductDetails` | `ProductDetails.kt` |
| `ProductBrowseQuery`, `ProductSearchQuery` | `ProductQueries.kt` |
| `CatalogFilters`, `CatalogSort` (request-only) | `catalog/CatalogFilters.kt`, `catalog/CatalogSort.kt` |
| `ProductRepository` | `repository/ProductRepository.kt` |

## 5. Home domain (`feature/home/domain`)

| Type | File |
|------|------|
| `HomeFeed` | `model/HomeFeed.kt` |
| `HomeRepository` | `repository/HomeRepository.kt` |

`HomeFeed.itemsVerified` is `false` until live item schemas are captured. Section counts are mapped from envelope arrays stored as `JsonElement`.

## 6. Data APIs

| API | Auth | Path |
|-----|------|------|
| `PublicShopApi` | `None` | `/shops`, `/shops/browse`, `/shops/{id}`, `/shops/slug/{slug}` |
| `PublicProductApi` | `None` | `/products`, `/products/search`, `/products/{id}` |
| `HomeApi` | `Optional` | `/home?city_id=` |

Query encoding: `MarketplaceQueryParams.kt`, `PaginationQueryParams.appendCursorPagination` (default `per_page=20`).

## 7. DTO strategy

Endpoint-specific transport types — no universal `ShopDto` / `ProductDto`:

| Endpoint | DTOs |
|----------|------|
| `GET /shops` | `ShopsDataDto` → `ShopListItemDto` |
| `GET /shops/browse` | `BrowseShopsDataDto` → `BrowseShopItemDto` |
| Shop detail | `ShopDataDto` → `PublicShopDetailsDto` |
| `GET /products` | `ProductsDataDto` → `ProductListItemDto` |
| Product detail | `ProductDataDto` → `ProductDetailsDto` |
| `GET /home` | `HomeDataDto` → `HomeSectionsDto` (arrays as `JsonElement`) |

## 8. Category slug compatibility (Gap 1)

`FlexibleCategorySlugSerializer` and `FlexibleCategorySlugListSerializer` accept `String | Number` at the DTO boundary. Domain `CategorySlug` remains `String`.

## 9. Repositories

| Implementation | Cache |
|----------------|-------|
| `DefaultShopRepository` | In-memory detail by id/slug |
| `DefaultProductRepository` | In-memory detail by id |
| `DefaultHomeRepository` | In-memory by `cityId`; refresh on session change |

404 from detail endpoints maps to `AppError.NotFound` at repository layer.

## 10. Pagination presentation

`CursorListState<T>` and `CursorListController<T, Id>` in `common/presentation/CursorListController.kt`:

- Serialized load-more (`isLoadingMore` guard)
- Refresh resets cursor
- Filter/query change resets pagination via `resetForNewQuery()`
- Dedupe append by id
- Pagination failure keeps existing items

## 11. ViewModels

| ViewModel | Module | Used by |
|-----------|--------|---------|
| `ProductDetailsViewModel` | marketplace | `ProductDetailScreen` |
| `ProductListViewModel` | marketplace | `CategoriesMarketplaceFeed` |
| `ProductSearchViewModel` | marketplace | `SearchResultsScreen` |
| `ShopDetailsViewModel` | marketplace | `StoreScreen` |
| `ShopBrowseViewModel` | marketplace | `CategoriesMarketplaceFeed` |
| `HomeViewModel` | home | `HomeScreen` |

Factories in `MarketplaceViewModelFactories.kt`; composables use `remember*ViewModel()` helpers in `:shared/di/MarketplaceViewModelHelpers.kt`.

## 12. UI mappers (`:shared`)

`ui/sections/reference/MarketplaceUiMapper.kt`:

- `ProductSummary` → `CategoriesProduct`
- `ShopSummary` → merchant grid models
- `ShopDetails` → `StoreMock` subset (placeholders for missing API fields)
- `ProductDetails` → PDP models
- `formatMarketplacePrice(Long)` — raw amount, no currency assumption (Gap 9)

## 13. Screen wiring

| Screen | Data source | Mock retention |
|--------|-------------|----------------|
| `ProductDetailScreen` | `GET /products/{id}` | Preview fixtures only |
| `StoreScreen` | Shop detail + paginated products | Cover, avatar, collections, nav chips |
| `SearchResultsScreen` | `GET /products/search` | — |
| `CategoriesScreen` | Browse shops + category products | Fallback when API empty |
| `HomeScreen` | `HomeViewModel` observes feed counts | All sections mock until item schemas verified |

## 14. Navigation

| Route | Path |
|-------|------|
| `Route.Search(query)` | `/search?q=` |
| `Route.Store(shopId)` | `/m/{shopId}` — numeric id or slug via `parseShopNavigationKey` |
| Product | `/products/{id}/{slug}` — id authoritative |

`RouteMapper.kt` percent-encodes query strings for Wasm/JS compatibility.

## 15. Live probe results (2026-08)

Probed `https://vitran.ir/api/v1/home`, `/products`, `/categories` during implementation:

| Endpoint | Result |
|----------|--------|
| `GET /home` | **404** on production — envelope + tests implemented; item DTOs **not invented** |
| `GET /catalog/search` | **UNRESOLVED — NOT INVENTED** — request models only |
| `GET /me/home/feed` | **UNRESOLVED — NOT INVENTED** |

When Home sections remain empty or unverified, `HomeScreen` keeps mock sections (`useApiSections == false`).

## 16. Catalog deferred

`CatalogFilters` and `CatalogSort` exist in domain for future wiring. No `CatalogSearchApi` or response DTOs until backend schema is verified.

## 17. Phase 6 boundaries

| UI action | Phase 5 behavior |
|-----------|------------------|
| Follow / Favorite | No-op / disabled |
| Reviews | Hidden or empty |
| Contact seller | Placeholder |
| Share | Client-side only if present |

## 18. Known UI/API gaps

Public shop detail lacks fields `StoreMock` expects: `coverUrl`, `avatarUrl`, `wordmarkUrl`, `brandColor`, `ratingLabel`, `reviewCountLabel`, `collections`, rich `navChips`. UI uses placeholders; see `docs/api-gaps.md`.

Home merchant spotlight cards may lack cover art and product peeks until Home item schemas are verified.

## 19. Home optional authentication

`HomeApi` uses `AuthMode.Optional`. `HomeViewModel` observes `SessionReader` and refreshes when session transitions between anonymous and authenticated while Home is active.

No app-wide browsing city yet — `city_id` passed only when UI provides it.

## 20. Tests

| Area | Location |
|------|----------|
| `PublicShopApiTest` | shop API paths, auth, browse |
| `PublicProductApiTest` | list, search query, detail |
| `FlexibleCategorySlugSerializerTest` | int/string slug compatibility |
| `HomeApiTest` | anonymous vs optional auth header |

Repository and ViewModel pagination tests follow Phase 4 patterns; extend as backend integration becomes available.

## 21. DI registration

```kotlin
// VitranKoin.kt
modules(..., marketplaceModule, homeModule, ...)
```

## 22. Dependency rules

`:shared` → `:feature:marketplace`, `:feature:home` (presentation + domain types for mappers). Feature modules do **not** depend on `:shared`.

## 23. Related documentation

| Document | Updates |
|----------|---------|
| `docs/api-gaps.md` | Gap 1 workaround, shop detail completeness, Home/Catalog status |
| `docs/api-feature-map.md` | Implemented ✅ markers |
| `docs/screen-feature-map.md` | Per-screen integration status |
| `docs/architecture.md` | Module ownership |
| `docs/decisions/0007-marketplace-pagination-and-projections.md` | ADR |
