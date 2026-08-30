# ADR 0008 — Engagement state and analytics

## Status

Accepted — Phase 6 (2026)

## Context

Shopper PDP and store already expose save/follow/share controls as no-ops. Backend Postman documents follow, favorite shops, wishlist, reviews, comments, product contact, and two analytics APIs. Follow **response** examples are missing. Favorite/wishlist have no per-id status GET. Public product/shop details do not include `is_favorite`.

A single `EngagementRepository` would mix unrelated capabilities. Marketplace ViewModels already own catalog load. Analytics must not fail user mutations. Session logout must drop user-scoped engagement caches without wiping public reviews.

## Decision

1. **Follow ≠ Favorite** — Separate repositories, store maps, and buttons. Gap 3 stays Open.

2. **Server authoritative after mutation** — Optimistic UI writes `EngagementStateStore` first; rollback on `AppResult.Failure`. Unknown is not rendered as “not saved / not followed.”

3. **User-scoped cache clears on logout** — `EngagementStateStore` and wishlist share-settings cache implement `SessionInvalidationListener`. Public reviews, comments, and marketplace details are not session state.

4. **Analytics best-effort / no offline queue** — `DefaultMarketplaceAnalyticsTracker` uses a structured `SupervisorJob` scope. Failures are debug-logged. Shop API uses `event` + `AuthMode.None`; user API uses `event_type` + `AuthMode.Optional`. Promotion and unverified search/category events are typed but not emitted.

5. **Do not invent unverified schemas** — Follow list/GET-by-id, contact redirect/webhook payloads, favorite 409 `reason`, review/comment metadata, and wishlist frontend URLs stay unimplemented.

## Alternatives considered

1. **Treat follow list as favorite shops** — Rejected; endpoints and payloads differ; product semantics unverified.
2. **Treat Unknown as false** — Rejected; would show empty hearts as “not saved” without a status API.
3. **Offline analytics / mutation queue (Room)** — Rejected for Phase 6; no product requirement; POSTs are not GET-retried.
4. **Grow `ProductDetailsViewModel` with engagement fields** — Rejected; sibling ViewModels in `:feature:engagement` avoid a cycle and 60-field god state.

## Consequences

- `:feature:engagement` depends on `:feature:marketplace` IDs one-way.
- `FollowingScreen` and `SavedScreen` stay mock/placeholder until list schemas or UI exist.
- Contact WhatsApp open is implemented in VM/effects only; no invented PDP button.
