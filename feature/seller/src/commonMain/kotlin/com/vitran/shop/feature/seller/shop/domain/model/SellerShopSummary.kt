package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

data class SellerShopSummary(
    val id: ShopId,
    val title: String,
    val active: Boolean,
    val confirmed: Boolean,
    val publicationState: ShopPublicationState =
        shopPublicationState(active = active, confirmed = confirmed),
)
