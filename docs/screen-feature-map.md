# Screen Feature Map

Maps existing Compose screens to business features, future APIs, mock data, and ViewModels. **Do not regenerate these screens** — future phases wire real data progressively.

Package root: `com.vitran.shop.ui.screens`  
Navigation: Navigation 3 — [`Route`](../shared/src/commonMain/kotlin/com/vitran/shop/ui/navigation/Route.kt)

## Mock data classification

| Type | Rule |
|------|------|
| **Preview fixture** | `@Preview` parameters; keep permanently |
| **Runtime temporary** | `rememberMock*()`, `MockProductCatalog`, etc.; replace per feature |
| **Design-only sample** | Static copy/images for layout; replace with API or CMS |

---

## Screen inventory

| Existing Screen | Route | Business Feature | Expected API / Domain | Runtime fake data | Future ViewModel | Previews | Notes |
|-----------------|-------|------------------|----------------------|-------------------|------------------|----------|-------|
| HomeScreen | `/` | Home | `GET /home`, `POST /events` | Category rows, mosaics, shop feeds (`HomeCategory*.kt`, `HeroCollageModels.kt`) when API items unverified | `HomeViewModel` ✅ | Yes | Omnibox → `Route.Search`; API sections deferred |
| CategoriesScreen | `/categories` | Catalog / Marketplace | Taxonomy ✅, `GET /shops/browse`, `GET /products?category_slug=` | Visual fallbacks in `BrowseCategoryVisuals.kt`; mock when feeds empty | `CategoriesBrowseViewModel` ✅, `ShopBrowseViewModel` ✅, `ProductListViewModel` ✅ | Partial | Explore / browse |
| OffersScreen | `/offers` | Deals | TBD — no dedicated API in collection | `PlaceholderScreen` | `OffersViewModel` | — | Placeholder only |
| SavedScreen | `/saved` | Engagement / Wishlist | `GET /me/favorites/products` | `PlaceholderScreen` | `WishlistViewModel` ✅ (unconnected) | — | Placeholder only — API exists |
| AccountScreen | `/account` | Account | `GET /auth/me` | Hub extras still mock | `AccountRepository` ✅ (hub identity) | Yes | Wired to `CurrentUserState` |
| ProfileScreen | `/account/profile` | Account | `GET/PUT /auth/profile` | Preview fixtures only | `ProfileViewModel` ✅ | Yes | |
| ReferralsScreen | `/account/referrals` | Referral | `GET /me/referral`, credits apply | Preview fixtures | `ReferralsViewModel` ✅ | Yes | Real API; share via ShareManager; no toman fake credits |
| FollowingScreen | `/account/following` | Engagement (follows) | `GET /me/follows/shops` | Inline mock in section | — | Yes | Schema unresolved — mock kept; follow **mutations** wired on PDP/store |
| AccountSettingsScreen | `/account/settings` | Account | Profile preferences (partial `/auth/profile`) | Local `remember` state | `AccountSettingsViewModel` | Yes | |
| AccountUsersScreen | `/account/users` | Admin Users | `GET /admin/users` | Preview fixtures only | `AdminUsersViewModel` ✅ | Yes | Real page-mode API; RBAC-gated entry |
| AccountUserDetailScreen | `/account/users/{id}` | Admin Users | `GET/PATCH /admin/users/{id}` | Preview fixtures only | `AdminUserDetailViewModel` ✅ | Yes | Preserves Super Admin; current-user refresh |
| AccountCitiesScreen | `/account/cities` | Admin Cities | public `GET /cities` + admin mutations | Preview fixtures only | `AdminCitiesViewModel` ✅ | Yes | Real city source |
| AccountCityCreateScreen | `/account/cities/new` | Admin Cities | `POST /admin/cities` | Preview fixtures only | `AdminCityCreateViewModel` ✅ | Yes | slug+name only |
| AccountCityDetailScreen | `/account/cities/{id}` | Admin Cities | `PATCH/DELETE /admin/cities/{id}` | Preview fixtures only | `AdminCityDetailViewModel` ✅ | Yes | Super Admin delete; 409→CityInUse workaround |
| LoginScreen | `/account/login` | Auth | `POST /auth/login` | Preview only | `LoginViewModel` ✅ | Yes | No app chrome |
| RegisterScreen | `/account/register` | Auth | `POST /auth/register` | Preview only | `RegisterViewModel` ✅ | Yes | Optional referral code |
| RegisterVerifyScreen | `/account/register/verify` | Auth | `POST /auth/verify` | Preview only | `RegisterVerifyViewModel` ✅ | Yes | Challenge from `AuthFlowStateHolder` |
| ForgotPasswordScreen | `/account/forgot` | Auth | `POST /auth/forgot-password` | Preview only | `ForgotPasswordViewModel` ✅ | Yes | |
| ResetPasswordScreen | `/account/forgot/reset` | Auth | `POST /auth/reset-password` | Preview only | `ResetPasswordViewModel` ✅ | Yes | |
| CreateStoreScreen | `/admin/stores/new` | Seller Shop | `POST /seller/shops`, `GET /cities`, `GET /seller/shops/check-slug` | `CreateStoreModels.kt` (unsupported fields deferred) | `CreateShopViewModel` + `CreateStoreLocationViewModel` | Partial — create+slug+cities real; category IDs / logo / policies deferred | No shopper chrome; navigates Account on success |
| StorePlanScreen | `/admin/stores/plan` | Seller Subscription | `GET /seller/shops/{id}/subscription` | Preview fixtures | `StorePlanViewModel` ✅ | Yes | Per-shop; billing history deferred |
| StorePlanUpgradeScreen | `/admin/stores/plan/upgrade` | Seller Subscription | Plans public + purchase | Preview fixtures | `StorePlanUpgradeViewModel` ✅ | Yes | External payment handoff + verify; no yearly toggle |
| AdminPlansScreen | `/admin/plans` | Admin Plans | `GET/POST/PATCH/DELETE /admin/plans` | Preview fixtures only | `AdminPlansViewModel` ✅ | Yes | Real CRUD; raw features preserved; Super Admin delete |
| AdminShopsModerationScreen | `/admin/moderation/shops` | Admin Moderation | `GET /admin/shops`, confirm PATCH | — | `AdminShopsViewModel` ✅ | — | Thin real-API queue; public cache invalidation |
| AdminProductsModerationScreen | `/admin/moderation/products` | Admin Moderation | `GET /admin/products`, confirm PATCH | — | `AdminProductsViewModel` ✅ | — | Thin real-API queue |
| AdminProductModerationDetailScreen | `/admin/moderation/products/{id}` | Admin Moderation | `GET /admin/products/{id}`, confirm PATCH | — | `AdminProductDetailsViewModel` ✅ | — | Preserves active vs confirmed |
| AdminCommentConfirmScreen | `/admin/moderation/comments` | Admin Moderation | confirm PATCH only | — | `AdminCommentsViewModel` ✅ | — | **Blocked list contract**; known-ID mutation only |
| AdminTaxonomyScreen | `/admin/taxonomy` | Admin Taxonomy | import/name/icon endpoints | Local form input only | taxonomy admin ViewModels ✅ | — | Multipart keys verified; response schemas unresolved |
| AdminStaticPagesScreen | `/admin/content/pages` | Admin CMS | `GET/DELETE /admin/static-pages` | — | `AdminStaticPagesViewModel` ✅ | — | Real API; Super Admin delete |
| AdminStaticPageEditorScreen | `/admin/content/pages/{id|new}` | Admin CMS | `GET/POST/PATCH /admin/static-pages` | — | `AdminStaticPageEditorViewModel` ✅ | — | Real HTML editor |
| CreateProductScreen | `/admin/products/new` | Seller Product | `POST /seller/shops/{id}/products` multipart, `GET /categories` | Preview mocks only (`CreateProductMocks`); unsupported fields local-only | `CreateProductViewModel` ✅, `TaxonomyPickerViewModel` ✅ | Real create + taxonomy + `ImagePicker`; no list/edit UI yet |
| CreateCategoryScreen | `/admin/categories/new` | Admin Catalog / Taxonomy | `GET /categories` | `ProductTaxonomyMocks.kt` (Preview only) | `TaxonomyPickerViewModel` | Real API — taxonomy tree | Standard taxonomy picker |
| ProductDetailScreen | `/products/{id}/{slug}` | Public Product | `GET /products/{id}`, reviews, save/follow | Preview fixtures only | `ProductDetailsViewModel` ✅, `ProductEngagementViewModel` ✅, `ProductReviewsViewModel` ✅ | Yes | No purchase CTAs; reviews hide unverified meta |
| StoreScreen | `/m/{shopId}` | Public Shop | `GET /shops/{id}` or slug + products list | Cover, collections, menu mocks | `ShopDetailsViewModel` ✅, `ShopEngagementViewModel` ✅ | Yes | Follow wired; mock product-review sheet not opened |
| SearchResultsScreen | `/search?q=` | Marketplace search | `GET /products/search` | — | `ProductSearchViewModel` ✅ | — | New in Phase 5 |
| AboutScreen | `/about` | CMS | `GET /static-pages/slug/about-us` | Marketing shell/design content remains | `PublicStaticPageViewModel` ✅ | Yes | Sanitized CMS body rendered with `SafeHtml` |
| StaticPageScreen | `/terms`, `/privacy`, `/service-levels` | CMS | `GET /static-pages/slug/{slug}` | — | `PublicStaticPageViewModel` ✅ | — | Real API; sanitized Compose rendering |
| PlaceholderScreen | — | — | — | — | — | — | Used by Offers/Saved |

