# API Contract Summary

Client-facing summary of the Vitran marketplace backend. **Source of truth:** [`postman/vitran-api.postman_collection.json`](postman/vitran-api.postman_collection.json) (154 requests, 141 `/api/v1` route templates). Home-screen field guide: [`frontend/home-screen/`](frontend/home-screen/).

**Client active version:** `/api/v1` only. `/api/v2` mirror exists in the collection but is not implemented in the client until an explicit migration phase.

---

## 1. API origin and versioning

| Concept | Value |
|---------|-------|
| Collection variable | `baseUrl` = **origin only** (no path suffix) |
| Local origin | `http://localhost:8080` |
| Production origin | `https://api.vitran.ir` |
| API prefix | `/api/v1` |
| **Wrong** | `https://api.vitran.ir/api` as origin |

Client configuration (`ApiEnvironment` in `:core:network`):

```text
apiBaseUrl = origin.trimEnd('/') + "/api/v1"
```

Health probes: `GET /health`, `GET /api/v1/health`, `GET /api/v2/health`.

---

## 2. Response envelope

All business responses use a global envelope:

```json
{
  "success": true,
  "message": "...",
  "code": 1,
  "data": {},
  "errors": []
}
```

| Field | Layer | Notes |
|-------|-------|-------|
| `success` | Transport | Boolean outcome flag |
| `message` | Transport | Human-readable (often Persian) |
| `code` | Transport | **Not a tiny enum** — see below |
| `data` | Transport | Payload; shape varies by endpoint |
| `errors` | Transport | Validation / field errors |

**Code field:**

- Collection description: `code = 1` success, `code = -2` validation error
- Examples also use HTTP-like values: `400`, `403`, `404`, `409` inside `code`
- Phase 2 must model `code` as `Int` (or flexible type), not a two-value enum

Implementation in `:core:network` — see [networking.md](networking.md):

- `ApiEnvelope<T>`, `ApiErrorDto`, `EmptyDataDto`
- `ApiRequestExecutor` → `AppResult<T>`
- `AppError` hierarchy in `:core:domain`

---

## 3. Authentication modes

Future network layer concept: `AuthMode` in `:core:domain`.

| Mode | Behavior | Examples |
|------|----------|----------|
| **None** | No Bearer token | `GET /cities`, `GET /shops`, `GET /plans`, static pages |
| **Required** | Bearer access token | `GET /auth/me`, `PUT /auth/profile`, seller CRUD, admin APIs, `GET /me/favorites/*`, `/me/profile/*`, `/me/persons`, `/me/matches` |
| **Optional** | Anonymous OK; auth may enrich response | `GET /api/v1/home`, `GET /home/screen`, `GET /home/sections/{key}`, `POST /api/v1/events`, `POST /products/{id}/contact`, public wishlist share |

Postman saves tokens from auth responses into collection variables: `accessToken`, `refreshToken`, `tempToken`.

---

## 4. Authentication tokens

| Token | Purpose | Storage (future) |
|-------|---------|------------------|
| **access_token** | Authorize authenticated requests | Secure platform storage |
| **refresh_token** | Renew access token | Secure platform storage |
| **temp_token** | Phone verification workflow | Secure platform storage |
| **otp_code** | Dev-only OTP when `EXPOSE_OTP_IN_RESPONSE=true` | Never persist in production |

Tokens must **never** be stored in Room or plain preferences.

Token payload shape (from examples):

```json
{
  "tokens": {
    "access_token": "...",
    "refresh_token": "...",
    "expires_at": "2026-06-09T12:30:00Z"
  }
}
```

---

## 5. User roles

Backend roles (JWT / profile):

| Role | Notes |
|------|-------|
| `user` | Default shopper |
| `admin` | Platform admin |
| `super_admin` | Highest privilege |

Hierarchical: `user` < `admin` < `super_admin`. Signup assigns `user`. Shop ownership is `shops.owner_id`, not a role — see `GET /auth/me` `shop_types` (`[]` = viewer). Legacy JWT/profile values `customer` and `seller` still map to `user` on the client so old tokens do not crash.

**Admin assignment rules (from collection):**

- Only `super_admin` may assign roles on `PATCH /admin/users/{id}` (client omits `roles` for a normal `admin`)
- `super_admin` cannot be granted to arbitrary new users via normal admin user-update API
- Existing `super_admin` users must retain `super_admin` in roles array when roles are sent

