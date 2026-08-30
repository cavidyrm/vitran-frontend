# Marketplace Engagement (Phase 6)

Phase 6 adds `:feature:engagement` for verified customer marketplace APIs. Existing Compose surfaces are wired; screens that do not exist stay API-only.

## 1. Scope

| In scope | Out of scope |
|----------|----------------|
| Follow POST/DELETE + `FollowStatus` | Follow list / GET-by-id (unverified schemas) |
| Favorite shops list + toggle | Favorite-shops screen |
| Wishlist list/toggle/share/public list | Saved tab UI, public-wishlist screen |
| Product reviews list + submit | Write-review UI; invented author/date |
| Shop comments list + submit | Shop comments UI; admin confirm |
| Product contact + `PurchaseIntent` | Contact CTA on PDP |
| User + shop analytics (best-effort) | Offline queues, promotion emit |
| Optimistic save/follow on existing buttons | Cart / Order / payments |

## 2. Gradle module

| Module | Package root | Depends on |
|--------|--------------|------------|
| `:feature:engagement` | `com.vitran.shop.feature.engagement` | `:core:domain`, `:core:network`, `:core:session`, `:core:platform`, `:feature:marketplace` (one-way) |

Registered in `settings.gradle.kts`, `shared/build.gradle.kts`, and `VitranKoin.kt` as `engagementModule`.

Marketplace ViewModels stay engagement-free. No Gradle cycle. `ShopId` / `ProductId` were not extracted to `:core:domain`.

## 3. Internal packages

`follow`, `favorite`, `wishlist`, `review`, `comment`, `contact`, `analytics`, `session`, `state`, `presentation`, `di`.

No `EngagementRepository`. Capability repositories only.

## 4. API boundaries

| API | Endpoints | Auth |
|-----|-----------|------|
| `EngagementApi` | `/me/follows/shops*`, `/me/favorites/shops*`, `/me/favorites/products*`, `/me/wishlist/share`, `/wishlists/share/{slug}` | Required except public wishlist = `None` |
| `ProductReviewApi` | `GET/POST /products/{id}/reviews` | None / Required |
| `ShopCommentApi` | `GET/POST /shops/{id}/comments` | None / Required |
| `ProductContactApi` | `POST /products/{id}/contact?session_id=` | Optional |
| `UserEventApi` | `POST /events` (`event_type`) | Optional |
| `ShopAnalyticsApi` | `POST /shops/{id}/analytics/events` (`event`) | None |

## 5. Follow contract

| Endpoint | Status |
|----------|--------|
| `POST /me/follows/shops/{id}` | Implemented. `AuthMode.Required`. `executeEmpty()`. Idempotent. |
| `DELETE /me/follows/shops/{id}` | Implemented. Required. `executeEmpty()`. |
| `GET` list / `GET` by id | **UNRESOLVED — NOT INVENTED.** No `FollowedShop` DTO. `FollowRepository` exposes only `setFollowed`. |

`FollowStatus` = `Unknown | NotFollowed | Followed`. Unknown is not false. `FollowingScreen` keeps mock data.

Follow vs favorite stay separate maps (Gap 3 remains Open).

## 6. Favorite shops

- Domain: `FavoriteShop(favoritedAt, shop: FavoriteShopSummary(id, slug, title))` from `data.favorite_shops`
- `ShopFavoriteRepository` — list + `setFavorite` (no status GET)
- `FavoriteShopStatus` = `Unknown | NotFavorited | Favorited`
- 409 already-favorited stays `AppError.Conflict` (no verified `FieldError.reason`)
- `FavoriteShopsViewModel` exists unconnected

## 7. Wishlist

- Domain: `WishlistItem(savedAt, product: WishlistProductSummary(id, title, priceAmount))` from `data.favorite_products`
- Share: `WishlistShareSettings(shareSlug: WishlistShareSlug, isPublic)` — backend owns slug
- Public: `data.wishlist` items use `saved_at` + `{id, title}` only
- `SaveStatus` = `Unknown | NotSaved | Saved`. Unknown ≠ empty-heart-as-false
- `PublicWishlistResult` = `Content | Private | NotFound | Failure`. 403 → `Private` (not session forbidden). 404 → `NotFound`
- `WishlistViewModel` / `PublicWishlistViewModel` exist unconnected. `SavedScreen` stays `PlaceholderScreen`
- Frontend share URL `https://vitran.ir/wishlist/{slug}` is **not invented**

