package com.vitran.shop.core.network.executor

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.createTestClient
import com.vitran.shop.core.network.createTestExecutor
import com.vitran.shop.core.network.error.NetworkExceptionMapper
import com.vitran.shop.core.network.health.HealthDto
import com.vitran.shop.core.network.jsonResponse
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest

class ApiRequestExecutorBehaviorTest {

    private val executor = createTestExecutor()

    @Test
    fun cancellation_propagates() = runTest {
        assertFailsWith<CancellationException> {
            executor.execute<HealthDto> {
                throw CancellationException()
            }
        }
    }

    @Test
    fun timeout_mapsToNetworkTimeout() {
        val error = NetworkExceptionMapper.map(
            HttpRequestTimeoutException("timeout", 1_000),
            NoOpNetworkLogger,
        )
        assertIs<AppError.Network.Timeout>(error)
    }

    @Test
    fun connectionFailure_mapsCorrectly() {
        val error = NetworkExceptionMapper.map(
            IOException("connection reset"),
            NoOpNetworkLogger,
        )
        assertIs<AppError.Network.ConnectionFailure>(error)
    }

    @Test
    fun transportExceptionDuringRequest_mapsToFailure() = runTest {
        val client = createTestClient(
            MockEngine {
                throw IOException("connection failed")
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/health") }
        assertIs<AppError.Network.ConnectionFailure>(assertIs<AppResult.Failure>(result).error)
    }
}