Client-side role checks are **UX/navigation only**. Backend authorization is authoritative.

---

## 6. Pagination modes

**Never combine `page` and `cursor`.**

### Cursor mode (recommended for infinite scroll)

Request: `per_page=20` (or `limit`, default 20, max 100), optional `cursor=<opaque>`

Response:

```json
{
  "per_page": 20,
  "has_more": true,
  "next_cursor": "42",
  "results": []
}
```

- `next_cursor` is **opaque** — model as `String?` in client even if example looks numeric
- No `total` in cursor mode

### Page mode (admin tables with totals)

Request: `page=1`, `per_page=20`

Response adds: `per_page`, `has_more`, `results`. Optional: `page`, `last_page`, `from`, `to`, `total`.

When `page`, `last_page`, or `total` are omitted, the client infers them (`page` defaults to 1; `last_page` / `total` from the current page size and `has_more`).

**Client guidance:**

- Shopper infinite-scroll screens → cursor pagination
- Admin table screens needing totals → page pagination

---

## 6a. Server-driven home

`GET /home/screen` and `GET /home/sections/{key}` always send `platform` (`ios` | `android` | `web`) and `app_version` (for example `1.0.0`). The same values are accepted as headers `X-App-Platform` and `X-App-Version`. Rails limited to a platform or a minimum app version are omitted when those values are missing or do not match. A direct section GET then returns **404**.

`GET /home` and `GET /me/home/feed` stay in the collection for old builds. The shopper home calls `GET /home/screen` only.

Guest calls omit `requires_auth` rails (`for-you`, `following`, favourites, `picked-for-you` / `product_matches`). A direct GET of those rails without a token returns **401**. `for-you` is one page (`has_more` is always false). Rails backed by `featured_products`, `personal_products`, `product_matches`, `categories`, and `banners` are also one page: a stale `cursor` returns an empty last page, not page one again. `popular` paginates; its cursor is opaque (example `pop:12`).

`POST /events` (optional JWT) feeds those rails. `view_product` ranks **popular** (last 30 days, per city) and, when sent with a token and `category_slug`, teaches **for-you**. From a home rail the body also includes `metadata` `{ from, section_key, position }` (`position` is the 1-based card index). `click_category` sends `category_slug`. `search` sends `search_query`.

`data.screen` has `revision`, `city_id`, and `sections` in order. Each section:

| Field | Values |
|-------|--------|
| `key` | Stable rail id (replaces the old `slug`) |
| `type` | `products` \| `shops` \| `categories` \| `banners` |
| `layout` | `horizontal_rail` \| `grid` \| `chips` \| `hero` |
| `items` | `{ per_page, has_more, next_cursor?, total?, results }` |
| `more` | `{ path, cursor_param }` — `path` has no `/api/v1` prefix, e.g. `/home/sections/popular` |
| flags | `requires_auth`, `hide_when_empty`, `empty`, optional `subtitle`, optional `category_slug` |
| `tracking` | `{ section_key, source, position? }` — rail fingerprint for `POST /events` metadata |

Skip a section when `type` or `layout` is unknown, and when `empty` is true and `hide_when_empty` is true. Do not throw.

`category_slug` is present when a product rail is limited to that category and its subcategories (for example `bt`). See-all still pages `GET /home/sections/{key}`. `GET /products?category_slug=` matches one exact category and is not a substitute.

Item shapes (illustrative fields; unknown keys ignored):

- **products** — `id` required; `title`, `slug`, `category_slug`, `shop_id`, `price`, `compare_at_price`, `main_image` optional. Saved examples are full product cards; extra keys are ignored.
- **categories** — `slug`, `name` or `title`, `level`, `is_leaf`
- **banners** — `id`, `image_url`, optional `title`, `action: { type, value? }`
- **shops** — no saved item example; require `id` plus `title` or `name`, otherwise skip the rail

Banner `action.type`: `open_product`, `open_shop`, `open_category`, `open_section`, `open_url`, `none`. Unknown types show the image and do nothing on tap.

Load more only when `more` is present and `items.has_more` is true:

```text
GET /api/v1{more.path}?{cursor_param}={next_cursor}&limit={per_page}&city_id=&platform=&app_version=
```

