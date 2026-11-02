# Home screen — reference

## Screen object (`data.screen`)

| Field | Type | Notes |
|-------|------|-------|
| `revision` | integer | Opaque layout fingerprint (fits a JavaScript number, ≤ 2^53−1). Same layout → same value. Changes when rails are added/removed/edited/reordered, when a scheduled rail or banner starts or ends, and when banners change. Compare for equality only. |
| `city_id` | integer, optional | Echo of the requested city. |
| `sections` | array | Ordered. Render in this order. |

## Section object (`data.screen.sections[]` and `data.section`)

| Field | Type | Notes |
|-------|------|-------|
| `id` | integer, optional | Internal id. Do not rely on it. |
| `key` | string | Stable section id (`popular`, `for-you`, …). Also the `:slug` in `/home/sections/:slug`. |
| `type` | string | `products` \| `shops` \| `categories` \| `banners`. See below. |
| `layout` | string | `horizontal_rail` \| `grid` \| `chips` \| `hero`. See below. |
| `title` | string | Localized, ready to show. |
| `subtitle` | string, optional | Omitted when empty. |
| `category_slug` | string, optional | Present when the rail is limited to a category (and its subcategories). For "see all" keep using `/home/sections/{key}`; the product list endpoints match one exact category only. |
| `requires_auth` | boolean | `true` when the rail is for logged-in users only: its audience is `authenticated`, or its data only exists for a user (following, favourites, for you, picked for you). Guests never receive such rails. |
| `hide_when_empty` | boolean | Informational. The server already drops such rails when empty. |
| `empty` | boolean | `true` when `items.results` has no entries. |
| `items` | object | The data, see "Items page". |
| `more` | object, optional | Present for `products` and `shops` rails only. `{ path, cursor_param }`. Also the "see all" endpoint. Paginate only while `items.has_more` is `true`. |
| `tracking` | object | `{ section_key, source, position }`. Attach to analytics events. `position` is 1-based and only present on `/home/screen`. |

### `type` — what the items are

| `type` | `items.results[]` is | Card |
|--------|----------------------|------|
| `products` | list of products | product card |
| `shops` | list of shops | shop card |
| `categories` | list of categories | category chip/tile |
| `banners` | list of banners | banner image; tap runs its `action` |

### `layout` — how to draw

| `layout` | Meaning |
|----------|---------|
| `horizontal_rail` | one row, scrolls sideways |
| `grid` | vertical grid, usually 2 columns on phones |
| `chips` | compact tappable chips/tiles, typically for categories |
| `hero` | large banner block. Designed for `type: banners` (a pager or a single full-width image). With any other `type`, skip the section. |

Any combination of `type` and `layout` is possible because admins configure them
independently. Pair them defensively: if you have no component for a combination,
skip the section.

## Items page (`section.items`)

| Field | Type | Notes |
|-------|------|-------|
| `per_page` | integer | Page size used. |
| `has_more` | boolean | `true` when another page exists. |
| `next_cursor` | string, optional | Opaque. Pass back as `cursor`. Absent or `""` when there is no next page. |
| `total` | integer, optional | Only on the first page (no `cursor` sent) of rails that support it. Do not depend on it. |
| `results` | array | Items, shape depends on `type`. Never `null`. |

### Pagination support

| Rail source | Cursor pagination |
|-------------|-------------------|
| `latest_products`, `popular_products`, `following_products`, `favorite_products` | yes |
| `latest_shops`, `following_shops`, `favorite_shops` | yes |
| `featured_products`, `personal_products`, `product_matches`, `categories`, `banners` | no — one page, `has_more` is always `false`; a `cursor` returns an empty page |

Do not hard-code this table. Drive the UI from `has_more` and `next_cursor`.

## Data sources (what fills each rail)

`latest_products`, `popular_products` and `featured_products` rails can be
limited to one category with the admin field `category_slug`; the rail then
shows products of that category **and all its subcategories** (e.g. `bt` for a
"Baby & Toddler" rail), and the section carries `category_slug`.


Admins pick a `source` per rail; it is echoed in `tracking.source`. All product
sources only return **live** products (active, confirmed, `status: active`, from
an active confirmed shop) and respect `city_id`.

