# Home page: handover for the frontend team

A one-page summary of how the app home page works. The full details are in
[README.md](README.md), [reference.md](reference.md) and [examples.md](examples.md).
Every call below is in Postman under **Home**, with example responses.

## The idea in one sentence

The **backend decides** what is on the home page (which sections, in what order,
with which title and data). The **app only draws** what it receives. Admins
change the home page from the admin panel, without an app release.

## The two endpoints you need

| What | Request |
|------|---------|
| Load the whole home page | `GET /api/v1/home/screen?city_id=1&platform=ios&app_version=1.4.0` |
| Load more / "see all" for one section | `GET /api/v1/home/sections/{key}?cursor={next_cursor}&city_id=1&platform=ios&app_version=1.4.0` |

- `platform`: `ios`, `android` or `web`. **Always send it**, together with `app_version` (e.g. `1.4.0`).
- `city_id`: the user's city, when known.
- `Authorization: Bearer <token>`: send it when the user is logged in. Some sections are only for logged-in users.

## What you get back

```json
{
  "success": true,
  "code": 1,
  "data": {
    "screen": {
      "revision": 3791378435928646,
      "sections": [
        {
          "key": "popular",
          "type": "products",
          "layout": "grid",
          "title": "پربازدید",
          "requires_auth": false,
          "empty": false,
          "items": {
            "has_more": true,
            "next_cursor": "pop:12",
            "results": [ { "id": 501, "title": "کاپشن زمستانه", "price": 890000, "main_image": { "url": "https://…" } } ]
          },
          "more": { "path": "/home/sections/popular", "cursor_param": "cursor" },
          "tracking": { "section_key": "popular", "source": "popular_products", "position": 2 }
        }
      ]
    }
  },
  "errors": []
}
```

Each item in `sections` is one block on the page:

| Field | Meaning |
|-------|---------|
| `key` | Stable id of the section. Use it as the list key and in `/home/sections/{key}`. |
| `type` | What the items are: `products`, `shops`, `categories`, `banners` → picks the **card** |
| `layout` | How to draw it: `horizontal_rail`, `grid`, `chips`, `hero` → picks the **container** |
| `title`, `subtitle` | Ready to show (already in Persian) |
| `items.results` | The data |
| `items.has_more`, `items.next_cursor` | Paging |
| `empty` | `true` when there are no items |
| `tracking` | Attach to your analytics events |
| `category_slug` | Only on category sections (e.g. `bt`). "See all" still uses `/home/sections/{key}` |

## Rules for drawing the page

1. Draw `sections` **in the order received**. Do not sort.
2. Pick the card from `type` and the container from `layout`.
3. If you don't know a `type` or `layout`, **skip that section**. Never crash.
   This is how new sections reach users without breaking old app versions.
4. If `empty` is `true`, show an empty state or hide the section.
5. If `sections` is an empty list (nothing set up by admins yet), show a
   full-page empty state.

## Paging ("load more")

- Only when `items.has_more` is `true`.
- Call `GET /api/v1{more.path}?cursor={items.next_cursor}` with the same
  `city_id`, `platform`, `app_version`.
- Add the new `data.section.items.results` to the list. Stop when `has_more` is `false`.
- Send the cursor back **exactly as received** (it can look like `412` or `pop:12`).

## Banners (`type: banners`)

Each banner has `image_url` and an `action` that runs on tap:

| `action.type` | `action.value` | Do |
|---------------|----------------|----|
| `open_product` | product id | open the product page |
| `open_shop` | shop id | open the shop page |
| `open_category` | category slug | open the product list for that category |
| `open_section` | section key | open "see all" for that section (`/home/sections/{value}`) |
| `open_url` | web link | open in the in-app browser |
| `none` | — | nothing |
| anything else | — | nothing (new type, needs a newer app) |

## Logged in vs guest

- Sections with `requires_auth: true` (for you, following shops, picked for you, …)
  are only sent to logged-in users.
- **Reload the home page after login, logout, and city change**, and when the app
  comes back to the foreground after a long time.
- `revision`: if it is different from the last load, the layout changed.
  Compare with **equal / not equal only**.

## Errors

