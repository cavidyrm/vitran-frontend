package com.vitran.shop.feature.marketplace.shop.domain.repository

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

/**
 * Clears cached public shop projections after a seller mutation that can take a shop offline.
 */
fun interface ShopPublicCacheInvalidator {
    suspend fun invalidate(shopId: ShopId)
}
