# ADR 0006 — Shared reference data (Location + Taxonomy)

## Status

Accepted — Phase 4 (2026)

## Context

Cities and product taxonomy are used across shopper browse, seller shop/product creation, admin catalog, and future search/home APIs. Phase 1–3 established feature modules (`:feature:auth`, `:feature:account`) and shared networking. UI already contained city selectors and taxonomy pickers backed by in-memory mocks.

Reference data must not be owned by Shop, Product, or a single screen because:

- Multiple features filter or select by `CitySlug` / `CategorySlug`
- Taxonomy is a hierarchical tree reused by admin pickers and browse surfaces
- Public endpoints are anonymous (`AuthMode.None`)

## Decision

Introduce two Gradle feature modules:

| Module | Contracts | Data |
|--------|-----------|------|
| `:feature:location` | `LocationRepository`, `City`, `CityId`, `CitySlug` | `LocationApi`, `DefaultLocationRepository` |
| `:feature:taxonomy` | `TaxonomyRepository`, `CategoryNode`, `CategoryDetails`, `CategorySlug` | `TaxonomyApi`, `DefaultTaxonomyRepository` |

Presentation ViewModels live in feature modules; `:shared` maps domain models to existing UI types (`AdminTaxonomyNode`, `BrowseCategory`, `AdminSelectOption`).

Both repositories are application-scoped singletons with in-memory caches (list/tree + taxonomy detail by slug). No Room/offline persistence in Phase 4.

Category attributes and return-reasons endpoints remain **unimplemented** until Postman/backend verifies non-empty response schemas.

## Alternatives considered

1. **`shared/location` and `shared/taxonomy` packages only** — Rejected; inconsistent with Phase 3 `:feature:*` extraction and prevents marketplace/seller modules from depending on reference data without `:shared`.
2. **Single `:feature:reference` module** — Rejected for now; separate modules match `api-feature-map.md` (`LocationApi` vs `TaxonomyApi`) and keep dependency graphs clear.
3. **Put cities in Account admin feature** — Rejected; public city list is needed by seller create-store and future marketplace filters, not only admin CRUD.

## Consequences

- Future `:feature:marketplace`, `:feature:seller`, `:feature:home` depend on `:feature:location` and/or `:feature:taxonomy` domain interfaces — not DTOs or `:shared` mocks.
- Admin city CRUD and taxonomy admin mutations remain out of Phase 4; `AccountCities*` screens stay on mocks until Admin phase.
- `CategorySlug` stays `String` in domain (Gap 1); Shop/Product DTO int/string coercion is deferred to their phase.
- Browse grid visuals on `CategoriesScreen` still use shop.app CDN fallbacks; API list nodes supply real slug/title only.

See [reference-data.md](../reference-data.md).
