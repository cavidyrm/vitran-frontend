# ADR 0010 — Server-driven plan capabilities and payment verification

## Context

Vitran sellers subscribe to plans per shop. Plan `features` JSON is heterogeneous. Payment is an external URL handoff with a provider-facing callback and **no** client payment-status API.

## Decision

1. Map known feature keys to typed `PlanCapabilities` at the Data boundary; keep raw `JsonObject` out of Domain/Presentation.
2. Model `ShopSubscription` per `ShopId`, not a user-global plan.
3. Treat `PaymentSession` as initiation metadata only — never as entitlement proof.
4. Never call `/payments/callback` from the app; verify by refreshing `GET .../subscription`.
5. Do not automatically retry non-idempotent `POST` purchase or credit-apply.
6. Use returned `payment_url` unchanged after scheme validation (`http`/`https`).

## Alternatives

- Drive UI by plan slug — rejected (fragile, contradicts server capabilities).
- Poll payment status endpoint — none exists.
- Forge provider callback from client — insecure / incorrect.

## Consequences

- Phase 10 can gate analytics/boosts on `PlanCapabilities` without rewriting Phase 9.
- Payment UX must support `NotYetConfirmed` and resume verification.
- Missing deep-link return and payment-status APIs remain documented gaps.
