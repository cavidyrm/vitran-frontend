---
name: shop-design-system
description: >-
  Extracts and applies VitranShop visual tokens and section patterns from
  docs/ui-reference screenshots (colors, radii, typography, cards, search, nav).
  Use when creating theme, colors, typography, shared components, or matching
  shop.app look for a single UI piece — not for building a whole screen at once.
---

# Shop Design System

## When to use
- Setting up theme (colors, typography, shapes).
- Building a shared component (search pill, product card, section header, nav).
- Checking that a new piece matches shop.app visuals.

## When to update
- Accent color, font, or corner radii are finalized or changed.
- New recurring patterns appear (e.g. deal badge, brand row).
- Screenshots in `docs/ui-reference/` are replaced with better references.

## Source of truth
- Folder: `docs/ui-reference/`
- Screen list: `docs/ui-reference/screens.md`
- Theme code: `shared/.../ui/theme/` (`VitranTheme`, `VitranLightColorScheme`, `VitranExtraColors`)
- Screenshots define **tokens and patterns**, not a mandate to ship a full screen in one go.

## Locked visual tokens
| Token | Hex | Notes |
|-------|-----|-------|
| Accent purple (`primary`) | `#5A31F4` | Buttons, search action, accents |
| Purple dark | `#4524DB` | Pressed / hover |
| Purple soft (`primaryContainer`) | `#DBD1FF` | Soft brand fills |
| Purple tint | `#EEEAFF` | Light brand tint |
| Page background | `#F6F6F6` | Canvas |
| Surface | `#FFFFFF` | Cards, panels |
| Primary text | `#1A1A1A` | Titles |
| Secondary text | `#757575` | Meta, breadcrumbs |
| Outline | `#E5E5E5` | Chip / filter borders |
| Sale badge | `#000000` | Via `VitranTheme.extraColors.saleBadge` |
| Star | `#F5A623` | Via `VitranTheme.extraColors.star` |
| Card / image radius | ~12–20 dp | `VitranShapes` |
| Search bar | full pill | Purple circular action button |
| Spacing | generous | Prefer airy gaps over dense packing |

## Modern theme APIs (CMP 1.11)
- Fonts: `@Composable Font(Res.font…)` + `VitranTypography()` — do not use legacy non-composable font loaders.
- Extras (sale/star): `LocalVitranExtraColors` / `VitranTheme.extraColors`.
- RTL: set once in `VitranTheme` via `LocalLayoutDirection.Rtl`.
- Strings: `composeResources/values` + `stringResource`.

## Recurring patterns
- **Search pill**: wide rounded field + purple circular icon button.
- **Product card**: rounded image, optional sale badge, heart control, store name, title, stars, price.
- **Section header**: bold title + chevron / “see all”.
- **Horizontal brand/product rows**: `LazyRow`-style carousels.
- **Nav**: Home, Explore, Deals, Saved, Account (rail on large, bottom bar on compact).

## Persian / RTL
- App content is Persian; layout is RTL via `VitranTheme`.
- Mirror directional icons (chevrons, back, exit) for RTL.
- Prefer `Start`/`End` alignment APIs.

## Workflow for one piece
1. Open the relevant screenshot(s).
2. Note only tokens needed for **this** piece (color, size, radius, text style).
3. Implement that piece in `shared` commonMain using theme tokens.
4. Stop. Do not assemble the full screen unless asked.
