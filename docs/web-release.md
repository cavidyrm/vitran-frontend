# Web / Wasm production notes (Phase 12)

## Build

```bash
./gradlew :webApp:wasmJsBrowserDistribution
```

Artifact: `webApp/build/dist/wasmJs/productionExecutable` (see Dockerfile).

## Hosting

- HTTPS via Traefik
- SPA fallback: nginx `try_files`
- **COOP/COEP** required for Room OPFS (`nginx.conf`)
- CSP baseline in `nginx.conf` — do not allow CMS scripts via `unsafe-inline` script-src beyond wasm eval needs

## Credentials

- Bearer tokens: **in-memory only** (no localStorage)
- Authenticated Web is session-tab scoped — see production-blockers P12-001

## Persistence

- Room 3 via `WasmDatabaseFactory` + `WebWorkerSQLiteDriver` when `sqlite3.worker.js` implements the AndroidX protocol
- Placeholder worker falls back to in-memory SQLite
- Vendor the official AndroidX sqlite-wasm worker before claiming durable OPFS offline

## Source maps

Decide per deploy: public / private to crash tooling / disabled. Do not expose map upload credentials.

## CORS

Browser API calls are same-origin: the Wasm client uses `window.location.origin`, Traefik routes `/api/*` on `vitran.ir` to the backend, and nginx/webpack proxy `/api` and `/health` when that split is not in front. Native apps still use `https://api.vitran.ir` (no CORS). Do not expect `api.vitran.ir` to send `Access-Control-Allow-Origin`.

## Payment

Same as other targets: open URL ≠ success; no invented return deep link.
