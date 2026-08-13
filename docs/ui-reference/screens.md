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
5. Account (Profile hub) (`/account`)
6. Category landing (Women/Men/Beauty/...)
7. Product list (filters + grid)
8. Product detail (UI only; no checkout) — route `/products/{productId}/{slug}` ↔ shop.app PDP; `ProductDetailScreen`
9. Store / Shop — route `/m/{shopId}` ↔ shop.app merchant; `StoreScreen` (header + products grid)
10. Collection / Edit page
11. Search
12. Auth / Login — route `/account/login` (`LoginScreen` / `AuthShell`) ↔ shop.app `/accounts/login`; email step then OTP verify step (same URL); opened from signed-out Saved / Account nav (`onLoginRequest` → push); no app chrome while showing
13. Create store (merchant admin) — route `/admin/stores/new` (`CreateStoreScreen`); 5-step wizard with live phone preview; no shopper chrome; mock-only. Opened from Account or the URL.

## Bottom / Side nav tabs
- Home
- Explore (route `/categories`, `Route.Categories`)
- Deals
- Saved
- Account
