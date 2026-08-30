package com.vitran.shop.feature.seller.analytics.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsExport
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod
import com.vitran.shop.feature.seller.analytics.domain.repository.SellerAnalyticsRepository

/**
 * Downloads seller analytics CSV. Does not write files. Does not reject success based on local
 * [com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities.advancedAnalytics].
 */
class ExportSellerAnalyticsUseCase(
    private val repository: SellerAnalyticsRepository,
) {
    suspend operator fun invoke(
        shopId: ShopId,
        period: AnalyticsPeriod,
    ): AppResult<AnalyticsExport> = repository.exportAnalytics(shopId, period)
}
