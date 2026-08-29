# ADR 0003: API contract and transport boundaries

## Status

Accepted — Phase 1

## Context

The Vitran backend contract is defined by a Postman collection ([`vitran-api.postman_collection.json`](../postman/vitran-api.postman_collection.json)) with:

- Global response envelope (`success`, `message`, `code`, `data`, `errors`)
- Known inconsistencies (`category_slug` int vs string, heterogeneous plan `features`)
- 15 endpoints without saved response examples
- Postman folder names that do not match ideal client feature names

The client must integrate without letting transport quirks leak into domain or UI.

## Decision

1. **Postman collection is the client contract reference** — committed at `docs/postman/`.
2. **Transport stays in Data layer** — DTOs, `ApiEnvelope`, Ktor services in `:core:network` and feature `data/remote`.
3. **Domain models never carry `Dto` suffix** — mappers convert at repository boundary.
4. **API folder organization does not dictate feature architecture** — map by business ownership ([api-feature-map.md](../api-feature-map.md)).
5. **Ambiguities are documented, not invented** — see [api-gaps.md](../api-gaps.md).
6. **Active API version** — `/api/v1` only until explicit migration; origin and prefix configured separately (`ApiEnvironment`).
7. **`code` field** — treat as flexible int, not a two-value enum.

## Consequences

**Positive:**

- Phase 2 networking has clear ownership and boundaries
- Backend contract changes are traceable to Postman + gap doc
- UI remains stable while DTOs evolve

**Negative:**

- Some features blocked until missing response schemas are verified
- Mapper boilerplate for inconsistent fields (e.g. category slug)
- Dual maintenance of Postman export and docs until OpenAPI sync exists

## Related

- [api-contract.md](../api-contract.md)
- [api-gaps.md](../api-gaps.md)
