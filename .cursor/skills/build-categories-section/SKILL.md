---
name: build-categories-section
description: >-
  Builds one Categories (Explore) screen section at a time for VitranShop:
  component first, then wire into CategoriesScreen. Use when working on Explore
  featured, browse categories, product rows, merchant grids, or any other
  Categories section — not the whole page at once.
---

# Build Categories Section

## When to use
- User asks to work on the Categories / Explore screen (`/categories`).
- Building or refining: Explore featured, Browse categories, product carousels, category merchant grids, etc.

## When to update
- Categories section order changes.
- Real component paths exist — add “Example from this repo” links.
- A new recurring Categories block is added to the order list below.

## Categories section order
1. App chrome (`AppShell` — shared; usually already done)
2. Explore featured (`CategoriesExploreFeaturedSection` — H1 + editorial cards)
3. Browse categories
4. Product rows (Top rated / New in …)
5. Category merchant grids (Women / Men / Beauty / Food & drinks / Baby & toddler)
6. Site footer (`SiteFooter` — shared with Home)
7. Floating search (always-on; reuse Home `FloatingSearchOmnibox` / `FloatingSearchFab`)

Do **one** section per task unless the user asks for more.

## Live shop.app
- Open https://shop.app/categories and inspect the **active** section before coding.
- Save/use screenshots under `docs/ui-reference/` as a supplement, not a substitute.
- **Required:** measure responsive sizes (compact + desktop) and the **gap above** from the previous Categories section; apply `CategoriesSectionGap` (40dp) between major blocks unless live shop.app shows a different rhythm for that pair.

## Workflow
Copy and track:

```
Categories section progress:
- [ ] 1. Name section + open live shop.app + screenshot in docs/ui-reference/
- [ ] 2. Measure responsive sizing (compact/desktop) + internal spacing on live shop.app
- [ ] 3. Measure gap above from previous section (and note shared page gap)
- [ ] 4. Design props / mock fields (Persian copy)
- [ ] 5. Build composable (components/ or sections/categories/)
- [ ] 6. Preview / demo with mock data + RTL check
- [ ] 7. Wire into CategoriesScreen with correct gap above (CategoriesSectionGap)
- [ ] 8. Stop — wait for the next section request
```

### 1. Name the section
- Example: `CategoriesExploreFeatured`, `BrowseCategoriesGrid`
- Pick the matching Categories screenshot(s) under `docs/ui-reference/`.

### 2. Responsive + spacing (live)
- Card/row widths, column counts, and gaps must shrink/grow like shop.app — no fixed-only sizes when the live page is fluid.
- Record title→content and item gaps from the browser.

### 3. Gap above (live)
- Scroll so the previous section and the new section are both visible; measure previous bottom → new top.
- On Categories, major blocks use `CategoriesSectionGap` (40dp). Avoid stacking extra section bottom pad that doubles the gap.

### 4. Props / mock
- Keep models UI-only (title, image URL, labels).
- Strings via `composeResources` + `stringResource` when shared.

### 5. Component first
- Implement under theme tokens (`VitranTheme`, shapes, colors).
- Use `shop-design-system` and `build-ui-piece` skills as needed.
- Reuse shared UI when the shape already exists — see rule `reuse-shared-ui.mdc`.

### 6. Preview
- Local preview or tiny demo state with Persian mock text.
- Confirm RTL (`Start`/`End`, mirrored chevrons).

### 7. Wire into Categories
- Add only this section into `CategoriesScreen` (create files if missing).
- Confirm the gap from the section above matches shop.app.
- Do not assemble unrelated Categories sections “while we’re here”.

### 8. Stop
- Deliver the section.
- Wait for the next Categories section request.

## Anti-patterns
- Implementing all of Categories in one go.
- Wiring a half-finished component into the screen.
- Skipping live shop.app inspection and relying only on screenshots.
- Fixed card widths when shop.app uses viewport-based carousel/grid sizing.
- Ignoring the gap above the new section (or double-padding against `CategoriesSectionGap`).
- Adding navigation graph, API, or purchase UI during Categories section work.

## Example from this repo
- Explore featured: `shared/.../ui/sections/categories/CategoriesExploreFeaturedSection.kt`
- Browse: `shared/.../ui/sections/categories/CategoriesBrowseCategoriesSection.kt`
- Product rows: `shared/.../ui/sections/categories/CategoriesProductRowsSection.kt`
- Merchant grids: `shared/.../ui/sections/categories/CategoriesMerchantGridsSection.kt`
- Merchant card: `shared/.../ui/components/CategoriesMerchantCard.kt`
- Site footer: `shared/.../ui/components/SiteFooter.kt` (shared with Home)
- Floating search: wired in `CategoriesScreen` (always-on; same components as Home)
- Screen host: `shared/.../ui/screens/CategoriesScreen.kt`
- Route: `Route.Categories` ↔ `/categories`
