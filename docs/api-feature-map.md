# API Feature Map

Maps Postman endpoint groups to client feature ownership, future data-layer services, repositories, and auth requirements.

**Source:** [`postman/vitran-api.postman_collection.json`](postman/vitran-api.postman_collection.json)

> **Note:** Auth column reflects Postman folder/request descriptions. Protected endpoints use Bearer `accessToken` in practice even when the export omits explicit Authorization headers on every row.

## Domain summary

| Client domain | Postman folders | Future API service | Future repository | Primary auth |
|---------------|-----------------|--------------------|--------------------|--------------|
| Platform | Health | `HealthApi` | — | Public |
| Auth | Auth | `AuthApi` ✅ | `AuthRepository` ✅ | Public (issues tokens) |
| Session | Auth refresh, seller shop create | `KtorTokenRefreshRemoteDataSource` ✅ | `SessionRepository` ✅ | Required when mutating session |
| Account | Users | `AccountApi` ✅ | `AccountRepository` ✅ | Required |
| Admin Users | Admin — Users | `AdminUserApi` ✅ | `AdminUserRepository` ✅ | Required (admin) |
| Location / Cities | Cities | `LocationApi` ✅ | `LocationRepository` ✅ | Public + admin |
| Taxonomy | Taxonomy | `TaxonomyApi` ✅ (public read) | `TaxonomyRepository` ✅ | Public + admin |
| Home | Home | `HomeApi` ✅ | `HomeRepository` ✅ | Optional |
| Marketplace Shops | Shops — Public | `PublicShopApi` ✅ | `ShopRepository` ✅ | Public |
| Marketplace Products | Products — Public | `PublicProductApi` ✅ | `ProductRepository` ✅ | Public |
| Catalog / Search | catalog/search, products/search | `PublicProductApi` ✅ (simple search) | `ProductRepository` ✅ | Public / catalog **deferred** |
| Engagement | Favorites, Shop follows, Wishlists | `EngagementApi` ✅ | `FollowRepository` ✅ (mutations), `ShopFavoriteRepository` ✅, `WishlistRepository` ✅ | Required / public wishlist None |
| Product Reviews | Products — Public (reviews) | `ProductReviewApi` ✅ | `ProductReviewRepository` ✅ | None / Required |
| Shop Comments | Comments | `ShopCommentApi` ✅ | `ShopCommentRepository` ✅ | None / Required |
| Seller Shops | Shops — Seller | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required (auth; seller role not required for first create) |
| Seller Products | Products — Seller | `SellerProductApi` ✅ | `SellerProductRepository` ✅ | Required (seller) |
| Seller Analytics | Shops — Seller (analytics) | `SellerAnalyticsApi` ✅ | `SellerAnalyticsRepository` ✅ | Required (seller) |
| Seller Boosts | Boosts — Seller | `SellerBoostApi` ✅ | `SellerBoostRepository` ✅ | Required (seller) |
| Seller Subscription | Shops — Seller (subscription) | `SellerSubscriptionApi` | `SubscriptionRepository` | Required (seller) |
| Referral | Referrals | `ReferralApi` | `ReferralRepository` | Mixed |
| Plans | Plans — Public | `PlanApi` | `PlanRepository` | Public |
| Admin Plans | Plans — Admin | `AdminPlanApi` ✅ | `AdminPlanRepository` ✅ | Required (admin) |
| Payments | Payments — Public | `PaymentApi` | `PaymentRepository` | Public callback |
| CMS | Static Pages — Public | `ContentApi` ✅ | `ContentRepository` ✅ | Public |
| Admin CMS | Static Pages — Admin | `AdminContentApi` ✅ | `AdminContentRepository` ✅ | Required (admin) |
| Admin Moderation | Shops/Products/Comments — Admin | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required (admin); comment discovery blocked |

## Future API service boundaries

Avoid a single `VitranApi` with 100+ methods. Incremental services:

`AuthApi`, `AccountApi`, `ReferralApi` ✅, `LocationApi`, `TaxonomyApi`, `HomeApi`, `PublicShopApi`, `PublicProductApi`, `EngagementApi` ✅, `ProductReviewApi` ✅, `ShopCommentApi` ✅, `ProductContactApi` ✅, `UserEventApi` ✅, `ShopAnalyticsApi` ✅, `SellerShopApi` ✅, `SellerProductApi` ✅, `SellerAnalyticsApi` ✅, `SellerBoostApi` ✅, `PlanApi` ✅, `SellerSubscriptionApi` ✅, `ContentApi` ✅, `AdminUserApi` ✅, `AdminLocationApi` ✅, `AdminTaxonomyApi` ✅, `AdminModerationApi` ✅, `AdminPlanApi` ✅, `AdminContentApi` ✅

