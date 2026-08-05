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
- Match shop.app using **live shop.app in the agent browser** plus `docs/ui-reference/` screenshots. Screenshots alone are not enough.
- Use mock data only (see mock-phase rule).
- Read `shop-design-system` skill for tokens when needed.

## Steps
Copy and track:

```
Piece progress:
- [ ] 1. Name the piece; open live shop.app section + note screenshot
- [ ] 2. List props / mock fields
- [ ] 3. If this is a page section: measure responsive sizing + gap above previous section on live shop.app
- [ ] 4. Implement composable in shared commonMain
- [ ] 5. Wire local mock sample for preview/demo
- [ ] 6. Check RTL + Persian strings
- [ ] 7. Stop (no full-screen assembly unless asked)
```

### 1. Name the piece
- Example: `ShopSearchBar`, `ProductCard`, `SectionHeader`.
- Open the matching shop.app page/section in the browser; note the screenshot file under `docs/ui-reference/`.

### 2. Props / mock fields
- Define a small data class or parameters (title, price, image placeholder, etc.).
- Keep it UI-only; no API types.

### 3. Section spacing + responsive (when building a page section)
- Required by rule `page-section-workflow.mdc`: live-check compact/desktop sizes and the gap from the section above before wiring.
- Do not use fixed-only layout sizes when shop.app scales with viewport.

### 4. Implement
- Place under a clear package (e.g. `.../ui/components/` or `.../ui/sections/`).
- Reuse theme tokens; avoid hardcoding random colors when theme exists.
- Reuse existing shared UI when the shape matches — see rule `reuse-shared-ui.mdc`.

### 5. Mock sample
- Provide a `@Preview` or a tiny demo state with fake Persian text.

### 6. RTL check
- Alignment uses Start/End.
- Directional icons flip correctly.

### 7. Stop
- Deliver the piece.
- Wait for the next piece request (or an explicit “assemble this screen” request).

## Anti-patterns
- Building Home / Explore / Account entirely in one shot without being asked.
- Adding navigation graph + all tabs “while we’re here”.
- Introducing networking “just for this card”.
- Skipping responsive or “gap above” checks for a new page section.
