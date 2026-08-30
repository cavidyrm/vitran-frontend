package com.vitran.shop.feature.seller.boost.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.data.remote.dto.ActiveBoostsDataDto
import com.vitran.shop.feature.seller.boost.data.remote.dto.CreateBoostDataDto
import com.vitran.shop.feature.seller.boost.data.remote.dto.CreateBoostRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class SellerBoostApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun createBoost(
        shopId: ShopId,
        request: CreateBoostRequestDto,
    ): AppResult<CreateBoostDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/seller/shops/${shopId.value}/boosts")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun listActiveBoosts(shopId: ShopId): AppResult<ActiveBoostsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/shops/${shopId.value}/boosts")) {
                authMode(AuthMode.Required)
            }
        }
}
