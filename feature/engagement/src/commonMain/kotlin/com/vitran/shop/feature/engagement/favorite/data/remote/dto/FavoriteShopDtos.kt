package com.vitran.shop.feature.engagement.favorite.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteShopsDataDto(
    @SerialName("favorite_shops")
    val favoriteShops: CursorPageDto<FavoriteShopItemDto>,
)

@Serializable
data class FavoriteShopItemDto(
    @SerialName("favorited_at")
    val favoritedAt: String,
    val shop: FavoriteShopSummaryDto,
)

@Serializable
data class FavoriteShopSummaryDto(
    val id: Long,
    val slug: String,
    val title: String,
)
