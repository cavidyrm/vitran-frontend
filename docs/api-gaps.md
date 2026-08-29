# API Gaps and Ambiguities

Unresolved backend contract issues. **Do not silently fix these in client code.** Track status until verified or resolved.

Status values: `Open` | `Verified from backend source` | `Resolved by backend update` | `Client compatibility workaround`

---

## Gap 1 — `category_slug` type inconsistency

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Taxonomy uses string slugs (`aa-1-2-3-4`). Query params use string slugs. Request/response examples also show `"category_slug": 1` and `"category_slugs": [1]`. |
| **Client impact** | Domain must use `CategorySlug` as `String`. Accept int/string only at DTO boundary if backend requires compatibility. |
| **Phase 2+ handling** | Custom serializer or flexible DTO field; never leak ambiguous typing into domain. |

---

## Gap 2 — Product `active` vs `confirmed` behavior

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Create/update examples send `active=true`; sample responses show `active=false`, `confirmed=false`. Moderation/reapproval rules in descriptions. |
| **Client impact** | Preserve `active` and `confirmed` as distinct states. Do not guess authoritative create response. |
| **Phase 2+ handling** | Map to domain lifecycle (`PendingApproval`, `ApprovedHidden`, `Live`) after backend confirmation. |

---

## Gap 3 — Follow shops vs favorite shops

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | `/me/follows/shops` and `/me/favorites/shops` both exist; semantics overlap. |
| **Client impact** | Keep separate repositories/APIs. `FollowingScreen` may map to follows; favorites may differ. |
| **Phase 2+ handling** | Confirm product semantics before merging UI or domain concepts. |

---

## Gap 4 — Home `favorite_shops` naming

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Personalized home feed params include `favorite_shops`; description suggests discovery/high-performing shops, not necessarily user's saved favorites. |
| **Client impact** | DTO preserves backend field names; domain uses semantically correct names (e.g. `DiscoveryShops`). |
| **Phase 2+ handling** | Map in Home mapper after response schema verified. |

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

**Client impact:** Do not invent response DTOs. Use integration tests or backend verification before implementation.

---

## Gap 6 — Plan `features` heterogeneous typing

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | `features` JSON mixes `Boolean` and `String` (e.g. `"ranking_boost": "slight"`, `"contact_buttons": true`). |
| **Client impact** | Cannot use `Map<String, Boolean>`. Decode flexible JSON; map known keys to typed domain capabilities. |
| **Phase 2+ handling** | `JsonObject` or sealed capability parser at data layer. |

---

## Gap 7 — Payment status endpoint

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Collection documents purchase + payment callback; no dedicated client payment-status query endpoint. |
| **Client impact** | After payment redirect, client may need to refresh shop subscription state. |
| **Phase 2+ handling** | Poll `GET /seller/shops/{id}/subscription` unless backend adds status API. |

---

## Gap 8 — Phone normalization

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Examples use `9123456789`; filters use `0912`. Canonical format not specified. |
| **Client impact** | Do not invent normalization in Phase 1. |
| **Phase 2+ handling** | Align with backend validation rules when documented. |

---

## Gap 9 — Currency

| Field | Status |
|-------|--------|
| **Status** | Open |
| **Issue** | Fields `price`, `price_amount`, `price_paid`, `amount` with no explicit currency in transport contract. |
| **Client impact** | Do not assume IRR or any currency in shared domain without product confirmation. |
| **Phase 2+ handling** | Use `Money` or amount-only display until backend specifies currency. |

---

## Additional notes

- **Register with referral:** Same endpoint as register (`POST /auth/register`) with optional `referral_code` — one request model, not duplicate HTTP methods.
- **Login 403:** Phone verification required — business outcome with `temp_token`, not generic error.
- **Shop create session mutation:** `data.tokens.access_token` updates JWT roles — session owner is `:core:session`, not Auth ViewModel.