Append `data.section.items.results`. `total` is omitted once a cursor is sent. Stop when `has_more` is false.

Admin home CRUD (`/admin/home/sections`, `/admin/home/banners`) is called from `/admin/home` for admin and super-admin. List is `data.home_sections[]`, one section is `data.home_section` (`id`, `slug`, `source`, `item_kind`, `layout`, `title`, optional `subtitle`, `item_limit`, `sort_order`, `active`, `audience`, `hide_when_empty`, `platforms`, optional `category_slug`, `min_app_version`, `starts_at` / `ends_at`). `slug` is sent only on create and stays fixed after that. Optional `subtitle` is sent on create and update; an empty string clears it. `category_slug` is sent only for `latest_products`, `popular_products`, and `featured_products` (the category and its subcategories; `""` means all categories). Other sources omit it, unless a saved slug must be cleared with `""` so a source change does not return 400. Create returns **409** when `slug` already exists; that error is shown on the slug field. A 400 on `category_slug` is shown on the category field. Deleting a section needs a super-admin. Banners are edited only when `source` is `banners`. A banner `image_url` must be `http` or `https`. Banner actions are `none`, `open_product`, `open_shop`, `open_category`, `open_section`, and `open_url`. The response nests `action: { type, value }`.

---

## 7. Endpoint business domains

See [api-feature-map.md](api-feature-map.md) for full endpoint index.

| Domain | Postman folders |
|--------|-----------------|
| Auth | Auth (includes `POST /auth/check-phone`) |
| Account | Users, Profile & Persons — Me, Product Matches — Me |
| Session | Auth refresh, shop-create token mutation |
| Referral | Referrals |
| Location / Cities | Cities |
| Taxonomy | Taxonomy |
| Home | Home |
| Marketplace Shops | Shops — Public, Shop collections (public reads) |
| Marketplace Products | Products — Public |
| Catalog / Search | Catalog search, product search |
| Engagement | Favorites — Me, Shop follows — Me, Wishlists — Public, reviews, comments, contact, `/events`, shop analytics |
| Product Reviews | Products — Public (reviews) |
| Shop Comments | Comments |
| Seller Shops / Products / Analytics / Boosts | Shops — Seller, Shop collections (seller writes), Products — Seller, Boosts — Seller |
| Plans / Payments | Plans — Public/Admin, Payments — Public |
| CMS | Static Pages — Public/Admin |
| Admin | Admin — Users, Shops — Admin, Products — Admin |

---

## 8. Multipart / file endpoints

| Operation | Method / path | Notes |
|-----------|---------------|-------|
| Create seller product (with images) | `POST /seller/shops/{id}/products` | multipart; optional `compare_at_price`, `price_after_off` (`< price`), `status` (`draft`\|`active`\|`inactive`, default `draft`), `seo_title`, `seo_description`, `noindex`, `slug`, `attributes[<slug>]`; images repeat, max count from the shop plan, each file ≤ 5 MB. No variants. No collection field. |
| Update seller product | `PATCH /seller/products/{id}` | multipart; same optional fields as create, including `slug`. Empty `compare_at_price` / `price_after_off` clears. Omit `attributes[...]` to leave them unchanged. Collection membership is a collection PATCH, not this request. |
| Set product cover | `PATCH /seller/products/{id}/images/{imageId}/main` | no body; response includes `main_image` and `images[].is_main` |
| Upload category icon | `PUT /admin/categories/{slug}/icon` | multipart |
| Import Shopify taxonomy | `POST /admin/taxonomy/import` | multipart JSON files |
| Analytics export | `GET /seller/shops/{id}/analytics/export` | file response (CSV) |

**Multiplatform rule:** Domain/common code must not depend on `android.net.Uri`, `java.io.File`, or `NSURL`. Platform pickers convert to a shared upload abstraction (seller/admin phases).

---

## 9. Public vs seller vs admin APIs

| Layer | Path patterns | Auth |
|-------|---------------|------|
| **Public marketplace** | `/shops`, `/products`, `/catalog`, `/plans`, `/static-pages`, `/cities`, `/categories` | Mostly none |
| **Authenticated consumer** | `/auth/me`, `/auth/profile`, `/me/favorites/*`, `/me/follows/*`, `/me/wishlist/*`, `/me/home/feed`, `/me/profile/*`, `/me/persons`, `/me/matches` | Required |
| **Seller** | `/seller/shops/*`, `/seller/products/*`, `/me/referral` | Required (`user`+ token; mutations still require shop ownership) |
| **Admin** | `/admin/*` | Required (admin/super_admin) |