---

## Screens in roadmap but not implemented

From [`ui-reference/screens.md`](ui-reference/screens.md):

| Planned area | Status |
|--------------|--------|
| Category landing (Women/Men/Beauty) | UI not currently found |
| Product list (filters + grid) | UI not currently found |
| Search results | `SearchResultsScreen` ✅ (`/search?q=`) |
| Collection / Edit page | UI not currently found |
| Cart / Checkout / Payment | Out of scope (mock phase) |

---

## Cross-feature dependencies

| Screen | Depends on |
|--------|------------|
| AccountScreen | `SessionRepository` + `AccountRepository`; seller/admin role gates |
| CreateStoreScreen | Session update after shop create via `CreateShopUseCase` (not Auth ViewModel) |
| Seller list / edit / details / API key | ViewModels in `:feature:seller` ready; Compose screens deferred |
| Seller analytics / boosts | `SellerAnalyticsViewModel`, `SellerBoostsViewModel`, `CreateBoostViewModel` ready; **no new Compose screens** (Phase 10) |
| StorePlanScreen | Wired Phase 9 — shop from SellerShopRepository; SubscriptionRepository |
| StorePlanUpgradeScreen | Wired Phase 9 — PlanRepository + PurchasePlan + VerifyPendingPayment |
| ReferralsScreen | Wired Phase 9 — ReferralRepository + ApplyReferralCredit |
| Admin users/cities/plans | `:feature:admin` real repositories + ViewModels; `AdminPermissions` gates UX |
| Admin moderation/taxonomy/CMS | Thin screens backed by Phase 11 APIs; comment discovery remains blocked |
| Public CMS pages | `:feature:content` + `AllowlistHtmlSanitizer`; `SafeHtml` in `:shared` |
| HomeScreen / CategoriesScreen / PDP / Store | `:feature:marketplace` + `MarketplaceUiMapper`; omnibox → search |
| App chrome | `SessionState` + `CurrentUserState` in `App.kt` via `AppSessionCoordinator` |

