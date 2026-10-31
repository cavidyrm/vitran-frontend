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

`GET /seller/products` with cursor pagination and optional `shop_id`, `active`, `confirmed`, `category_slug`. The store edit screen lists these rows. Opening a row goes to `/account/stores/{shopId}/products/{productId}` (`GET /seller/products/{id}`) and shows the create-product form read-only.

## 7. Create Product

Multipart fields (Postman-verified): `title`, exactly one `category_slug`, `price`, `description`, `active`, optional `compare_at_price`, optional `price_after_off` (must be lower than `price`), `status` (`draft`|`active`|`inactive`, default `draft`), optional `seo_title`, `seo_description`, `noindex`, optional `slug`, optional `attributes[<slug>]=<value_slug>`, repeated `images` (count from the shop plan, each file ≤ 5 MB). There is no variant model and no collection field on create.

UI: [`CreateProductScreen`](../shared/src/commonMain/kotlin/com/vitran/shop/ui/screens/CreateProductScreen.kt) + `CreateProductViewModel`. The basics card sends `slug` and `price_after_off`. Save-as-draft sends `status=draft` and `active=false`; publish sends `status=active` and `active=true`. The status card can also select `inactive`. The SEO card sends `seoTitle`, `seoDescription`, and `noindex` when the toggle is on. The first image is the cover; after create the client calls set-main when that image is not already `is_main`. The variants card is not shown. Attribute values are sent only when the caller supplies them; the form has no picker while `GET /categories/{slug}/attributes` returns an empty list. Admin review and the seller's own detail reuse this form read-only.

## 8. Update Product

`PATCH /seller/products/{id}` multipart — same text fields as create, including optional `slug`, `price_after_off`, `status`, and `attributes[...]`, plus appended `images`. Empty `seo_title` / `seo_description` / `compare_at_price` / `price_after_off` clears those fields. Omit `attributes[...]` to leave them unchanged. Data + `UpdateProductUseCase` (public cache invalidation). No edit UI yet. `category_slug` is required on update. Collection membership stays on the collection PATCH.

## 9. Active / publish endpoint

`PATCH /seller/products/{id}/active` JSON `{ "active": bool }`. `SetProductActiveUseCase` blocks client-side publish when `localConfirmed == false`.

## 10. Delete Product

`DELETE /seller/products/{id}` — no automatic retry; removes from `SellerProductStateStore`; invalidates public product cache.

## 11. Image deletion

`DELETE /seller/products/{id}/images/{imageId}`. Local-only selected images are removed without API calls. If the deleted image was the cover, the server promotes the remaining image with the lowest `sort_order`.

## 11b. Cover image

`PATCH /seller/products/{id}/images/{imageId}/main` has no body. The response includes `main_image` and `images[].is_main`. `SetProductMainImageUseCase` invalidates the public product cache. On the add-product form, moving an image to the front marks it as the cover; after a successful create the client sets that image as main when the response has not already done so. Cards use `main_image.url`, not `images[0]`.

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

Client enforces max images from `ShopEntitlements.limits.maxImages` when known (fallback 5) and rejects a file larger than 5 MB. MIME passed when known. Backend remains authoritative for plan limits. The PATCH description still says “total ≤ 5”; the image field says the max comes from the plan.

## 20. Binary logging / security

- `HttpRequestRetry` retries **GET/HEAD only** (including exception retries)
- Logging redacts multipart `images` bodies via `sanitizeLogMessage`
- Filenames are basenames only (`safeFileName`)
- Never log full local paths or raw image bytes

## 21. Category integration

Create uses taxonomy picker (`TaxonomyPickerViewModel`) → `CategorySlug` string in multipart. Gap 1: responses may return numeric slug; `FlexibleCategorySlugSerializer` at DTO boundary.

## 22. Category attribute integration

Create/update accept optional `attributes[<attribute_slug>]=<value_slug>` (repeat per attribute). `GET /categories/{slug}/attributes` still returns `"attributes": []`, so the form does not build a picker. The multipart builder sends the map when a command includes it.

## 23. Attribute serialization

Form field name is `attributes[<slug>]` with the value slug as the part body. One part per attribute. Omitted on update when the map is null.

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

See [`api-gaps.md`](api-gaps.md) Phase 8 updates (category_slug, create active, multipart fields, optional `attributes[<slug>]`, image key `images`, plan image cap, 5 MB per file).

## 30. Phase 9 capability integration points

Do not hard-code `max_products` / `max_images` from plan slugs. Keep validation in use cases / ViewModel thin so Phase 9 can inject server-driven capabilities. Map verified limit errors when backend reasons are known.
