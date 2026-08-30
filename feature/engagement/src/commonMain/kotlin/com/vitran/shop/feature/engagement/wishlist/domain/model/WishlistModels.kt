package com.vitran.shop.feature.engagement.wishlist.domain.model

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlin.jvm.JvmInline
import kotlinx.datetime.Instant

@JvmInline
value class WishlistShareSlug(val value: String)

data class WishlistProductSummary(
    val id: ProductId,
    val title: String,
    val priceAmount: Long,
)

data class WishlistItem(
    val savedAt: Instant,
    val product: WishlistProductSummary,
)

data class PublicWishlistProductSummary(
    val id: ProductId,
    val title: String,
)

data class PublicWishlistItem(
    val savedAt: Instant,
    val product: PublicWishlistProductSummary,
)

data class WishlistShareSettings(
    val shareSlug: WishlistShareSlug,
    val isPublic: Boolean,
)
