# VitranShop Architecture

Phase 1 establishes architectural direction and module ownership. **Existing UI is preserved** in `:shared`. Phase 2 adds shared networking; Phase 3 adds auth/session/account; **Phase 4 adds shared reference data** (`:feature:location`, `:feature:taxonomy`); **Phase 5 adds public marketplace** (`:feature:marketplace`, `:feature:home`); **Phase 6 adds marketplace engagement** (`:feature:engagement`); **Phase 7 adds seller shop management** (`:feature:seller`); **Phase 8 adds seller product management** (same `:feature:seller` module, `product/` package) plus `:core:platform` `SelectedFile` / `ImagePicker`; **Phase 9 adds plans, per-shop subscriptions, payment handoff, entitlements, and referrals** (`plan/`, `subscription/`, `referral/` packages in `:feature:seller`); **Phase 10 adds seller analytics CSV export, `FileDownloadExecutor`, `FileSaver`, and placement-boost transport** (`analytics/`, `boost/` packages; Compose screens deferred).

## Selected architecture

**Feature-first Clean Architecture + Unidirectional Data Flow (UDF) + shared Compose Multiplatform ViewModels**

| Layer | Responsibility |
|-------|----------------|
| **Presentation** | Existing Compose screens/sections; future `UiState`, `UiAction`, ViewModels |
| **Domain** | Feature-owned models, repository contracts, use cases with real business rules |
| **Data** | Ktor APIs, DTOs, mappers, repository implementations (Phase 2+) |

Dependency direction:

```text
Presentation ──► Domain
Data         ──► Domain

Domain ──X Presentation, Data, Compose, Ktor, Room, Koin, platform APIs
```

## Why this combination

VitranShop already has **29 screens** and a mature design system in `:shared`. A layer-first monolith (`global/domain`, `global/data`) would fight existing package layout. Feature-first ownership maps cleanly to Postman business domains and future Gradle extraction.

UDF with shared ViewModels fits Compose Multiplatform: one ViewModel per screen/feature, `StateFlow<UiState>`, actions as methods or sealed types. Global Redux/MVI stores are unnecessary for this app's scope.

## Current physical structure (Phase 1)

```text
VitranShop/
├── androidApp/          # Android application shell
├── desktopApp/          # JVM Compose Desktop shell
├── webApp/              # JS + Wasm browser shells
├── iosApp/              # Xcode shell (links Shared.framework)
├── core/
│   ├── common/          # Universal Kotlin primitives (minimal)
│   ├── domain/          # Cross-feature domain types (AuthMode, UserRole)
│   ├── network/         # Ktor client, envelope, executor, health API (Phase 2)
│   ├── platform/        # Secure storage, platform JSON (Phase 3)
│   └── session/         # Session lifecycle, token refresh (Phase 3)
├── feature/
│   ├── auth/            # Auth flows (Phase 3)
│   ├── account/         # Profile / current user (Phase 3)
│   ├── location/        # Public cities reference data (Phase 4)
│   ├── taxonomy/        # Public category tree (Phase 4)
│   ├── marketplace/     # Public shops + products (Phase 5)
│   ├── home/            # Home feed envelope (Phase 5)
│   ├── engagement/      # Follow, favorites, wishlist, reviews, contact, analytics (Phase 6)
│   └── seller/          # Seller shops: create/list/update, slug, fulfillment, API key (Phase 7)
└── shared/              # UI, navigation, DI bootstrap
    └── src/commonMain/kotlin/com/vitran/shop/
        ├── App.kt
        ├── di/
        └── ui/
            ├── components/
            ├── navigation/
            ├── screens/
            ├── sections/
            ├── shell/
            └── theme/
```

## Target scalable structure (future)

Gradle feature modules and additional core modules are introduced **incrementally** when dependency isolation pays off:

```text
feature/auth, feature/account, feature/home, feature/marketplace/…
core/designsystem, core/ui, core/platform, core/database, …
```

Phase 1 does **not** create empty feature modules or move UI out of `:shared`.

## Boundaries that exist now

| Boundary | Module / location | Phase 1 contents |
|----------|-------------------|------------------|
| API origin config | `:core:network` | `ApiEnvironment`, `ApiEnvironments` |
| Auth mode / roles (types) | `:core:domain` | `AuthMode`, `UserRole` |
| Session contract | `:core:session` | `SessionReader`, `TokenKind` |
| DI bootstrap | `:shared` / `di/` | Koin: `ApiEnvironment`, `networkModule`, `sessionModule` |
| UI + mocks | `:shared` / `ui/` | Unchanged screens and sections |
| Navigation | `:shared` / `ui/navigation/` | Navigation 3, typed `Route` |
| Design system | `:shared` / `ui/theme/` | `VitranTheme`, tokens (future `:core:designsystem`) |

