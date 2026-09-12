package com.vitran.shop.feature.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Home envelope — section item schemas unverified (Postman empty arrays; live vitran.ir probe returned 404).
 * Arrays deserialize as [JsonElement] until non-empty contract is captured.
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

/** `GET /home/screen` — no saved Postman example; keep unknown `type`/`layout` for the UI to skip. */
@Serializable
data class HomeScreenDataDto(
    val screen: HomeScreenDto? = null,
)

@Serializable
data class HomeScreenDto(
    val sections: List<HomeScreenSectionDto> = emptyList(),
)

@Serializable
data class HomeScreenSectionDto(
    val slug: String? = null,
    val title: String? = null,
    val type: String? = null,
    val layout: String? = null,
    val source: String? = null,
    val more: HomeSectionMoreDto? = null,
)

@Serializable
data class HomeSectionMoreDto(
    val path: String? = null,
)

@Serializable
data class HomeSectionDataDto(
    val section: HomeSectionPageDto,
)

@Serializable
data class HomeSectionPageDto(
    val slug: String,
    val title: String? = null,
    val type: String? = null,
    val layout: String? = null,
    val source: String? = null,
    @SerialName("per_page") val perPage: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_cursor") val nextCursor: String? = null,
    val results: List<HomeSectionProductDto> = emptyList(),
)

@Serializable
data class HomeSectionProductDto(
    val id: Long,
    val title: String? = null,
    val price: Long? = null,
    @SerialName("compare_at_price") val compareAtPrice: Long? = null,
)
