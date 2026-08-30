# Production Blockers (Phase 12)

Open release risks from Phase 12 audit, [api-gaps.md](api-gaps.md), [security-production-review.md](security-production-review.md), and [release-readiness.md](release-readiness.md).

| Severity | Meaning |
|----------|---------|
| **P0** | Ship blocker for any target that claims the affected feature |
| **P1** | Major incomplete / store or commercial risk |
| **P2** | Important gap; ship only with explicit acceptance |
| **P3** | Polish, deferred enrichment, or accepted decision |

| Owner | Meaning |
|-------|---------|
| **CLIENT** | App / KMP code |
| **BACKEND** | API contract or server behavior |
| **PRODUCT** | Scope / messaging / acceptance |
| **INFRASTRUCTURE** | Build, hosting, CI secrets, headers |
| **EXTERNAL STORE/PLATFORM** | Apple / Google / payment vendor / crash vendor |

Do **not** silently invent missing API contracts in client code. Prefer disable/hide over fake success.

---

## P0 — Release blockers (conditional where noted)

### P12-001 — Web bearer tokens in-memory only

- **severity:** P0
- **owner:** CLIENT / PRODUCT
- **affected:** Wasm / JS authenticated Web
- **description:** Access/refresh tokens use `InMemorySecureSessionStorage`. Full page refresh or tab restart clears the session. `localStorage` for bearers is intentionally disallowed.
- **evidence:** Gap 10; `PlatformModule.wasmJs.kt` / `PlatformModule.js.kt`; [session-authentication.md](session-authentication.md); [security-production-review.md](security-production-review.md)
- **release impact:** Authenticated Web cannot be claimed production-complete; users appear logged out after reload.
- **required resolution:** Cookie/BFF or other durable Web session model **or** PRODUCT accepts Web as anonymous/browse-only and removes sticky-auth claims.
- **workaround:** Re-login each browser session; anonymous browse + Room OPFS cache (when hosting allows) may still work.

### P12-002 — Desktop secure storage (partially resolved)

- **severity:** P2 (was P0; encrypted file store landed in Phase 12)
- **owner:** CLIENT / PRODUCT
- **affected:** JVM Desktop auth
- **description:** Desktop now uses `JvmSecureSessionStorage` (AES-GCM under user config dir, owner-restricted where POSIX allows). Not OS Keychain/Credential Manager yet; restart persistence should be smoke-tested before authenticated Desktop ship.
- **evidence:** `JvmSecureSessionStorage.kt`; `PlatformModule.jvm.kt`; Gap 10 (partially resolved)
- **release impact:** Residual ops/UX risk until Keychain upgrade + packaging/signing EXTERNAL complete — not plaintext anymore.
- **required resolution:** Optional Keychain migration; Desktop restart smoke; packaging/notarization EXTERNAL.
- **workaround:** Ship with documented encrypted-file caveat, or delay Desktop.

### P12-003 — Payment return / deep-link contract missing (if payment advertised)

- **severity:** P0 (if plan purchase / payment is advertised as complete on any target); else P1 until advertised
- **owner:** BACKEND / PRODUCT / CLIENT
- **affected:** Plan purchase & payment resume (all targets)
- **description:** No verified App Link, Universal Link, custom scheme, or documented web return route for post-gateway return. Client must not invent return URLs. Opening `payment_url` ≠ success.
- **evidence:** Gap 41; Gap 7; [plans-subscriptions-payments-referrals.md](plans-subscriptions-payments-referrals.md); security-production-review “Payment return **not invented**”
- **release impact:** Cannot claim end-to-end paid upgrade UX; users may not return to a reliable in-app resume state.
- **required resolution:** Backend/PRODUCT document return contract; client handles only verified routes and still verifies via subscription refresh (never trust return query params; never call provider callback from app).
- **workaround:** Manual resume + “Check payment” → `VerifyPendingPaymentUseCase` / `GET .../subscription`.

### P12-004 — Release environment Local hard-gate (softened)

- **severity:** P2 (was P0; Production is now the DI default)
- **owner:** CLIENT / INFRASTRUCTURE
- **affected:** All release / store / production Wasm artifacts
- **description:** `startVitranKoin` defaults to `ApiEnvironments.Production`. Local still enables OTP UI + body logging when explicitly selected. No CI assert fails a release candidate that injects Local.
- **evidence:** `VitranKoin.kt`; Local-gated OTP ViewModels; `NetworkModule` Local logging
- **release impact:** Misconfigured entrypoint could still ship Local.
- **required resolution:** CI/flavor hard-gate rejecting Local for release tasks.
- **workaround:** Checklist verify Production before each candidate.