| Status | When | Do |
|--------|------|----|
| 400 | wrong `platform`, `app_version`, `limit` or `city_id` | fix the request |
| 401 | `/home/sections/{key}` for a logged-in-only section without a token | show login, or hide the section |
| 404 | `/home/sections/{key}` for a section that no longer exists or is not for this app | remove the section, reload the screen |

Error body: `{ "success": false, "message": "…", "errors": [ { "reason": "platform", "messages": ["…"] } ] }`.
`message` is in Persian and can be shown to the user.

## One thing the app must send back

When a product page opens, send:

```http
POST /api/v1/events
Authorization: Bearer <token>      (when logged in)

{ "event_type": "view_product", "product_id": 501, "shop_id": 3, "category_slug": "aa-1-2", "session_id": "<random id per install>" }
```

The **popular** and **for you** sections are built from these events. Without
them, popular shows only the newest products and "for you" is not personal.

## Adding a category section (e.g. "Baby & Toddler")

Sections are created by an **admin** (panel or API), not by the app. To make a
section that only shows one category, set `category_slug` on the section. It
includes the category **and all its subcategories** (products are filed under
subcategories, so `bt` covers everything under Baby & Toddler).

- Works with `source`: `latest_products` (newest), `popular_products` (most
  viewed), `featured_products` (boosted). Other sources answer 400.
- Find the slug with `GET /api/v1/categories` (top level: `aa` Apparel &
  Accessories, `bt` Baby & Toddler, `el` Electronics, …).
- The app needs **no change**: the new section arrives in `/home/screen` like any
  other. For **"See all"**, page through `GET /api/v1/home/sections/{key}`
  (it covers the whole category tree). `category_slug` is also sent, for a title
  or icon. Note: `GET /api/v1/products?category_slug=…` matches one exact
  category only, so it is **not** a substitute for a top-level category like `bt`.

### Example 1: "Baby & Toddler", newest products, horizontal row

**Request** (admin)
```http
POST /api/v1/admin/home/sections
Authorization: Bearer <admin token>
Content-Type: application/json

{
  "slug": "baby-toddler",
  "source": "latest_products",
  "category_slug": "bt",
  "layout": "horizontal_rail",
  "title": "کودک و نوزاد",
  "subtitle": "تازه‌ترین‌ها",
  "item_limit": 12,
  "sort_order": 3,
  "active": true,
  "audience": "all",
  "hide_when_empty": true
}
```

**Response 201**
```json
{
  "success": true,
  "message": "بخش صفحه اصلی با موفقیت ایجاد شد",
  "code": 1,
  "data": {
    "home_section": {
      "id": 21,
      "slug": "baby-toddler",
      "source": "latest_products",
      "item_kind": "products",
      "layout": "horizontal_rail",
      "title": "کودک و نوزاد",
      "subtitle": "تازه‌ترین‌ها",
      "item_limit": 12,
      "sort_order": 3,
      "active": true,
      "audience": "all",
      "hide_when_empty": true,
      "category_slug": "bt",
      "platforms": [],
      "created_at": "2026-11-02T10:00:00Z",
      "updated_at": "2026-11-02T10:00:00Z"
    }
  },
  "errors": []
}
```

**What the app then receives** inside `GET /api/v1/home/screen` → `data.screen.sections`:
```json
{
  "id": 21,
  "key": "baby-toddler",
  "type": "products",
  "layout": "horizontal_rail",
  "title": "کودک و نوزاد",
  "subtitle": "تازه‌ترین‌ها",
  "requires_auth": false,
  "category_slug": "bt",
  "hide_when_empty": true,
  "empty": false,
  "items": {
    "per_page": 12,
    "has_more": true,
    "next_cursor": "731",
    "total": 54,
    "results": [
      {
        "id": 742,
        "shop_id": 18,
        "title": "سرهمی نوزادی پنبه‌ای",
        "slug": "cotton-baby-romper",
        "category_slug": "bt-4-2",
        "price": 380000,
        "compare_at_price": 450000,
        "rating_avg": 4.7,
        "rating_count": 9,
        "main_image": { "id": 1201, "url": "https://cdn.vitran.ir/p/1201.jpg", "sort_order": 0, "is_main": true }
      }
    ]
  },
  "more": { "path": "/home/sections/baby-toddler", "cursor_param": "cursor" },
  "tracking": { "section_key": "baby-toddler", "source": "latest_products", "position": 3 }
}
```

### Example 2: "Popular in clothing", most viewed products, grid

