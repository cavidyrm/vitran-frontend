package com.vitran.shop.feature.content.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StaticPageSummaryDto(
    val id: Long,
    val slug: String,
    val title: String,
    val active: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
internal data class StaticPageDto(
    val id: Long,
    val slug: String,
    val title: String,
    val body: String,
    val active: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
internal data class StaticPagesDataDto(
    @SerialName("static_pages") val staticPages: List<StaticPageSummaryDto> = emptyList(),
)

@Serializable
internal data class StaticPageDataDto(
    @SerialName("static_page") val staticPage: StaticPageDto,
)
