package com.vitran.shop.feature.seller.analytics.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsExport
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod

interface SellerAnalyticsRepository {
    suspend fun exportAnalytics(shopId: ShopId, period: AnalyticsPeriod): AppResult<AnalyticsExport>
}
