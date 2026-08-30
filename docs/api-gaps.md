# API Gaps and Ambiguities

Unresolved backend contract issues. **Do not silently fix these in client code.** Track status until verified or resolved.

Status values: `Open` | `Verified from backend source` | `Resolved by backend update` | `Client compatibility workaround`

---

## Gap 1 — `category_slug` type inconsistency

| Field | Status |
|-------|--------|
| **Status** | Client compatibility workaround |
| **Issue** | Taxonomy uses string slugs (`aa-1-2-3-4`). Query params use string slugs. Request/response examples also show `"category_slug": 1` and `"category_slugs": [1]`. |
| **Client impact** | Domain uses `CategorySlug` as `String`. `FlexibleCategorySlugSerializer` accepts int/string at DTO boundary in `:feature:marketplace`. |
| **Phase 5 handling** | Implemented in product/shop DTOs. Live production probe did not re-verify numeric slugs; Postman examples drive tests. |

---

## Gap 2 — Product `active` vs `confirmed` behavior

| Field | Status |
|-------|--------|
| **Status** | Client compatibility workaround |
| **Issue** | Create/update examples send `active=true`; sample responses show `active=false`, `confirmed=false`. Moderation/reapproval rules in descriptions. |
| **Client impact** | Preserve `active` and `confirmed` as distinct states. Map via `ProductPublicationState`. Always trust response over request. |
| **Phase 8 handling** | Create/Update repositories store response flags only. `SetProductActiveUseCase` blocks publish when local `confirmed=false`. |

---

## Gap 13 — Cities API lacks province hierarchy

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Public `GET /cities` returns flat `{ id, slug, name }`. CreateStore UI uses province → city cascade; no `province_id` on city objects. |
| **Client impact** | Phase 4 loads real cities into city dropdown; province selector remains mock-only. All API cities shown when province is selected. |
| **Phase 4 handling** | Documented in [reference-data.md](reference-data.md). Resolve when backend adds province or nested geography. |

---

## Gap 14 — Category localized name nullability

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Postman examples include Persian `name`, but imported Shopify taxonomy may omit names before admin PATCH. |
| **Client impact** | Domain uses `localizedName: String?`; `displayName` falls back to `sourceTitle`. |
| **Phase 4 handling** | Nullable in DTO/domain; no forced non-null defaults. |

---

## Gap 15 — Duplicate category lookup routes

| Field | Status |
|-------|--------|
| **Status** | Workaround |
| **Issue** | `GET /categories/{slug}` and `GET /categories/slug/{slug}` return identical example payloads. |
| **Client impact** | Domain exposes one `getCategory(slug)`. Data uses **`GET /categories/{slug}`** as canonical. |
| **Phase 4 handling** | SEO alias route unused unless backend documents semantic difference. |

---

## Gap 16 — Category list lacks browse visuals

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | List tree nodes have no `icon_url` or marketing collage assets. Browse grid UI expects colors + dual CDN images. |
| **Client impact** | `CategoriesScreen` uses API slug/title with index-based visual fallbacks from `BrowseCategoryVisuals.kt`. |
| **Phase 4 handling** | Home API or enriched taxonomy list may replace fallbacks in Phase 5+. |

---

## Gap 5 — Missing response examples

| Field | Status |
|-------|--------|
| **Status** | Open |

Postman requests **without saved response examples** (15):

| Method | Path | Request name |
|--------|------|--------------|
| POST | `/api/v1/auth/register` | Register with referral code |
| POST | `/api/v1/admin/taxonomy/import` | Import Shopify taxonomy |
| PATCH | `/api/v1/admin/categories/{slug}/name` | Update category Persian name |
| PUT | `/api/v1/admin/categories/{slug}/icon` | Upload category icon |
| PATCH | `/api/v1/admin/attributes/{id}/name` | Update attribute Persian name |
| PATCH | `/api/v1/admin/values/{id}/name` | Update value Persian name |
| GET | `/api/v1/categories/{slug}/return-reasons` | List return reasons |
| GET | `/api/v1/seller/shops/{id}/analytics` | Shop analytics dashboard |
| GET | `/api/v1/seller/shops/{id}/analytics/export` | Analytics CSV export |
| GET | `/api/v1/catalog/search` | Advanced catalog search |
| GET | `/api/v1/me/home/feed` | Custom personalized home feed |
| POST | `/api/v1/me/follows/shops/{id}` | Follow shop |
| GET | `/api/v1/me/follows/shops` | List followed shops |
| GET | `/api/v1/me/follows/shops/{id}` | Get followed shop |
| DELETE | `/api/v1/me/follows/shops/{id}` | Unfollow shop |

