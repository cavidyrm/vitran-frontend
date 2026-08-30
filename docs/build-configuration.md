# Build Configuration

VitranShop build and environment ownership for all KMP targets.

## Version ownership

Central catalog: [`gradle/libs.versions.toml`](../gradle/libs.versions.toml)

| Component | Version | Catalog key |
|-----------|---------|-------------|
| Kotlin | 2.4.10 | `kotlin` |
| Compose Multiplatform | 1.11.1 | `composeMultiplatform` |
| Compose Material3 | 1.11.0-alpha07 | `material3` |
| Android Gradle Plugin | 9.2.1 | `agp` |
| Gradle wrapper | 9.6.1 | `gradle/wrapper/gradle-wrapper.properties` |
| Ktor (Coil in `:shared`; API client in `:core:network`) | 3.1.3 | `ktor` |
| Navigation 3 | 1.1.1 | `navigation3` |
| Koin | 4.0.0 | `koin` |
| kotlinx-coroutines | 1.11.0 | `kotlinx-coroutines` |
| kotlinx-serialization | 1.11.0 | `kotlinx-serialization` |

Do not scatter versions in module `build.gradle.kts` files except where AGP requires explicit SDK ints from the catalog.

## Gradle modules (Phase 1)

| Module | Type | Role |
|--------|------|------|
| `:shared` | KMP library | UI, navigation, mocks, DI bootstrap |
| `:core:common` | KMP library | Shared primitives |
| `:core:network` | KMP library | Ktor client, envelope, executor, HealthApi |
| `:core:domain` | KMP library | Cross-feature types, AppError, AppResult, pagination |
| `:core:session` | KMP library | Session contracts |
| `:androidApp` | Android app | Thin launcher |
| `:desktopApp` | JVM app | Compose Desktop launcher |
| `:webApp` | KMP JS/Wasm | Browser launcher |
| `iosApp/` | Xcode | iOS launcher (not in Gradle settings) |

No `build-logic/` convention plugins in Phase 1 — duplication does not yet justify them.

## KMP targets

Configured in [`shared/build.gradle.kts`](../shared/build.gradle.kts) and mirrored in core modules:

| Target | Output |
|--------|--------|
| `android` | KMP Android library |
| `iosArm64`, `iosSimulatorArm64` | Static `Shared` / `Core*` frameworks |
| `jvm` | Desktop + shared JVM |
| `js` | Browser |
| `wasmJs` | Browser |

## Source-set hierarchy

Uses Kotlin default hierarchy — **no custom `dependsOn` chains** added in Phase 1.

| Source set | Purpose |
|------------|---------|
| `commonMain` | Default for shared logic |
| `commonTest` | Shared unit tests |
| `androidMain` | Android Ktor engine (Coil + `:core:network`) |
| `iosMain` | Darwin Ktor engine |
| `jvmMain` | Java Ktor engine |
| `jsMain`, `wasmJsMain` | JS Ktor engine (`:core:network`), browser wrappers |
| `androidHostTest`, `iosTest`, `jvmTest`, `webTest` | Platform tests |

## Gradle properties

[`gradle.properties`](../gradle.properties):

- Configuration cache and build cache enabled
- JVM heap: 4096M (Gradle), 3072M (Kotlin daemon)
- AndroidX, non-transitive R classes

## Web SPA fallback

[`webApp/webpack.config.d/spa-history-fallback.js`](../webApp/webpack.config.d/spa-history-fallback.js) enables `historyApiFallback` for client-side routes in development. Production hosts need equivalent rewrites.

## API environment ownership

Configuration lives in `:core:network`:

```kotlin
ApiEnvironment(origin = "http://localhost:8080", apiVersionPath = "/api/v1")
// → apiBaseUrl = "http://localhost:8080/api/v1"
```

| Environment | Origin | Notes |
|-------------|--------|-------|
| Local / development | `http://localhost:8080` | `ApiEnvironments.Local` |
| Production | `https://api.vitran.ir` | `ApiEnvironments.Production` (default in `startVitranKoin`) |

**Rules:**

- `origin` is the API host only — **never** `https://api.vitran.ir/api`
- API prefix `/api/v1` is separate (`apiVersionPath`)
- Origins are configuration, not secrets

Phase 2 wires `networkModule` + `sessionModule` via Koin in [`VitranKoin.kt`](../shared/src/commonMain/kotlin/com/vitran/shop/di/VitranKoin.kt). The default app environment is `ApiEnvironments.Production`. HTTP logging is enabled for local environment only — see [networking.md](networking.md).

### Future platform-specific overrides

| Platform | Future approach |
|----------|-----------------|
| Android | `BuildConfig.API_ORIGIN` per build type (debug/release) |
| iOS / Desktop / Web | Shared config object or compile-time flag — not Android-only |

## Android build types

[`androidApp/build.gradle.kts`](../androidApp/build.gradle.kts): `debug` and `release` (minify off). Future: inject production origin into Koin startup from `BuildConfig` without committing secrets.

## Secret management

**Safe in repo:** API origin, API version path, environment name, logging flags.

**Never commit:** private keys, signing passwords, backend credentials, service account secrets, user tokens.

Authentication tokens (`accessToken`, `refreshToken`, `tempToken`) must use **secure platform storage** in Phase 3 — not Room, not plain SharedPreferences/DataStore in `commonMain`.

## Static analysis

No ktlint, detekt, or Spotless configured today. Recommendation for production hardening: adopt **one** formatter/linter in a dedicated tooling phase — not Phase 1.

## Running builds and tests

See [`README.md`](../README.md). After dependency changes, sync Gradle in Android Studio before compiling.

Example verification tasks:

```bash
export GRADLE_USER_HOME="$HOME/.gradle"
./gradlew :shared:compileKotlinJvm :androidApp:compileDebugKotlin :desktopApp:compileKotlin
```

## Postman contract reference

[`docs/postman/vitran-api.postman_collection.json`](postman/vitran-api.postman_collection.json) — source of truth for API documentation in this repo.
