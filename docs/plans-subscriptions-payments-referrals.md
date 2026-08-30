# Plans, Subscriptions, Payments & Referrals (Phase 9)

## 1. Commercial feature ownership

All Phase 9 domain/data/presentation lives in `:feature:seller` packages:

| Package | Responsibility |
|---------|----------------|
| `feature/seller/plan/` | Public plan catalog |
| `feature/seller/subscription/` | Per-shop subscription, purchase, payment verification, entitlements |
| `feature/seller/referral/` | Referral validation, profile, credit apply |

UI remains in `:shared` (`StorePlanScreen`, `StorePlanUpgradeScreen`, `ReferralsScreen`). Admin plan CRUD stays Phase 11 mock.

## 2. Public Plan catalog

- `PlanApi` → `GET /api/v1/plans`, `GET /api/v1/plans/{id}`
- `AuthMode.None`
- `DefaultPlanRepository` in-memory cache; **survives logout**
- Server `sort_order` preserved (no client re-sort)

## 3. Plan IDs and slugs

- `PlanId(Long)`
- `PlanSlug(rawValue)` with helpers `isFree` / `isStarter` / `isGrowth` / `isBusiness`
- Unknown slugs never crash; capabilities are **not** driven by slug (except centralized referral eligibility)

## 4. Plan price / duration

- `priceAmount: Long` (non-floating)
- `durationDays: Int?` — Free may be null; never fake `30`
- Currency unit: **unresolved API gap** — UI reuses existing toman formatter as display convention only

## 5. Plan limits

`PlanLimits(maxProducts, maxImages, maxShops)` from backend integers. No `0 = unlimited` assumption.

## 6–8. Heterogeneous features → PlanCapabilities

DTO `features: JsonObject` → `PlanCapabilitiesMapper` → Domain only.

| Key | Type | Domain | Missing |
|-----|------|--------|---------|
| `ranking_boost` | string | `RankingBoostLevel` | `None` |
| `contact_buttons` | boolean | `Boolean` | `false` |
| `basic_analytics` | boolean | `Boolean` | `false` |
| `offers_discounts` | boolean | `Boolean` | `false` |
| `advanced_analytics` | boolean | `Boolean` | `false` (Phase 10 readiness) |

Unknown keys ignored. Wrong types do not coerce (`"yes"` ≠ true). No `JsonElement` in Domain.

## 9–12. ShopSubscription

- Per `ShopId` via `GET /seller/shops/{id}/subscription` (`AuthMode.Required`)
- `SubscriptionPlan` is a **separate** DTO projection (no public detail requirement)
- Free: `expiresAt` / `daysRemaining` null is valid
- Paid: use server `expiresAt` / `daysRemaining`; do not recompute from duration
- `SubscriptionStatus.Active | Unknown(raw)`
- User-scoped `SubscriptionStateStore` clears on logout

## 13–15. ShopEntitlements & seller integration

`GetShopEntitlementsUseCase` composes subscription limits + catalog capabilities when available.

- Create Product uses `maxImages` / `maxProducts` from entitlements (fallback 5 images if unknown)
- Server remains authoritative
- Fulfillment still from Phase 7 `getFulfillmentOptions` — not inferred from plan features
- After confirmed purchase/credit: `ShopPublicCacheInvalidator.invalidate(shopId)`
- **`max_shops` semantics: Open** — not enforced on create-shop

## 16–23. Plan purchase & payment

- `POST .../subscription/purchase` `{ "plan_id": N }` — `AuthMode.Required`
- `PurchasePlanUseCase` → `PaymentSession(paymentId, authority, paymentUrl)`
- Free plan not purchasable client-side
- Duplicate purchase prevented per shop (`tryLock`)
- **No automatic retry** on purchase POST
- Presentation emits `OpenExternalUrl`; `ExternalUrlLauncher` opens URL
- Launch ≠ success; session retained for launch retry
- Client **never** calls `/payments/callback`
- Returned `payment_url` used unchanged (callback path inconsistency documented)
- Verify via subscription refresh: plan-change OR same-plan `expiresAt` extension
- Unchanged → `NotYetConfirmed`; network error retains pending
- Resume + explicit “Check payment”; no aggressive polling
- No app deep-link return contract; process-death recovery not fabricated

## 24–29. Referrals

- `GET /referrals/{code}` → `valid` bool (`200` + false ≠ HTTP error)
- `GET /me/referral` profile; invite URL from server
- Credits: `ReferralCreditId`, status/source future-safe
- `ApplyReferralCreditUseCase`: available only; Free/Starter eligibility centralized; POST no auto-retry; refresh subscription + profile; no local +30 days
- Share via `ShareManager`
- Profile cache user-scoped; clears on logout
- Register still accepts optional `referral_code` without live prevalidation (avoids auth→seller dep)

## 30–32. Invalidation, logout, privacy

- Subscription + referral stores: `SessionInvalidationListener`
- Plan catalog: not cleared
- LoggingSanitizer redacts `authority`, `payment_url`, Authority query params
- No card data; no SecureStorage of payment URLs

## 33. Tests

See `feature/seller/.../plan|subscription|referral/**/*Test.kt` and network `LoggingSanitizerTest`.

## 34. API gaps

See [api-gaps.md](api-gaps.md) Phase 9 section.

## 35. Phase 10 integration points

Reuse: `PlanId`, `PlanCapabilities` (incl. `advancedAnalytics`), `ShopSubscription`, `ShopEntitlements`, `ShopId`, ExternalUrlLauncher, AppResult, AuthMode.Required, Koin, UDF.

Do **not** duplicate plan parsing, subscription retrieval, or slug capability spaghetti.