**Category attributes:** `GET /categories/{slug}/attributes` has an example but only `"attributes": []` — **non-empty item schema unverified (Phase 4 deferred).** Seller product create/update multipart has **no attributes fields** (Postman-verified) — Phase 8 does not invent attribute encoding.

---

## Gap 40 — Seller Product multipart & filters (Phase 8)

| Field | Status |
|-------|--------|
| **Status** | Verified from Postman collection |
| **Create/Update fields** | `title`, `category_slug`, `price`, `description`, `active`, `images` (file, repeat, ≤5) |
| **Image key** | `images` (not `image`) |
| **Update images** | Appended; total ≤ 5 (collection description) |
| **List filters** | `active`, `shop_id`, `category_slug` (+ cursor pagination); `confirmed` accepted as optional client query when used |
| **Open** | Exact server error reasons for publish-unapproved / plan limits; seller list item fields beyond Postman example; GET detail may omit `description` |

---

## Gap 3 — Follow shops vs favorite shops

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | `/me/follows/shops` and `/me/favorites/shops` both exist; semantics overlap. |
| **Client impact** | Keep separate repositories/APIs and `EngagementStateStore` maps. `FollowingScreen` stays mock. |
| **Phase 6 handling** | Follow mutations vs `ShopFavoriteRepository` remain distinct. Confirm product semantics before merging UI. |

---

## Gap 4 — Home `favorite_shops` naming

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Personalized home feed params include `favorite_shops`; description suggests discovery/high-performing shops, not necessarily user's saved favorites. |
| **Client impact** | DTO preserves backend field names; domain uses semantically correct names (e.g. `DiscoveryShops`). |
| **Phase 2+ handling** | Map in Home mapper after response schema verified. |

---

## Gap 6 — Plan `features` heterogeneous typing

| Field | Status |
|-------|--------|
| **Status** | Client compatibility workaround (Phase 9) |
| **Issue** | `features` JSON mixes `Boolean` and `String` (e.g. `"ranking_boost": "slight"`, `"contact_buttons": true`). |
| **Client impact** | Cannot use `Map<String, Boolean>`. Decode flexible JSON; map known keys to typed domain capabilities. |
| **Phase 9 handling** | `JsonObject` at DTO → `PlanCapabilitiesMapper` → typed `PlanCapabilities`. Unknown keys ignored. |

---

## Gap 7 — Payment status endpoint

| Field | Status |
|-------|--------|
| **Status** | Open — client workaround (Phase 9) |
| **Issue** | Collection documents purchase + payment callback; no dedicated client payment-status query endpoint. |
| **Client impact** | After payment redirect, client may need to refresh shop subscription state. |
| **Phase 9 handling** | Verify via `GET /seller/shops/{id}/subscription`. Never call provider callback from app. |

---

## Gap 8 — Phone normalization

| Field | Status |
|-------|--------|
| **Status** | Open (Phase 3 client workaround) |
| **Issue** | Examples use `9123456789`; filters use `0912`. Canonical format not specified. |
| **Client impact** | UI keeps `09xxxxxxxxx`; `PhoneMapper.toApiPhone()` strips leading `0` for auth requests. |
| **Phase 3 handling** | Documented in `:feature:auth`; verify with backend integration tests. |

---

## Gap 10 — Web / Desktop persistent session

| Field | Status |
|-------|--------|
| **Status** | Partially resolved (Desktop); Open (Web) |
| **Issue** | JS/Wasm use `InMemorySecureSessionStorage` — sessions lost on restart. Desktop now uses AES-GCM encrypted files under the user config directory (`JvmSecureSessionStorage`). |
| **Client impact** | Web users must re-login after refresh. Desktop credentials persist encrypted at rest. |
| **Phase 12 handling** | Web: intentional no localStorage bearer (P12-001). Desktop: encrypted file store (not OS Keychain yet). |

---

