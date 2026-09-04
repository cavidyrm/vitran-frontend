# VitranShop Networking

Phase 2 shared networking foundation for all future API features. Transport stays in `:core:network`; domain-safe errors and pagination live in `:core:domain`.

## 1. Network module ownership

| Concern | Module | Package |
|---------|--------|---------|
| HttpClient, plugins, executor | `:core:network` | `com.vitran.shop.core.network.*` |
| API envelope / DTOs | `:core:network` | `model`, `pagination`, `health` |
| AppError / AppResult | `:core:domain` | `error`, `result` |
| Domain pagination | `:core:domain` | `pagination` |
| Auth mode enum | `:core:domain` | `auth.AuthMode` |
| Session read contract | `:core:session` | `SessionReader` |
| DI modules | `:core:network`, `:core:session` | `di` |

Feature `data/remote` APIs (Phase 3+) depend on `:core:network` and map DTOs to feature domain — never expose `ApiEnvelope` to presentation.

## 2. HttpClient lifecycle

- **One application-scoped singleton** provided by Koin (`networkModule`).
- **Two HttpClient instances** in `networkModule`: the main client installs `SessionAuthPlugin`; token refresh uses a separate unauthenticated client to avoid a Koin circular dependency (`HttpClient` → `SessionAuthCoordinator` → `TokenRefreshRemoteDataSource` → `HttpClient`).
- Platform engines: Android, Darwin, Java, JS (including Wasm browser target).
- Tests inject `MockEngine` through the optional `engine` parameter — no global singleton client object.
- Close behavior: Ktor manages engine lifecycle with the app; features must not create or close clients per request.

## 3. ApiConfig / URL construction

Phase 1 `ApiEnvironment` remains the source of truth:

```kotlin
ApiEnvironment(origin = "http://localhost:8080", apiVersionPath = "/api/v1")
// apiBaseUrl → http://localhost:8080/api/v1
```

Helpers in `config/ApiEnvironmentUrls.kt`:

- `originUrl("/health")` → unversioned routes (`GET /health`)
- `apiUrl("/shops")` → versioned routes (`GET /api/v1/shops`)

Never embed `/api/v1` inside `origin` or hard-code full URLs in feature API methods.

## 4. JSON serialization

Central `Json` instance from `serialization/NetworkJson.kt`:

```kotlin
Json {
    ignoreUnknownKeys = true
    isLenient = false
    encodeDefaults = true
    explicitNulls = false
}
```

- DTOs use `@SerialName` for snake_case transport fields.
- No global lenient coercion or number-to-string hacks.
- Domain models are **not** `@Serializable`.

## 5. ApiEnvelope

```kotlin
@Serializable
data class ApiEnvelope<T>(
    val success: Boolean,
    val message: String,
    val code: Int,
    val data: T? = null,
    val errors: List<ApiErrorDto> = emptyList(),
)
```

- `code` is `Int` (not a two-value enum): `1`, `-2`, `400`, `403`, etc.
- `EmptyDataDto` handles successful `{}` payloads.
- Inner endpoint wrappers (`data.user`, `data.products`) belong in feature DTOs, not core.

## 6. API error handling

Policy: **`expectSuccess = false`** — read body once, parse envelope, evaluate **HTTP status + `success` + `code` + `errors`**.

| HTTP | Envelope | Result |
|------|----------|--------|
| 2xx | `success=true` | `AppResult.Success(data)` |
| 2xx | `success=false` | Mapped `AppError` |
| 4xx/5xx | parsed envelope | Mapped `AppError` with preserved metadata |
| 4xx/5xx | unparseable body | Status-based fallback |

`ApiErrorDto` preserves `reason` + `messages[]` for field-level UI.

Presentation policy (`splitForForm` in `:core:domain`):

- When `errors` is non-empty, show each joined `messages` list under the input whose name matches `reason` (case-insensitive). Aliases (e.g. `category_slug` → category, `new_password` → password) live in the feature ViewModel.
- If every `reason` maps to a visible field, **do not** also show the envelope `message` (e.g. «داده‌های ارسالی نامعتبر است.»).
- Unmapped reasons (including login `credentials`) stay on the form banner.
- This applies to form POSTs (and the shared plans editor, which also binds PATCH). Mapping is **not** gated on HTTP method in `ApiErrorMapper`.

## 7. AppError mapping

Generic hierarchy in `:core:domain/error/AppError.kt`:

- `Network.{NoConnection, Timeout, ConnectionFailure, ServerUnavailable}`
- `Authentication.{Unauthorized, SessionExpired}`
- `Forbidden`, `NotFound`, `Conflict`, `Validation`, `Server`, `Serialization`, `Unexpected`

Shared metadata on applicable variants: `message`, `httpStatus`, `backendCode`, `fieldErrors`, **`errorDataJson`**.

HTTP-specific statuses (401, 403, 404, 409) take precedence over generic validation when both apply.

Feature repositories refine generic errors (e.g. `AppError.Conflict` + `reason=slug` → `ShopError.SlugAlreadyTaken`) in feature domain — not in core.

## 8. Result representation

```kotlin
sealed class AppResult<out T> {
    data class Success<T>(val value: T)
    data class Failure(val error: AppError)
}
```

Success returns **unwrapped `data` only** — not envelope `message`/`code`. Mutation flows needing server messages can read them from repository layer if required later.

## 9. Authentication modes (Phase 3)

Reuse `AuthMode` from `:core:domain`:

