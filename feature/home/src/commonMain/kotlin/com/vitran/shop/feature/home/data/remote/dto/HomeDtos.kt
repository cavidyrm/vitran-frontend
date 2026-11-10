package com.vitran.shop.feature.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Legacy `GET /home` envelope. Item arrays stay untyped; the shopper home uses [HomeScreenDto].
 */
@Serializable
data class HomeDataDto(
    val home: HomeSectionsDto,
)

@Serializable
data class HomeSectionsDto(
    val featured: List<JsonElement> = emptyList(),
    val popular: List<JsonElement> = emptyList(),
    val categories: List<JsonElement> = emptyList(),
    val following: List<JsonElement> = emptyList(),
    val personal: List<JsonElement> = emptyList(),
)

/** `GET /home/screen` — `data.screen`. */
@Serializable
data class HomeScreenDataDto(
    val screen: HomeScreenDto? = null,
)

@Serializable
data class HomeScreenDto(
    val revision: Long? = null,
    @SerialName("city_id") val cityId: Long? = null,
    val sections: List<HomeScreenSectionDto> = emptyList(),
)

/**
 * One rail inside the screen, and the same object under `data.section` for pagination.
 */
@Serializable
data class HomeScreenSectionDto(
    val id: Long? = null,
    val key: String? = null,
    val type: String? = null,
    val layout: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    @SerialName("category_slug") val categorySlug: String? = null,
    @SerialName("requires_auth") val requiresAuth: Boolean = false,
    @SerialName("hide_when_empty") val hideWhenEmpty: Boolean = false,
    val empty: Boolean = false,
    val items: HomeSectionItemsDto? = null,
    val more: HomeSectionMoreDto? = null,
    val tracking: HomeSectionTrackingDto? = null,
)

@Serializable
data class HomeSectionTrackingDto(
    @SerialName("section_key") val sectionKey: String? = null,
    val source: String? = null,
    val position: Int? = null,
)

@Serializable
data class HomeSectionItemsDto(
    @SerialName("per_page") val perPage: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_cursor") val nextCursor: String? = null,
    val total: Int? = null,
    val results: List<JsonElement> = emptyList(),
)

@Serializable
data class HomeSectionMoreDto(
    val path: String? = null,
    @SerialName("cursor_param") val cursorParam: String? = null,
)

/** `GET /home/sections/{key}` — `data.section` matches a screen rail. */
@Serializable
data class HomeSectionDataDto(
    val section: HomeScreenSectionDto,
)

@Serializable
data class HomeProductItemDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long? = null,
    val title: String? = null,
    val slug: String? = null,
    @SerialName("category_slug") val categorySlug: String? = null,
    val price: Long? = null,
    @SerialName("compare_at_price") val compareAtPrice: Long? = null,
    @SerialName("rating_avg") val ratingAvg: Double? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    @SerialName("main_image") val mainImage: HomeImageDto? = null,
)

@Serializable
data class HomeImageDto(
    val id: Long? = null,
    val url: String? = null,
)

@Serializable
data class HomeCategoryItemDto(
    val slug: String? = null,
    val title: String? = null,
    val name: String? = null,
    val level: Int? = null,
    @SerialName("is_leaf") val isLeaf: Boolean? = null,
)

@Serializable
data class HomeBannerItemDto(
    val id: Long,
    val title: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val action: HomeBannerActionDto? = null,
)

@Serializable
data class HomeBannerActionDto(
    val type: String? = null,
    val value: String? = null,
)

@Serializable
data class HomeShopItemDto(
    val id: Long? = null,
    val title: String? = null,
    val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("rating_avg") val ratingAvg: Double? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    val products: List<HomeShopProductDto> = emptyList(),
)

@Serializable
data class HomeShopProductDto(
    val id: Long? = null,
    val title: String? = null,
    val price: Long? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("main_image") val mainImage: HomeImageDto? = null,
)