## 8. Reviews

- List: `ProductReview(id, productId, authorUserId, rating, comment)` — no username/date/avatar
- Submit: `SubmittedProductReview` (no `user_id`); refresh first page after submit
- `Rating` value type `1..5`; reject 0/6 in `SubmitProductReviewUseCase`
- `CreateReviewRequestDto.intentId` is nullable; `explicitNulls = false` omits `intent_id` (never `0`)
- PDP shows first page of verified fields; histogram/author/date/helpful hidden (`showSummaryMetrics` / `showAuthorMeta` = false)

## 9. Comments

- Public: `PublicShopComment(id, title, confirmed)` only
- Submit: `SubmittedShopComment` including `confirmed = false`; **never** appended to the public list
- `ShopCommentsViewModel` exists unconnected. Store mock product-review sheet is not opened for API shops

## 10. Product contact

- `ContactProductResult(route, intent: PurchaseIntent)`
- `ContactRoute.WhatsApp(url) | Unsupported(rawType)`
- `PurchaseIntent(id, productId, shopId, route)` — not an Order
- No Redirect/Webhook payload fields
- Repository does not open URLs. `ProductContactViewModel` emits `ProductContactEffect.OpenExternalUrl`
- No WhatsApp button invented on PDP

## 11. Visitor session

`VisitorSessionProvider` / `DefaultVisitorSessionProvider`:

- Stable process-lifetime `session_id` via `kotlin.uuid.Uuid.random()`
- Not persisted, not SecureStorage, not rotated on login
- Never placed in UiState, routes, or visible text

## 12. EngagementStateStore

Application-scoped maps:

- `followStateByShopId`
- `favoriteShopStateByShopId`
- `wishlistStateByProductId`
- in-memory `shareSettings`

Implements `SessionInvalidationListener`. Registered on the Koin `mutableListOf<SessionInvalidationListener>()` (same pattern as account). Clears only those maps + wishlist share-settings cache on logout / invalidation / account replacement.

Does **not** clear public reviews, comments, or product/shop details. `:core:session` does not depend on engagement.

## 13. Optimistic toggles

`SetShopFollowedUseCase`, `SetShopFavoriteUseCase`, `SetProductSavedUseCase` write store first, rollback on `AppResult.Failure`.

Presentation: per-entity pending flags set **before** the coroutine; ignore duplicate taps while in flight. Anonymous tap inspects `SessionState` and emits `ProductEngagementEffect.RequestLogin` — does not wait for 401. No auto-resume-after-login.

## 14. Analytics

| System | DTO | Field | Auth |
|--------|-----|-------|------|
| User personalization | `UserEventRequestDto` | `event_type` | Optional |
| Shop | `ShopAnalyticsEventRequestDto` | `event` | None |

Typed events only (`UserPersonalizationEvent`, `ShopAnalyticsEvent`). Context fields from verified examples: `session_id`, `product_id`, `shop_id`, `category_slug`, `city_id`. No invented `query` / `search_term`.

`category_slug: 1` stays Gap 1: domain `CategorySlug` is String; analytics DTO uses `String?`.

`DefaultMarketplaceAnalyticsTracker` is application-scoped with named `engagementAnalyticsScope` (`SupervisorJob`). Failures log in debug, never fail save/follow/contact, no snackbar, no offline queue, no mutation retry.

Emit once from ViewModel load-success / explicit action (`onProductDisplayed`, follow success, wishlist add success). Not unfollow/remove. Promotion / `search` / `click_category` are typed but **not emitted**.

Contact already records shop analytics; `ContactProductUseCase` also sends best-effort `UserPersonalizationEvent.PurchaseIntent` for personalization only.

## 15. Use cases

| Use case | Policy |
|----------|--------|
| `SetShopFollowedUseCase` | Optimistic + `follow_shop` on follow success |
| `SetShopFavoriteUseCase` | Optimistic; no analytics (no verified favorite event) |
| `SetProductSavedUseCase` | Optimistic + `wishlist` on save success |
| `UpdateWishlistSharingUseCase` | Updates store share settings |
| `SubmitProductReviewUseCase` | Rating 1..5 + non-blank comment |
| `SubmitShopCommentUseCase` | Non-blank title/description |
| `ContactProductUseCase` | Contact then personalization event |

