# Home screen — frontend guide

The home page is **server-driven**. The API decides which rails appear, in what
order, with which title, layout hint and data. The app only renders what it
gets. Merchandising changes (new rail, reorder, retitle) therefore ship without
an app release.

| Doc | What is in it |
|-----|---------------|
| [HANDOVER.md](HANDOVER.md) | **start here**: one-page summary and release checklist |
| this file | quick start, mental model, endpoints, the render algorithm |
| [reference.md](reference.md) | every field, every enum, item shapes, actions, errors |
| [examples.md](examples.md) | full guest / logged-in / banner responses and a pagination walkthrough |
| [admin.md](admin.md) | for product and design: how to change the home page without an app release |
| [openapi.yaml](openapi.yaml) | machine-readable schema, generate your types from it |
| [testing.md](testing.md) | run the backend with demo data and check every behaviour (checklist for frontend and mobile) |
| [legacy.md](legacy.md) | `GET /home` and `GET /me/home/feed` (old clients only) and `POST /events` |
| [`postman/vitran-api.postman_collection.json`](../../postman/vitran-api.postman_collection.json) | folders **Home** and **Home — Admin**, every request with saved example responses |

> New screens use **`GET /home/screen`** and **`GET /home/sections/:key`** only.
> `GET /home` and `GET /me/home/feed` are kept for old app builds, see [legacy.md](legacy.md).

## Quick start (read this first)

Seven rules are enough to build the whole home page:

1. Call `GET /api/v1/home/screen?platform=ios&app_version=1.4.0&city_id=1`.
   Always send `platform` (`ios` | `android` | `web`) and your app version. Send
   the token when the user is logged in.
2. Render `data.screen.sections` **in the order received**.
3. For every section pick a component from `type` (what the items are) and
   `layout` (how to draw them). **Skip any `type`, `layout` or `action.type` you
   do not know.** Never crash on an unknown value.
4. The items are in `section.items.results`. The card depends on `type`:
   `products`, `shops`, `categories` or `banners`.
5. A tap on a **banner** runs `action` (`open_product`, `open_shop`,
   `open_category`, `open_section`, `open_url`, `none`). Products, shops and
   categories open their own detail screens.
6. Load more with `more.path` + `items.next_cursor` while `items.has_more` is true.
7. Send `tracking` back with your analytics events. Refetch the screen on login,
   logout, city change, and when `revision` changes.
