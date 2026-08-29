package com.vitran.shop.feature.account.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.account.data.remote.dto.GetCurrentUserDataDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateProfileDataDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateProfileRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AccountApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getCurrentUser(): AppResult<GetCurrentUserDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/auth/me")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun updateProfile(request: UpdateProfileRequestDto): AppResult<UpdateProfileDataDto> =
        executor.execute {
            client.put(environment.apiUrl("/auth/profile")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