Payment callback `GET /api/v1/payments/callback` — **backend/provider endpoint; not a normal client operation**.

## Full endpoint index

| Method | Path | Postman folder | Client domain | Future API | Future repository | Auth | Example |
|--------|------|----------------|---------------|------------|-------------------|------|---------|
| GET | `/api/v1/admin/users/2` | Admin — Users | Admin Users | `AdminUserApi` ✅ | `AdminUserRepository` ✅ | Required | Implemented |
| PATCH | `/api/v1/admin/users/2` | Admin — Users | Admin Users | `AdminUserApi` ✅ | `AdminUserRepository` ✅ | Required | Implemented |
| GET | `/api/v1/admin/users?per_page=20&role=customer&phone=0912&is_active=true` | Admin — Users | Admin Users | `AdminUserApi` ✅ | `AdminUserRepository` ✅ | Required | Implemented (page mode) |
| POST | `/api/v1/auth/forgot-password` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/login` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/logout` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/refresh` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/register` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/register` | Auth | Auth | AuthApi | SessionRepository | Public | **Missing** |
| POST | `/api/v1/auth/resend-otp` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/reset-password` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/auth/verify` | Auth | Auth | AuthApi | SessionRepository | Public | Yes |
| POST | `/api/v1/seller/shops/1/boosts` | Boosts — Seller | Seller Boosts | `SellerBoostApi` ✅ | `SellerBoostRepository` ✅ | Required | Yes (Phase 10 — transport; purchase blocked) |
| GET | `/api/v1/seller/shops/1/boosts` | Boosts — Seller | Seller Boosts | `SellerBoostApi` ✅ | `SellerBoostRepository` ✅ | Required | Yes (Phase 10 — empty wrapper; items unmapped) |
| POST | `/api/v1/admin/cities` | Cities | Admin Cities | `AdminLocationApi` ✅ | `AdminLocationRepository` ✅ | Required | Implemented — slug+name |
| PATCH | `/api/v1/admin/cities/1` | Cities | Admin Cities | `AdminLocationApi` ✅ | `AdminLocationRepository` ✅ | Required | Implemented — slug+name |
| DELETE | `/api/v1/admin/cities/3` | Cities | Admin Cities | `AdminLocationApi` ✅ | `AdminLocationRepository` ✅ | Required | Implemented — 409 endpoint-context workaround |
| GET | `/api/v1/cities` | Cities | Location / Cities | `LocationApi` ✅ | `LocationRepository` ✅ | Public | Yes |
| GET | `/api/v1/cities/1` | Cities | Location / Cities | `LocationApi` ✅ | `LocationRepository` ✅ | Public | Yes |
| GET | `/api/v1/cities/slug/tehran` | Cities | Location / Cities | `LocationApi` ✅ | `LocationRepository` ✅ | Public | Yes |
| PATCH | `/api/v1/admin/comments/1/confirm` | Comments | Admin Moderation | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required | Implemented mutation only; list **Blocked** |
| POST | `/api/v1/shops/1/comments` | Comments | Shop Comments | `ShopCommentApi` ✅ | `ShopCommentRepository` ✅ | Required | Yes |
| GET | `/api/v1/shops/1/comments?per_page=20` | Comments | Shop Comments | `ShopCommentApi` ✅ | `ShopCommentRepository` ✅ | Public | Yes |
| POST | `/api/v1/me/favorites/products/1` | Favorites — Me | Engagement | `EngagementApi` ✅ | `WishlistRepository` ✅ | Required | Yes |
| DELETE | `/api/v1/me/favorites/products/1` | Favorites — Me | Engagement | `EngagementApi` ✅ | `WishlistRepository` ✅ | Required | Yes |
| GET | `/api/v1/me/favorites/products?per_page=20` | Favorites — Me | Engagement | `EngagementApi` ✅ | `WishlistRepository` ✅ | Required | Yes |
| POST | `/api/v1/me/favorites/shops/1` | Favorites — Me | Engagement | `EngagementApi` ✅ | `ShopFavoriteRepository` ✅ | Required | Yes |
| DELETE | `/api/v1/me/favorites/shops/1` | Favorites — Me | Engagement | `EngagementApi` ✅ | `ShopFavoriteRepository` ✅ | Required | Yes |
| GET | `/api/v1/me/favorites/shops?per_page=20` | Favorites — Me | Engagement | `EngagementApi` ✅ | `ShopFavoriteRepository` ✅ | Required | Yes |
| GET | `/api/v1/me/wishlist/share` | Favorites — Me | Engagement | `EngagementApi` ✅ | `WishlistRepository` ✅ | Required | Yes |
| PUT | `/api/v1/me/wishlist/share` | Favorites — Me | Engagement | `EngagementApi` ✅ | `WishlistRepository` ✅ | Required | Yes |
| GET | `/api/v1/health` | Health | Platform | HealthApi | — | Public | Yes |
| GET | `/api/v2/health` | Health | Platform | HealthApi | — | Public | Yes |
| GET | `/health` | Health | Platform | HealthApi | — | Public | Yes |
| POST | `/api/v1/events` | Home | Engagement | `UserEventApi` ✅ | `MarketplaceAnalyticsTracker` ✅ | Optional | Yes |
| GET | `/api/v1/home?city_id=1` | Home | Home | HomeApi | HomeRepository | Optional | Yes |
| GET | `/api/v1/me/home/feed?city_id=1&latest_products=12&following_products=20&following_shops=8&favorite_products=12&latest_shops=10&favorite_shops=10` | Home | Home | HomeApi | HomeRepository | Public | **Missing** |
| GET | `/api/v1/payments/callback?Authority=mock-99000&Status=OK` | Payments — Public | Payments | — | — | Provider | Backend/provider only — not client |
| GET | `/api/v1/admin/plans` | Plans — Admin | Admin Plans | `AdminPlanApi` ✅ | `AdminPlanRepository` ✅ | Required | Implemented |
| POST | `/api/v1/admin/plans` | Plans — Admin | Admin Plans | `AdminPlanApi` ✅ | `AdminPlanRepository` ✅ | Required | Implemented |
| PATCH | `/api/v1/admin/plans/2` | Plans — Admin | Admin Plans | `AdminPlanApi` ✅ | `AdminPlanRepository` ✅ | Required | Implemented; features semantics unresolved |
| DELETE | `/api/v1/admin/plans/5` | Plans — Admin | Admin Plans | `AdminPlanApi` ✅ | `AdminPlanRepository` ✅ | Required | Implemented; Free conflict workaround |
| GET | `/api/v1/plans` | Plans — Public | Plans | PlanApi | PlanRepository | Public | Yes (Phase 9) |
| GET | `/api/v1/plans/2` | Plans — Public | Plans | PlanApi | PlanRepository | Public | Yes (Phase 9) |
| GET | `/api/v1/admin/products/1` | Products — Admin | Admin Moderation | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required | Implemented |
| PATCH | `/api/v1/admin/products/1/confirm` | Products — Admin | Admin Moderation | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required | Implemented |
| GET | `/api/v1/admin/products?per_page=20&active=false&shop_id=1&category_slug=aa-1-2-3-4&user_id=2` | Products — Admin | Admin Moderation | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required | Implemented |
| GET | `/api/v1/catalog/search?category_slug=aa-1-2-3-4&min_price=100000&max_price=500000&min_rating=4&attributes[color]=color__red,color__blue&sort=price_asc&per_page=20` | Products — Public | Marketplace Products | PublicProductApi | ProductRepository | Public | **Missing** |
| GET | `/api/v1/products/1` | Products — Public | Marketplace Products | PublicProductApi | ProductRepository | Public | Yes |
| POST | `/api/v1/products/1/contact?session_id=visitor-abc-123` | Products — Public | Engagement | `ProductContactApi` ✅ | `ProductContactRepository` ✅ | Optional | Yes |
| POST | `/api/v1/products/1/reviews` | Products — Public | Engagement | `ProductReviewApi` ✅ | `ProductReviewRepository` ✅ | Required | Yes |
| GET | `/api/v1/products/1/reviews?per_page=20` | Products — Public | Engagement | `ProductReviewApi` ✅ | `ProductReviewRepository` ✅ | Public | Yes |
| GET | `/api/v1/products/search?q=widget&city_slug=tehran&category_slug=aa-1-2-3-4&per_page=20` | Products — Public | Marketplace Products | PublicProductApi | ProductRepository | Public | Yes |
| GET | `/api/v1/products?per_page=20&shop_slug=my-shop&category_slug=aa-1-2-3-4` | Products — Public | Marketplace Products | PublicProductApi | ProductRepository | Public | Yes |
| PATCH | `/api/v1/seller/products/1` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public (multipart) | Yes |
| DELETE | `/api/v1/seller/products/1` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public | Yes |
| GET | `/api/v1/seller/products/1` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public | Yes |
| PATCH | `/api/v1/seller/products/1/active` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public | Yes |
| DELETE | `/api/v1/seller/products/1/images/1` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public | Yes |
| GET | `/api/v1/seller/products?per_page=20&active=false&shop_id=1&category_slug=aa-1-2-3-4` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public | Yes |
| POST | `/api/v1/seller/shops/1/products` | Products — Seller | Seller Products | SellerProductApi | SellerProductRepository | Public (multipart) | Yes |
| GET | `/api/v1/me/referral` | Referrals | Referral | ReferralApi | ReferralRepository | Required | Yes (Phase 9) |
| POST | `/api/v1/me/referrals/credits/{{referralCreditId}}/apply` | Referrals | Referral | ReferralApi | ReferralRepository | Required | Yes (Phase 9) |
| GET | `/api/v1/referrals/{{referralCode}}` | Referrals | Referral | ReferralApi | ReferralRepository | Public | Yes (Phase 9) |
| POST | `/api/v1/me/follows/shops/1` | Shop follows — Me | Engagement | `EngagementApi` ✅ | `FollowRepository` ✅ | Required | **Missing** (empty body) |
| GET | `/api/v1/me/follows/shops/1` | Shop follows — Me | Engagement | — | — | Required | **Missing — Partially implemented due missing schema** |
| DELETE | `/api/v1/me/follows/shops/1` | Shop follows — Me | Engagement | `EngagementApi` ✅ | `FollowRepository` ✅ | Required | **Missing** (empty body) |
| GET | `/api/v1/me/follows/shops?per_page=20` | Shop follows — Me | Engagement | — | — | Required | **Missing — Partially implemented due missing schema** |
| PATCH | `/api/v1/admin/shops/1/confirm` | Shops — Admin | Admin Moderation | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required | Implemented |
| GET | `/api/v1/admin/shops?per_page=20&active=false&city_id=1&category_slug=aa-1-2-3-4&user_id=2` | Shops — Admin | Admin Moderation | `AdminModerationApi` ✅ | `AdminModerationRepository` ✅ | Required | Implemented |
| GET | `/api/v1/shops/1` | Shops — Public | Marketplace Shops | PublicShopApi | ShopRepository | Public | Yes |
| POST | `/api/v1/shops/1/analytics/events` | Shops — Public | Engagement | `ShopAnalyticsApi` ✅ | `MarketplaceAnalyticsTracker` ✅ | Public | Yes |
| GET | `/api/v1/shops/browse?per_page=20&city_slug=tehran&category_slug=aa-1-2-3-4` | Shops — Public | Marketplace Shops | PublicShopApi | ShopRepository | Public | Yes |
| GET | `/api/v1/shops/slug/my-shop` | Shops — Public | Marketplace Shops | PublicShopApi | ShopRepository | Public | Yes |
| GET | `/api/v1/shops?per_page=20&city_slug=tehran&category_slug=aa-1-2-3-4` | Shops — Public | Marketplace Shops | PublicShopApi | ShopRepository | Public | Yes |
| POST | `/api/v1/seller/shops` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes |
| PATCH | `/api/v1/seller/shops/1` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes |
| GET | `/api/v1/seller/shops/1` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes (minimal example) |
| GET | `/api/v1/seller/shops/1/analytics/export?period=30d` | Shops — Seller | Seller Analytics | `SellerAnalyticsApi` ✅ | `SellerAnalyticsRepository` ✅ | Required | Yes (Phase 10 — raw CSV; columns Open) |
| GET | `/api/v1/seller/shops/1/analytics?period=7d` | Shops — Seller | Seller Analytics | `SellerAnalyticsApi` | — | Required | **Unresolved contract** (Phase 10 — not implemented) |
| GET | `/api/v1/seller/shops/1/fulfillment-options` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes |
| POST | `/api/v1/seller/shops/1/regenerate-api-key` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes |
| GET | `/api/v1/seller/shops/1/subscription` | Shops — Seller | Seller Subscription | SellerSubscriptionApi | SubscriptionRepository | Required | Yes (Phase 9) |
| POST | `/api/v1/seller/shops/1/subscription/purchase` | Shops — Seller | Seller Subscription | SellerSubscriptionApi | SubscriptionRepository | Required | Yes (Phase 9) |
| GET | `/api/v1/seller/shops/check-slug?slug=my-shop&exclude_id=1` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes |
| GET | `/api/v1/seller/shops?per_page=20` | Shops — Seller | Seller Shops | `SellerShopApi` ✅ | `SellerShopRepository` ✅ | Required | Yes |
| GET | `/api/v1/admin/static-pages` | Static Pages — Admin | Admin CMS | `AdminContentApi` ✅ | `AdminContentRepository` ✅ | Required | Implemented |
| POST | `/api/v1/admin/static-pages` | Static Pages — Admin | Admin CMS | `AdminContentApi` ✅ | `AdminContentRepository` ✅ | Required | Implemented |
| GET | `/api/v1/admin/static-pages/1` | Static Pages — Admin | Admin CMS | `AdminContentApi` ✅ | `AdminContentRepository` ✅ | Required | Implemented |
| PATCH | `/api/v1/admin/static-pages/1` | Static Pages — Admin | Admin CMS | `AdminContentApi` ✅ | `AdminContentRepository` ✅ | Required | Implemented |
| DELETE | `/api/v1/admin/static-pages/5` | Static Pages — Admin | Admin CMS | `AdminContentApi` ✅ | `AdminContentRepository` ✅ | Required | Implemented |
| GET | `/api/v1/static-pages` | Static Pages — Public | CMS | `ContentApi` ✅ | `ContentRepository` ✅ | Public | Implemented |
| GET | `/api/v1/static-pages/slug/about-us` | Static Pages — Public | CMS | `ContentApi` ✅ | `ContentRepository` ✅ | Public | Implemented |
| PATCH | `/api/v1/admin/attributes/color/name` | Taxonomy | Admin Taxonomy | `AdminTaxonomyApi` ✅ | `AdminTaxonomyRepository` ✅ | Required | Implemented; response schema unresolved |
| PATCH | `/api/v1/admin/categories/aa-1-2-3-4/name` | Taxonomy | Admin Taxonomy | `AdminTaxonomyApi` ✅ | `AdminTaxonomyRepository` ✅ | Required | Implemented; response schema unresolved |
| PUT | `/api/v1/admin/categories/ap/icon` | Taxonomy | Admin Taxonomy | `AdminTaxonomyApi` ✅ | `AdminTaxonomyRepository` ✅ | Required (multipart) | Implemented — `image` |
| POST | `/api/v1/admin/taxonomy/import` | Taxonomy | Admin Taxonomy | `AdminTaxonomyApi` ✅ | `AdminTaxonomyRepository` ✅ | Required (multipart) | Implemented — `categories` + `attributes`; response unresolved |
| PATCH | `/api/v1/admin/values/color__blue/name` | Taxonomy | Admin Taxonomy | `AdminTaxonomyApi` ✅ | `AdminTaxonomyRepository` ✅ | Required | Implemented; response schema unresolved |
| GET | `/api/v1/categories` | Taxonomy | Taxonomy | `TaxonomyApi` ✅ | `TaxonomyRepository` ✅ | Public | Yes |
| GET | `/api/v1/categories/aa-1-2-3-4` | Taxonomy | Taxonomy | `TaxonomyApi` ✅ | `TaxonomyRepository` ✅ | Public | Yes |
| GET | `/api/v1/categories/aa-1-2-3-4/attributes` | Taxonomy | Taxonomy | — | — | Public | Yes (empty only) **Deferred** |
| GET | `/api/v1/categories/aa-1-2-3-4/return-reasons` | Taxonomy | Taxonomy | — | — | Public | **Missing** **Deferred** |
| GET | `/api/v1/categories/slug/aa-1-2-3-4` | Taxonomy | Taxonomy | — (alias) | — | Public | Yes |
| GET | `/api/v1/auth/me` | Users | Account | AccountApi | AccountRepository | Public | Yes |
| PUT | `/api/v1/auth/profile` | Users | Account | AccountApi | AccountRepository | Public | Yes |
| GET | `/api/v1/wishlists/share/wl-a1b2c3d4e5f67890?per_page=20` | Wishlists — Public | Engagement | `EngagementApi` ✅ | `WishlistRepository` ✅ | None | Yes |