## Gap 11 — Profile PUT partial update semantics

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Whether omitted null fields clear username/email vs leave unchanged is not documented. |
| **Client impact** | Profile editor sends explicit null for blank fields; confirm backend behavior. |
| **Phase 3 handling** | Monitor integration test results before changing mapper. |

---

## Gap 12 — Username / email nullability on `/auth/me`

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Postman examples sometimes omit `username` or `email`. |
| **Client impact** | Domain uses `String?`; UI shows empty string placeholders. |
| **Phase 3 handling** | No crash on null; revisit when backend schema confirmed. |

---

## Gap 9 — Currency

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Fields `price`, `price_amount`, `price_paid`, `amount` with no explicit currency in transport contract. |
| **Client impact** | Do not assume IRR or any currency in shared domain without product confirmation. |
| **Phase 5 handling** | `formatMarketplacePrice(Long)` displays raw amounts; domain stores `priceAmount: Long`. |
| **Phase 9 handling** | Plan `price_amount` stored as `Long`; UI toman formatting is display-only. Currency unit remains **Open**. |

---

## Gap 41 — Phase 9 commercial contract gaps

| Field | Status |
|-------|--------|
| **Status** | Open (unless noted) |
| **Currency** | Open — see Gap 9 |
| **Complete feature key schema** | Partial — typed keys from Postman examples + `advanced_analytics`; others ignored |
| **max_shops vs per-shop subscription** | Open — field mapped; create-shop not limited client-side |
| **payment_url callback path** | Open inconsistency — examples use `/payments/callback` while API is `/api/v1/payments/callback`; client uses returned URL unchanged |
| **App payment return / deep link** | Open — none documented; resume + manual verify |
| **Billing history / invoices** | Open — no API; UI payments list empty |
| **Cancel / downgrade to Free** | Open — no API; actions not faked |
| **Expired/canceled subscription statuses** | Open — only `active` + `Unknown(raw)` |
| **Referral credit statuses/sources** | Partial — `available` / `referral_referrer` known; else Unknown |
| **Credit stacking with early renewal** | Open — refresh subscription after apply; no client math |
| **Process-death payment recovery** | Open — in-memory pending only |

---

## Gap 42 — Phase 10 seller analytics & boosts

| Field | Status |
|-------|--------|
| **Status** | Open (unless noted) |
| **GET `/seller/shops/{id}/analytics` response** | **Open — UNRESOLVED, not invented.** No Postman example. Dashboard DTO not implemented. |
| **Metric IDs / `available_metrics` / `locked_metrics`** | Open — not mapped |
| **Timeseries / charts schema** | Open |
| **Complete period set** | Partial — `7d` / `30d` verified; `90d`/`today`/`1y` not added |
| **CSV columns / header row** | Open — bytes passed through, not parsed |
| **CSV Content-Disposition / charset** | Partial — filename extracted as untrusted; sanitized in Presentation |
| **`advanced_analytics` type** | Verified Boolean in Phase 9 `PlanCapabilities`; UX precheck only |
| **Non-empty boost list item schema** | Open — `Unmapped(count)` only; no `PlacementBoost` fields |
| **Boost duration allowed set** | Open — `days` is required `Int`; example `7` is not policy |
| **Boost pricing / `GET /boost-prices`** | **Open — UNRESOLVED, no client invention.** Postman `50000` is payload shape. `CreateBoostViewModel.submit()` does not POST. |
| **`price_paid` server validation** | Open — backend may trust client amount (security gap) |
| **Currency for `price_paid`** | Open — see Gap 9 |
| **Boost eligibility (publication, plan gate)** | Open — not invented |
| **Boost history / cancel / edit** | Open — no APIs |
| **Boost payment gateway** | Open — not in Phase 10 |

See [seller-analytics-and-boosts.md](seller-analytics-and-boosts.md).

---

## Gap 43 — Phase 11 admin and CMS contracts

