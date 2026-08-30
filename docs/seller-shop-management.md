# Seller Shop Management

Phase 7 seller shop ownership, onboarding, and management. Implementation lives in `:feature:seller`.

## 1. Seller Shop ownership

Seller endpoints are scoped to shops owned by the authenticated user. Server authorization is authoritative. Client list membership is not security.

## 2. Customer → Seller onboarding

An authenticated **customer** may enter Create Store (`/admin/stores/new`). Seller role is **not** required before first shop creation. `POST /seller/shops` may add the `seller` role server-side.

## 3. First-shop access-token replacement

Create response may include `data.tokens.access_token` + `expires_at` (no refresh token). `CreateShopUseCase` calls `SessionRepository.updateAccessToken` and preserves the existing refresh token.

## 4. Why refresh token is preserved

This is a partial JWT role upgrade for the same session, not a new login. Replacing refresh with null would break rotation and force re-auth.

## 5. Current-user role refresh

After optional token update, `CreateShopUseCase` best-effort calls `AccountRepository.refreshCurrentUser()` (`GET /auth/me`). Roles are never decoded from JWT claims.

## 6. Seller vs Public Shop APIs

| Concern | Public (`:feature:marketplace`) | Seller (`:feature:seller`) |
|---------|----------------------------------|----------------------------|
| API | `PublicShopApi` `/shops` | `SellerShopApi` `/seller/shops` |
| Pending shops | 404 / absent | Visible (`active=false`, `confirmed=false`) |
| DTO | `PublicShopDetailsDto` | `SellerShopDetailsDto` / create response DTO |
| Cache | `DefaultShopRepository` detail maps | `SellerShopStateStore` |

Never load owner edit/detail through public GET.

## 7. Seller Shop domain models

Package: `com.vitran.shop.feature.seller.shop.domain.model`

- `SellerShopSummary`, `SellerShopDetails`
- `ShopPublicationState`, `ShopSlugAvailability`
- `CreateShopCommand`, `UpdateShopCommand`, `CreateShopResult`, `SessionAccessUpdate`
- `FulfillmentMode`, `ShopApiKey`

Reuses marketplace `ShopId` / `ShopSlug` and location `CityId`.

## 8. Shop publication state

Centralized in `shopPublicationState(active, confirmed)`:

| active | confirmed | State |
|--------|-----------|-------|
| false | false | PendingApproval |
| true | true | Live |
| false | true | ApprovedHidden |
| true | false | Inconsistent (no crash) |

## 9. Slug availability

`GET /seller/shops/check-slug?slug=&exclude_id=`. Debounce ~400 ms in `CreateShopViewModel` / `EditShopViewModel`. Empty/invalid local slug → no request. Job cancellation prevents stale overwrites. `exclude_id` on edit.

## 10. Create Shop

`CreateShopUseCase` → `SellerShopRepository.createShop`. Request fields per Postman; null optionals omitted (`explicitNulls=false` + `@EncodeDefault(NEVER)`). `category_slugs` as `List<Long>` (see gap). Create Store UI does **not** invent category IDs.

## 11. Update Shop

`UpdateShopUseCase` → PATCH. Backend resets `active=false`, `confirmed=false`. Response is authoritative. Public cache invalidated via `ShopPublicCacheInvalidator`.

## 12. Re-confirmation behavior

Any successful update → PendingApproval. Live local state must not be retained.

## 13. Seller Shop list pagination

Cursor mode via `SellerShopListQuery` + `CursorPagination`. Optional `SellerShopFilter` → `active=true|false`.

## 14. Ownership / security

403/404 → `SellerShopError.ShopNotOwnedOrUnavailable`. No client-side ownership trust.

## 15. City integration

Reuse `LocationRepository` / `CityId`. Create Store city select uses numeric `CityId` string ids.

## 16. Taxonomy integration

Public taxonomy exposes string `CategorySlug` only — **no numeric CategoryId**. Seller create category selection is blocked until backend exposes mappable IDs.

## 17. `category_slugs` compatibility status

**Open / blocking:** Postman examples use numeric arrays (`[1]`). Taxonomy has no numeric IDs. Client serializes `List<Long>` when provided; Create Store sends `[]` and does not invent IDs.

## 18. Fulfillment options

`GET .../fulfillment-options` → `manual`, `redirect`, plus `Unknown(raw)`. Server-driven; no plan-slug hard-coding.

## 19. API-key regeneration

`POST .../regenerate-api-key` → ephemeral `ShopApiKey`. Deferred UI: `ShopApiKeyViewModel`.

## 20. API-key security rules

Never log, persist, navigate with, or put in analytics/crash metadata. `LoggingSanitizer` redacts `api_key` and `access_token`. No automatic retry.

## 21. In-memory seller-state behavior

`SellerShopStateStore` holds summaries/details. Separate from public shop cache.

## 22. User-session clearing

Implements `SessionInvalidationListener` — cleared on logout / terminal invalidation.

## 23. UI integration

| Screen | Status |
|--------|--------|
| CreateStoreScreen | Wired to `CreateShopViewModel` |
| Seller list / edit / details / API key | ViewModels ready; Compose routes deferred |

Unsupported Create Store fields (logo, theme, policies, province, category chips as IDs) remain visual/deferred.

## 24. Tests

`feature/seller` commonTest: API/repository, publication state, Create/Update use cases, ViewModels, logout clear. Network redaction regression in `core/network`.

## 25. API gaps

See [api-gaps.md](api-gaps.md) Phase 7 section.

## 26. Future Phase 8/9/10

Reuse `ShopId`, seller ownership, session/role orchestration, pagination, Koin. Do **not** reuse SellerShop request DTOs or PublicProduct/PublicShop DTOs for seller products. Subscription/analytics/boosts remain later phases.
