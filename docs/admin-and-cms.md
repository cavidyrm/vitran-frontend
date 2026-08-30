# Admin Platform and CMS (Phase 11)

Phase 11 adds the administrative platform and static-page CMS through `:feature:admin` and `:feature:content`. It wires real APIs and shared ViewModels into existing admin screens and adds thin moderation, taxonomy, CMS, and public static-page screens. The backend remains authoritative for authentication, authorization, validation, and mutation outcomes.

## 1. Scope

Implemented areas are admin users, shop/product/comment moderation, city administration, taxonomy import and localization, plan administration, admin static-page CRUD, public static pages, RBAC-aware UX, and safe HTML rendering.

Comment moderation is deliberately mutation-only because no admin comment discovery endpoint is documented. No taxonomy delete/category-create API, offline mutation queue, or new admin analytics contract was invented.

## 2. Module ownership

| Module | Ownership |
|--------|-----------|
| `:feature:admin` | Admin users, moderation, catalog administration, plans, admin CMS, RBAC, private admin state |
| `:feature:content` | Public static-page transport/domain/cache, `HtmlContent`, sanitization, public page ViewModel |
| `:shared` | Compose screens, navigation, `SafeHtml`, Koin bootstrap |

`:feature:admin` depends on `:feature:content`, account, location, taxonomy, marketplace, engagement, and seller contracts. `:feature:content` depends only on core modules.

## 3. Package boundaries

The `:feature:admin` packages are:

- `users` — user list/detail/update
- `moderation` — shops, products, and comment confirmation
- `catalog/location` — city create/update/delete
- `catalog/taxonomy` — import, category icon/name, attribute/value names
- `plans` — admin plan CRUD and delete policy
- `content` — admin static-page CRUD
- `rbac` — `AdminPermissions`
- `state` — session-scoped admin state clearing

There is no giant `AdminApi` or `AdminRepository`.

## 4. Dependency and type reuse

Admin code reuses established identifiers and contracts: `ShopId`, `ProductId`, `ShopCommentId`, `CityId`, `CategorySlug`, `AttributeSlug`, `AttributeValueSlug`, `PlanId`, `PlanSlug`, public cache invalidators, `AppResult`, `AppError`, and page pagination. Admin transport projections remain admin-specific DTOs.

## 5. Authentication and authorization

Every `AdminUserApi`, `AdminModerationApi`, `AdminLocationApi`, `AdminTaxonomyApi`, `AdminPlanApi`, and `AdminContentApi` request uses `AuthMode.Required`. `ContentApi` uses `AuthMode.None`.

Client role checks control navigation and affordances only. A visible or enabled action is not proof of authorization; server `403` remains authoritative.

## 6. `AdminPermissions`

`AdminPermissions` centralizes these capabilities:

| Capability | Admin | Super Admin |
|------------|-------|-------------|
| `canAccessAdmin` | Yes | Yes |
| `canAssignAdminRole` | No | Yes |
| `canDeleteCity` | No | Yes |
| `canImportTaxonomy` | No | Yes |
| `canDeletePlan` | No | Yes |
| `canDeleteStaticPage` | No | Yes |

Customer, seller, empty, and unknown-role sets receive no admin privilege. `assignableRoles` always offers customer/seller and offers admin only to Super Admin. It never offers `super_admin`.

## 7. Role-update safety

Admin user PATCH sends only `is_active` and `roles`. `buildRolesUpdatePayload` prevents a normal Admin from assigning the admin role and preserves an existing target `super_admin` role even though it is not editable in the form. Updating the current user triggers `AccountRepository.refreshCurrentUser()`.

See [ADR 0012](decisions/0012-admin-rbac-client-policy.md).

## 8. Admin users

`AdminUserApi` implements:

- `GET /admin/users` with `role`, `phone`, `is_active`, `page`, and `per_page`
- `GET /admin/users/{id}`
- `PATCH /admin/users/{id}`

`AdminUserRepository` maps page results and unknown backend roles to safe `UserRole.Unknown`. `AdminUsersViewModel` and `AdminUserDetailViewModel` drive `AccountUsersScreen` and `AccountUserDetailScreen`.

## 9. Moderation ownership and queries

`AdminModerationApi` and `AdminModerationRepository` own shop, product, and comment moderation. Admin list queries support page/per-page plus applicable `active`, `city_id`, `category_slug`, `user_id`, and `shop_id` filters.

Shop and product screens default to the inactive queue and use per-item pending ID sets to prevent duplicate confirmation submits.

## 10. Shop moderation

Implemented:

