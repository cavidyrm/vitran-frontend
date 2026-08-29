package com.vitran.shop.core.network.client

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.createTestClient
import com.vitran.shop.core.network.jsonResponse
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionAuthPluginTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")

    @Test
    fun requiredAuthMode_withoutToken_failsBeforeNetwork() = runTest {
        var requestCount = 0
        val client = createTestClient(
            mockEngine = MockEngine {
                requestCount++
                jsonResponse(HttpStatusCode.OK, """{"success":true,"message":"ok","code":1,"data":{},"errors":[]}""")
            },
            sessionAuthCoordinator = FailFastCoordinator(),
        )

        val result = runCatching {
            client.get(environment.apiUrl("/auth/me")) { authMode(AuthMode.Required) }
        }

        assertTrue(result.isFailure)
        assertEquals(0, requestCount)
    }

    @Test
    fun unauthorizedResponse_triggersSingleRetry() = runTest {
        var attempts = 0
        val coordinator = RetryOnceCoordinator()
        val client = createTestClient(
            mockEngine = MockEngine {
                attempts++
                if (attempts == 1) {
                    jsonResponse(HttpStatusCode.Unauthorized, """{"success":false,"message":"expired","code":401,"data":null,"errors":[]}""")
                } else {
                    jsonResponse(HttpStatusCode.OK, """{"success":true,"message":"ok","code":1,"data":{},"errors":[]}""")
                }
            },
            sessionAuthCoordinator = coordinator,
        )

        val response: HttpResponse = client.get(environment.apiUrl("/auth/me")) { authMode(AuthMode.Required) }

        assertEquals(200, response.status.value)
        assertEquals(2, attempts)
        assertEquals(1, coordinator.refreshCount)
    }

    @Test
    fun optionalPost_doesNotRetryAfter401() = runTest {
        var attempts = 0
        val client = createTestClient(
            mockEngine = MockEngine {
                attempts++
                jsonResponse(HttpStatusCode.Unauthorized, """{"success":false,"message":"expired","code":401,"data":null,"errors":[]}""")
            },
            sessionAuthCoordinator = RetryOnceCoordinator(),
        )

        val response: HttpResponse = client.post(environment.apiUrl("/auth/profile")) { authMode(AuthMode.Optional) }

        assertEquals(401, response.status.value)
        assertEquals(1, attempts)
    }

    private class FailFastCoordinator : SessionAuthCoordinator {
        override suspend fun resolveAccessToken(authMode: AuthMode): AppResult<String?> =
            AppResult.Failure(AppError.Authentication.SessionExpired())

        override suspend fun handleUnauthorizedResponse(
            authMode: AuthMode,
            retryOnce: suspend () -> HttpResponse,
        ): AppResult<HttpResponse> = AppResult.Failure(AppError.Authentication.Unauthorized())
    }

    private class RetryOnceCoordinator : SessionAuthCoordinator {
        var refreshCount = 0

        override suspend fun resolveAccessToken(authMode: AuthMode): AppResult<String?> =
            AppResult.Success("token")

        override suspend fun handleUnauthorizedResponse(
            authMode: AuthMode,
            retryOnce: suspend () -> HttpResponse,
        ): AppResult<HttpResponse> {
            refreshCount++
            return AppResult.Success(retryOnce())
        }
    }
}
