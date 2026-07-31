---
name: build-ui-piece
description: >-
  Step-by-step workflow to implement one VitranShop UI component or section
  with mock data and screenshot reference. Use when the user asks to build,
  refine, or restyle a single component or page section — not an entire screen
  unless they explicitly request assembly.
---

# Build UI Piece

## When to use
- User asks for one component (e.g. search bar, product card, category chip).
- User asks for one section (e.g. “Following” row, filter chip row).
- User wants a visual tweak on an existing piece.

## When to update
- After the first real components exist — add short “Example from this repo” paths.
- Folder or naming conventions change.
- The team adds a required checklist item (preview, screenshot diff, etc.).

## Rules of engagement
- **One piece per task** unless the user asks for more.
- Match shop.app using `docs/ui-reference/` for look-and-feel.
- Use mock data only (see mock-phase rule).
- Read `shop-design-system` skill for tokens when needed.

## Steps
Copy and track:

```
Piece progress:
- [ ] 1. Name the piece and target screenshot
- [ ] 2. List props / mock fields
- [ ] 3. Implement composable in shared commonMain
- [ ] 4. Wire local mock sample for preview/demo
- [ ] 5. Check RTL + Persian strings
- [ ] 6. Stop (no full-screen assembly unless asked)
```

### 1. Name the piece
- Example: `ShopSearchBar`, `ProductCard`, `SectionHeader`.
- Note which screenshot file is the visual reference.

### 2. Props / mock fields
- Define a small data class or parameters (title, price, image placeholder, etc.).
- Keep it UI-only; no API types.

### 3. Implement
- Place under a clear package (e.g. `.../ui/components/` or `.../ui/sections/`).
- Reuse theme tokens; avoid hardcoding random colors when theme exists.

### 4. Mock sample
- Provide a `@Preview` or a tiny demo state with fake Persian text.

### 5. RTL check
- Alignment uses Start/End.
- Directional icons flip correctly.

### 6. Stop
- Deliver the piece.
- Wait for the next piece request (or an explicit “assemble this screen” request).

## Anti-patterns
- Building Home / Explore / Account entirely in one shot without being asked.
- Adding navigation graph + all tabs “while we’re here”.
- Introducing networking “just for this card”.
