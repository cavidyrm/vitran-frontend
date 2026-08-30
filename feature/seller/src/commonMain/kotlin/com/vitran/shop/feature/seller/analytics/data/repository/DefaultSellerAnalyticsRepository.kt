package com.vitran.shop.feature.seller.analytics.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.data.remote.SellerAnalyticsApi
import com.vitran.shop.feature.seller.analytics.data.state.SellerAnalyticsStateStore
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsExport
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod
import com.vitran.shop.feature.seller.analytics.domain.repository.SellerAnalyticsRepository

internal class DefaultSellerAnalyticsRepository(
    private val api: SellerAnalyticsApi,
    private val stateStore: SellerAnalyticsStateStore,
) : SellerAnalyticsRepository {

    override suspend fun exportAnalytics(
        shopId: ShopId,
        period: AnalyticsPeriod,
    ): AppResult<AnalyticsExport> =
        when (val result = api.exportAnalytics(shopId, period)) {
            is AppResult.Success -> {
                stateStore.markExportAttempted(shopId, period)
                AppResult.Success(
                    AnalyticsExport(
                        bytes = result.value.bytes,
                        contentType = result.value.contentType,
                        serverSuggestedFileName = result.value.suggestedFileName,
                    ),
                )
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
}