8. Send `POST /api/v1/events` with `event_type: view_product` (with `product_id`,
   `shop_id`, `category_slug`) whenever a product page opens, and
   `click_category` / `search` when they happen, with the token when logged in.
   These events rank the **popular** rail and personalise **for you**
   (see [legacy.md](legacy.md#post-apiv1events-feeds-popular-and-for-you)).

New rails, banners, campaigns, reordering and retitling are done by admins on the
server (see [admin.md](admin.md)) and need **no app release**, as long as the app
follows rules 3 and 5. A new `type`, `layout` or `action.type` needs an app update;
older apps simply skip it.

## Mental model

```
GET /home/screen            ->  screen { revision, sections[] }
                                          |
                                          +-- section { key, type, layout, title, items, more, ... }
                                                         |            |
                                          what to draw --+            +-- the data (first page)

GET /home/sections/:key     ->  one section, any page (used for "load more")
```

- A **section** (also called a rail) = one block on the page.
- `type` says **what the items are**: `products`, `shops`, `categories` or `banners`.
- `layout` says **how to draw them**: `horizontal_rail`, `grid`, `chips`, `hero`.
- `items.results` is the data. The item shape depends on `type` only.
- `key` is the stable id of the section (e.g. `popular`). Use it as the list key
  and as the `:slug` in the section endpoint.

## Endpoints

| Endpoint | Auth | Purpose |
|----------|------|---------|
| `GET /api/v1/home/screen?city_id=1&platform=ios&app_version=1.4.0` | optional JWT | whole page, first page of every rail |
| `GET /api/v1/home/sections/:key?limit=12&cursor=…&city_id=1&platform=ios&app_version=1.4.0` | optional JWT | one rail, for pagination or refresh |

- Send `Authorization: Bearer <token>` whenever the user is logged in. Without
  it the server omits rails meant for logged-in users.
- `platform` and `app_version` tell the server which app is asking. Rails can be
  limited to a platform or to a minimum app version, and the server leaves out
  the ones your build cannot show. You can send them as headers instead
  (`X-App-Platform`, `X-App-Version`); the query string wins. Without them you
  only receive rails that have no restriction. A malformed value returns 400.
  Version format: `1`, `1.4` or `1.4.0` (up to four numeric parts, no `v`).
- `city_id` (or `city_slug`) filters products and shops to that city. Send it on
  both endpoints, with the same value.
- Responses use the usual envelope: `{ success, message, code, data, errors }`.
  The screen is in `data.screen`, a single section in `data.section`.

## Render algorithm

1. Call `GET /home/screen` (with token if logged in, with `city_id` if known).
2. Loop over `data.screen.sections` **in the order received**. Do not sort.
3. For each section:
   1. If `type` or `layout` is a value your app version does not know, **skip
      the section silently**. This is how new rails ship without breaking old
      app versions. Never crash on an unknown value.
   2. If `empty` is `true`, show your empty state or hide the rail. (Rails with
      `hide_when_empty: true` are already removed by the server, so you only see
      `empty: true` for rails that are meant to stay visible.)
   3. Pick the component from `layout`, pick the item card from `type`.
   4. Show `title` (and `subtitle` when present). These are already localized.
   5. Attach `tracking` (`section_key`, `source`, `position`) to the impression and
      click events you send to your analytics.
4. If `more` is present and `items.has_more` is `true`, load further pages with
   `GET {baseUrl}/api/v1{more.path}?cursor={items.next_cursor}&limit=…`.
   Stop when `has_more` is `false`.
5. Store `data.screen.revision`. When a later screen call returns a different
   value the layout changed on the server, so re-render from the new response.
   The revision is an opaque integer: compare it for equality only, never with
   `<` / `>`.

## What the server guarantees

The home page is built like a storefront app home (think shop.app): always full,
never broken by one bad rail.

| Guarantee | Detail |
|-----------|--------|
| One failing rail never fails the screen | If a rail's data source errors, that rail is dropped from the response and logged on the server. The rest of the screen is returned with 200. Only a failure to read the rail definitions returns 5xx. |
| Guests never see rails that need an account | Rails fed by the user's follows, favourites, history or sizing profile (`following_products`, `favorite_products`, `personal_products`, `product_matches`, `following_shops`) are left out for guests even if an admin set `audience: all`, and are flagged `requires_auth: true`. A direct fetch without a token answers 401. |
| Popular is really popular | `popular_products` ranks live products by product views of the last 30 days (`view_product` events), per city. With no view data yet it shows the newest live products, so the rail is never empty on a fresh install. |
| "For you" is never half empty | Products from the user's top categories, de-duplicated, then topped up with popular products for users without enough history. |
| Featured fills its slots | Boosted products that went inactive or are outside the city are skipped and the next boosted product takes the slot. |
| No duplicates when paginating | A `cursor` sent to a rail that only has one page returns an empty last page, never page one again. |
| `revision` is stable | The same layout always gives the same `revision`, on every server instance. It changes when a rail is added, removed, switched off, reordered, retitled, when a scheduled rail or banner starts or ends, and when banners change. Product/shop data inside rails does **not** change it. |
| Unknown values are additive | New `type`, `layout` or `action.type` values may appear. Old apps skip them (rule 3). |

### Things that are easy to get wrong

- The data is under **`items`**, not at the top level of the section.
- The id field is **`key`**, not `slug`.
- `more.path` is **relative to the API prefix**: prepend `/api/v1`.
- `next_cursor` is an **opaque string** (for example `"412"` or `"pop:24"`). Do
  not parse it or build it yourself; send back exactly what you received.
- Without `limit`, `/home/sections/:key` returns the rail's configured size
  (`items.per_page` of the screen). Send `limit` only when you need a different page size.
- Only some rails paginate, see "Pagination support" in [reference.md](reference.md).
  For the others `has_more` is always `false`.
- A guest never receives rails with `requires_auth: true`, but the same rail
  appears after login. Refetch the screen on login and logout.
- Rails depend on the clock too: a rail or banner with a start/end date appears
  and disappears by itself. Refetch the screen when the app returns to the
  foreground after a long time.
- Money values are integers in the shop's currency unit. Do not divide or round.