- `GET /admin/shops`
- `PATCH /admin/shops/{id}/confirm` with an empty body

The response is mapped to `AdminShopSummary` and the existing `ShopPublicationState`. Successful confirmation invalidates the public shop cache for that `ShopId`.

## 11. Product moderation

Implemented:

- `GET /admin/products`
- `GET /admin/products/{id}`
- `PATCH /admin/products/{id}/confirm` with an empty body

The response is mapped to admin product models and the existing `ProductPublicationState`. Confirmation does not force `active=true`; `confirmed=true` with `active=false` remains `ApprovedHidden`. Success invalidates the public product cache.

## 12. Comment moderation

`PATCH /admin/comments/{id}/confirm` is implemented with an empty body.

**MISSING / UNRESOLVED:** the contract contains no admin comment list/discovery endpoint. `AdminCommentsViewModel` therefore accepts a known numeric comment ID and performs confirmation only. The UI explicitly states that no queue is available; it does not fabricate comments from public lists.

## 13. City administration

`AdminLocationApi` implements:

- `POST /admin/cities`
- `PATCH /admin/cities/{id}`
- `DELETE /admin/cities/{id}`

Create and update bodies contain **slug + name only**. No province, parent, type, or hierarchy fields are sent. Successful mutations call `LocationRepository.invalidateCities()`.

## 14. `CityInUse` conflict policy

The Postman conflict describes HTTP 409 with `reason=global`, which is not a stable domain discriminator. The current client workaround maps any `AppError.Conflict` from the city-delete operation, in that endpoint context, to `AdminLocationError.CityInUse`. Other failures remain `RequestFailed`.

Delete is a Super Admin UX capability and is guarded against concurrent update/delete work.

## 15. Taxonomy administration

`AdminTaxonomyApi` implements category rename, category icon upload, attribute rename, value rename, and taxonomy import. Successful operations call `TaxonomyRepository.invalidateTaxonomy()`.

Category/attribute/value identifiers are opaque slug value classes; the client does not infer a hierarchy or numeric IDs.

## 16. Taxonomy import

`POST /admin/taxonomy/import` is multipart with exactly two file parts:

- `categories`
- `attributes`

Both are `SelectedFile` values. Filenames are sanitized before transport. The response schema is unresolved, so success is decoded as an empty envelope rather than an invented import report.

## 17. Taxonomy localization and icon upload

Implemented operations:

- `PATCH /admin/categories/{slug}/name` with `{ "name": ... }`
- `PUT /admin/categories/{slug}/icon` multipart with key **`image`**
- `PATCH /admin/attributes/{slug}/name` with `{ "name": ... }`
- `PATCH /admin/values/{slug}/name` with `{ "name": ... }`

Mutation response schemas remain unresolved and are intentionally treated as empty success envelopes.

## 18. Plan administration

`AdminPlanApi` implements `GET`, `POST`, `PATCH`, and `DELETE` under `/admin/plans`. Admin plan DTOs retain heterogeneous raw `JsonObject` features and are separate from public plan transport models. Successful create/update/delete refreshes the public `PlanRepository`.

Create supports slug, title, description, price amount, duration days, product/image/shop limits, features, active state, and sort order.

## 19. Plan features PATCH policy

**UNRESOLVED:** the backend contract does not state whether PATCH merges or replaces `features`.

The client uses a REPLACE-safe policy:

- when `featuresUpdated=true`, send the **full features object**, including unknown keys retained from the loaded admin plan;
- when `featuresUpdated=false`, omit `features`;
- do not reduce admin features to `Map<String, Boolean>` because values are heterogeneous.

This protects unknown capabilities from accidental deletion if the server replaces the object.

## 20. Free Plan deletion

Deletion is offered only to Super Admin. The repository blocks a plan whose normalized slug is `free` before issuing a request. It also maps a delete `AppError.Conflict` (and a legacy “free plan” server message fallback) to `FreePlanCannotBeDeleted`.

The structured backend error reason is still missing; broad Conflict mapping is an explicit client workaround, not a proven contract.

## 21. Public content

`ContentApi` implements:

- `GET /static-pages`
- `GET /static-pages/slug/{slug}`

`ContentRepository` caches the public list and slug details in memory behind a mutex, supports forced refresh, and exposes `invalidate()`. `PublicStaticPageViewModel` maps 404 to `NotFound` and sanitizes page HTML before exposing content.

## 22. Admin CMS

`AdminContentApi` implements:

- `GET /admin/static-pages`
- `GET /admin/static-pages/{id}`
- `POST /admin/static-pages`
- `PATCH /admin/static-pages/{id}`
- `DELETE /admin/static-pages/{id}`

