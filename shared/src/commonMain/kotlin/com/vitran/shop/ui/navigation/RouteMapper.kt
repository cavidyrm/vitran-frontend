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
            Route.Login -> "/account/login"
            Route.Register -> "/account/register"
            is Route.RegisterVerify -> "/account/register/verify"
            Route.ForgotPassword -> "/account/forgot"
            is Route.ResetPassword -> "/account/forgot/reset"
            Route.CreateStore -> "/admin/stores/new"
            Route.CreateProduct -> "/admin/products/new"
            Route.CreateCategory -> "/admin/categories/new"
            is Route.ProductDetail -> "/products/${route.productId}/${route.slug}"
            is Route.Store -> "/m/${route.shopId}"
        }

    fun fromPath(path: String): Route? {
        val normalized = normalizePath(path)
        return when (normalized) {
            "/", "" -> Route.Home
            "/categories" -> Route.Categories
            "/offers" -> Route.Offers
            "/saved" -> Route.Saved
            "/account/login" -> Route.Login
            "/account/register" -> Route.Register
            "/account/register/verify" -> Route.RegisterVerify()
            "/account/forgot" -> Route.ForgotPassword
            "/account/forgot/reset" -> Route.ResetPassword()
            "/account" -> Route.Account
            "/admin/stores/new" -> Route.CreateStore
            "/admin/products/new" -> Route.CreateProduct
            "/admin/categories/new" -> Route.CreateCategory
            else -> parseProductPath(normalized) ?: parseStorePath(normalized)
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

    private fun parseProductPath(path: String): Route.ProductDetail? {
        // /products/{id}/{slug}
        val parts = path.trim('/').split('/')
        if (parts.size != 3) return null
        if (parts[0] != "products") return null
        val id = parts[1]
        val slug = parts[2]
        if (id.isEmpty() || slug.isEmpty()) return null
        if (id.contains('/') || slug.contains('/')) return null
        return Route.ProductDetail(productId = id, slug = slug)
    }

    private fun parseStorePath(path: String): Route.Store? {
        // /m/{shopId} — shop.app merchant handle
        val parts = path.trim('/').split('/')
        if (parts.size != 2) return null
        if (parts[0] != "m") return null
        val shopId = parts[1]
        if (shopId.isEmpty() || shopId.contains('/')) return null
        return Route.Store(shopId = shopId)
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
