# Seller Product Management

Phase 8 — seller-owned product CRUD, multipart create, publication state, and multiplatform image picking. **Presentation in this pass wires Create Product only**; list/edit/detail screens remain for a later UI pass. Data APIs for all Postman seller-product endpoints are implemented.

## 1. Seller Product ownership

Products belong to shops owned by the authenticated seller. Path create uses `POST /seller/shops/{shopId}/products`. The client selects a `ShopId` from `SellerShopRepository`; the backend remains authoritative for ownership.

## 2. Seller vs Public Product APIs

| Concern | Public (`PublicProductApi`) | Seller (`SellerProductApi`) |
|---------|----------------------------|-----------------------------|
| List/detail | Marketplace catalog | Own products including `confirmed=false` |
| Auth | `AuthMode.None` | `AuthMode.Required` |
| DTOs | `ProductListItemDto` / `ProductDetailsDto` | `SellerProductListItemDto` / `SellerProductDto` |
| Pending products | May 404 | Must load |

Never use the public product endpoint to edit or manage pending products.

## 3. Product domain models

Package: `com.vitran.shop.feature.seller.product.domain.model`

- `SellerProductSummary` — list projection (`id`, `shopId`, `title`, `active`, `confirmed`, `publicationState`)
- `SellerProductDetails` — management detail
- `SellerProductImage` + `ProductImageId`
- `CreateProductCommand` / `UpdateProductCommand`
- Reuses marketplace `ProductId`, `ShopId`; taxonomy `CategorySlug`

## 4. ProductPublicationState

```text
!active && !confirmed → PendingApproval
 active &&  confirmed → Live
!active &&  confirmed → ApprovedHidden
 active && !confirmed → Inconsistent
```

Centralized in `productPublicationState(active, confirmed)`. Do not scatter raw booleans in Composables.

## 5. Moderation lifecycle

- `confirmed` — admin approval (seller cannot set `true`)
- `active` — seller visibility (`PATCH .../active`)
- Public live requires both `true`
- Create/Update responses are authoritative for both flags (request `active=true` may still return inactive pending)

## 6. Seller Product list

`GET /seller/products` with cursor pagination and optional `shop_id`, `active`, `confirmed`, `category_slug`. Implemented in Data; no list UI yet.

## 7. Create Product

Multipart fields (Postman-verified): `title`, `category_slug`, `price`, `description`, `active`, repeated `images` (≤5).

UI: [`CreateProductScreen`](../shared/src/commonMain/kotlin/com/vitran/shop/ui/screens/CreateProductScreen.kt) + `CreateProductViewModel`. Unsupported Shopify-admin fields (SKU, inventory, variants, SEO, compare-at) remain local-only and are **not** sent.

## 8. Update Product

`PATCH /seller/products/{id}` multipart — same text fields + optional appended `images`. Data + `UpdateProductUseCase` (public cache invalidation). No edit UI yet.

## 9. Active / publish endpoint

`PATCH /seller/products/{id}/active` JSON `{ "active": bool }`. `SetProductActiveUseCase` blocks client-side publish when `localConfirmed == false`.

## 10. Delete Product

`DELETE /seller/products/{id}` — no automatic retry; removes from `SellerProductStateStore`; invalidates public product cache.

## 11. Image deletion

`DELETE /seller/products/{id}/images/{imageId}`. Local-only selected images are removed without API calls.

## 12. Multipart architecture

Built with Ktor `MultiPartFormDataContent` / `formData` on the shared `HttpClient`. No manual boundaries. `Accept: application/json` default remains; Content-Type is multipart for these requests.

## 13. Shared file abstraction

`com.vitran.shop.core.platform.file.SelectedFile` — name, contentType, sizeBytes, suspending `readBytes()`. ByteArray-backed for modest product images (document Phase 11 streaming need).

## 14. Platform file picker strategy

`ImagePicker.pickImages(maxCount)` — cancel → empty list. Platform DI; Presentation depends on interface only.

## 15. Android implementation

`HostedImagePicker` (+ `AndroidSelectedFileFactory` for Uri→SelectedFile). Bind from Activity Result / Compose host. No broad storage permission required when using system picker.

## 16. iOS implementation

`HostedImagePicker` registered in iOS `platformModule`. Bind from Photos/document picker in UI when available. Compiles unbound (cancel = empty).

## 17. Desktop implementation

`JvmFileImagePicker` — AWT `FileDialog`, converts to `SelectedFile` inside JVM source set.

## 18. Web/Wasm implementation

- JS: `BrowserImagePicker` (`<input type=file accept=image/*>`)
- Wasm: `HostedImagePicker` (bind from DOM when needed)

## 19. Image validation

Client enforces max images from `ShopEntitlements.limits.maxImages` when known (fallback 5). MIME passed when known. No invented byte-size limits. Backend remains authoritative for plan limits.

## 20. Binary logging / security

- `HttpRequestRetry` retries **GET/HEAD only** (including exception retries)
- Logging redacts multipart `images` bodies via `sanitizeLogMessage`
- Filenames are basenames only (`safeFileName`)
- Never log full local paths or raw image bytes

## 21. Category integration

Create uses taxonomy picker (`TaxonomyPickerViewModel`) → `CategorySlug` string in multipart. Gap 1: responses may return numeric slug; `FlexibleCategorySlugSerializer` at DTO boundary.

## 22. Category attribute integration

**Not implemented.** Postman product create/update has no attributes fields; `GET /categories/{slug}/attributes` schema still empty (Gap 5).

## 23. Attribute serialization

**N/A — not invented.**

## 24. category_slug contract

**Client compatibility workaround (Gap 1):** Domain `CategorySlug` is String. Multipart sends selected slug as-is. JSON decode accepts string or number via `FlexibleCategorySlugSerializer`.

## 25. Mutation retry policy

Create / Update / Delete / image delete are **not** auto-retried on transport failure. Manual refresh reconciles state.

## 26. Seller / public cache invalidation

- `SellerProductStateStore` — user-scoped; clears on session invalidation
- `ProductPublicCacheInvalidator` → `ProductRepository.invalidateProduct`
- Create does **not** insert into public cache
- Update / set-active / delete use cases invalidate public detail cache

## 27. UI integration

| Screen | ViewModel | Real API | Fake removed | Picker | Publication |
|--------|-----------|----------|--------------|--------|-------------|
| CreateProductScreen | `CreateProductViewModel` | Create multipart ✅ | Fake save/upload delays removed | `ImagePicker` | Response-authoritative |
| List / Edit / Detail | — | Data ready | — | — | Deferred UI |

Preview fixtures in `CreateProductMocks` preserved for tooling.

## 28. Tests

- `ProductPublicationStateTest`
- `SellerProductApiRepositoryTest` (multipart, auth, authority, no-retry, delete, logout clear)
- `SellerProductUseCaseTest`
- `CreateProductViewModelTest`
- `SelectedFileTest` / logging sanitizer binary redaction

## 29. API gaps

See [`api-gaps.md`](api-gaps.md) Phase 8 updates (category_slug, create active, multipart fields, attributes deferred, image key `images`, append semantics).

## 30. Phase 9 capability integration points

Do not hard-code `max_products` / `max_images` from plan slugs. Keep validation in use cases / ViewModel thin so Phase 9 can inject server-driven capabilities. Map verified limit errors when backend reasons are known.