---

## Runtime mock data file index

| File | Classification | Used by |
|------|----------------|---------|
| `ProductDetailModels.kt` | Runtime temporary | Home, Categories, PDP |
| `MockProductCatalog` | Runtime temporary | Cross-screen product navigation |
| `HomeCategory*.kt`, `HeroCollageModels.kt` | Runtime temporary | HomeScreen |
| `Categories*Models.kt` | Runtime temporary | CategoriesScreen |
| `StoreModels.kt`, `StoreProductsModels.kt` | Runtime temporary | StoreScreen |
| `AccountProfileModels.kt`, `ReferralModels.kt` | Runtime temporary | Account flows |
| `AccountUserModels.kt`, `AccountCityModels.kt` | Preview fixtures | Admin account screens use real ViewModels at runtime |
| `CreateStoreModels.kt`, `CreateProductModels.kt` | Runtime temporary | Seller admin |
| `StorePlanModels.kt`, `AdminPlansModels.kt` | Preview fixtures | Plan screens use real ViewModels at runtime |
| `ReferralModels.kt` | Preview fixtures | ReferralsScreen uses API mapper at runtime |
| `AboutModels.kt` | Design-only shell content | AboutScreen CMS body is real |
| `ProductTaxonomyMocks.kt` | Runtime temporary | CreateCategoryScreen |
| `OmniboxModels.kt` | Runtime temporary | Hero search |
| `SiteFooterModels.kt` | Design-only / runtime | Footer links |

---

## Auth chrome state

[`App.kt`](../shared/src/commonMain/kotlin/com/vitran/shop/App.kt): `NavAuthUiState.SignedOut` hardcoded for mock phase. Future: drive from `:core:session` via ViewModel or composition local — not from Auth screen directly.
