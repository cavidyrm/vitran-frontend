package com.vitran.shop.feature.seller.boost.data.mapper

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.data.remote.dto.ActiveBoostsDataDto
import com.vitran.shop.feature.seller.boost.data.remote.dto.CreateBoostRequestDto
import com.vitran.shop.feature.seller.boost.data.remote.dto.CreatedBoostDto
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.model.BoostId
import com.vitran.shop.feature.seller.boost.domain.model.BoostTarget
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost

internal fun CreateBoostCommand.toRequestDto(): CreateBoostRequestDto =
    CreateBoostRequestDto(
        productId =
            when (val target = target) {
                BoostTarget.Shop -> null
                is BoostTarget.Product -> target.productId.value
            },
        days = days,
        pricePaid = pricePaid,
    )

internal fun CreatedBoostDto.toDomain(): CreatedBoost =
    CreatedBoost(
        id = BoostId(id),
        shopId = ShopId(shopId),
        days = days,
    )

internal fun ActiveBoostsDataDto.toDomain(): ActiveBoosts =
    if (boosts.isEmpty()) {
        ActiveBoosts.Empty
    } else {
        ActiveBoosts.Unmapped(boosts.size)
    }
