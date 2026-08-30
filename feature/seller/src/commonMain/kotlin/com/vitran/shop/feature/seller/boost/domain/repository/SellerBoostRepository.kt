package com.vitran.shop.feature.seller.boost.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost

interface SellerBoostRepository {
    suspend fun getActiveBoosts(shopId: ShopId, forceRefresh: Boolean = false): AppResult<ActiveBoosts>

    suspend fun createBoost(command: CreateBoostCommand): AppResult<CreatedBoost>
}
