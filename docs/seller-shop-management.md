# Seller Shop Management

Phase 7 seller shop ownership, onboarding, and management. Implementation lives in `:feature:seller`.

## 1. Seller Shop ownership

Seller endpoints are scoped to shops owned by the authenticated user. Server authorization is authoritative. Client list membership is not security.

## 2. First-shop onboarding

An authenticated **user** may enter Create Store (`/admin/stores/new`). A dedicated seller role is **not** required (and no longer exists). `POST /seller/shops` requires a `user` (or higher) token; the caller must own the shop for later mutations. `GET /auth/me` `shop_types` is empty for viewers and non-empty when the user owns retail/wholesale shops.

## 3. First-shop access-token replacement

Create response may include `data.tokens.access_token` + `expires_at` (no refresh token). `CreateShopUseCase` calls `SessionRepository.updateAccessToken` and preserves the existing refresh token.

## 4. Why refresh token is preserved

This is a partial JWT update for the same session (historically a role upgrade), not a new login. Replacing refresh with null would break rotation and force re-auth.

## 5. Current-user role refresh

After optional token update, `CreateShopUseCase` best-effort calls `AccountRepository.refreshCurrentUser()` (`GET /auth/me`). Roles and `shop_types` are never decoded from JWT claims.

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

`CreateShopUseCase` → `SellerShopRepository.createShop`. Request fields per Postman, including optional SEO, rules, shipping methods, working hours, `fulfillment_type`, `webhook_url`, and socials (`whatsapp`, `bale`, `rubika`, `telegram`, `instagram`, `tiktok`, `website`). Null optionals are omitted (`explicitNulls=false` + `@EncodeDefault(NEVER)`). `category_slugs` is `List<String>` of taxonomy slugs. Create Store collects those profile fields on the policies step, plus bale, rubika, and tiktok on the contact step. The existing policies and shipping text is sent as a rule and a default shipping method when the structured lists are empty.

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

Public taxonomy exposes string `CategorySlug` values such as `aa-1-2`. Seller create/update send those strings.

## 17. `category_slugs` compatibility status

**Resolved.** Postman examples use string arrays (`["aa-1-2"]`). The client serializes `List<String>`. `FlexibleCategorySlugListSerializer` still accepts a legacy number.

## 18. Fulfillment options

`GET .../fulfillment-options` → `manual`, `redirect`, plus `Unknown(raw)`. Create/update `fulfillment_type` also documents `api`, `marketplace`, and `local`. `webhook_url` is required when the type is `api`. Server-driven; no plan-slug hard-coding.

## 18b. Shop collections

Owner CRUD: `POST/GET/PATCH/DELETE /seller/shops/{id}/collections`, edited on the store edit screen. Public reads: `GET /shops/{id}/collections` and `GET /shops/{id}/collections/{collectionId}`. Title is 1–150 characters, description up to 5000, at most 500 `product_ids`. Seller list/detail have no saved response examples; the client decodes the public shape. `StoreScreen` shows those rows (title and product count). Create and edit shop forms send SEO, rules, shipping methods, working hours, fulfillment, webhook, and bale/rubika/tiktok. Edit omits profile fields the user did not change.

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

Unsupported Create Store fields (logo file upload, theme, policies, shipping, hours, province) remain visual/deferred. Category chips send taxonomy slugs when the form has a selection.

## 24. Tests

`feature/seller` commonTest: API/repository, publication state, Create/Update use cases, ViewModels, logout clear. Network redaction regression in `core/network`.

## 25. API gaps

See [api-gaps.md](api-gaps.md) Phase 7 section.

## 26. Future Phase 8/9/10

Reuse `ShopId`, seller ownership, session/role orchestration, pagination, Koin. Do **not** reuse SellerShop request DTOs or PublicProduct/PublicShop DTOs for seller products. Subscription/analytics/boosts remain later phases.