### P12-005 — Boost create does not POST (if Boost purchase advertised)

- **severity:** P0 (if Boost create/purchase is advertised); else hide CTA (treat as P1 product gate)
- **owner:** BACKEND / CLIENT / PRODUCT
- **affected:** Seller placement boosts
- **description:** Boost pricing is unresolved (no `GET /boost-prices`). `CreateBoostViewModel.submit()` is intentionally a no-op and must not call create. Postman sample `price_paid` is not a pricing policy. Backend may trust client `price_paid` (server-side integrity gap).
- **evidence:** Gap 42; [seller-analytics-and-boosts.md](seller-analytics-and-boosts.md) §25; `CreateBoostViewModel.kt`; SellerBoost ViewModel tests (“must not call createBoost”)
- **release impact:** Advertised Boost purchase would be false / unsafe.
- **required resolution:** Server-authoritative pricing + validated POST **or** PRODUCT disables/hides Boost create in production UX until contract exists.
- **workaround:** Keep submit disabled; ship list/export-only seller tooling without Boost purchase claims.

### P12-006 — Currency unresolved (if amounts claimed as production money)

- **severity:** P0 (if commercial money/currency is claimed in store copy or legal); else P1 for display ambiguity
- **owner:** BACKEND / PRODUCT
- **affected:** Product prices, plan `price_amount`, boost `price_paid`, referral credits
- **description:** Transport amounts are `Long` with **no currency** in the API contract. UI toman formatting is display convention only — domain must not invent IRR/IRT/ISO codes.
- **evidence:** Gaps 9, 41; plans-subscriptions-payments-referrals.md §4
- **release impact:** Misleading commercial/legal presentation if unit is wrong or unstated.
- **required resolution:** Backend currency field (or PRODUCT-signed canonical unit documented for all surfaces) before money claims.
- **workaround:** Show numeric amounts without inventing ISO currency in domain; avoid “تومان/ریال” legal claims until confirmed.

### P12-007 — Android cleartext / backup / R8 (largely resolved)

- **severity:** P2 (was P0; Phase 12 added network security, backup exclusions, release minify)
- **owner:** CLIENT / INFRASTRUCTURE
- **affected:** Android release
- **description:** Manifest now references `network_security_config` (cleartext denied in base), `backup_rules` / `data_extraction_rules` exclude `vitran_secure_session`, and release `isMinifyEnabled = true`. Remaining: signed release R8 smoke not executed in Phase 12 session; Local OTP still must stay debug-only.
- **evidence:** `androidApp/.../network_security_config.xml`; `backup_rules.xml`; `androidApp/build.gradle.kts`
- **release impact:** Need one successful signed minify release before Play.
- **required resolution:** Run signed `:androidApp:assembleRelease` smoke with store secrets.
- **workaround:** None for Play upload — complete EXTERNAL signing + R8 smoke.

---

## P1 — Major risks

### P12-008 — Seller analytics dashboard schema unresolved

- **severity:** P1
- **owner:** BACKEND
- **affected:** Seller analytics dashboard
- **description:** `GET /seller/shops/{id}/analytics` has no saved Postman response example. Dashboard DTOs were not invented; ViewModel stays contract-unresolved. CSV export path exists separately.
- **evidence:** Gaps 5, 42; seller-analytics-and-boosts.md §§3–4
- **release impact:** In-app analytics dashboard cannot ship as complete.
- **required resolution:** Verified response schema (`available_metrics` / `locked_metrics` / timeseries) then client mapping.
- **workaround:** Ship CSV export only; UI states dashboard blocked.

### P12-009 — Advanced catalog search unresolved

- **severity:** P1
- **owner:** BACKEND
- **affected:** `GET /catalog/search` / advanced search UX
- **description:** No Postman response example; no `searchCatalog()` API or response DTOs. Not invented.
- **evidence:** Gaps 5, 19
- **release impact:** Advanced catalog search cannot be advertised.
- **required resolution:** Capture and verify response schema; implement client only after.
- **workaround:** Simple `GET /products/search` only.

