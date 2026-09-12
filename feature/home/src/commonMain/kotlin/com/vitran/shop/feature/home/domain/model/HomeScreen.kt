package com.vitran.shop.feature.home.domain.model

import com.vitran.shop.feature.location.domain.model.CityId

/**
 * Server-driven homepage from `GET /home/screen`.
 * Unknown [HomeScreenSection.type] / [HomeScreenSection.layout] values are kept so UI can skip them.
 */
data class HomeScreen(
    val cityId: CityId?,
    val sections: List<HomeScreenSection>,
)

data class HomeScreenSection(
    val slug: String?,
    val title: String?,
    val type: String?,
    val layout: String?,
    val source: String?,
    val morePath: String?,
)

data class HomeSectionPage(
    val slug: String,
    val title: String?,
    val type: String?,
    val layout: String?,
    val source: String?,
    val perPage: Int,
    val hasMore: Boolean,
    val nextCursor: String?,
    val products: List<HomeSectionProduct>,
)

data class HomeSectionProduct(
    val id: Long,
    val title: String?,
    val priceAmount: Long?,
    val compareAtPriceAmount: Long?,
)
