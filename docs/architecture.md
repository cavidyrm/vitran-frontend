# VitranShop Architecture

Phase 1 establishes architectural direction and module ownership. **Existing UI is preserved** in `:shared`; business networking and repositories are deferred to later phases.

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
│   ├── network/         # ApiEnvironment config (no Ktor yet)
│   └── session/         # SessionReader contract, TokenKind (no storage yet)
└── shared/              # All UI, navigation, mocks, DI bootstrap
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
| DI bootstrap | `:shared` / `di/` | Koin with `ApiEnvironment` only |
| UI + mocks | `:shared` / `ui/` | Unchanged screens and sections |
| Navigation | `:shared` / `ui/navigation/` | Navigation 3, typed `Route` |
| Design system | `:shared` / `ui/theme/` | `VitranTheme`, tokens (future `:core:designsystem`) |

## App shell responsibility

[`App.kt`](../shared/src/commonMain/kotlin/com/vitran/shop/App.kt) is the shared root composable:

1. `startVitranKoin()` — DI bootstrap (Phase 1: `ApiEnvironment` only)
2. `VitranTheme` — RTL Material3 + design tokens
3. `NavigationState` + `Navigator` + `BindBrowserNavigation` (web URL sync)
4. `AppShell` — desktop side rail / mobile bottom bar; auth-aware chrome
5. `AppNavHost` — sole `Route` → screen mapping

Platform launchers (`androidApp`, `desktopApp`, `webApp`, `iosApp`) stay thin and call shared `App()`.

## Core responsibilities

See [dependency-rules.md](dependency-rules.md). Summary:

- **`core:common`** — genuinely universal helpers only; not a dumping ground
- **`core:domain`** — cross-feature primitives; not feature domain models
- **`core:network`** — future Ktor, envelope, pagination transport (Phase 2)
- **`core:session`** — future auth state, secure storage (Phase 3)
- **`core:designsystem` / `core:ui`** — deferred; theme lives in `:shared` today

## Feature responsibility (conceptual packages)

Future packages under `com.vitran.shop.feature.*` (or Gradle modules) own vertical slices:

| Feature area | Screens today | Postman domains |
|--------------|---------------|-----------------|
| Auth | Login, Register, Verify, Forgot, Reset | Auth |
| Account | Account hub, Profile, Settings | Users, engagement |
| Home | HomeScreen | Home |
| Marketplace | Categories, PDP, Store | Shops/Products public, catalog |
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

Prefer **interface + DI** for replaceable capabilities (`SecureStorage`, `ShareManager`, `FilePicker`). Use **expect/actual** only when a small platform primitive is genuinely needed and DI would be artificial.

## Related documents

- [dependency-rules.md](dependency-rules.md)
- [api-contract.md](api-contract.md)
- [screen-feature-map.md](screen-feature-map.md)
- [build-configuration.md](build-configuration.md)
- [decisions/](decisions/)
