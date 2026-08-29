package com.vitran.shop.core.network.client

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.config.NetworkDiagnosticsConfig
import com.vitran.shop.core.network.createTestExecutor
import com.vitran.shop.core.network.health.HealthDto
import com.vitran.shop.core.network.jsonResponse
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class RetryPolicyTest {

    private val executor = createTestExecutor()

    @Test
    fun getRetriesOnTransientFailureThenSucceeds() = runTest {
        var attempts = 0
        val client = createHttpClient(
            config = NetworkConfig(
                apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
                diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
                maxRetryCount = 2,
            ),
            json = com.vitran.shop.core.network.serialization.createNetworkJson(),
            sessionReader = com.vitran.shop.core.session.EmptySessionReader(),
            engine = MockEngine {
                attempts++
                if (attempts < 3) {
                    throw IOException("temporary")
                }
                jsonResponse(
                    HttpStatusCode.OK,
                    """
                    {
                      "success": true,
                      "message": "ok",
                      "code": 1,
                      "data": { "status": "ok" },
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/health") }

        assertEquals(3, attempts)
        assertIs<AppResult.Success<HealthDto>>(result)
    }

    @Test
    fun postIsNotRetriedOnServerError() = runTest {
        var attempts = 0
        val client = createHttpClient(
            config = NetworkConfig(
                apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
                diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
                maxRetryCount = 2,
            ),
            json = com.vitran.shop.core.network.serialization.createNetworkJson(),
            sessionReader = com.vitran.shop.core.session.EmptySessionReader(),
            engine = MockEngine {
                attempts++
                jsonResponse(
                    HttpStatusCode.InternalServerError,
                    """
                    {
                      "success": false,
                      "message": "server error",
                      "code": 500,
                      "data": null,
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        executor.execute<HealthDto> {
            client.post("http://localhost/resource") { setBody("{}") }
        }

        assertEquals(1, attempts)
    }
}
