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
| HomeScreen | `/` | Home | `GET /home`, `POST /events` | `HomeCategoryModels.kt`, `HomeCategoryShopModels.kt`, `HomeCategoryMosaicModels.kt`, `HeroCollageModels.kt` | `HomeViewModel` | Yes | Omnibox uses `OmniboxModels.kt` |
| CategoriesScreen | `/categories` | Catalog / Marketplace | Taxonomy, product rows, merchant grids, catalog search | `CategoriesProductRowModels.kt`, `CategoriesMerchantGridModels.kt`, `BrowseCategoryModels.kt`, `ExploreEditModels.kt` | `CategoriesViewModel` | Yes | Explore / browse |
| OffersScreen | `/offers` | Deals | TBD — no dedicated API in collection | `PlaceholderScreen` | `OffersViewModel` | — | Placeholder only |
| SavedScreen | `/saved` | Engagement / Wishlist | `GET /me/favorites/products` | `PlaceholderScreen` | `SavedViewModel` | — | Placeholder only |
| AccountScreen | `/account` | Account | `GET /auth/me` | `AccountProfileModels.kt` | `AccountViewModel` | Yes | Hub; mock signed-in/out via `NavAuthUiState` |
| ProfileScreen | `/account/profile` | Account | `GET/PUT /auth/profile` | `AccountProfileModels.kt` | `ProfileViewModel` | Yes | |
| ReferralsScreen | `/account/referrals` | Referral | `GET /me/referral`, credits apply | `ReferralModels.kt` | `ReferralsViewModel` | Yes | |
| FollowingScreen | `/account/following` | Engagement (follows) | `GET /me/follows/shops` | Inline mock in section | `FollowingViewModel` | Yes | Response schema missing in Postman |
| AccountSettingsScreen | `/account/settings` | Account | Profile preferences (partial `/auth/profile`) | Local `remember` state | `AccountSettingsViewModel` | Yes | |
| AccountUsersScreen | `/account/users` | Admin Users | `GET /admin/users` | `AccountUserModels.kt` | `AccountUsersViewModel` | Yes | Platform admin |
| AccountUserDetailScreen | `/account/users/{id}` | Admin Users | `GET/PATCH /admin/users/{id}` | `AccountUserModels.kt` | `AccountUserDetailViewModel` | Yes | |
| AccountCitiesScreen | `/account/cities` | Admin Cities | `GET /admin/cities`, public cities | `AccountCityModels.kt`, `MockAccountCities` | `AccountCitiesViewModel` | Yes | Mutable mock CRUD |
| AccountCityCreateScreen | `/account/cities/new` | Admin Cities | `POST /admin/cities` | `AccountCityModels.kt` | `AccountCityCreateViewModel` | Yes | |
| AccountCityDetailScreen | `/account/cities/{id}` | Admin Cities | `PATCH/DELETE /admin/cities/{id}` | `AccountCityModels.kt` | `AccountCityDetailViewModel` | Yes | |
| LoginScreen | `/account/login` | Auth | `POST /auth/login` | Form `remember` state | `LoginViewModel` | Yes | No app chrome |
| RegisterScreen | `/account/register` | Auth | `POST /auth/register` | Form `remember` state | `RegisterViewModel` | Yes | Optional referral code |
| RegisterVerifyScreen | `/account/register/verify` | Auth | `POST /auth/verify` | Form `remember`; mock OTP `000000` | `RegisterVerifyViewModel` | Yes | |
| ForgotPasswordScreen | `/account/forgot` | Auth | `POST /auth/forgot-password` | Form `remember` state | `ForgotPasswordViewModel` | Yes | |
| ResetPasswordScreen | `/account/forgot/reset` | Auth | `POST /auth/reset-password` | Form `remember`; mock OTP `000000` | `ResetPasswordViewModel` | Yes | |
| CreateStoreScreen | `/admin/stores/new` | Seller Shop | `POST /seller/shops` (+ session token update) | `CreateStoreModels.kt` | `CreateStoreViewModel` | Yes | No shopper chrome |
| StorePlanScreen | `/admin/stores/plan` | Seller Subscription | `GET /seller/shops/{id}/subscription` | `StorePlanModels.kt` | `StorePlanViewModel` | Yes | Per-shop plan |
| StorePlanUpgradeScreen | `/admin/stores/plan/upgrade` | Seller Subscription | Plans public + purchase | `StorePlanModels.kt` | `StorePlanUpgradeViewModel` | Yes | |
| AdminPlansScreen | `/admin/plans` | Admin Plans | `GET/POST/PATCH/DELETE /admin/plans` | `AdminPlansModels.kt` | `AdminPlansViewModel` | Yes | Platform admin |
| CreateProductScreen | `/admin/products/new` | Seller Product | Seller product CRUD, multipart images | `CreateProductModels.kt` | `CreateProductViewModel` | Yes | |
| CreateCategoryScreen | `/admin/categories/new` | Admin Catalog / Taxonomy | `GET /categories`, taxonomy tree | `CreateCategoryModels.kt`, `ProductTaxonomyMocks.kt` | `CreateCategoryViewModel` | Yes | Standard taxonomy picker |
| ProductDetailScreen | `/products/{id}/{slug}` | Public Product | `GET /products/{id}`, reviews, contact | `ProductDetailModels.kt`, `MockProductCatalog` | `ProductDetailViewModel` | Yes | No purchase CTAs |
| StoreScreen | `/m/{shopId}` | Public Shop | `GET /shops/{id}` or slug | `StoreModels.kt`, `StoreProductsModels.kt`, `StoreMenuModels.kt` | `StoreViewModel` | Yes | |
| AboutScreen | `/about` | CMS | `GET /static-pages/slug/about-us` | `AboutModels.kt` | `AboutViewModel` | Yes | Marketing page |
| PlaceholderScreen | — | — | — | — | — | — | Used by Offers/Saved |

