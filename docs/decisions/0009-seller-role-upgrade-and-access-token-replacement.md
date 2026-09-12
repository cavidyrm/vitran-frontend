# ADR 0009 — First-shop access-token replacement

## Status

Accepted — Phase 7 (2026). Superseded in part by the 2026 role model: there is no `seller` JWT role. Shop ownership is `shops.owner_id`; `GET /auth/me` exposes `shop_types` (`[]` = viewer). `/seller/*` accepts a `user` (or higher) token; mutations still require the caller to own the shop. Legacy `customer`/`seller` strings still map to `UserRole.User` so old tokens do not crash.

## Context

Creating a user’s first shop may return a replacement `access_token` (+ `expires_at`) without a new refresh token (historically this also added a `seller` role). Seller features must not write SecureStorage directly. Roles must not be inferred from undocumented JWT claims.

## Decision

1. **`CreateShopUseCase` orchestrates** `SellerShopRepository` + `SessionRepository.updateAccessToken` + best-effort `AccountRepository.refreshCurrentUser`.
2. **Refresh token is always preserved** on first-shop token replacement.
3. **Role and shop-type visibility** comes only from `/auth/me` via Account infrastructure (`roles` + `shop_types`).
4. **`/auth/me` failure after successful create does not roll back** shop creation or discard the new access token.
5. **Seller data layer never touches SecureStorage** or Account implementations.

## Alternatives considered

1. **Decode seller role from JWT** — Rejected; Phase 3 rule; claims undocumented.
2. **Write tokens from SellerShopRepository** — Rejected; breaks session ownership.
3. **Require a dedicated seller role before create** — Rejected; first-shop onboarding uses create as the transition. Backend no longer uses a `seller` role.

## Consequences

- Create success then list/seller APIs must use the updated access token (tested).
- Account/session remain the same authenticated user identity.
- Seller navigation reacts to refreshed `/auth/me` (`shop_types` non-empty, or shops list) without ViewModel faking `isSeller`.