| Field | Status |
|-------|--------|
| **Admin comment discovery/list** | **MISSING / UNRESOLVED.** Only `PATCH /admin/comments/{id}/confirm` exists. The client implements confirm-by-known-ID and does not derive an admin queue from public comments. |
| **Taxonomy mutation/import responses** | **UNRESOLVED.** Import, category name/icon, attribute name, and value name requests have no verified payload schema. `AdminTaxonomyApi` decodes empty success envelopes and invalidates public taxonomy. |
| **Plan `features` PATCH** | **UNRESOLVED — MERGE vs REPLACE.** When features are edited, the client sends the complete raw heterogeneous object, preserving unknown keys. When untouched, it omits `features`. |
| **City delete `CityInUse` reason** | **Client compatibility workaround.** The documented 409 uses non-specific `reason=global`; the city-delete ViewModel maps `AppError.Conflict` to `CityInUse` by endpoint context. |
| **Free Plan delete reason** | **Open / client workaround.** No stable structured reason is verified. The client blocks normalized `slug == free` before transport and maps delete Conflict to `FreePlanCannotBeDeleted` (with legacy message fallback). |
| **HTML rich rendering** | Intentional limit: only the `AllowlistHtmlSanitizer`/`SafeHtml` subset is supported. Images, tables, styles, and embeds require an explicit future contract and renderer extension. |

See [admin-and-cms.md](admin-and-cms.md).

---

## Gap 17 — Public shop detail visual fields

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | `GET /shops/{id}` Postman payload lacks cover, avatar, wordmark, brand color, rating, review count, collections, nav chips expected by `StoreMock`. |
| **Client impact** | `StoreScreen` maps API title/slug/categories/share URL; placeholders for visual-only fields. |
| **Phase 5 handling** | Documented in [public-marketplace.md](public-marketplace.md). Resolve when backend enriches public shop projection or separate media endpoint exists. |

---

## Gap 18 — Home section item schemas

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | `GET /home` Postman has envelope; live probe to `vitran.ir` returned 404. Non-empty item shapes for `featured`, `popular`, `categories`, `following`, `personal` unverified. |
| **Client impact** | `HomeApi` + `HomeFeed` counts implemented; `HomeScreen` sections remain mock until schemas captured. |
| **Phase 5 handling** | `HomeDtos` stores section arrays as `JsonElement`; `HomeFeed.itemsVerified = false`. |

---

## Gap 19 — Catalog search response

| Field | Status |
|-------|--------|
| **Status** | UNRESOLVED — NOT INVENTED |
| **Issue** | `GET /catalog/search` has no saved Postman response example; live probe unavailable. |
| **Client impact** | `CatalogFilters` / `CatalogSort` domain request models only. No `searchCatalog()` API or response DTOs. |
| **Phase 5 handling** | Defer to backend verification. Simple product search uses `GET /products/search` instead. |

---

## Gap 20 — Personalized home feed

| Field | Status |
|-------|--------|
| **Status** | UNRESOLVED — NOT INVENTED |
| **Issue** | `GET /me/home/feed` has no response example in Postman. |
| **Client impact** | Not implemented. `HomeViewModel` uses public `/home` only. |
| **Phase 5 handling** | Implement only after authenticated probe captures schema. |

---

## Additional notes

- **Register with referral:** Same endpoint as register (`POST /auth/register`) with optional `referral_code` — one request model, not duplicate HTTP methods.
- **Login 403:** Phone verification required — business outcome with `temp_token`, not generic error.
- **Shop create session mutation:** `data.tokens.access_token` updates JWT roles — session owner is `:core:session`, not Auth ViewModel.

---

## Gap 21 — Follow response schemas

| Field | Status |
|-------|--------|
| **Status** | UNRESOLVED — NOT INVENTED |
| **Issue** | All four follow endpoints lack saved Postman response examples. List description mentions `data.followed_shops` but no item shape. |
| **Client impact** | `FollowRepository.setFollowed` only. No `FollowedShop` DTO. `FollowingScreen` remains mock. |
| **Phase 6 handling** | POST/DELETE implemented; list/status not invented. |

---

## Gap 22 — No favorite / wishlist status GET

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | No `GET .../{id}` for favorite shop or wishlist product. Public product/shop details have no `is_favorite`. |
| **Client impact** | `SaveStatus` / `FavoriteShopStatus` / `FollowStatus` start as `Unknown`. UI must not treat Unknown as false. |
| **Phase 6 handling** | Optimistic store after successful mutation only. |

---

## Gap 23 — Contact redirect / webhook payloads

