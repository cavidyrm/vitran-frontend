package com.vitran.shop.feature.seller.analytics.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.download.DownloadResponse
import com.vitran.shop.core.network.executor.FileDownloadExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.data.mapper.toQueryValue
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class SellerAnalyticsApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val downloadExecutor: FileDownloadExecutor,
) {
    suspend fun exportAnalytics(
        shopId: ShopId,
        period: AnalyticsPeriod,
    ): AppResult<DownloadResponse> =
        downloadExecutor.execute {
            client.get(environment.apiUrl("/seller/shops/${shopId.value}/analytics/export")) {
                authMode(AuthMode.Required)
                parameter("period", period.toQueryValue())
            }
        }
}
