package com.vitran.shop.feature.seller.analytics.data.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod

/**
 * User-scoped analytics session flags. Does not cache CSV bytes.
 * Cleared on logout / terminal session invalidation.
 */
class SellerAnalyticsStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val lastExportKeys = mutableSetOf<Pair<Long, AnalyticsPeriod>>()

    init {
        invalidationListeners.add(this)
    }

    fun markExportAttempted(shopId: ShopId, period: AnalyticsPeriod) {
        lastExportKeys += shopId.value to period
    }

    fun hasExportAttempt(shopId: ShopId, period: AnalyticsPeriod): Boolean =
        (shopId.value to period) in lastExportKeys

    fun clear() {
        lastExportKeys.clear()
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
