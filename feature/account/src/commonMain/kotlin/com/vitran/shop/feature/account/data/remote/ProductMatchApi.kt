package com.vitran.shop.feature.account.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.account.data.remote.dto.ProductMatchesDataDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class ProductMatchApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun listMatches(limit: Int = 20): AppResult<ProductMatchesDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/matches")) {
                authMode(AuthMode.Required)
                parameter("limit", limit)
            }
        }
}
