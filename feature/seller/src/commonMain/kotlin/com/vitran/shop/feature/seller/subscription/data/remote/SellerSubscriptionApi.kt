package com.vitran.shop.feature.seller.subscription.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.subscription.data.remote.dto.PurchaseDataDto
import com.vitran.shop.feature.seller.subscription.data.remote.dto.PurchasePlanRequestDto
import com.vitran.shop.feature.seller.subscription.data.remote.dto.SubscriptionDataDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class SellerSubscriptionApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getSubscription(shopId: ShopId): AppResult<SubscriptionDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/shops/${shopId.value}/subscription")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun purchasePlan(shopId: ShopId, planId: PlanId): AppResult<PurchaseDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/seller/shops/${shopId.value}/subscription/purchase")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(PurchasePlanRequestDto(planId = planId.value))
            }
        }
}
