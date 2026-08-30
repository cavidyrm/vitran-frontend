# ADR 0009 — Seller role upgrade and access-token replacement

## Status

Accepted — Phase 7 (2026)

## Context

Creating a user’s first shop may add the `seller` role and return a replacement `access_token` (+ `expires_at`) without a new refresh token. Seller features must not write SecureStorage directly. Roles must not be inferred from undocumented JWT claims.

## Decision

1. **`CreateShopUseCase` orchestrates** `SellerShopRepository` + `SessionRepository.updateAccessToken` + best-effort `AccountRepository.refreshCurrentUser`.
2. **Refresh token is always preserved** on first-shop token replacement.
3. **Role visibility** comes only from `/auth/me` via Account infrastructure.
4. **`/auth/me` failure after successful create does not roll back** shop creation or discard the new access token.
5. **Seller data layer never touches SecureStorage** or Account implementations.

## Alternatives considered

1. **Decode seller role from JWT** — Rejected; Phase 3 rule; claims undocumented.
2. **Write tokens from SellerShopRepository** — Rejected; breaks session ownership.
3. **Require seller role before create** — Rejected; customer→seller onboarding uses create as the transition.

## Consequences

- Create success then list/seller APIs must use the updated access token (tested).
- Account/session remain the same authenticated user identity.
- Seller navigation reacts to refreshed `UserRole.Seller` without ViewModel faking `isSeller`.