| Mode | Behavior |
|------|----------|
| `None` | Never attach Bearer token |
| `Optional` | Attach if session exists; proactive refresh near expiry; GET/HEAD may retry once after 401 refresh |
| `Required` | Fail locally with `SessionExpired` if no credentials; proactive refresh; single 401 retry |

Set per request:

```kotlin
client.get(url) { authMode(AuthMode.Required) }
```

`SessionAuthPlugin` (not `AuthHeaderPlugin`) calls `SessionAuthCoordinator` for token resolution and 401 handling.

Refresh endpoint uses `AuthMode.None` + `markSkipSessionAuth()` to prevent recursion.

### Optional mutation policy

- **GET/HEAD:** may retry after failed auth refresh
- **POST/PUT/PATCH/DELETE:** do **not** silently replay without token after 401

## 10. Token integration (implemented Phase 3)

- `DefaultSessionReader` backed by `CredentialStore` + secure storage
- `SessionAuthPlugin` installed in `HttpClientFactory`
- Token refresh via `TokenRefreshCoordinator` (mutex single-flight)
- Separate from `HttpRequestRetry` transport retry

## 11. Timeout policy

`NetworkTimeouts` defaults:

| Setting | Default |
|---------|---------|
| Connect | 10s |
| Request | 30s |
| Socket | 30s |

Configured centrally in `HttpTimeout` plugin. Mapped to `AppError.Network.Timeout`.

## 12. Retry policy

`HttpRequestRetry` with `maxRetryCount = 2`:

- **Retry:** GET, HEAD on connection failures, selected 5xx, 429
- **Never retry:** POST, PATCH, PUT, DELETE or 4xx responses
- **Backoff:** exponential, 200ms base, 2s cap

Auth token refresh retry is Phase 3 — architecturally separate.

## 13. Logging / redaction

Controlled by `NetworkDiagnosticsConfig.enableHttpLogging`:

- **Local environment:** headers + optional bodies
- **Production:** logging disabled by default in `networkModule`

Mandatory redaction for: `Authorization`, `access_token`, `refresh_token`, `temp_token`, `otp_code`, `password`, `new_password`, `api_key`, payment authority/URLs.

Implemented in `LoggingSanitizer.kt` + Ktor `sanitizeHeader`. CSV / `Content-Disposition: attachment` bodies are redacted (Phase 10).

## 13b. FileDownloadExecutor (Phase 10)

JSON envelope decoding cannot carry CSV. `FileDownloadExecutor` is a **separate** executor in `:core:network`:

- Success: HTTP 2xx and Content-Type is not HTML → opaque `DownloadResponse` (`ByteArray`).
- Failure: non-2xx → existing `ApiErrorMapper` (never save error JSON as `.csv`).
- Do not log bytes. Memory: `ByteArray` (not unlimited streaming).

CSV export uses this path; `ApiRequestExecutor` stays JSON-only.

## 14. Cursor pagination

Transport (`CursorPageDto<T>`) in `:core:network`; domain (`CursorPage<T>`) in `:core:domain`.

- `next_cursor` is **opaque `String?`** — never parse as Int/Long
- Request: `CursorPagination(cursor, perPage)` — clamped 1..100, default 20
- Query helpers: `ParametersBuilder.appendCursorPagination(...)`

## 15. Page pagination

Transport (`PageDto<T>`) and domain (`PageResult<T>`).

- Request: `PagePagination(page, perPage)` — separate type from cursor mode
- **Never send `page` + `cursor` together** — type system enforces separate request types
- Query helpers: `ParametersBuilder.appendPagePagination(...)`

## 16. Cancellation behavior

`CancellationException` is **rethrown** — never mapped to `AppError.Unexpected`. Important for debounced search, screen lifecycle, and pagination cancellation.

## 17. Test strategy

All tests in `:core:network/src/commonTest` use **Ktor MockEngine** — no live backend, no `localhost` in CI.

Coverage includes: success (200/201), validation, 400/403/404/409, 500, malformed JSON, unknown fields, pagination, cancellation, timeout/connection mapping, retry (GET yes / POST no), logging sanitizer, health API.

Run: `./gradlew :core:network:jvmTest :core:domain:jvmTest`

## 18. Future API service pattern

```kotlin
internal class SomeApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getSomething(): AppResult<SomeDto> =
        executor.execute {
            client.get(environment.apiUrl("/something")) {
                authMode(AuthMode.Optional)
            }
        }
}
```

Repository impl maps `SomeDto` → domain model and refines `AppError` as needed.

### Typed error data (Phase 3 Auth)

On failure, `AppError` may include `errorDataJson` — raw JSON of envelope `data`. Auth feature decodes locally:

```kotlin
when (val result = authApi.login(...)) {
    is AppResult.Failure -> when (val error = result.error) {
        is AppError.Forbidden -> {
            val data = error.errorDataJson?.let { json.decodeFromString<VerificationRequiredDto>(it) }
            // map to feature error — core never knows about temp_token semantics
        }
        else -> ...
    }
}
```

## Local development notes

- **Android cleartext:** `http://localhost:8080` may require debug-only network security config (not yet added).
- **Emulator localhost:** Android emulator may need `10.0.2.2` instead of `localhost` — override `ApiEnvironment` at startup, not in shared network code.
- **Web CORS:** Browser uses the page origin (`defaultApiEnvironment`). Production Traefik already routes `/api/*` on `vitran.ir` to the backend; webpack-dev-server and nginx proxy `/api` and `/health` so local/dev is same-origin too. Do not call `https://api.vitran.ir` from the browser.
- **TLS:** Production uses `https://api.vitran.ir` — never disable certificate validation.
