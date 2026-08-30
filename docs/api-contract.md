# API Contract Summary

Client-facing summary of the Vitran marketplace backend. **Source of truth:** [`postman/vitran-api.postman_collection.json`](postman/vitran-api.postman_collection.json) (106 requests, 104 `/api/v1` business routes).

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
| **Required** | Bearer access token | `GET /auth/me`, seller CRUD, admin APIs, `GET /me/favorites/*` |
| **Optional** | Anonymous OK; auth may enrich response | `GET /api/v1/home`, `POST /api/v1/events`, `POST /products/{id}/contact`, public wishlist share |

Postman saves tokens from auth responses into collection variables: `accessToken`, `refreshToken`, `tempToken`.

---

## 4. Authentication tokens

| Token | Purpose | Storage (future) |
|-------|---------|------------------|
| **access_token** | Authorize authenticated requests | Secure platform storage |
| **refresh_token** | Renew access token | Secure platform storage |
| **temp_token** | Phone verification workflow | Secure platform storage |
| **otp_code** | Dev-only OTP when `SMS_OTP_PROVIDER=log` | Never persist in production |

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
| `customer` | Default shopper |
| `seller` | Shop owner; added on first shop creation if missing |
| `admin` | Platform admin |
| `super_admin` | Highest privilege |

**Admin assignment rules (from collection):**

- Only `super_admin` may assign `admin`
- `super_admin` cannot be granted to arbitrary new users via normal admin user-update API
- Existing `super_admin` users must retain `super_admin` in roles array

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

Response adds: `page`, `last_page`, `from`, `to`, `total`, `has_more`, `results`

**Client guidance:**

- Shopper infinite-scroll screens → cursor pagination
- Admin table screens needing totals → page pagination

---

## 7. Endpoint business domains

See [api-feature-map.md](api-feature-map.md) for full endpoint index.

| Domain | Postman folders |
|--------|-----------------|
| Auth | Auth |
| Account | Users |
| Session | Auth refresh, shop-create token mutation |
| Referral | Referrals |
| Location / Cities | Cities |
| Taxonomy | Taxonomy |
| Home | Home |
| Marketplace Shops | Shops — Public |
| Marketplace Products | Products — Public |
| Catalog / Search | Catalog search, product search |
| Engagement | Favorites — Me, Shop follows — Me, Wishlists — Public, reviews, comments, contact, `/events`, shop analytics |
| Product Reviews | Products — Public (reviews) |
| Shop Comments | Comments |
| Seller Shops / Products / Analytics / Boosts | Shops — Seller, Products — Seller, Boosts — Seller |
| Plans / Payments | Plans — Public/Admin, Payments — Public |
| CMS | Static Pages — Public/Admin |
| Admin | Admin — Users, Shops — Admin, Products — Admin |

---

## 8. Multipart / file endpoints

| Operation | Method / path | Notes |
|-----------|---------------|-------|
| Create seller product (with images) | `POST /seller/shops/{id}/products` | multipart |
| Update seller product | `PATCH /seller/products/{id}` | multipart |
| Upload category icon | `PUT /admin/categories/{slug}/icon` | multipart |
| Import Shopify taxonomy | `POST /admin/taxonomy/import` | multipart JSON files |
| Analytics export | `GET /seller/shops/{id}/analytics/export` | file response (CSV) |

**Multiplatform rule:** Domain/common code must not depend on `android.net.Uri`, `java.io.File`, or `NSURL`. Platform pickers convert to a shared upload abstraction (seller/admin phases).

---

## 9. Public vs seller vs admin APIs

| Layer | Path patterns | Auth |
|-------|---------------|------|
| **Public marketplace** | `/shops`, `/products`, `/catalog`, `/plans`, `/static-pages`, `/cities`, `/categories` | Mostly none |
| **Authenticated consumer** | `/auth/me`, `/me/favorites/*`, `/me/follows/*`, `/me/wishlist/*`, `/me/home/feed` | Required |
| **Seller** | `/seller/shops/*`, `/seller/products/*`, `/me/referral` | Required (seller role) |
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

**First shop creation:** adds `seller` role; may return `data.tokens.access_token` with updated JWT. Session is updated via `:core:session`, not Auth ViewModel.

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
| Phone verification required | 403 | `temp_token`, optional dev `otp_code` |

Represent as **business outcome**, not generic forbidden exception.

Register: single `POST /auth/register` with optional `referral_code` — do not duplicate HTTP methods for referral variant.

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
