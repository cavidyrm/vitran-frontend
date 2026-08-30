package com.vitran.shop.feature.admin.content.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdminStaticPageSummaryDto(
    val id: Long,
    val slug: String,
    val title: String,
    val active: Boolean,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
internal data class AdminStaticPageDetailsDto(
    val id: Long,
    val slug: String,
    val title: String,
    val body: String,
    val active: Boolean,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
internal data class AdminStaticPagesDataDto(
    @SerialName("static_pages") val staticPages: List<AdminStaticPageSummaryDto> = emptyList(),
)

@Serializable
internal data class AdminStaticPageDataDto(
    @SerialName("static_page") val staticPage: AdminStaticPageDetailsDto,
)

@Serializable
internal data class CreateStaticPageRequestDto(
    val slug: String,
    val title: String,
    val body: String,
    val active: Boolean,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
internal data class UpdateStaticPageRequestDto(
    val slug: String? = null,
    val title: String? = null,
    val body: String? = null,
    val active: Boolean? = null,
    @SerialName("sort_order") val sortOrder: Int? = null,
)
