# Reference Data — Location & Taxonomy

Phase 4 shared reference-data foundation. Public cities and category taxonomy for reuse across marketplace, seller, search, and admin flows.

## 1. Location ownership

**Module:** `:feature:location`  
**Package:** `com.vitran.shop.feature.location`

| Layer | Path |
|-------|------|
| Domain | `domain/model/`, `domain/repository/LocationRepository.kt` |
| Data | `data/remote/LocationApi.kt`, `data/repository/DefaultLocationRepository.kt` |
| DI | `di/LocationModule.kt` |
| Presentation | `presentation/CreateStoreLocationViewModel.kt` |

## 2. Taxonomy ownership

**Module:** `:feature:taxonomy`  
**Package:** `com.vitran.shop.feature.taxonomy`

| Layer | Path |
|-------|------|
| Domain | `domain/model/`, `domain/repository/TaxonomyRepository.kt` |
| Data | `data/remote/TaxonomyApi.kt`, `data/repository/DefaultTaxonomyRepository.kt` |
| DI | `di/TaxonomyModule.kt` |
| Presentation | `presentation/TaxonomyPickerViewModel.kt`, `CategoriesBrowseViewModel.kt` |

## 3. Why shared (not feature-specific)

Cities and taxonomy are **reference concepts** used by:

- Seller shop creation (city)
- Admin/product category pickers (taxonomy tree)
- Categories browse grid (root taxonomy titles/slugs)
- Future: home filters, catalog search, shop browse

They must not live in `:feature:shop`, `:feature:product`, or `:shared` mocks long term.

## 4. City domain model

```kotlin
@JvmInline value class CityId(val value: Long)
@JvmInline value class CitySlug(val value: String)

data class City(
    val id: CityId,
    val slug: CitySlug,
    val name: String,
)
```

## 5. City ID vs slug

| Type | Use |
|------|-----|
| `CityId` | Numeric server id (`GET /cities/{id}`) |
| `CitySlug` | Text slug (`tehran`, `GET /cities/slug/{slug}`) |

CreateStore city dropdown stores **`CitySlug.value`** as the selected option id.

## 6. Category domain model

**Tree node (list + nested children):**

```kotlin
data class CategoryNode(
    val slug: CategorySlug,
    val sourceTitle: String,
    val localizedName: String?,
    val isLeaf: Boolean,
    val children: List<CategoryNode>,
)
```

**Detail projection:**

```kotlin
data class CategoryDetails(
    val slug: CategorySlug,
    val sourceTitle: String,
    val localizedName: String?,
    val fullName: String?,
    val isLeaf: Boolean,
    val iconUrl: String?,
    val children: List<CategoryNode>,
)
```

Both expose `displayName = localizedName ?: sourceTitle`.

## 7. Category hierarchy

Repository canonical representation is an **immutable tree** (`List<CategoryNode>` roots). Mapping is recursive from `CategoryTreeNodeDto`; the repository does not flatten for storage.

Helpers: `findBySlug`, `collectLeafCategories` in `CategoryTreeHelpers.kt`.

## 8. Source title vs localized name

| API field | Domain |
|-----------|--------|
| `title` | `sourceTitle` (English / Shopify import) |
| `name` | `localizedName` (Persian; nullable) |

`displayName` prefers localized name; UI should not overwrite `sourceTitle` with Persian text.

## 9. `isLeaf` semantics

`isLeaf` comes from API `is_leaf` and is **authoritative**. Do not infer leaf state from `children.isEmpty()` alone.

## 10. Category detail metadata

Detail-only fields: `fullName` (breadcrumb text), `iconUrl` (remote URL when S3 configured). List tree nodes do not include these.

## 11. API endpoints (Phase 4 implemented)

| Method | Path | Auth | Status |
|--------|------|------|--------|
| GET | `/api/v1/cities` | None | Implemented |
| GET | `/api/v1/cities/{id}` | None | Implemented |
| GET | `/api/v1/cities/slug/{slug}` | None | Implemented |
| GET | `/api/v1/categories` | None | Implemented |
| GET | `/api/v1/categories/{slug}` | None | Implemented (canonical lookup) |
| GET | `/api/v1/categories/slug/{slug}` | None | Not used in client (SEO alias) |
| GET | `/api/v1/categories/{slug}/attributes` | None | **Deferred** |
| GET | `/api/v1/categories/{slug}/return-reasons` | None | **Deferred** |

## 12. Repository contracts

```kotlin
interface LocationRepository {
    suspend fun getCities(forceRefresh: Boolean = false): AppResult<List<City>>
    suspend fun getCityById(id: CityId): AppResult<City>
    suspend fun getCityBySlug(slug: CitySlug): AppResult<City>
}

interface TaxonomyRepository {
    suspend fun getCategoryTree(forceRefresh: Boolean = false): AppResult<List<CategoryNode>>
    suspend fun getCategory(slug: CategorySlug, forceRefresh: Boolean = false): AppResult<CategoryDetails>
}
```

## 13. In-memory caching

| Repository | Cached | Scope |
|------------|--------|-------|
| `DefaultLocationRepository` | City list | App singleton |
| `DefaultTaxonomyRepository` | Category tree + detail by slug | App singleton |

Synchronized with `Mutex`. Not persisted across process death.

## 14. Refresh behavior

- `forceRefresh = false`: return cache when present.
- `forceRefresh = true`: network fetch; on success replace cache; on failure return `AppResult.Failure` without clearing existing cache.

## 15. Selected-city ownership

**Not implemented.** No app-wide browsing city exists in current UI. Local province/city on CreateStore is form-scoped; province remains mock-only (see Gap 13).

## 16. Category attribute status

**UNRESOLVED — NOT INVENTED.** Postman example returns `"attributes": []` only. No typed `CategoryAttribute` model until backend verifies item schema.

## 17. Return reason status

**UNRESOLVED — NOT INVENTED.** No Postman response example. No `ReturnReason` model in Phase 4.

## 18. Known API gaps

See [api-gaps.md](api-gaps.md): Gap 1 (`category_slug`), Gap 5 (attributes item schema, return reasons), Gap 13 (city province), Gap 14 (localized name nullability), Gap 15 (duplicate category lookup), Gap 16 (list-level category icons).

## 19. DI organization

[`VitranKoin.kt`](../shared/src/commonMain/kotlin/com/vitran/shop/di/VitranKoin.kt):

```kotlin
locationModule,
taxonomyModule,
```

Registrations: `LocationApi`, `LocationRepository`, `TaxonomyApi`, `TaxonomyRepository`, ViewModels (`CreateStoreLocationViewModel`, `TaxonomyPickerViewModel`, `CategoriesBrowseViewModel`).

## 20. Tests

| Module | Tests |
|--------|-------|
| `:feature:location` | `LocationApiTest`, `DefaultLocationRepositoryTest`, `CreateStoreLocationViewModelTest` |
| `:feature:taxonomy` | `TaxonomyApiTest`, `DefaultTaxonomyRepositoryTest`, `CategoryTreeHelpersTest`, `TaxonomyViewModelsTest` |

All network tests use Ktor `MockEngine`. Run: `./gradlew :feature:location:jvmTest :feature:taxonomy:jvmTest`

## 21. Future reuse

Phase 5+ should import:

- `City`, `CitySlug`, `LocationRepository`
- `CategoryNode`, `CategorySlug`, `TaxonomyRepository`
- Presentation mappers in `:shared/ui/sections/reference/`

Do **not** duplicate city/category HTTP clients or DTOs in Home, Marketplace, or Seller modules.
