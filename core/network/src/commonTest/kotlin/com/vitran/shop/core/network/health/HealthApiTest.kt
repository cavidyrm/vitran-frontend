package com.vitran.shop.core.network.health

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.createTestClient
import com.vitran.shop.core.network.createTestExecutor
import com.vitran.shop.core.network.jsonResponse
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class HealthApiTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createTestExecutor()

    @Test
    fun checkHealth_hitsUnversionedPath() = runTest {
        var requestedUrl: String? = null
        val client = createTestClient(
            MockEngine { request ->
                requestedUrl = request.url.toString()
                jsonResponse(
                    HttpStatusCode.OK,
                    """
                    {
                      "success": true,
                      "message": "سرویس در دسترس است",
                      "code": 1,
                      "data": { "status": "ok" },
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val api = HealthApi(client, environment, executor)
        val result = api.check()

        assertEquals("http://localhost:8080/health", requestedUrl)
        assertIs<AppResult.Success<HealthDto>>(result)
        assertEquals("ok", result.value.status)
    }

    @Test
    fun checkVersionedHealth_usesApiPrefix() = runTest {
        var requestedUrl: String? = null
        val client = createTestClient(
            MockEngine { request ->
                requestedUrl = request.url.toString()
                jsonResponse(
                    HttpStatusCode.OK,
                    """
                    {
                      "success": true,
                      "message": "ok",
                      "code": 1,
                      "data": { "status": "ok", "version": "v1" },
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val api = HealthApi(client, environment, executor)
        val result = api.checkVersioned()

        assertEquals("http://localhost:8080/api/v1/health", requestedUrl)
        assertIs<AppResult.Success<VersionedHealthDto>>(result)
        assertEquals("v1", result.value.version)
    }
}
