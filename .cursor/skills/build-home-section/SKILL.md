---
name: build-home-section
description: >-
  Builds one Home screen section at a time for VitranShop: component first,
  then wire into HomeScreen. Use when working on Home app navigation, categories,
  shop/product rows, or any other Home section — not the whole Home page at once.
---

# Build Home Section

## When to use
- User asks to work on the Home screen or any Home section.
- Building or refining: App navigation, Categories, category product/shop rows, etc.

## When to update
- Home section order changes.
- Real component paths exist — add “Example from this repo” links.
- A new recurring Home block is added to the order list below.

## Home section order
1. App navigation (`AppShell` breakpoint + `AppSideNav` / `AppBottomNav`)
2. Content container (`AppContentContainer` — empty shop.app frame)
3. Download app banner (`DownloadAppBanner`)
4. HomeHero (collage / wordmark / omnibox — when requested)
5. Categories
6. Shops / products by category (horizontal rows)
7. Site footer (`SiteFooter`)
8. Other Home blocks (only when requested)

Do **one** section per task unless the user asks for more.

## Workflow
Copy and track:

```
Home section progress:
- [ ] 1. Name section + screenshot in docs/ui-reference/
- [ ] 2. Design props / mock fields (Persian copy)
- [ ] 3. Build composable (components/, navigation/, or sections/home/)
- [ ] 4. Preview / demo with mock data + RTL check
- [ ] 5. Wire into HomeScreen / AppShell only after the component is done
- [ ] 6. Stop — wait for the next section request
```

### 1. Name the section
- Example: `AppNavigation`, `HomeCategoryRow`, `HomeBrandCarousel`
- Pick the matching Home screenshot(s) under `docs/ui-reference/`.

### 2. Props / mock
- Keep models UI-only (title, image placeholder, labels).
- Strings via `composeResources` + `stringResource` when shared.

### 3. Component first
- Implement the reusable composable under theme tokens (`VitranTheme`, shapes, colors).
- Use `shop-design-system` and `build-ui-piece` skills as needed.
- For chrome navigation: custom layout only — never Material3 `NavigationBar` / `NavigationRail`.

### 4. Preview
- Local preview or tiny demo state with Persian mock text.
- Confirm RTL (`Start`/`End`, mirrored chevrons).

### 5. Wire into Home
- Add only this section into `HomeScreen` / `AppShell` (create files if missing).
- Do not assemble unrelated Home sections “while we’re here”.

### 6. Stop
- Deliver the section.
- Wait for the next Home section request.

## Anti-patterns
- Implementing all of Home in one go.
- Wiring a half-finished component into the screen.
- Jumping ahead while the current section is unfinished (unless user redirects).
- Adding navigation graph, API, or purchase UI during Home section work.
- Using Material navigation chrome widgets for shop.app-style rails/bars.

## Example from this repo
- App navigation: `shared/.../ui/navigation/AppNavigation.kt`
- Shell: `shared/.../ui/shell/AppShell.kt`
- Content frame: `shared/.../ui/shell/AppContentContainer.kt`
- Download banner: `shared/.../ui/components/DownloadAppBanner.kt`
- Site footer: `shared/.../ui/components/SiteFooter.kt`
- Home host: `shared/.../ui/screens/HomeScreen.kt`
