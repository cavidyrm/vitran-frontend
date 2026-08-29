package com.vitran.shop.feature.marketplace.shop.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopsDataDto(
    val shops: CursorPageDto<ShopListItemDto>,
)

@Serializable
data class BrowseShopsDataDto(
    val shops: CursorPageDto<BrowseShopItemDto>,
)

@Serializable
data class ShopListItemDto(
    val id: Long,
    val title: String? = null,
    val slug: String,
    val active: Boolean = true,
    val confirmed: Boolean = true,
)

@Serializable
data class BrowseShopItemDto(
    val id: Long,
    val slug: String,
    val title: String? = null,
    val plan: ShopPlanSummaryDto? = null,
)

@Serializable
data class ShopPlanSummaryDto(
    val slug: String,
    val title: String,
)

@Serializable
data class ShopDataDto(
    val shop: PublicShopDetailsDto,
)

@Serializable
data class PublicShopDetailsDto(
    val id: Long,
    @SerialName("owner_id") val ownerId: Long,
    @SerialName("city_id") val cityId: Long,
    val title: String,
    val slug: String,
    val type: String,
    @SerialName("share_url") val shareUrl: String,
    val active: Boolean,
    val confirmed: Boolean,
    @Serializable(with = FlexibleCategorySlugListSerializer::class)
    @SerialName("category_slugs")
    val categorySlugs: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)
