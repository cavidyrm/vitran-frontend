package com.vitran.shop.feature.admin.home.domain

/** Product sources that accept a home-rail `category_slug`. */
val homeSectionCategorySources: Set<String> = setOf(
    "latest_products",
    "popular_products",
    "featured_products",
)

/**
 * Value to send as `category_slug`.
 * A compatible source always sends the slug, including `""` for every category.
 * An incompatible source sends `""` only to clear a slug that was typed or already saved, and otherwise omits the field.
 */
fun resolveHomeSectionCategorySlug(
    source: String,
    typedSlug: String,
    savedSlug: String?,
): String? {
    val typed = typedSlug.trim()
    return when {
        source in homeSectionCategorySources -> typed
        typed.isNotBlank() || !savedSlug.isNullOrBlank() -> ""
        else -> null
    }
}

data class AdminHomeSection(
    val id: Long,
    val slug: String,
    val source: String,
    val itemKind: String?,
    val layout: String,
    val title: String,
    val subtitle: String? = null,
    val categorySlug: String? = null,
    val itemLimit: Int,
    val sortOrder: Int,
    val active: Boolean,
    val audience: String,
    val hideWhenEmpty: Boolean,
    val platforms: List<String>,
    val minAppVersion: String?,
    val startsAt: String?,
    val endsAt: String?,
)

data class AdminHomeBanner(
    val id: Long,
    val sectionId: Long,
    val title: String?,
    val imageUrl: String,
    val actionType: String,
    val actionValue: String?,
    val sortOrder: Int,
    val active: Boolean,
    val startsAt: String?,
    val endsAt: String?,
)

data class SaveAdminHomeSectionCommand(
    val slug: String? = null,
    val source: String,
    val layout: String,
    val title: String,
    val subtitle: String = "",
    /**
     * `null` omits `category_slug`. `""` clears it. Any other value limits the rail.
     * Build it with [resolveHomeSectionCategorySlug].
     */
    val categorySlug: String? = null,
    val itemLimit: Int,
    val sortOrder: Int,
    val active: Boolean,
    val audience: String,
    val hideWhenEmpty: Boolean,
    val platforms: List<String>,
    val minAppVersion: String,
    val startsAt: String,
    val endsAt: String,
)

data class SaveAdminHomeBannerCommand(
    val imageUrl: String,
    val title: String,
    val actionType: String,
    val actionValue: String,
    val sortOrder: Int,
    val active: Boolean,
    val startsAt: String,
    val endsAt: String,
)
