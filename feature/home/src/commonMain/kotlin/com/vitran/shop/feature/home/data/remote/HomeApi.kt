package com.vitran.shop.feature.home.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.home.data.remote.dto.HomeDataDto
import com.vitran.shop.feature.location.domain.model.CityId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class HomeApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getHome(cityId: CityId? = null): AppResult<HomeDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/home")) {
                authMode(AuthMode.Optional)
                cityId?.let { parameter("city_id", it.value) }
            }
        }
}