Do not build one giant `VitranApi` with 100+ methods — split by service boundary (see api-feature-map).

---

## 10. Important state machines

### Shop lifecycle

```text
Seller creates shop
    → active=false, confirmed=false
Admin confirms (PATCH /admin/shops/{id}/confirm)
    → active=true, confirmed=true → publicly visible
Seller updates shop
    → may reset active=false, confirmed=false (re-approval)
```

Future domain concept: `ShopPublicationState` — do not scatter raw booleans in Composables.

**First shop creation:** may return `data.tokens.access_token` with an updated JWT. Session is updated via `:core:session`, not Auth ViewModel. Shop ownership is not a user role.

### Product lifecycle

```text
Seller creates/edits product → confirmed=false
Admin confirms → confirmed=true
Seller controls active visibility
Public when active && confirmed
```

Future states: `PendingApproval`, `ApprovedHidden`, `Live`.

`active` and `confirmed` are **distinct** backend states — see [api-gaps.md](api-gaps.md) Gap 2.

---

## 11. Subscription ownership

Subscription is **per shop**, not per user:

```text
User → Shop A (Plan X), Shop B (Plan Y)
```

Routes: `/seller/shops/{shopId}/subscription`, `.../subscription/purchase`

Do **not** model `CurrentUser.plan` as authoritative. Use server-provided plan data per shop.

---

## 12. Plan capability behavior

Public plan slugs: `free`, `starter`, `growth`, `business`.

Plan fields from backend: `price_amount`, `duration_days`, `max_products`, `max_images`, `max_shops`, `features`, `active`, `sort_order`.

`features` is heterogeneous JSON (booleans and strings) — see Gap 6. **Never hard-code** plan limits in Compose; use server values.

Referral credit: first paid purchase by referred user grants Starter credit; apply via `/me/referrals/credits/{id}/apply` on Free/Starter shops.

---

## 13. Authentication workflow behavior

Login is not binary:

| Outcome | HTTP | Payload |
|---------|------|---------|
| Authenticated | 200 | `data.tokens` |
| Phone verification required | 403 | `temp_token`; `otp_code` only when `EXPOSE_OTP_IN_RESPONSE=true` |

Represent as **business outcome**, not generic forbidden exception.

`POST /auth/check-phone` (no SMS) returns `status` / `next_step` so the client can route to register, login, or verify without 409/404/429 loops.

`POST /auth/logout` uses Bearer access token only — **no JSON body**.

Register: pending signup in Redis (no `users` row until verify). Single `POST /auth/register` with optional `referral_code` — do not duplicate HTTP methods for referral variant.

Password recovery: `POST /auth/forgot-password` → `POST /auth/reset-password` (OTP + new password).

---

## 14. Known incomplete contracts

See [api-gaps.md](api-gaps.md) for ambiguities and missing response examples. Do not invent DTOs for undocumented responses.

---

## 15. Admin and CMS contract notes

Phase 11 admin endpoints use `AuthMode.Required`; public `ContentApi` static-page reads use `AuthMode.None`.

- City create/update bodies are `{ slug, name }` only.
- Moderation confirm requests for shops, products, and comments have empty bodies.
- Admin comment discovery is unavailable; only confirm-by-ID is implemented.
- Taxonomy import multipart keys are `categories` and `attributes`; category icon uses `image`.
- Taxonomy mutation/import response schemas are unresolved and decoded as empty success envelopes.
- Admin plan `features` is heterogeneous JSON. PATCH merge-vs-replace is unresolved, so an edited feature set is sent as a complete object with unknown keys preserved; otherwise `features` is omitted.
- Public static-page HTML crosses the domain boundary as `HtmlContent`, is sanitized by `AllowlistHtmlSanitizer`, and is rendered by Compose `SafeHtml`.
- POST/PATCH/PUT/DELETE admin mutations are not automatically retried.

See [admin-and-cms.md](admin-and-cms.md) and ADRs [0012](decisions/0012-admin-rbac-client-policy.md) / [0013](decisions/0013-cms-html-sanitization.md).
