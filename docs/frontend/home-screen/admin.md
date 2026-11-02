# Home screen — admin guide

For product, design and support. Everything here changes the home page of every
installed app **without a release**. Admin endpoints need an admin token
(`/api/v1/admin/...`). Deleting a section needs a super-admin.

## What you can do without an app release

| Goal | How |
|------|-----|
| Add, remove, hide or reorder a rail | create/patch a section, set `active` and `sort_order` |
| Rename a rail | patch `title` / `subtitle` |
| Show a rail only to logged-in users, or only to guests | `audience` |
| Run a campaign for a limited time | `starts_at` / `ends_at` |
| Show a rail only on one platform | `platforms: ["ios"]` |
| Hide a rail from old app builds that cannot draw it | `min_app_version` |
| Add a banner that opens a product, shop, category, rail or web page | a `banners` rail plus banners |
| Change what feeds a rail | patch `source` |
| A rail for one category, e.g. "Baby & Toddler" | `source` latest/popular/featured products + `category_slug` (see [HANDOVER.md](HANDOVER.md#adding-a-category-section-eg-baby--toddler)) |

## What still needs an app update

A brand-new `type`, `layout` or `action.type`. Older apps skip them safely, so
ship the update first, then switch the rail on with `min_app_version` set to the
new build.

## Section fields

| Field | Values | Notes |
|-------|--------|-------|
| `slug` | 3–100 chars, lowercase, unique | Becomes the section `key` the apps use. Cannot be changed later. |
| `source` | `featured_products`, `personal_products`, `following_products`, `popular_products`, `latest_products`, `favorite_products`, `product_matches`, `latest_shops`, `following_shops`, `favorite_shops`, `categories`, `banners` | |
| `layout` | `horizontal_rail`, `grid`, `chips`, `hero` | Use `hero` with `banners`. |
| `category_slug` | a category slug, e.g. `bt` | Only with `latest_products`, `popular_products`, `featured_products`. Includes all subcategories. Empty = all categories. Slugs: `GET /api/v1/categories`. |
| `title`, `subtitle` | text | |
| `item_limit` | 1–100 | Items on the first page. |
| `sort_order` | integer | Lower first. |
| `active` | true/false | |
| `audience` | `all`, `authenticated`, `guest` | |
| `hide_when_empty` | true/false | Recommended `true` for campaigns. |
| `platforms` | subset of `ios`, `android`, `web` | Empty = all. |
| `min_app_version` | `2.0.0` | Empty = all versions. |
| `starts_at`, `ends_at` | RFC 3339, e.g. `2026-07-01T00:00:00Z` | Empty = open ended. `ends_at` must be after `starts_at`. |

Patch semantics: omit a field to keep it; send `""` for `starts_at`, `ends_at`,
`min_app_version` or `category_slug` to clear it; send `[]` for `platforms` to mean "all".

## Recipe: summer campaign banner on iOS and Android from 1 July to 31 July

1. Create the rail:

```http
POST /api/v1/admin/home/sections
{
  "slug": "summer-campaign",
  "source": "banners",
  "layout": "hero",
  "title": "حراج تابستانه",
  "item_limit": 5,
  "sort_order": 2,
  "active": true,
  "audience": "all",
  "hide_when_empty": true,
  "platforms": ["ios", "android"],
  "starts_at": "2026-07-01T00:00:00Z",
  "ends_at": "2026-08-01T00:00:00Z"
}
```

2. Add banners to it (`image_url` is the final public URL of an uploaded image):

```http
POST /api/v1/admin/home/sections/{section_id}/banners
{
  "image_url": "https://cdn.example/banners/summer.jpg",
  "title": "تا ۴۰٪ تخفیف",
  "action_type": "open_section",
  "action_value": "popular",
  "sort_order": 1,
  "active": true
}
```

Action values:

| `action_type` | `action_value` |
|---------------|----------------|
| `none` | leave empty |
| `open_product` | product id |
| `open_shop` | shop id |
| `open_category` | category slug |
| `open_section` | another rail's `slug` |
| `open_url` | full `https://…` URL |

3. List, edit and remove banners:

```http
GET    /api/v1/admin/home/sections/{section_id}/banners
GET    /api/v1/admin/home/banners/{banner_id}
PATCH  /api/v1/admin/home/banners/{banner_id}     { "title": "...", "active": false, "ends_at": "" }
DELETE /api/v1/admin/home/banners/{banner_id}
```

A banner has its own `starts_at`/`ends_at`, so a rail can stay while individual
banners come and go. Editing a banner, or a banner starting or ending, changes
the screen `revision`, so apps pick it up on the next fetch.

## Rules of thumb

- What is in the `home_sections` table is exactly what the apps get. Deleting or
  switching off every rail gives an empty home screen; there is no hidden
  fallback layout. To restore the seeded rails, create them again (see
  "Default rails" in [reference.md](reference.md)).
- Keep a rail's `slug` stable; analytics use it as `section_key`.
- Set `hide_when_empty: true` on anything that can run out of content, so users
  never see an empty block.
- Check the result on a real device with the right `platform` and `app_version`
  before the start date: `GET /api/v1/home/screen?platform=ios&app_version=2.0.0`.
- A `banners` rail only accepts banners; adding a banner to a product rail
  returns 400.
- Images are not uploaded through these endpoints. Upload first, then paste the
  public URL.
- Rails fed by user data (`personal_products`, `following_products`,
  `favorite_products`, `product_matches`, `following_shops`) are never shown to
  guests, whatever `audience` says. Use `audience: guest` with a `banners` or
  `popular_products` rail for a "sign up" or welcome block instead.
- `popular_products` is ranked by product views (the apps send `view_product`
  events). The ranking refreshes every 5 minutes.
- If a rail's data fails to load, the server drops that rail and logs
  `[home] section "<slug>" dropped from screen: …`. Watch for it after creating
  or changing a rail.

## Endpoint reference

All under `/api/v1/admin`, `Authorization: Bearer <admin token>`. Responses use
the usual envelope; the object is in `data.home_section`, `data.home_sections`,
`data.home_banner` or `data.home_banners`.

| Method | Path | Body | Success |
|--------|------|------|---------|
| GET | `/home/sections` | — | 200, `home_sections[]` (all rails, active or not, in `sort_order`) |
| GET | `/home/sections/{id}` | — | 200, `home_section` |
| POST | `/home/sections` | section fields, `slug` `source` `layout` `title` required | 201, `home_section` |
| PATCH | `/home/sections/{id}` | any section field except `slug` | 200, `home_section` |
| DELETE | `/home/sections/{id}` | — (**super-admin**) | 200, deletes its banners too |
| GET | `/home/sections/{id}/banners` | — | 200, `home_banners[]` (all, live or not) |
| POST | `/home/sections/{id}/banners` | `image_url` required, `title` `subtitle` `action_type` `action_value` `sort_order` `active` `starts_at` `ends_at` | 201, `home_banner` |
| GET | `/home/banners/{banner_id}` | — | 200, `home_banner` |
| PATCH | `/home/banners/{banner_id}` | any banner field | 200, `home_banner` |
| DELETE | `/home/banners/{banner_id}` | — | 200 |

### Section object (admin)

```json
{
  "id": 14,
  "slug": "summer-campaign",
  "source": "banners",
  "item_kind": "banners",
  "layout": "hero",
  "title": "حراج تابستانه",
  "item_limit": 5,
  "sort_order": 0,
  "active": true,
  "audience": "all",
  "hide_when_empty": true,
  "platforms": ["ios", "android"],
  "min_app_version": "2.0.0",
  "starts_at": "2026-07-01T00:00:00Z",
  "ends_at": "2026-08-01T00:00:00Z",
  "created_at": "2026-06-20T10:00:00Z",
  "updated_at": "2026-06-20T10:00:00Z"
}
```

`subtitle`, `min_app_version`, `starts_at` and `ends_at` are omitted when empty.
`item_kind` is derived from `source` and is what apps receive as `type`.

### Banner object (admin)

```json
{
  "id": 12,
  "section_id": 14,
  "title": "تا ۴۰٪ تخفیف",
  "image_url": "https://cdn.vitran.ir/banners/summer.jpg",
  "action": { "type": "open_section", "value": "popular" },
  "sort_order": 1,
  "active": true,
  "starts_at": "2026-07-01T00:00:00Z",
  "ends_at": "2026-08-01T00:00:00Z",
  "created_at": "2026-06-20T10:05:00Z",
  "updated_at": "2026-06-20T10:05:00Z"
}
```

Note: the request uses flat `action_type` / `action_value`; the response nests
them in `action`.

### Admin errors

| Status | `errors[].reason` | When |
|--------|-------------------|------|
| 400 | `slug`, `title`, `source`, `layout`, `audience`, `item_limit`, `platforms`, `min_app_version`, `starts_at`, `ends_at`, `category_slug` | invalid value (e.g. unknown `source`, `ends_at` not after `starts_at`) |
| 400 | `section_id` | adding a banner to a rail whose `source` is not `banners` |
| 400 | `image_url`, `action_value` | banner image is not an http(s) URL; action type/value do not match (e.g. `open_url` with `javascript:`) |
| 401 / 403 | `_global` | missing token / not an admin (delete needs super-admin) |
| 404 | `_global` | unknown section or banner id |
| 409 | `slug` | a rail with this slug already exists |

Example (duplicate slug):

```json
{
  "success": false,
  "message": "بخش با این نامک وجود دارد.",
  "code": 409,
  "data": null,
  "errors": [ { "reason": "slug", "messages": ["بخش با این نامک وجود دارد."] } ]
}
```
