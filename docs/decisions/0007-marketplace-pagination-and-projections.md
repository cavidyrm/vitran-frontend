# ADR 0007 — Marketplace cursor pagination and endpoint projections

## Status

Accepted — Phase 5 (2026)

## Context

Phase 5 wires shopper marketplace UI to public Vitran APIs. List endpoints use cursor pagination (`per_page`, `cursor`, `has_more`, `next_cursor`). Home, shop browse, and product list/search return different item shapes for the same logical entities.

Android Paging 3 is not available in Compose Multiplatform common code. A single shared `ShopDto` / `ProductDto` would force nullable fields and incorrect coupling across endpoints.

## Decision

1. **Cursor pagination in presentation** — `CursorListState` + `CursorListController` in `:feature:marketplace` (not a framework module). ViewModels own load-initial, refresh, load-more, and generation guards.

2. **Endpoint-specific DTOs** — Separate list, browse, detail, and home section transport types. Mappers convert to domain `ShopSummary`, `ShopDetails`, `ProductSummary`, `ProductDetails`.

3. **No Paging 3** — Infinite scroll uses explicit `loadNextPage()` from UI scroll observers; state is `StateFlow`-based UDF.

4. **DTO-only slug compatibility** — `FlexibleCategorySlugSerializer` accepts int/string at JSON boundary; domain keeps `CategorySlug` as `String`.

5. **Defer unverified schemas** — Home item arrays and catalog search response are not invented when live probe or Postman lacks examples.

## Alternatives considered

1. **Shared universal marketplace DTO** — Rejected; Postman shows shape differences (browse plan nested object vs list title-only).
2. **Paging 3 on Android only** — Rejected; breaks KMP parity for Web/Desktop.
3. **Put pagination in `:core:domain`** — Rejected; presentation concern with loading flags belongs in feature presentation layer.

## Consequences

- Each new list surface reuses `CursorListController` with typed id extractor.
- Home section wiring requires a second probe pass before replacing mock UI rows.
- Catalog advanced search remains request-only until `GET /catalog/search` response is verified.
