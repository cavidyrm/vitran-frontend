package com.vitran.shop.feature.location.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.location.data.remote.dto.CitiesDataDto
import com.vitran.shop.feature.location.data.remote.dto.CityDataDto
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.encodeURLPathPart

internal class LocationApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getCities(): AppResult<CitiesDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/cities")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getCityById(id: CityId): AppResult<CityDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/cities/${id.value}")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getCityBySlug(slug: CitySlug): AppResult<CityDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/cities/slug/${slug.value.encodeURLPathPart()}")) {
                authMode(AuthMode.None)
            }
        }
}
