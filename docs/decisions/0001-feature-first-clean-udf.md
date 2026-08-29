# ADR 0001: Feature-first Clean Architecture with UDF

## Status

Accepted — Phase 1

## Context

VitranShop is a Kotlin Multiplatform Compose app with **29 existing screens**, rich mock UI, Navigation 3, and a documented backend contract (100+ API routes). We need an architecture that:

- Preserves existing UI investment
- Scales to auth, marketplace, seller, and admin domains
- Shares ViewModels and business logic across Android, iOS, Desktop, and Web
- Avoids rewrite when connecting real APIs

Alternatives considered:

| Approach | Tradeoff |
|----------|----------|
| **Pure MVVM** (ViewModel → repository only) | Simple but no place for business rules/orchestration as flows grow |
| **Strict global MVI** | Heavy boilerplate per screen; overkill for read-heavy marketplace UI |
| **Redux global store** | Central store fights feature ownership and KMP lifecycle |
| **Layer-first monolith** (`global/domain`, `global/data`) | Conflicts with existing `ui/sections/*` layout and feature boundaries |

## Decision

Adopt **feature-first Clean Architecture** with **Unidirectional Data Flow**:

- Features own presentation, domain, and data (packages now; Gradle modules incrementally)
- ViewModels in `commonMain` expose `StateFlow<UiState>`; screens send `UiAction`
- Domain has no dependency on Compose, Ktor, or platform APIs
- Data implements domain repository contracts
- Cross-cutting session/network live in `:core:*` modules

## Consequences

**Positive:**

- Maps cleanly to Postman business domains and existing screen groupings
- Enables incremental wiring (Home first, Auth third, etc.) without monolith refactors
- Shared ViewModels work across all CMP targets

**Negative:**

- Requires discipline to avoid `core` becoming a dumping ground
- Feature extraction to Gradle modules needs planning to prevent cycles
- Some simple reads may skip use-case classes (documented policy)

## Related

- [architecture.md](../architecture.md)
- [dependency-rules.md](../dependency-rules.md)
