# VitranShop — Screen Map (UI-only / Mock)

## Out of scope (no purchase flow)
- Cart
- Checkout
- Payment
- Place order

## In scope
1. Home (`/` — `HomeScreen`)
2. Explore / Categories (`/categories` — `CategoriesScreen`; shop.app title “Explore”, nav label «دسته‌بندی‌ها»)
3. Deals (`/offers`)
4. Saved (`/saved`)
5. Account (Profile hub) (`/account` — `AccountScreen`; compact identity row, referral promo, Saved/Following tiles, recently viewed, seller empty-state, sign out). Visual: `docs/ui-reference/account/hub-desktop.png`
5b. Edit profile (`/account/profile` — `ProfileScreen`; avatar + camera badge, personal/contact/sizing cards with icon headers, dropdown sizes, cancel+save). Child of Account; chrome stays. Visual: `docs/ui-reference/account/profile-redesign.png`
5c. Referrals (`/account/referrals` — `ReferralsScreen`; hero invite card with gift illustration + code/link + share/copy, 4 stat cards, how-it-works, filtered invite list with status badges, seller-credit progress). Child of Account. Visual: `docs/ui-reference/account/referrals-desktop.png`
5d. Following (`/account/following` — `FollowingScreen`; followed stores). Child of Account.
5e. Account settings (`/account/settings` — `AccountSettingsScreen`; two-column settings+privacy cards with icon rows/toggles, interests accordion, shopping-for). Child of Account. Visual: `docs/ui-reference/account/settings-desktop.png`
6. Category landing (Women/Men/Beauty/...)
7. Product list (filters + grid)
8. Product detail (UI only; no checkout) — route `/products/{productId}/{slug}` ↔ shop.app PDP; `ProductDetailScreen`
9. Store / Shop — route `/m/{shopId}` ↔ shop.app merchant; `StoreScreen` (header + products grid)
10. Collection / Edit page
11. Search
12. Auth — Login `/account/login` + Register `/account/register` (`AuthSplitShell` two-column brand panel on md+ / form-only below; segmented ورود|ساخت حساب; mobile +98 prefix + password strength; invite code collapsed behind «کد دعوت دارید؟»; forgot-password on login). Register verify `/account/register/verify` (OTP card, masked phone, resend timer→link, paste + auto-submit, verify CTA, mock error `000000`). Forgot password `/account/forgot` (mobile + send code). Reset password `/account/forgot/reset` (OTP + new password, no confirm, no auto-submit; mock error `000000`). No Google. Mock-only; no app chrome.
13. Create store (merchant admin) — route `/admin/stores/new` (`CreateStoreScreen`); 5-step wizard with live phone preview; no shopper chrome; mock-only. Opened from Account or the URL.
14. Add product (merchant admin) — route `/admin/products/new` (`CreateProductScreen`); two-column form from 1024dp; default status Draft; variant chips + combination table; no shopper chrome; mock-only. Opened from Account, Create store empty-products CTA, or the URL.
15. Select standard category (merchant admin) — route `/admin/categories/new` (`CreateCategoryScreen`); Shopify Standard Product Taxonomy picker (search + hierarchy drill-down); mock-only; no custom taxonomy IDs; no shopper chrome. Opened from Account or the URL.

## Bottom / Side nav tabs
- Home
- Explore (route `/categories`, `Route.Categories`)
- Deals
- Saved
- Account
