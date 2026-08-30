package com.vitran.shop.feature.seller.boost.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

data class CreateBoostCommand(
    val shopId: ShopId,
    val target: BoostTarget,
    val days: Int,
    val pricePaid: Long,
)