### P12-010 — Seller `category_slugs` numeric vs taxonomy string slugs

- **severity:** P1
- **owner:** BACKEND
- **affected:** Create / update seller shop categories
- **description:** Seller create/update/examples use `"category_slugs": [1]` (numbers). Public taxonomy exposes string slugs (`aa-1-2-3-4`) with no mappable numeric CategoryId. Client sends empty list rather than inventing IDs.
- **evidence:** Gap 30; [seller-shop-management.md](seller-shop-management.md)
- **release impact:** New shops cannot attach real categories from taxonomy UI.
- **required resolution:** Backend unifies ID/slug mapping (or exposes numeric IDs on taxonomy).
- **workaround:** Send empty `category_slugs`; do not invent `CategoryId`.

### P12-011 — Home section item schemas mock / unverified

- **severity:** P1
- **owner:** BACKEND / CLIENT
- **affected:** Home feed UI
- **description:** `GET /home` envelope exists in Postman; live probe historically 404; non-empty item shapes for featured/popular/categories/following/personal unverified. `HomeScreen` sections remain mock; `itemsVerified = false`.
- **evidence:** Gap 18; [public-marketplace.md](public-marketplace.md) (as referenced from gaps)
- **release impact:** Home is not API-complete for production discovery UX.
- **required resolution:** Verified non-empty schemas + wire sections; stop mock when `itemsVerified`.
- **workaround:** Keep mock sections; cache only verified envelope fields.

### P12-012 — Admin comment moderation queue missing

- **severity:** P1
- **owner:** BACKEND
- **affected:** Admin comment moderation
- **description:** Only `PATCH /admin/comments/{id}/confirm` exists. No admin list/discovery endpoint. Client confirms by known ID only; does not fabricate a queue from public comments.
- **evidence:** Gap 43; [admin-and-cms.md](admin-and-cms.md) §12
- **release impact:** Comment moderation workflow incomplete for operators.
- **required resolution:** Admin comment list/queue API with stable item schema.
- **workaround:** Confirm known numeric IDs only; UI discloses no queue.

### P12-013 — Process-death payment pending is memory-only

- **severity:** P1
- **owner:** CLIENT / BACKEND
- **affected:** Payment resume after process death
- **description:** Pending payment/session context is in-memory ViewModel/state only. App kill mid-payment loses resume context (distinct from missing deep-link return).
- **evidence:** Gap 41; security-production-review payment section
- **release impact:** Users who background-kill during gateway may need manual re-select + verify.
- **required resolution:** Minimal durable non-secret pending record (shopId/planId/baseline) + return contract (P12-003); still verify via subscription refresh.
- **workaround:** Manual shop/plan re-select + “Check payment”.

### P12-014 — Wasm Room OPFS hosting verification

- **severity:** P1
- **owner:** INFRASTRUCTURE
- **affected:** Web offline Room cache (Wasm)
- **description:** Repo `nginx.conf` now sets COOP/COEP (+ CORP on CDN proxies). Production Traefik/host must actually serve those headers. OPFS still needs the real AndroidX sqlite-wasm worker (placeholder `sqlite3.worker.js` is not production-complete).
- **evidence:** `nginx.conf`; `WasmDatabaseFactory.kt`; `webApp/.../sqlite3.worker.js`; [persistence-offline-strategy.md](persistence-offline-strategy.md)
- **release impact:** Without live headers + worker, Wasm DB may fail open or lack durable OPFS.
- **required resolution:** Deploy/verify headers on vitran.ir; vendor official worker asset.
- **workaround:** Accept weaker offline until verified.

### P12-015 — Android release signing not configured

- **severity:** P1
- **owner:** INFRASTRUCTURE / EXTERNAL STORE/PLATFORM
- **affected:** Play / sideload release APK·AAB
- **description:** `signingConfigs.release` reads env/`local.properties` (no secrets in git). Minify/R8 is **enabled** for release. Store keystore values must still be supplied EXTERNAL before a shippable AAB.
- **evidence:** `androidApp/build.gradle.kts`; release-readiness Android section
- **release impact:** Cannot publish without EXTERNAL signing secrets.
- **required resolution:** Configure CI/local secrets; smoke signed `assembleRelease`.
- **workaround:** Debug/unsigned local builds only.

