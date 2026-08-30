package com.vitran.shop.feature.seller.boost.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

/**
 * Create-boost response projection. Postman verifies only [id], [shopId], [days].
 */
data class CreatedBoost(
    val id: BoostId,
    val shopId: ShopId,
    val days: Int,
)