---

## Screens in roadmap but not implemented

From [`ui-reference/screens.md`](ui-reference/screens.md):

| Planned area | Status |
|--------------|--------|
| Category landing (Women/Men/Beauty) | UI not currently found |
| Product list (filters + grid) | UI not currently found |
| Search results | UI not currently found (omnibox mock only) |
| Collection / Edit page | UI not currently found |
| Cart / Checkout / Payment | Out of scope (mock phase) |

---

## Cross-feature dependencies

| Screen | Depends on |
|--------|------------|
| AccountScreen | Future `SessionReader` for auth state; seller/admin role gates |
| CreateStoreScreen | Session update after shop create (not Auth ViewModel) |
| StorePlanScreen | Shop context + `SubscriptionRepository` per shop |
| HomeScreen / CategoriesScreen | `MockProductCatalog` shared with PDP |
| App chrome | `NavAuthUiState` in `App.kt` (hardcoded `SignedOut` today) |

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
| `AccountUserModels.kt`, `AccountCityModels.kt` | Runtime temporary | Admin account screens |
| `CreateStoreModels.kt`, `CreateProductModels.kt` | Runtime temporary | Seller admin |
| `StorePlanModels.kt`, `AdminPlansModels.kt` | Runtime temporary | Plan screens |
| `AboutModels.kt` | Runtime temporary | AboutScreen |
| `ProductTaxonomyMocks.kt` | Runtime temporary | CreateCategoryScreen |
| `OmniboxModels.kt` | Runtime temporary | Hero search |
| `SiteFooterModels.kt` | Design-only / runtime | Footer links |

---

## Auth chrome state

[`App.kt`](../shared/src/commonMain/kotlin/com/vitran/shop/App.kt): `NavAuthUiState.SignedOut` hardcoded for mock phase. Future: drive from `:core:session` via ViewModel or composition local — not from Auth screen directly.
