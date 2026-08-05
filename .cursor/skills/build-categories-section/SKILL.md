---
name: build-categories-section
description: >-
  Builds one Categories (Explore) screen section at a time for VitranShop:
  component first, then wire into CategoriesScreen. Use when working on Explore
  featured, browse categories, product rows, or any other Categories section —
  not the whole page at once.
---

# Build Categories Section

## When to use
- User asks to work on the Categories / Explore screen (`/categories`).
- Building or refining: Explore featured, Browse categories, product carousels, etc.

## When to update
- Categories section order changes.
- Real component paths exist — add “Example from this repo” links.
- A new recurring Categories block is added to the order list below.

## Categories section order
1. App chrome (`AppShell` — shared; usually already done)
2. Explore featured (`CategoriesExploreFeaturedSection` — H1 + editorial cards)
3. Browse categories
4. Product rows (Top rated / New in …)
5. Site footer (`SiteFooter`)
6. Floating search (only when requested)

Do **one** section per task unless the user asks for more.

## Live shop.app
- Open https://shop.app/categories and inspect the **active** section before coding.
- Save/use screenshots under `docs/ui-reference/` as a supplement, not a substitute.

## Workflow
Copy and track:

```
Categories section progress:
- [ ] 1. Name section + open live shop.app + screenshot in docs/ui-reference/
- [ ] 2. Design props / mock fields (Persian copy)
- [ ] 3. Build composable (components/ or sections/categories/)
- [ ] 4. Preview / demo with mock data + RTL check
- [ ] 5. Wire into CategoriesScreen only after the component is done
- [ ] 6. Stop — wait for the next section request
```

### 1. Name the section
- Example: `CategoriesExploreFeatured`, `BrowseCategoriesGrid`
- Pick the matching Categories screenshot(s) under `docs/ui-reference/`.

### 2. Props / mock
- Keep models UI-only (title, image URL, labels).
- Strings via `composeResources` + `stringResource` when shared.

### 3. Component first
- Implement under theme tokens (`VitranTheme`, shapes, colors).
- Use `shop-design-system` and `build-ui-piece` skills as needed.

### 4. Preview
- Local preview or tiny demo state with Persian mock text.
- Confirm RTL (`Start`/`End`, mirrored chevrons).

### 5. Wire into Categories
- Add only this section into `CategoriesScreen` (create files if missing).
- Do not assemble unrelated Categories sections “while we’re here”.

### 6. Stop
- Deliver the section.
- Wait for the next Categories section request.

## Anti-patterns
- Implementing all of Categories in one go.
- Wiring a half-finished component into the screen.
- Skipping live shop.app inspection and relying only on screenshots.
- Adding navigation graph, API, or purchase UI during Categories section work.

## Example from this repo
- Explore featured: `shared/.../ui/sections/categories/CategoriesExploreFeaturedSection.kt`
- Screen host: `shared/.../ui/screens/CategoriesScreen.kt`
- Route: `Route.Categories` ↔ `/categories`
