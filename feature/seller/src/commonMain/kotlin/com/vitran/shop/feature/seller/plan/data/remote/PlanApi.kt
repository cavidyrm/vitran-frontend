package com.vitran.shop.feature.seller.plan.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.seller.plan.data.remote.dto.PlanDetailDataDto
import com.vitran.shop.feature.seller.plan.data.remote.dto.PlansListDataDto
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal class PlanApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getPlans(): AppResult<PlansListDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/plans")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getPlan(planId: PlanId): AppResult<PlanDetailDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/plans/${planId.value}")) {
                authMode(AuthMode.None)
            }
        }
}