### P12-016 — iOS `TEAM_ID` empty

- **severity:** P1
- **owner:** INFRASTRUCTURE / EXTERNAL STORE/PLATFORM
- **affected:** iOS Archive / App Store
- **description:** `iosApp/Configuration/Config.xcconfig` has `TEAM_ID=` empty; Xcode `DEVELOPMENT_TEAM = "${TEAM_ID}"`.
- **evidence:** `Config.xcconfig`; `iosApp.xcodeproj/project.pbxproj`
- **release impact:** Device provisioning / Archive blocked until Apple team is set.
- **required resolution:** Set team + profiles via EXTERNAL Apple account (do not commit secrets beyond team id if policy allows).
- **workaround:** Simulator-only development.

### P12-017 — Store privacy / support URLs & policy verification

- **severity:** P1
- **owner:** PRODUCT / EXTERNAL STORE/PLATFORM
- **affected:** Play Console Data Safety / App Store Privacy; store listing links
- **description:** Store forms and listing need real privacy/support URLs and current-policy EXTERNAL verification. In-app CMS slugs (`privacy`, `terms`, …) must resolve from backend content; listing URLs must be real public pages.
- **evidence:** release-readiness Privacy + Store metadata; admin-and-cms static page slug notes; Gap/CMS seeding (`privacy-policy`, etc.)
- **release impact:** Store submission blocked or rejected until forms/URLs/policy check complete.
- **required resolution:** Live privacy/support URLs + completed store questionnaires + EXTERNAL current-policy check.
- **workaround:** None for store submission.

### P12-018 — Boost `price_paid` may be client-trusted (server)

- **severity:** P1
- **owner:** BACKEND
- **affected:** Placement boost create (when enabled)
- **description:** If/when Boost POST is enabled, server may accept client-supplied `price_paid` without authoritative price list — financial integrity risk.
- **evidence:** Gap 42; seller-analytics-and-boosts.md §25
- **release impact:** Under/over-charge or fraud if create ships without server pricing.
- **required resolution:** Server-side price validation / ignore client amount; expose pricing API.
- **workaround:** Do not enable Boost create (see P12-005).

### P12-019 — Personalized home feed unresolved

- **severity:** P1
- **owner:** BACKEND
- **affected:** `GET /me/home/feed`
- **description:** No Postman response example; not implemented. Home uses public `/home` only.
- **evidence:** Gaps 5, 20
- **release impact:** Authenticated personalized Home cannot ship.
- **required resolution:** Verified schema after authenticated probe.
- **workaround:** Public Home only (still subject to P12-011).

---

## P2 — Important / accepted-with-caveat

### P12-020 — Crash reporting provider missing (NoOp)

- **severity:** P2 (not P0)
- **owner:** INFRASTRUCTURE / PRODUCT / EXTERNAL STORE/PLATFORM
- **affected:** All targets
- **description:** Observability calls for a vendor-neutral `CrashReporter` with production binding `NoOpCrashReporter` until an EXTERNAL provider account exists. Missing aggregation is operational risk, not a functional ship stop, if PRODUCT accepts store-native crash reports only.
- **evidence:** [observability.md](observability.md); release-readiness Monitoring
- **release impact:** No first-party crash aggregation / breadcrumbs in production.
- **required resolution:** Choose vendor + privacy review + wire implementation; or explicitly accept NoOp.
- **workaround:** Rely on Play / App Store crash reports temporarily.

### P12-021 — Cities API lacks province hierarchy

- **severity:** P2
- **owner:** BACKEND / PRODUCT
- **affected:** Create Store province → city cascade
- **description:** `GET /cities` is flat `{ id, slug, name }` with no `province_id`. Province selector remains mock-only; all API cities shown when a province is selected.
- **evidence:** Gap 13; [reference-data.md](reference-data.md)
- **release impact:** Geography UX inaccurate vs real Iranian province filtering.
- **required resolution:** Backend province / nested geography.
- **workaround:** Current Phase 4 client behavior (documented).

### P12-022 — Category list lacks browse visuals

- **severity:** P2
- **owner:** BACKEND / CLIENT
- **affected:** Categories browse grid
- **description:** Taxonomy list nodes lack `icon_url` / marketing collage assets. UI uses index-based visual fallbacks.
- **evidence:** Gap 16; `BrowseCategoryVisuals.kt` (client fallback)
- **release impact:** Browse visuals are approximate, not CMS-driven.
- **required resolution:** Enriched taxonomy or Home assets from backend.
- **workaround:** Keep deterministic local fallbacks.