| `source` | `type` | Needs login | What it returns | Order |
|----------|--------|:-----------:|-----------------|-------|
| `featured_products` | products | no | Products with an active paid boost (in the city) | boost order |
| `popular_products` | products | no | Most viewed products of the last 30 days (`view_product` events, per city, ranking cached 5 min, up to 300 products). Without view data: newest products. | views, then newest |
| `latest_products` | products | no | Newest products | newest first |
| `personal_products` | products | **yes** | Products from the user's top 3 categories (learned from their events), de-duplicated, topped up with popular products | category affinity |
| `following_products` | products | **yes** | New products from shops the user follows | newest first |
| `favorite_products` | products | **yes** | The user's wishlist | most recently saved |
| `product_matches` | products | **yes** | Products matching the user's saved sizing profile | match score |
| `latest_shops` | shops | no | Newest live shops | newest first |
| `favorite_shops` | shops | no | Top live shops (discovery, not the user's follow list) | rating, then 30-day shop views, then rating count |
| `following_shops` | shops | **yes** | Shops the user follows | most recently followed |
| `categories` | categories | no | Top-level categories | taxonomy order |
| `banners` | banners | no | The rail's live banners (active and inside their schedule) | banner `sort_order` |

## Item shapes

Only the fields a card needs are listed. Fields marked *optional* are omitted
when empty. Treat unknown fields as ignorable.

### Product (`type: products`)

| Field | Type | Notes |
|-------|------|-------|
| `id` | integer | Use for the detail page: `GET /products/:id`. |
| `shop_id` | integer | |
| `title` | string | |
| `slug` | string | |
| `category_slug` | string | |
| `price` | integer | Regular price. |
| `compare_at_price` | integer, optional | Price before discount. The product is **on sale** when present and greater than `price`. Show it struck through. |
| `price_after_off` | integer, optional | Selling price after discount when the seller set one. |
| `rating_avg` | number | 0 when no reviews. |
| `rating_count` | integer | |
| `main_image` | object, optional | Cover image `{ id, url, sort_order, is_main }`. Prefer this for cards. |
| `images` | array | All images. |
| `attributes` | array, optional | Taxonomy values (color, size, …). |
| `city`, `category` | object, optional | Expanded when available. |
| `created_at`, `updated_at` | string (RFC 3339) | |

Discount percent for a badge: `round((compare_at_price - price) / compare_at_price * 100)`
when `compare_at_price > price`.

### Shop (`type: shops`)

| Field | Type | Notes |
|-------|------|-------|
| `id` | integer | Detail: `GET /shops/:id`. |
| `title`, `slug` | string | |
| `avatar_url` | string, optional | |
| `description` | string, optional | |
| `city_id` | integer | |
| `type` | string | `retailer` \| `wholesaler`. |
| `rating_avg`, `rating_count` | number, integer | |
| `share_url` | string | |

Shop objects carry more fields (contact links, working hours, …). Cards should
not need them.

### Banner (`type: banners`)

| Field | Type | Notes |
|-------|------|-------|
| `id` | integer | |
| `image_url` | string | Always present, absolute http(s) URL. |
| `title` | string, optional | Overlay text. |
| `subtitle` | string, optional | |
| `action` | object | What a tap does, see below. Always present. |

Banners never paginate and a banners rail has no `more`.

#### `action`

`{ "type": "...", "value": "..." }`. `value` is always a string and is omitted
for `none`.

| `action.type` | `value` | What the app does |
|---------------|---------|-------------------|
| `none` | omitted | nothing (display only) |
| `open_product` | product id, e.g. `"501"` | open the product detail screen |
| `open_shop` | shop id | open the shop screen |
| `open_category` | category slug, e.g. `"aa-1-2"` | open the product list filtered by `category_slug` |
| `open_section` | section `key`, e.g. `"popular"` | open a "see all" screen for that rail, loaded from `/home/sections/:key` |
| `open_url` | `http(s)` URL | open in the in-app browser or web view |

**Ignore an unknown `action.type`** (treat as `none`). New action types may be
added later; they need an app update, older apps just do nothing on tap.
`open_url` is validated to `http`/`https` on the server, but still open it in an
in-app browser, never inside your own authenticated web view.

### Category (`type: categories`)

| Field | Type | Notes |
|-------|------|-------|
| `slug` | string | Use as `category_slug` in `GET /products` and `GET /catalog/search`. |
| `title` | string | Original taxonomy title. |
| `name` | string, optional | Localized display name set by admins. **Prefer `name`, fall back to `title`.** |
| `icon_url` | string, optional | |
| `level` | integer | 0 for top level. |
| `is_leaf` | boolean | |
| `product_count`, `shop_count` | integer, optional | |
| `children` | array, optional | Usually absent in home rails. |

## Who sees a rail

The server applies these rules, in this order, before sending a section. You do
not need to re-check them, but it explains why a rail may be missing.

| Rule | Set by admin | Effect |
|------|--------------|--------|
| Active | `active` | inactive rails are never sent |
| Schedule | `starts_at`, `ends_at` | sent only inside the window (end is exclusive) |
| Audience | `audience` | see "Guest vs logged-in" |
| Platform | `platforms` (`ios`/`android`/`web`, empty = all) | sent only if the request's `platform` is listed |
| App version | `min_app_version` | sent only if the request's `app_version` is that or newer |
| Not empty | `hide_when_empty` | dropped when it has no items |

If you do not send `platform` / `app_version`, every rail that has a platform or
version restriction is left out.

## Guest vs logged-in

| Audience of the rail | Guest | Logged in |
|----------------------|-------|-----------|
| `all` | shown | shown |
| `authenticated` (`requires_auth: true`) | **not in the response** | shown |
| `guest` | shown | **not in the response** |

On `GET /home/sections/:key` for a rail with `requires_auth: true` without a
token the API answers **401**. Handle it by showing the login prompt or hiding
the rail. A guest-only rail requested with a token answers **404**.

## Errors

Every error uses the same envelope. `errors[].reason` is the offending query
field, or `_global`. `message` and `messages` are localized and can be shown.

```json
{
  "success": false,
  "message": "داده‌های ارسالی نامعتبر است.",
  "code": -2,
  "data": null,
  "errors": [ { "reason": "platform", "messages": ["پلتفرم باید ios، android یا web باشد."] } ]
}
```

| Status | `code` | When | What to do |
|--------|--------|------|------------|
| 400 | `-2` | bad `limit` (not a positive integer), bad `city_id` / `city_slug`, bad `platform` (not ios/android/web) or bad `app_version` | fix the request |
| 401 | `401` | rail needs login and no valid token | prompt login or hide the rail |
| 404 | `404` | unknown or inactive `key`; a rail outside its schedule or not for this platform/app version; a guest-only rail requested with a token | remove the rail from the UI, refetch the screen |
| 5xx | — | the rail definitions could not be read | keep showing the cached screen, retry with backoff |

A rail whose data fails on `/home/screen` is **dropped**, not reported as an error
(see "What the server guarantees" in [README.md](README.md)). On
`/home/sections/:key` the same failure answers 5xx; keep the items you already
have and retry later.

`limit` is capped at 100 and defaults to the rail's configured size (`item_limit`).

## Caching on the client

- Cache the last screen response per (user, city) and render it immediately on
  app start, then refresh in the background.
- Re-render only when `revision` changed **or** you want fresh items. Items
  change all the time without changing `revision`.
- Refetch on login, logout, city change, and when the app returns to the
  foreground after more than ~15 minutes.

## Default rails

These are inserted by migration `0049` when the database is created. Admins can
add, remove, reorder and retitle them, so never depend on a specific `key` being
present. The `home_sections` table is the only source: if it has no active rows,
`sections` is an empty array (handle it with an empty state).

| `key` | `type` | `layout` | Audience | Hidden when empty | Paginates |
|-------|--------|----------|----------|-------------------|-----------|
| `categories` | categories | chips | all | no | no |
| `featured` | products | horizontal_rail | all | yes | no |
| `for-you` | products | horizontal_rail | authenticated | yes | no |
| `following` | products | horizontal_rail | authenticated | yes | yes |
| `picked-for-you` | products | horizontal_rail | authenticated | yes | no |
| `popular` | products | grid | all | no | yes |

## What admins can configure

Admins manage rails through `/admin/home/sections`. For design and product
discussions, each rail has:

| Setting | Values |
|---------|--------|
| `source` (where data comes from) | `featured_products`, `personal_products`, `following_products`, `popular_products`, `latest_products`, `favorite_products`, `product_matches`, `latest_shops`, `following_shops`, `favorite_shops`, `categories`, `banners` |
| `layout` | `horizontal_rail`, `grid`, `chips`, `hero` |
| `audience` | `all`, `authenticated`, `guest` |
| `title`, `subtitle` | free text |
| `category_slug` | a category slug (latest/popular/featured products only); empty = all categories |
| `item_limit` | 1–100 |
| `sort_order` | integer, defines the order on screen |
| `hide_when_empty` | boolean |
| `active` | boolean |
| `platforms` | any of `ios`, `android`, `web`; empty = all |
| `min_app_version` | e.g. `2.0.0`; empty = every version |
| `starts_at`, `ends_at` | RFC 3339 timestamps; empty = open ended |

`source` decides `type` (`categories` → categories, `banners` → banners,
`*_shops` → shops, the rest → products). `source` is not a field of the section,
but it is echoed in `tracking.source`. See "Data sources" above for what each
one returns.
