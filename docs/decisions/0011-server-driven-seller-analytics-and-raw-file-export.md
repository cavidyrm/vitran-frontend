# ADR 0011 — Server-driven seller analytics and raw file export

## Status

Accepted — Phase 10

## Context

Seller analytics GET has no Postman response example. Export returns a CSV attachment, not `ApiEnvelope`. Placement boosts have a verified create request/response and an empty list example. Boost `price_paid` has no client-accessible price catalog. Event tracking already exists (Phase 6).

## Decision

1. Seller analytics **reporting** is separate from Phase 6 event **collection**.
2. Do **not** invent dashboard DTOs, metric names, timeseries, or locked-metric objects until backend source verifies them.
3. Metric availability, when implemented, is server-driven (`available_metrics` / `locked_metrics`); locked is not value `0`.
4. CSV export uses `FileDownloadExecutor`, bypassing JSON envelope decoding. Error JSON on non-2xx is mapped as `AppError`, never saved as CSV.
5. File saving is `FileSaver` in `:core:platform` (interface + DI). Domain has no filesystem types.
6. Boost `price_paid` is never inferred from the Postman sample `50000`. Create-boost UI/ViewModel stays blocked until an authoritative price source exists.
7. Boost POST is never automatically retried.
8. Plan `ranking_boost` is not a placement `Boost` record.

## Alternatives

- Copy sibling Vitran nullable dashboard DTO — rejected (invented schema).
- Force CSV through `ApiRequestExecutor` — rejected (would fail or mis-decode).
- FileKit — rejected (ADR 0008).
- Hard-code Business slug for export — rejected (capabilities are typed).

## Consequences

- Analytics Compose dashboard is deferred; ViewModel exposes `ContractUnresolved`.
- Export and FileSaver can ship before dashboard schema exists.
- Boost list shows Empty or Unmapped; no fake history/cancel/edit.
