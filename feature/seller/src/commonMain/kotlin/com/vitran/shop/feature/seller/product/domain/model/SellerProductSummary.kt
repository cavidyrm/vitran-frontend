package com.vitran.shop.feature.seller.product.domain.model

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

data class SellerProductSummary(
    val id: ProductId,
    val shopId: ShopId,
    val title: String,
    val active: Boolean,
    val confirmed: Boolean,
    val publicationState: ProductPublicationState =
        productPublicationState(active = active, confirmed = confirmed),
)
