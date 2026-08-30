package com.vitran.shop.feature.engagement.analytics.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.engagement.analytics.data.remote.dto.ShopAnalyticsEventRequestDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ShopAnalyticsApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun track(
        shopId: ShopId,
        request: ShopAnalyticsEventRequestDto,
    ): AppResult<Unit> =
        executor.executeEmpty {
            client.post(environment.apiUrl("/shops/${shopId.value}/analytics/events")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