## App shell responsibility

[`App.kt`](../shared/src/commonMain/kotlin/com/vitran/shop/App.kt) is the shared root composable:

1. `startVitranKoin()` — DI bootstrap (`ApiEnvironment`, networking, session stub)
2. `VitranTheme` — RTL Material3 + design tokens
3. `NavigationState` + `Navigator` + `BindBrowserNavigation` (web URL sync)
4. `AppShell` — desktop side rail / mobile bottom bar; auth-aware chrome
5. `AppNavHost` — sole `Route` → screen mapping

Platform launchers (`androidApp`, `desktopApp`, `webApp`, `iosApp`) stay thin and call shared `App()`.

## Core responsibilities

See [dependency-rules.md](dependency-rules.md). Summary:

- **`core:common`** — genuinely universal helpers only; not a dumping ground
- **`core:domain`** — cross-feature primitives (`AuthMode`, `UserRole`, `AppError`, `AppResult`, pagination)
- **`core:network`** — Ktor client, envelope, `ApiRequestExecutor`, `FileDownloadExecutor` (opaque bytes / CSV), pagination DTOs, HealthApi ([networking.md](networking.md))
- **`core:session`** — secure credential storage, token refresh, `SessionRepository` (Phase 3)
- **`core:platform`** — secure storage, `ImagePicker` (upload), `FileSaver` (Phase 10 download)
- **`feature:location`** — `LocationRepository`, public cities API, in-memory list cache ([reference-data.md](reference-data.md))
- **`feature:taxonomy`** — `TaxonomyRepository`, public category tree/detail, in-memory cache
- **`feature:marketplace`** — `ShopRepository`, `ProductRepository`, public shop/product APIs, cursor list ViewModels ([public-marketplace.md](public-marketplace.md))
- **`feature:home`** — `HomeRepository`, optional-auth home envelope ([public-marketplace.md](public-marketplace.md))
- **`feature:engagement`** — follow/favorite/wishlist/reviews/comments/contact/analytics ([marketplace-engagement.md](marketplace-engagement.md))
- **`feature:seller`** — seller shop CRUD, products, plans/subscriptions/referrals, analytics export, boost transport ([seller-shop-management.md](seller-shop-management.md), [seller-analytics-and-boosts.md](seller-analytics-and-boosts.md))
- **`core:designsystem` / `core:ui`** — deferred; theme lives in `:shared` today

## Feature responsibility (conceptual packages)

Future packages under `com.vitran.shop.feature.*` (or Gradle modules) own vertical slices:

| Feature area | Screens today | Postman domains |
|--------------|---------------|-----------------|
| Auth | Login, Register, Verify, Forgot, Reset | Auth |
| Account | Account hub, Profile, Settings | Users, engagement |
| Home | HomeScreen | Home |
| Marketplace | Categories, PDP, Store | Shops/Products public, catalog |
| Engagement | PDP/store save+follow, reviews list | Favorites, follows, wishlists, reviews, comments, contact, events |
| Seller | Create store/product, plans | Shops/Products seller, subscription |
| Admin | Users, cities, plans, category picker | Admin * folders |
| Referral | ReferralsScreen | Referrals |
| CMS | AboutScreen | Static pages |

## Presentation flow (future wiring)

```text
Existing Compose Screen
        │ UiAction
        ▼
Shared ViewModel (commonMain)
        │ use case / repository
        ▼
Domain ← Data
        │
        ▼
StateFlow<UiState> → Screen
```

Use `UiEffect` only for one-time UI work (snackbar, navigation side-effects) when truly needed.

## Use-case policy

Do **not** create one use-case class per repository method. Introduce use cases when they add:

- validation or business rules
- orchestration across repositories
- meaningful reusable operation semantics

Examples: `Login`, `CreateShop`, `PurchasePlan`. Simple reads may call repositories from ViewModels directly.

## commonMain-first strategy

Default to `commonMain` for domain, ViewModels, repositories, DTOs, and mappers. Platform source sets only for OS APIs (secure storage, file picker, notifications).

## Platform-specific strategy

Prefer **interface + DI** for replaceable capabilities (`SecureStorage`, `ShareManager`, `ImagePicker`, `FileSaver`). Use **expect/actual** only when a small platform primitive is genuinely needed and DI would be artificial.

## Related documents

- [dependency-rules.md](dependency-rules.md)
- [api-contract.md](api-contract.md)
- [screen-feature-map.md](screen-feature-map.md)
- [build-configuration.md](build-configuration.md)
- [marketplace-engagement.md](marketplace-engagement.md)
- [seller-analytics-and-boosts.md](seller-analytics-and-boosts.md)
- [decisions/](decisions/)