Create sends slug, title, body, active, and sort order. Update uses nullable fields as a partial request. Every successful mutation invalidates the public content cache. Delete is a Super Admin UX capability.

## 23. `HtmlContent`

CMS HTML is represented explicitly by the `HtmlContent` value class rather than an unlabelled `String`. `StaticPage.bodyHtml`, admin commands, sanitizer input/output, and `SafeHtml` all use this type, making the trust boundary visible.

## 24. `AllowlistHtmlSanitizer`

`AllowlistHtmlSanitizer` is common Kotlin code. It removes script/style/iframe/object/embed blocks, drops unknown tags and attributes, and permits a limited set: paragraphs, headings 1–3, lists/items, strong/emphasis variants, links, line breaks, and spans.

Only `http://` and `https://` link targets survive. Anchor output retains only a sanitized `href`; event handlers and other attributes are discarded.

## 25. `SafeHtml` Compose renderer

`SafeHtml` parses the sanitized subset into Compose `AnnotatedString` blocks. It renders headings, paragraphs/list items, bold/italic spans, line breaks, and clickable links. Links are revalidated and opened through `ExternalUrlLauncher`.

There is no shared WebView architecture and no unsanitized `innerHTML` on JS/Wasm. Unsupported rich HTML is intentionally reduced. See [ADR 0013](decisions/0013-cms-html-sanitization.md).

## 26. UI and navigation

Real ViewModels drive Users, Cities, Admin Plans, moderation shops/products/comments, taxonomy management, admin static-page list/editor, About, and generic static pages. Added routes cover moderation, taxonomy, CMS editor/list, Terms, Privacy, and Service Levels.

`AboutScreen` requests `about-us`. The generic routes currently request `terms`, `privacy`, and `service-levels`; those slugs must exist in CMS for content to resolve.

## 27. State, concurrency, and invalidation

ViewModels expose UDF state with loading/error/submission flags. Mutations use pending sets, active jobs, or saving/deleting flags to suppress duplicate submissions. Public city, taxonomy, plan, shop, product, and content caches are invalidated only after successful relevant mutations.

`AdminSessionStateStore` implements `SessionInvalidationListener` and clears registered private admin UI state on logout/account switch. Public reference/content caches are not cleared merely because a session ends.

## 28. Mutation retry policy

Admin POST, PATCH, PUT multipart, and DELETE requests are **never automatically retried**. The shared Ktor retry plugin retries only GET/HEAD for selected transport exceptions, HTTP 429, and 5xx responses. There is no offline mutation queue.

This policy avoids duplicate imports, creates, confirmations, uploads, updates, and deletes after ambiguous network failures.

## 29. Security

- Server authorization is authoritative; `AdminPermissions` is UX-only.
- Unknown roles grant no privilege, and Super Admin is never assignable in the client.
- Bearer credentials and sensitive headers use shared networking redaction.
- Multipart filenames are sanitized.
- HTML is sanitized before presentation and links are scheme-checked twice.
- Raw CMS HTML is not inserted into browser DOM.
- Admin mutation IDs come from typed models or digit-filtered confirmation input.

## 30. Tests

Common tests cover:

- RBAC matrix, admin-assignment gate, and existing Super Admin preservation
- user auth/query/body mapping and current-user refresh behavior
- city slug/name bodies, cache invalidation, and 409 conflict handling
- taxonomy multipart keys, icon `image` key, slug paths, invalidation, and no import retry
- shop/product/comment empty-body confirmations, cache invalidation, and `ApprovedHidden`
- plan feature omission/full-object unknown-key preservation and Free Plan deletion policy
- public/admin static-page transport and cache invalidation
- malicious HTML removal and safe basic HTML preservation

Tests use KMP `commonTest`, Ktor `MockEngine`, and coroutine test utilities. This documentation task did not run Gradle.

## 31. API gaps and Phase 12 readiness

Open Phase 11 contract gaps:

- admin comment discovery/list endpoint is missing;
- taxonomy mutation/import response schemas are missing;
- plan feature PATCH merge-vs-replace semantics are unresolved;
- city delete uses non-specific `reason=global`;
- Free Plan delete lacks a stable structured reason;
- rich HTML beyond the allowlisted Compose subset is unsupported by design.

Phase 12 can build on the two feature modules, typed APIs/repositories, centralized RBAC, safe content boundary, session invalidation, cache invalidators, and mutation retry policy. Before offline persistence, queues, richer CMS rendering, or broader moderation workflows are added, the unresolved contracts above should be stabilized rather than inferred.
