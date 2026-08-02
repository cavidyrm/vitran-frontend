package com.vitran.shop.ui.navigation

/**
 * Path ↔ [Route] mapping. Paths only — no host/domain constants.
 *
 * Absolute URIs are accepted by [fromUri] by parsing path (and later query);
 * scheme and host are ignored so deep links stay host-agnostic.
 */
object RouteMapper {
    fun toPath(route: Route): String =
        when (route) {
            Route.Home -> "/"
            Route.Categories -> "/categories"
            Route.Offers -> "/offers"
            Route.Saved -> "/saved"
            Route.Account -> "/account"
        }

    fun fromPath(path: String): Route? {
        val normalized = normalizePath(path)
        return when (normalized) {
            "/", "" -> Route.Home
            "/categories" -> Route.Categories
            "/offers" -> Route.Offers
            "/saved" -> Route.Saved
            "/account" -> Route.Account
            else -> null
        }
    }

    /**
     * Parses a relative path or absolute URI into a [Route].
     * Ignores scheme/host; uses path only (query reserved for future routes).
     */
    fun fromUri(uri: String): Route? {
        val path = extractPath(uri)
        return fromPath(path)
    }

    private fun extractPath(uri: String): String {
        val withoutFragment = uri.substringBefore('#')
        val withoutQuery = withoutFragment.substringBefore('?')
        val schemeSeparator = withoutQuery.indexOf("://")
        if (schemeSeparator >= 0) {
            val afterScheme = withoutQuery.substring(schemeSeparator + 3)
            val pathStart = afterScheme.indexOf('/')
            return if (pathStart >= 0) afterScheme.substring(pathStart) else "/"
        }
        return withoutQuery
    }

    private fun normalizePath(path: String): String {
        if (path.isEmpty()) return "/"
        val withLeading = if (path.startsWith('/')) path else "/$path"
        return if (withLeading.length > 1 && withLeading.endsWith('/')) {
            withLeading.dropLast(1)
        } else {
            withLeading
        }
    }
}
