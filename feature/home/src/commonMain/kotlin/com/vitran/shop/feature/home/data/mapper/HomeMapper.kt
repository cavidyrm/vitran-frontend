package com.vitran.shop.feature.home.data.mapper

import com.vitran.shop.feature.home.data.remote.dto.HomeBannerItemDto
import com.vitran.shop.feature.home.data.remote.dto.HomeCategoryItemDto
import com.vitran.shop.feature.home.data.remote.dto.HomeProductItemDto
import com.vitran.shop.feature.home.data.remote.dto.HomeScreenDataDto
import com.vitran.shop.feature.home.data.remote.dto.HomeScreenSectionDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionItemsDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionsDto
import com.vitran.shop.feature.home.data.remote.dto.HomeShopItemDto
import com.vitran.shop.feature.home.domain.model.HomeBannerAction
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.model.HomeRail
import com.vitran.shop.feature.home.domain.model.HomeRailItem
import com.vitran.shop.feature.home.domain.model.HomeRailItems
import com.vitran.shop.feature.home.domain.model.HomeRailLayout
import com.vitran.shop.feature.home.domain.model.HomeRailTracking
import com.vitran.shop.feature.home.domain.model.HomeRailType
import com.vitran.shop.feature.home.domain.model.HomeScreen
import com.vitran.shop.feature.location.domain.model.CityId
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private val itemJson = Json { ignoreUnknownKeys = true }

internal fun HomeSectionsDto.toDomain(cityId: CityId?): HomeFeed =
    HomeFeed(
        cityId = cityId,
        featuredCount = featured.size,
        popularCount = popular.size,
        categoriesCount = categories.size,
        followingCount = following.size,
        personalCount = personal.size,
    )

internal fun HomeScreenDataDto.toDomain(cityId: CityId?): HomeScreen =
    HomeScreen(
        cityId = cityId ?: screen?.cityId?.let(::CityId),
        revision = screen?.revision,
        sections = screen?.sections.orEmpty().map { it.toDomain() },
    )

internal fun HomeScreenSectionDto.toDomain(): HomeRail {
    val railType = HomeRailType.from(type)
    val rawResults = items?.results.orEmpty()
    return HomeRail(
        id = id,
        key = key?.takeIf { it.isNotBlank() } ?: id?.toString().orEmpty(),
        type = railType,
        layout = HomeRailLayout.from(layout),
        title = title,
        subtitle = subtitle,
        requiresAuth = requiresAuth,
        hideWhenEmpty = hideWhenEmpty,
        empty = empty,
        items = (items ?: HomeSectionItemsDto()).toDomain(railType, rawResults),
        morePath = more?.path,
        cursorParam = more?.cursorParam?.takeIf { it.isNotBlank() } ?: "cursor",
        tracking = tracking?.toDomain(),
    )
}

private fun com.vitran.shop.feature.home.data.remote.dto.HomeSectionTrackingDto.toDomain(): HomeRailTracking? {
    val key = sectionKey?.takeIf { it.isNotBlank() }
    val sourceName = source?.takeIf { it.isNotBlank() }
    if (key == null && sourceName == null && position == null) return null
    return HomeRailTracking(
        sectionKey = key,
        source = sourceName,
        position = position,
    )
}

private fun HomeSectionItemsDto.toDomain(
    type: HomeRailType,
    results: List<JsonElement>,
): HomeRailItems =
    HomeRailItems(
        perPage = perPage,
        hasMore = hasMore,
        nextCursor = nextCursor,
        total = total,
        results = results.mapNotNull { decodeItem(type, it) },
    )

private fun decodeItem(type: HomeRailType, element: JsonElement): HomeRailItem? =
    runCatching {
        when (type) {
            HomeRailType.Products ->
                itemJson.decodeFromJsonElement(HomeProductItemDto.serializer(), element).toDomain()
            HomeRailType.Categories ->
                itemJson.decodeFromJsonElement(HomeCategoryItemDto.serializer(), element).toDomain()
            HomeRailType.Banners ->
                itemJson.decodeFromJsonElement(HomeBannerItemDto.serializer(), element).toDomain()
            HomeRailType.Shops ->
                itemJson.decodeFromJsonElement(HomeShopItemDto.serializer(), element).toDomain()
            HomeRailType.Unknown -> null
        }
    }.getOrNull()

private fun HomeProductItemDto.toDomain(): HomeRailItem.Product =
    HomeRailItem.Product(
        id = id,
        shopId = shopId,
        title = title,
        slug = slug,
        categorySlug = categorySlug?.takeIf { it.isNotBlank() },
        priceAmount = price,
        compareAtPriceAmount = compareAtPrice,
        ratingAvg = ratingAvg,
        ratingCount = ratingCount,
        imageUrl = mainImage?.url,
    )

private fun HomeCategoryItemDto.toDomain(): HomeRailItem.Category? {
    val slugValue = slug?.takeIf { it.isNotBlank() } ?: return null
    val label = name?.takeIf { it.isNotBlank() } ?: title?.takeIf { it.isNotBlank() } ?: return null
    return HomeRailItem.Category(
        slug = slugValue,
        title = label,
        level = level,
        isLeaf = isLeaf,
    )

}

private fun HomeBannerItemDto.toDomain(): HomeRailItem.Banner? {
    val url = imageUrl?.takeIf { it.isNotBlank() } ?: return null
    return HomeRailItem.Banner(
        id = id,
        title = title,
        imageUrl = url,
        action = action.toDomain(),
    )
}

private fun com.vitran.shop.feature.home.data.remote.dto.HomeBannerActionDto?.toDomain(): HomeBannerAction {
    val type = this?.type
    val value = this?.value?.takeIf { it.isNotBlank() }
    return when (type) {
        "open_product" -> value?.let { HomeBannerAction.OpenProduct(it) } ?: HomeBannerAction.None
        "open_shop" -> value?.let { HomeBannerAction.OpenShop(it) } ?: HomeBannerAction.None
        "open_category" -> value?.let { HomeBannerAction.OpenCategory(it) } ?: HomeBannerAction.None
        "open_section" -> value?.let { HomeBannerAction.OpenSection(it) } ?: HomeBannerAction.None
        "open_url" -> value?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
            ?.let { HomeBannerAction.OpenUrl(it) }
            ?: HomeBannerAction.None
        else -> HomeBannerAction.None
    }
}

private fun HomeShopItemDto.toDomain(): HomeRailItem.Shop? {
    val shopId = id ?: return null
    val label = title?.takeIf { it.isNotBlank() } ?: name?.takeIf { it.isNotBlank() } ?: return null
    return HomeRailItem.Shop(id = shopId, title = label)
}
