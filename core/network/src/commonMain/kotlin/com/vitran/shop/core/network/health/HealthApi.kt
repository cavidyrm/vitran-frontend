package com.vitran.shop.core.network.health

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.config.originUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class HealthApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun check(): AppResult<HealthDto> =
        executor.execute {
            client.get(environment.originUrl("/health")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun checkVersioned(): AppResult<VersionedHealthDto> =
        executor.execute {
            client.get(environment.apiUrl("/health")) {
                authMode(AuthMode.None)
            }
        }
}
