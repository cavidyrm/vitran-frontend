package com.vitran.shop.core.network.config

/**
 * Builds an absolute URL from [origin] and an unversioned path such as `/health`.
 */
fun ApiEnvironment.originUrl(path: String): String {
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    return origin.trimEnd('/') + normalizedPath
}

/**
 * Builds an absolute URL under the versioned API prefix, e.g. `/shops`.
 */
fun ApiEnvironment.apiUrl(path: String): String {
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    return apiBaseUrl.trimEnd('/') + normalizedPath
}