**Request** (admin)
```http
POST /api/v1/admin/home/sections
Authorization: Bearer <admin token>
Content-Type: application/json

{
  "slug": "popular-apparel",
  "source": "popular_products",
  "category_slug": "aa",
  "layout": "grid",
  "title": "پربازدیدترین پوشاک",
  "item_limit": 6,
  "sort_order": 5,
  "active": true,
  "audience": "all",
  "hide_when_empty": true
}
```

**Response 201**
```json
{
  "success": true,
  "message": "بخش صفحه اصلی با موفقیت ایجاد شد",
  "code": 1,
  "data": {
    "home_section": {
      "id": 22,
      "slug": "popular-apparel",
      "source": "popular_products",
      "item_kind": "products",
      "layout": "grid",
      "title": "پربازدیدترین پوشاک",
      "item_limit": 6,
      "sort_order": 5,
      "active": true,
      "audience": "all",
      "hide_when_empty": true,
      "category_slug": "aa",
      "platforms": [],
      "created_at": "2026-11-02T10:05:00Z",
      "updated_at": "2026-11-02T10:05:00Z"
    }
  },
  "errors": []
}
```

The app loads the next page with
`GET /api/v1/home/sections/popular-apparel?cursor=pop:6&city_id=1&platform=ios&app_version=1.4.0`.

### Example 3: "Electronics week", boosted products, mobile apps only, for one week

**Request** (admin)
```http
POST /api/v1/admin/home/sections
Authorization: Bearer <admin token>
Content-Type: application/json

{
  "slug": "electronics-week",
  "source": "featured_products",
  "category_slug": "el",
  "layout": "horizontal_rail",
  "title": "هفته الکترونیک",
  "subtitle": "پیشنهادهای ویژه",
  "item_limit": 10,
  "sort_order": 2,
  "active": true,
  "audience": "all",
  "hide_when_empty": true,
  "platforms": ["ios", "android"],
  "starts_at": "2026-11-20T00:00:00Z",
  "ends_at": "2026-11-27T00:00:00Z"
}
```

**Response 201**
```json
{
  "success": true,
  "message": "بخش صفحه اصلی با موفقیت ایجاد شد",
  "code": 1,
  "data": {
    "home_section": {
      "id": 23,
      "slug": "electronics-week",
      "source": "featured_products",
      "item_kind": "products",
      "layout": "horizontal_rail",
      "title": "هفته الکترونیک",
      "subtitle": "پیشنهادهای ویژه",
      "item_limit": 10,
      "sort_order": 2,
      "active": true,
      "audience": "all",
      "hide_when_empty": true,
      "category_slug": "el",
      "platforms": ["ios", "android"],
      "starts_at": "2026-11-20T00:00:00Z",
      "ends_at": "2026-11-27T00:00:00Z",
      "created_at": "2026-11-02T10:10:00Z",
      "updated_at": "2026-11-02T10:10:00Z"
    }
  },
  "errors": []
}
```

It appears on iOS and Android between 20 and 27 November and disappears by
itself afterwards. It is hidden while no Electronics product is boosted.

### Change or remove the category later

```http
PATCH /api/v1/admin/home/sections/21
{ "category_slug": "aa" }        ← switch to another category
{ "category_slug": "" }          ← all categories again
{ "active": false }              ← hide the section
```

### Error example

Unknown category, or a source that cannot be limited to a category:

**Response 400**
```json
{
  "success": false,
  "message": "داده‌های ارسالی نامعتبر است.",
  "code": -2,
  "data": null,
  "errors": [
    {
      "reason": "category_slug",
      "messages": ["دسته‌بندی نامعتبر است یا برای این منبع داده قابل استفاده نیست (فقط latest_products، popular_products و featured_products)."]
    }
  ]
}
```

## Checklist before release

- [ ] `platform` and `app_version` sent on both endpoints
- [ ] Sections drawn in order; unknown `type` / `layout` skipped
- [ ] Empty section and empty page states
- [ ] Load more with `more.path` + `next_cursor` until `has_more` is false
- [ ] Banner taps for all action types; unknown action does nothing
- [ ] Reload on login, logout, city change, and return to foreground
- [ ] 401 / 404 on a single section handled
- [ ] `view_product` event sent on product pages
- [ ] `tracking` attached to impression and click analytics
