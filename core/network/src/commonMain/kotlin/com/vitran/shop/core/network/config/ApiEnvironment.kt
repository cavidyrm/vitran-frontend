package com.vitran.shop.core.network.config

/**
 * API origin + version path. Do not embed `/api` in [origin].
 *
 * Example:
 * - origin: `https://api.vitran.ir`
 * - apiVersionPath: `/api/v1`
 * - resolved base: `https://api.vitran.ir/api/v1`
 * - health: `https://api.vitran.ir/health` via [originUrl]
 */
data class ApiEnvironment(
    val origin: String,
    val apiVersionPath: String = DEFAULT_API_VERSION_PATH,
) {
    val apiBaseUrl: String =
        origin.trimEnd('/') + apiVersionPath

    companion object {
        const val DEFAULT_API_VERSION_PATH: String = "/api/v1"
    }
}

object ApiEnvironments {
    val Local = ApiEnvironment(origin = "http://localhost:8080")
    val Production = ApiEnvironment(origin = "https://api.vitran.ir")
}
