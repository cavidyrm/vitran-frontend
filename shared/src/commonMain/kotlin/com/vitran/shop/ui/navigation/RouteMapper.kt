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
            Route.Profile -> "/account/profile"
            Route.Referrals -> "/account/referrals"
            Route.Following -> "/account/following"
            Route.AccountSettings -> "/account/settings"
            Route.AccountUsers -> "/account/users"
            is Route.AccountUserDetail -> "/account/users/${route.userId}"
            Route.AccountCities -> "/account/cities"
            Route.AccountCityCreate -> "/account/cities/new"
            is Route.AccountCityDetail -> "/account/cities/${route.cityId}"
            Route.Login -> "/account/login"
            Route.Register -> "/account/register"
            is Route.RegisterVerify -> "/account/register/verify"
            Route.ForgotPassword -> "/account/forgot"
            is Route.ResetPassword -> "/account/forgot/reset"
            Route.CreateStore -> "/admin/stores/new"
            Route.StorePlan -> "/admin/stores/plan"
            Route.StorePlanUpgrade -> "/admin/stores/plan/upgrade"
            Route.AdminPlans -> "/admin/plans"
            Route.CreateProduct -> "/admin/products/new"
            Route.CreateCategory -> "/admin/categories/new"
            is Route.ProductDetail -> "/products/${route.productId}/${route.slug}"
            is Route.Store -> "/m/${route.shopId}"
            is Route.Search -> "/search?q=${percentEncode(route.query)}"
            Route.About -> "/about"
        }

    fun fromPath(path: String): Route? {
        val normalized = normalizePath(path)
        return when (normalized) {
            "/", "" -> Route.Home
            "/categories" -> Route.Categories
            "/offers" -> Route.Offers
            "/saved" -> Route.Saved
            "/account/profile" -> Route.Profile
            "/account/referrals" -> Route.Referrals
            "/account/following" -> Route.Following
            "/account/settings" -> Route.AccountSettings
            "/account/users" -> Route.AccountUsers
            "/account/cities" -> Route.AccountCities
            "/account/cities/new" -> Route.AccountCityCreate
            "/account/login" -> Route.Login
            "/account/register" -> Route.Register
            "/account/register/verify" -> Route.RegisterVerify()
            "/account/forgot" -> Route.ForgotPassword
            "/account/forgot/reset" -> Route.ResetPassword()
            "/account" -> Route.Account
            "/admin/stores/new" -> Route.CreateStore
            "/admin/stores/plan" -> Route.StorePlan
            "/admin/stores/plan/upgrade" -> Route.StorePlanUpgrade
            "/admin/plans" -> Route.AdminPlans
            "/admin/products/new" -> Route.CreateProduct
            "/admin/categories/new" -> Route.CreateCategory
            "/about" -> Route.About
            else -> parseProductPath(normalized)
                ?: parseStorePath(normalized)
                ?: parseAccountUserPath(normalized)
                ?: parseAccountCityPath(normalized)
        }
    }

    /**
     * Parses a relative path or absolute URI into a [Route].
     * Ignores scheme/host; uses path only (query reserved for future routes).
     */
    fun fromUri(uri: String): Route? {
        val withoutFragment = uri.substringBefore('#')
        val path = extractPath(withoutFragment)
        val query = extractQuery(withoutFragment)
        if (path == "/search" || path.startsWith("/search/")) {
            val q = query["q"] ?: path.removePrefix("/search/").takeIf { it.isNotBlank() }
            if (!q.isNullOrBlank()) return Route.Search(percentDecode(q))
        }
        return fromPath(path)
    }

    private fun parseAccountUserPath(path: String): Route.AccountUserDetail? {
        // /account/users/{userId}
        val parts = path.trim('/').split('/')
        if (parts.size != 3) return null
        if (parts[0] != "account" || parts[1] != "users") return null
        val userId = parts[2]
        if (userId.isEmpty() || userId.contains('/')) return null
        return Route.AccountUserDetail(userId = userId)
    }

    private fun parseAccountCityPath(path: String): Route.AccountCityDetail? {
        // /account/cities/{cityId} — `/new` is handled as [Route.AccountCityCreate]
        val parts = path.trim('/').split('/')
        if (parts.size != 3) return null
        if (parts[0] != "account" || parts[1] != "cities") return null
        val cityId = parts[2]
        if (cityId.isEmpty() || cityId.contains('/') || cityId == "new") return null
        return Route.AccountCityDetail(cityId = cityId)
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

    private fun extractQuery(uri: String): Map<String, String> {
        val withoutFragment = uri.substringBefore('#')
        val queryStart = withoutFragment.indexOf('?')
        if (queryStart < 0) return emptyMap()
        return withoutFragment.substring(queryStart + 1)
            .split('&')
            .mapNotNull { part ->
                if (part.isEmpty()) return@mapNotNull null
                val eq = part.indexOf('=')
                if (eq <= 0) null
                else percentDecode(part.substring(0, eq)) to percentDecode(part.substring(eq + 1))
            }
            .toMap()
    }

    private fun percentEncode(value: String): String =
        buildString {
            for (byte in value.encodeToByteArray()) {
                val c = byte.toInt().toChar()
                if (c.isLetterOrDigit() || c in "-_.~") append(c)
                else append('%', hexDigit((byte.toInt() and 0xFF) shr 4), hexDigit(byte.toInt() and 0x0F))
            }
        }

    private fun percentDecode(value: String): String {
        val bytes = ArrayList<Byte>()
        var i = 0
        while (i < value.length) {
            when (val c = value[i]) {
                '%' -> {
                    if (i + 2 >= value.length) break
                    val hex = value.substring(i + 1, i + 3)
                    bytes.add(hex.toInt(16).toByte())
                    i += 3
                }
                '+' -> {
                    bytes.add(' '.code.toByte())
                    i++
                }
                else -> {
                    bytes.add(c.code.toByte())
                    i++
                }
            }
        }
        return bytes.toByteArray().decodeToString()
    }

    private fun hexDigit(value: Int): Char =
        "0123456789ABCDEF"[value and 0xF]

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
