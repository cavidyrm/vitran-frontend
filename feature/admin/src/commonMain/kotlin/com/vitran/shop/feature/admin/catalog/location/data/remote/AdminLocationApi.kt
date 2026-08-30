package com.vitran.shop.feature.admin.catalog.location.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.admin.catalog.location.data.remote.dto.AdminCityDataDto
import com.vitran.shop.feature.admin.catalog.location.data.remote.dto.toRequestDto
import com.vitran.shop.feature.admin.catalog.location.domain.CreateCityCommand
import com.vitran.shop.feature.admin.catalog.location.domain.UpdateCityCommand
import com.vitran.shop.feature.location.domain.model.CityId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AdminLocationApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun createCity(command: CreateCityCommand): AppResult<AdminCityDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/admin/cities")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(command.toRequestDto())
            }
        }

    suspend fun updateCity(command: UpdateCityCommand): AppResult<AdminCityDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/cities/${command.id.value}")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(command.toRequestDto())
            }
        }

    suspend fun deleteCity(id: CityId): AppResult<Unit> =
        executor.executeEmpty {
            client.delete(environment.apiUrl("/admin/cities/${id.value}")) {
                authMode(AuthMode.Required)
            }
        }
}