### P12-023 — Follow list / status response schemas unresolved

- **severity:** P2
- **owner:** BACKEND
- **affected:** Following screen / followed-shop list
- **description:** Follow POST/DELETE exist; list/get response item shapes unverified — not invented. `FollowingScreen` remains mock. Follow vs favorite semantics still open (Gap 3).
- **evidence:** Gaps 3, 5, 21, 22
- **release impact:** Following hub not API-complete.
- **required resolution:** Verified list/status schemas + product semantics vs favorites.
- **workaround:** Mutation-only follow; mock Following UI.

### P12-024 — Review / public comment metadata thin

- **severity:** P2
- **owner:** BACKEND / PRODUCT
- **affected:** PDP reviews; public shop comments
- **description:** Reviews are `id`, `product_id`, `user_id`, `rating`, `comment` only — hide author/date/histogram/helpful. Public comments are `id`, `title`, `confirmed` only; pending submissions not appended.
- **evidence:** Gaps 28, 29
- **release impact:** Social-proof UX limited vs shop.app-class PDP.
- **required resolution:** Backend metadata enrichment if PRODUCT requires richer PDP/comments.
- **workaround:** Render only available fields; no invented authors/dates.

### P12-025 — Public shop detail visual fields missing

- **severity:** P2
- **owner:** BACKEND
- **affected:** Storefront `StoreScreen`
- **description:** Public shop payload lacks cover/avatar/wordmark/brand color/rating/collections expected by richer UI mocks.
- **evidence:** Gap 17
- **release impact:** Store page uses placeholders for visual-only fields.
- **required resolution:** Enriched public shop projection or media endpoint.
- **workaround:** Map title/slug/categories/share URL; placeholders for the rest.

### P12-026 — Android backup / session exclusion (resolved)

- **severity:** P3 (resolved in Phase 12; keep for audit trail)
- **owner:** CLIENT
- **affected:** Android credential backup/restore
- **description:** Manifest now wires `fullBackupContent` / `dataExtractionRules` excluding `vitran_secure_session`.
- **evidence:** `backup_rules.xml`; `data_extraction_rules.xml`; `AndroidManifest.xml`
- **release impact:** None remaining for this item.
- **required resolution:** Done — optional device-restore smoke later.
- **workaround:** N/A.

---

## P3 — Polish / accepted decisions

### P12-027 — Certificate pinning not implemented (accepted)

- **severity:** P3
- **owner:** PRODUCT / INFRASTRUCTURE
- **affected:** TLS for all clients
- **description:** No certificate pinning. Decision: avoid pinning without rotation/ops plan (pinning outages are a real risk). Standard system TLS trust remains.
- **evidence:** security-production-review.md “No certificate pinning (ops risk; decision documented)”
- **release impact:** None for current threat model (accepted).
- **required resolution:** N/A unless threat model changes; if added later, require pin rotation runbook.
- **workaround:** Standard platform TLS to `https://api.vitran.ir`.

### P12-028 — Duplicate category lookup routes / SEO alias

- **severity:** P3
- **owner:** BACKEND / CLIENT
- **affected:** Category by-slug fetch
- **description:** `GET /categories/{slug}` and `GET /categories/slug/{slug}` appear identical; client uses the former as canonical.
- **evidence:** Gap 15
- **release impact:** None if one route remains stable.
- **required resolution:** Backend documents semantic difference or deprecates alias.
- **workaround:** Canonical path already chosen.

### P12-029 — Category localized name nullability

- **severity:** P3
- **owner:** BACKEND / CLIENT
- **affected:** Taxonomy display names
- **description:** Imported taxonomy may omit Persian `name` until admin PATCH; domain keeps nullable + `displayName` fallback.
- **evidence:** Gap 14
- **release impact:** Some categories show source title instead of Persian name.
- **required resolution:** Admin localization coverage or import guarantees.
- **workaround:** Existing `localizedName` / `sourceTitle` fallback.

### P12-030 — Billing history / cancel-downgrade APIs missing

