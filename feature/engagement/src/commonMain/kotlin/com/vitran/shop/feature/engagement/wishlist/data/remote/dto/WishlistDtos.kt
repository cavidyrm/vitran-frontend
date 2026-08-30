package com.vitran.shop.feature.engagement.wishlist.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteProductsDataDto(
    @SerialName("favorite_products")
    val favoriteProducts: CursorPageDto<WishlistProductItemDto>,
)

@Serializable
data class WishlistProductItemDto(
    @SerialName("favorited_at")
    val favoritedAt: String,
    val product: WishlistProductSummaryDto,
)

@Serializable
data class WishlistProductSummaryDto(
    val id: Long,
    val title: String,
    val price: Long,
)

@Serializable
data class WishlistShareSettingsDto(
    @SerialName("share_slug")
    val shareSlug: String,
    val public: Boolean,
)

@Serializable
data class UpdateWishlistShareRequestDto(
    val public: Boolean,
)

@Serializable
data class PublicWishlistDataDto(
    val wishlist: CursorPageDto<PublicWishlistProductItemDto>,
)

@Serializable
data class PublicWishlistProductItemDto(
    @SerialName("saved_at")
    val savedAt: String,
    val product: PublicWishlistProductSummaryDto,
)

@Serializable
data class PublicWishlistProductSummaryDto(
    val id: Long,
    val title: String,
)
