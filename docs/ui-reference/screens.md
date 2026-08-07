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

## Bottom / Side nav tabs
- Home
- Explore (route `/categories`, `Route.Categories`)
- Deals
- Saved
- Account