- **severity:** P3 (P1 if PRODUCT advertises billing portal)
- **owner:** BACKEND / PRODUCT
- **affected:** Subscription management UX
- **description:** No billing history/invoices API; no cancel/downgrade-to-Free API. Client does not fake these actions.
- **evidence:** Gap 41
- **release impact:** Payments list empty; no self-serve cancel in-app.
- **required resolution:** Backend APIs or PRODUCT omits those affordances.
- **workaround:** Leave actions unimplemented / hidden.

### P12-031 — Wishlist share storefront URL undocumented

- **severity:** P3
- **owner:** BACKEND / PRODUCT
- **affected:** Wishlist sharing
- **description:** Backend returns `share_slug` without documented storefront URL. Client must not invent `https://vitran.ir/wishlist/{slug}`.
- **evidence:** Gap 27
- **release impact:** Wishlist public share link unavailable.
- **required resolution:** Documented share URL template or web route.
- **workaround:** Share only known PDP/store URLs.

### P12-032 — Plan `features` PATCH merge vs replace (Admin)

- **severity:** P3
- **owner:** BACKEND
- **affected:** Admin plan feature editing
- **description:** MERGE vs REPLACE semantics for heterogeneous `features` unresolved. Client sends full raw object when edited, omits when untouched.
- **evidence:** Gap 43
- **release impact:** Possible accidental feature-key loss if server replaces and client omitted unknowns incorrectly — mitigated by sending complete raw object when touched.
- **required resolution:** Backend documents merge/replace; client adjusts.
- **workaround:** Current send-complete-object-on-edit policy.

---

## External tasks

Tracked outside the app repo; required for store/production ops even when client code is ready.

| Task | Owner | Notes |
|------|-------|-------|
| Android upload keystore + Play App Signing | EXTERNAL STORE/PLATFORM / INFRASTRUCTURE | Never commit passwords; wire CI secrets (P12-015) |
| Apple Developer team, certs, profiles, notarization (Desktop if shipped) | EXTERNAL STORE/PLATFORM | Fill `TEAM_ID`; Archive on macOS (P12-016) |
| Privacy Policy + Support URLs live on HTTPS | PRODUCT / INFRASTRUCTURE | CMS + store listing (P12-017) |
| Google Play Data Safety form | EXTERNAL STORE/PLATFORM | EXTERNAL VERIFICATION REQUIRED |
| App Store Privacy Nutrition Labels | EXTERNAL STORE/PLATFORM | EXTERNAL VERIFICATION REQUIRED |
| Current Play / App Store policy review | EXTERNAL STORE/PLATFORM | EXTERNAL VERIFICATION REQUIRED — do not assume prior year rules |
| Payment provider sandbox + production dashboard | EXTERNAL STORE/PLATFORM / BACKEND | Align return URLs with P12-003 |
| Android App Links / Apple Universal Links files (if payment return used) | INFRASTRUCTURE / BACKEND | assetlinks.json / apple-app-site-association |
| Crash reporter project (Sentry/Firebase/etc.) | EXTERNAL STORE/PLATFORM / PRODUCT | Privacy review before wire-up (P12-020) |
| Production Wasm host COOP/COEP (+ CORP on CDN proxies) | INFRASTRUCTURE | Verify beyond Traefik TLS (P12-014) |
| Marketing screenshots & store listing copy | PRODUCT / EXTERNAL STORE/PLATFORM | Manual / EXTERNAL |
| Backend gap closures (analytics, catalog search, category IDs, Home schemas, comment queue, currency, boost pricing) | BACKEND | See api-gaps.md |

---

## Target Go / No-Go

**Status:** Final after Phase 12 implementation (see [phase-12-final-report.md](phase-12-final-report.md)).

| Target | Status | Blocking reasons |
|--------|--------|------------------|
| **Android** | **NO-GO** | Signing EXTERNAL; payment return / commercial gaps if advertised; store privacy EXTERNAL |
| **iOS** | **NO-GO** | TEAM_ID empty; archive unverified; same commercial gaps |
| **Web (Wasm)** | **NO-GO** | In-memory auth (P12-001); OPFS worker incomplete; payment return |
| **Desktop** | **NO-GO** | Packaging/signing EXTERNAL; commercial gaps (encrypted file store landed for tokens) |

**Hard rule:** No target with an open **P0** for a feature it advertises is marked **GO**.

Project label: **FUNCTIONALLY COMPLETE BUT BLOCKED**.