| Field | Status |
|-------|--------|
| **Status** | UNRESOLVED — NOT INVENTED |
| **Issue** | Contact description mentions redirect and webhook routing; Postman example is WhatsApp only. |
| **Client impact** | `ContactRoute.WhatsApp(url) \| Unsupported(rawType)` only. |
| **Phase 6 handling** | Unknown `routed_via` maps to `Unsupported`. |

---

## Gap 24 — Favorite 409 `reason`

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Postman describes 409 already-favorited with no `FieldError.reason` example. |
| **Client impact** | Keep `AppError.Conflict`. Do not match Persian/English message text. |
| **Phase 6 handling** | Optimistic rollback; do not map 409 to Favorited. |

---

## Gap 25 — Public wishlist 403 body

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Public GET returns 403 when private; body shape unverified. |
| **Client impact** | Repository maps `AppError.Forbidden` → `PublicWishlistResult.Private` (not session forbidden). |
| **Phase 6 handling** | 404 stays `NotFound`. |

---

## Gap 26 — Analytics per-event context and `category_slug`

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Shop analytics example uses `"category_slug": 1` (int). Per-event extra fields (search query, etc.) unverified. |
| **Client impact** | DTO `categorySlug: String?`; do not emit `search` / `click_category` until payload verified. |
| **Phase 6 handling** | Isolated at analytics DTO; domain `CategorySlug` remains String. |

---

## Gap 27 — Wishlist frontend share URL

| Field | Status |
|-------|--------|
| **Status** | UNRESOLVED — NOT INVENTED |
| **Issue** | Backend returns `share_slug`; no documented storefront URL. |
| **Client impact** | Do not invent `https://vitran.ir/wishlist/{slug}`. Share PDP/store only when `ShopDetails.shareUrl` or in-app route exists. |
| **Phase 6 handling** | `WishlistShareSettings` stores slug only. |

---

## Gap 28 — Review metadata

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | List items are `id`, `product_id`, `user_id`, `rating`, `comment` only. |
| **Client impact** | Hide author, date, histogram, helpful, title, variant on PDP. |
| **Phase 6 handling** | `ProductReview.authorUserId` kept; not displayed. |

---

## Gap 29 — Public comment metadata

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Public list is `id`, `title`, `confirmed` only. |
| **Client impact** | No description/author/date on public comments. Submitted comments stay pending, never appended. |
| **Phase 6 handling** | No shop-comments UI invented. |

---

## Gap 30 — Seller `category_slugs` numeric vs taxonomy string slugs

| Field | Status |
|-------|--------|
| **Status** | Open / blocking |
| **Issue** | Postman seller create/update/response use `"category_slugs": [1]` (numbers). Public taxonomy exposes only string slugs (`aa-1-2-3-4`) with no numeric CategoryId. |
| **Client impact** | Transport uses `List<Long>`. Create Store does **not** invent IDs; sends empty list. Do not introduce `CategoryId` until taxonomy exposes mappable IDs. |
| **Phase 7 handling** | Documented in [seller-shop-management.md](seller-shop-management.md). |

---

## Gap 31 — Seller GET-by-ID detail completeness

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Postman GET `/seller/shops/{id}` example only shows `id`, `slug`, `active`, `confirmed`. Create/update responses include more fields. |
| **Client impact** | `SellerShopDetails` keeps enrichment fields nullable. Edit form may be under-populated from GET alone. |
| **Phase 7 handling** | Do not merge public shop data to fabricate missing fields. |

---

## Gap 32 — Shop `type` allowed values

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Postman only proves `"retailer"`. No enum list. |
| **Client impact** | Domain keeps `type` as `String`; Create Store sends `"retailer"` as the only verified default. |
| **Phase 7 handling** | No invented wholesaler/manufacturer enum. |

---

## Gap 33 — Seller create/update field nullability and PATCH clearing

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Postman examples do not prove requiredness or how omitted/`null`/`""` clear optional social fields on PATCH. |
| **Client impact** | Client omits null optionals on create; update sends provided strings without inventing clear semantics. |
| **Phase 7 handling** | Documented; no invented clearing rules. |

---

## Gap 34 — Fulfillment modes and API-key capability errors

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Sample proves `manual`/`redirect` only. API-key 403 capability reason unverified. |
| **Client impact** | Unknown modes → `FulfillmentMode.Unknown`. Capability-specific 403 not mapped until verified. |
| **Phase 7 handling** | No plan-slug hard-coding. |
