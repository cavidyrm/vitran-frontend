# Performance (Phase 12)

Measurements below are qualitative / code-review based unless a numbered run is recorded. Do not invent benchmark numbers.

## Startup findings

- Avoid eager taxonomy/Home/Plans/Admin network at DI graph construction
- Database opens lazily via DI singleton; do not block first Compose frame on migrations
- Session restore remains explicit (`SessionState.Restoring`)

## Compose findings

- Prefer immutable `UiState`
- Stable Lazy keys (`ProductId`, `ShopId`, `CityId`, `PlanId`)
- Avoid remapping large Home/taxonomy trees on unrelated UI ticks

## Network findings

- Ktor suspend stays async; no blanket `Dispatchers.IO` wraps
- Search debounce/cancellation retained
- Large upload/export timeouts remain deliberate (Phase 8/10)

## Database findings

- Indices on slug, parentId, shopId, productId as used by DAOs
- Transactional multi-table taxonomy/home replace
- Bound detail snapshot growth via eviction on 404 + documented cleanup

## Image / file findings

- Coil caches images; Room stores URLs only
- Avoid unnecessary ByteArray copies on modest uploads
- Stream taxonomy import / CSV where architecture already supports it

## Memory findings

Watch multipart image lists, CSV downloads, taxonomy import payloads.

## Optimizations performed

Documented in Phase 12 implementation notes: lazy DB, public cache Flow, R8 when enabled, remove inappropriate `GlobalScope`.

## Measurements / limitations

Full Baseline Profiles / macrobenchmarks not required for v1. Wasm OPFS latency TBD per browser.

## Remaining performance risks

Large taxonomy reconstruction; unverified Home item schemas (mock UI); image-heavy seller forms; Wasm bundle size.
