package com.vitran.shop.feature.home.domain.model

import com.vitran.shop.feature.location.domain.model.CityId

/**
 * Server-driven homepage from `GET /home/screen`.
 * Unknown [HomeRail.type] / [HomeRail.layout] values stay on the model so the UI can skip them.
 */
data class HomeScreen(
    val cityId: CityId?,
    val revision: Long?,
    val sections: List<HomeRail>,
)

enum class HomeRailType {
    Products,
    Shops,
    Categories,
    Banners,
    Unknown,
    ;

    companion object {
        fun from(raw: String?): HomeRailType = when (raw) {
            "products" -> Products
            "shops" -> Shops
            "categories" -> Categories
            "banners" -> Banners
            else -> Unknown
        }
    }
}

enum class HomeRailLayout {
    HorizontalRail,
    Grid,
    Chips,
    Hero,
    Unknown,
    ;

    companion object {
        fun from(raw: String?): HomeRailLayout = when (raw) {
            "horizontal_rail" -> HorizontalRail
            "grid" -> Grid
            "chips" -> Chips
            "hero" -> Hero
            else -> Unknown
        }
    }
}

data class HomeRail(
    val id: Long?,
    val key: String,
    val type: HomeRailType,
    val layout: HomeRailLayout,
    val title: String?,
    val subtitle: String?,
    /** Set when the rail is limited to this category and its subcategories. See-all still uses [morePath]. */
    val categorySlug: String? = null,
    val requiresAuth: Boolean,
    val hideWhenEmpty: Boolean,
    val empty: Boolean,
    val items: HomeRailItems,
    val morePath: String?,
    val cursorParam: String,
    val tracking: HomeRailTracking? = null,
)

/** Rail fingerprint from `tracking`. [position] is the rail's place on the screen, not a card index. */
data class HomeRailTracking(
    val sectionKey: String?,
    val source: String?,
    val position: Int?,
)

data class HomeRailItems(
    val perPage: Int,
    val hasMore: Boolean,
    val nextCursor: String?,
    val total: Int?,
    val results: List<HomeRailItem>,
)

sealed interface HomeRailItem {
    val stableId: String

    data class Product(
        val id: Long,
        val shopId: Long?,
        val title: String?,
        val slug: String?,
        val categorySlug: String? = null,
        val priceAmount: Long?,
        val compareAtPriceAmount: Long?,
        val ratingAvg: Double?,
        val ratingCount: Int?,
        val imageUrl: String?,
    ) : HomeRailItem {
        override val stableId: String = "product:$id"
    }

    data class Category(
        val slug: String,
        val title: String,
        val level: Int?,
        val isLeaf: Boolean?,
    ) : HomeRailItem {
        override val stableId: String = "category:$slug"
    }

    data class Banner(
        val id: Long,
        val title: String?,
        val imageUrl: String,
        val action: HomeBannerAction,
    ) : HomeRailItem {
        override val stableId: String = "banner:$id"
    }

    data class Shop(
        val id: Long,
        val title: String,
        val avatarUrl: String? = null,
        val ratingAvg: Double? = null,
        val ratingCount: Int? = null,
        val products: List<HomeShopProduct> = emptyList(),
    ) : HomeRailItem {
        override val stableId: String = "shop:$id"
    }
}

data class HomeShopProduct(
    val id: Long,
    val title: String,
    val priceAmount: Long?,
    val imageUrl: String,
)

sealed interface HomeBannerAction {
    data class OpenProduct(val productId: String) : HomeBannerAction
    data class OpenShop(val shopId: String) : HomeBannerAction
    data class OpenCategory(val slug: String) : HomeBannerAction
    data class OpenSection(val sectionKey: String) : HomeBannerAction
    data class OpenUrl(val url: String) : HomeBannerAction
    data object None : HomeBannerAction
}

fun HomeRail.shouldRender(): Boolean {
    if (empty && hideWhenEmpty) return false
    if (type == HomeRailType.Unknown || layout == HomeRailLayout.Unknown) return false
    val supported = when (type) {
        HomeRailType.Products ->
            layout == HomeRailLayout.HorizontalRail || layout == HomeRailLayout.Grid
        HomeRailType.Categories -> layout == HomeRailLayout.Chips
        HomeRailType.Banners -> layout == HomeRailLayout.Hero
        HomeRailType.Shops ->
            layout == HomeRailLayout.HorizontalRail || layout == HomeRailLayout.Grid
        HomeRailType.Unknown -> false
    }
    if (!supported) return false
    if (type == HomeRailType.Shops && items.results.isEmpty()) return false
    if (type == HomeRailType.Banners && items.results.isEmpty()) return false
    return true
}

fun HomeRail.canLoadMore(): Boolean =
    items.hasMore && !morePath.isNullOrBlank() && !items.nextCursor.isNullOrBlank()

fun HomeRail.appendPage(next: HomeRail): HomeRail {
    val seen = items.results.map { it.stableId }.toSet()
    val added = next.items.results.filter { it.stableId !in seen }
    return copy(
        items = items.copy(
            perPage = next.items.perPage,
            hasMore = next.items.hasMore,
            nextCursor = next.items.nextCursor,
            total = next.items.total ?: items.total,
            results = items.results + added,
        ),
        morePath = next.morePath ?: morePath,
        cursorParam = next.cursorParam,
    )
}
