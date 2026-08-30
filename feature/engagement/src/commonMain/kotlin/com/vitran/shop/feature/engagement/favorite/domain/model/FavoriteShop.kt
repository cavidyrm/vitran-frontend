package com.vitran.shop.feature.engagement.favorite.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import kotlinx.datetime.Instant

data class FavoriteShopSummary(
    val id: ShopId,
    val slug: ShopSlug,
    val title: String,
)

data class FavoriteShop(
    val favoritedAt: Instant,
    val shop: FavoriteShopSummary,
)