Simple list reads stay on repositories.

## 16. ViewModels

Sibling VMs in `:feature:engagement` (marketplace VMs stay lean):

| ViewModel | Wired UI |
|-----------|----------|
| `ProductEngagementViewModel` | PDP save/follow |
| `ShopEngagementViewModel` | Store follow/share |
| `CatalogEngagementViewModel` | Categories + store product hearts |
| `ProductReviewsViewModel` | PDP reviews section |
| `ProductContactViewModel` | Unconnected |
| `WishlistViewModel` | Unconnected |
| `FavoriteShopsViewModel` | Unconnected |
| `PublicWishlistViewModel` | Unconnected |
| `ShopCommentsViewModel` | Unconnected |

Factories: `EngagementViewModelFactories.kt`. Shared helpers: `EngagementViewModelHelpers.kt`.

## 17. Platform contracts

ADR 0002 interfaces in `:core:platform`:

- `ShareManager` — PDP title; store uses `ShopDetails.shareUrl` when present
- `ExternalUrlLauncher` — for future WhatsApp open from `ProductContactViewModel`

Implementations: JS (`navigator.share` + prompt fallback), Wasm (`window.open` when a URL exists — text-only share returns false; no Web Share / `prompt` on Wasm), JVM / iOS (`:core:platform`), Android (`AndroidShareManager` / `AndroidExternalUrlLauncher` via `androidPlatformModule`).

## 18. Presentation wiring

| Surface | Behavior |
|---------|----------|
| PDP save + follow | Optimistic; Unknown ≠ unsaved claim; login redirect |
| Categories / store product hearts | `CatalogEngagementViewModel.onSaveClick` |
| PDP reviews | API first page, verified fields only |
| Store follow pill | `ShopEngagementViewModel` |
| Store reviews sheet | Not opened (no fake product reviews) |
| Following list | Unchanged mock |
| Saved tab | `PlaceholderScreen` |
| Account settings “public lists” | Not hijacked as wishlist share |

`@Preview` fixtures kept. Runtime fake reviews removed from API-backed PDP.

## 19. Auth / anonymous

Saved tab already routes signed-out users to Login. Follow/save on PDP, Store, and Categories emit `RequestLogin` → `Route.Login`. Contact uses `AuthMode.Optional` so anonymous `session_id` still works.

## 20. Tests

`feature/engagement/src/commonTest` (MockEngine, `maxRetryCount = 0`):

- Follow POST/DELETE + idempotent POST; optimistic rollback
- Favorite list/add/remove; 409 stays Conflict
- Wishlist list/save rollback; share GET/PUT; public 403 → Private
- Logout clears `EngagementStateStore` + share cache; reviews remain fetchable
- Rating 1/5 vs 0/6; review omits null `intent_id`
- Comment `confirmed=false` not appended
- Contact anonymous vs authenticated; stable `session_id`; WhatsApp + Unsupported; duplicate tap
- Visitor uniqueness; shop `event` vs user `event_type`; optional vs none auth
- Analytics failure does not fail wishlist
- ViewModel: anonymous save → login; save rollback; review validation

No invented follow list/status tests.

## 21. DI registration

```kotlin
// VitranKoin.kt
modules(..., marketplaceModule, homeModule, engagementModule, ...)
```

`EngagementStateStore` and `DefaultWishlistRepository` register on `mutableListOf<SessionInvalidationListener>()`.

## 22. Known gaps

See [api-gaps.md](api-gaps.md) Gaps 21–29. Follow list/status is **Partially implemented due missing schema**.

## 23. Out of scope

Admin comment confirm, seller APIs, plans, payments, referrals, boosts, Room, offline mutation/analytics queues, Cart/Order, invented Follow/Contact/review metadata, new Saved/contact/review-compose screens, Phase 7 seller shop management.

## 24. Related documents

- [ADR 0008](decisions/0008-engagement-state-and-analytics.md)
- [public-marketplace.md](public-marketplace.md)
- [api-feature-map.md](api-feature-map.md)
- [api-gaps.md](api-gaps.md)
- [screen-feature-map.md](screen-feature-map.md)
- [architecture.md](architecture.md)
- [dependency-rules.md](dependency-rules.md)
