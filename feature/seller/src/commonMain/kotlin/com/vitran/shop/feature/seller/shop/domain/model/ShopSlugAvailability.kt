package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug

data class ShopSlugAvailability(
    val slug: ShopSlug,
    val isAvailable: Boolean,
)
