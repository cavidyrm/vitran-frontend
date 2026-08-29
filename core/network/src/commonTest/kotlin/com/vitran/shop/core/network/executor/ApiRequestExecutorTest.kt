package com.vitran.shop.core.network.executor

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.createTestClient
import com.vitran.shop.core.network.createTestExecutor
import com.vitran.shop.core.network.health.HealthDto
import com.vitran.shop.core.network.jsonResponse
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import com.vitran.shop.core.network.rawResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ApiRequestExecutorTest {

    private val executor = createTestExecutor()

    @Test
    fun successfulEnvelope_returnsData() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
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

        val result = executor.execute<HealthDto> {
            client.get("http://localhost/health")
        }

        assertIs<AppResult.Success<HealthDto>>(result)
        assertEquals("ok", result.value.status)
    }

    @Test
    fun http201_isTreatedAsSuccess() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.Created,
                    """
                    {
                      "success": true,
                      "message": "created",
                      "code": 1,
                      "data": { "status": "ok" },
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> {
            client.post("http://localhost/resource") {
                setBody("{}")
            }
        }

        assertIs<AppResult.Success<HealthDto>>(result)
    }

    @Test
    fun validationError_preservesStructuredErrors() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.BadRequest,
                    """
                    {
                      "success": false,
                      "message": "validation failed",
                      "code": -2,
                      "data": null,
                      "errors": [
                        { "reason": "phone", "messages": ["invalid phone"] }
                      ]
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> {
            client.get("http://localhost/x")
        }

        val error = assertIs<AppResult.Failure>(result).error
        val validation = assertIs<AppError.Validation>(error)
        assertEquals(-2, validation.backendCode)
        assertEquals("phone", validation.fieldErrors.first().reason)
    }

    @Test
    fun badRequest400_preservesCredentialsError() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.BadRequest,
                    """
                    {
                      "success": false,
                      "message": "invalid credentials",
                      "code": 400,
                      "data": {},
                      "errors": [
                        { "reason": "credentials", "messages": ["invalid credentials"] }
                      ]
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/login") }
        val validation = assertIs<AppError.Validation>(assertIs<AppResult.Failure>(result).error)
        assertEquals("credentials", validation.fieldErrors.first().reason)
    }

    @Test
    fun forbidden403_isDistinctFromUnauthorized() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.Forbidden,
                    """
                    {
                      "success": false,
                      "message": "phone verification required",
                      "code": 403,
                      "data": { "temp_token": "abc123" },
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/login") }
        val forbidden = assertIs<AppError.Forbidden>(assertIs<AppResult.Failure>(result).error)
        assertEquals(403, forbidden.httpStatus)
        assertTrue(forbidden.errorDataJson?.contains("temp_token") == true)
    }

    @Test
    fun notFound404_mapsCorrectly() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.NotFound,
                    """
                    {
                      "success": false,
                      "message": "shop not found",
                      "code": 404,
                      "data": null,
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/shops/x") }
        assertIs<AppError.NotFound>(assertIs<AppResult.Failure>(result).error)
    }

    @Test
    fun conflict409_preservesSlugError() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.Conflict,
                    """
                    {
                      "success": false,
                      "message": "shop slug already taken",
                      "code": 409,
                      "data": null,
                      "errors": [
                        {
                          "reason": "slug",
                          "messages": ["این نامک قبلاً استفاده شده است."]
                        }
                      ]
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.post("http://localhost/shops") { setBody("{}") } }
        val conflict = assertIs<AppError.Conflict>(assertIs<AppResult.Failure>(result).error)
        assertEquals("slug", conflict.fieldErrors.first().reason)
    }

    @Test
    fun server500_mapsToServerError() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
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

        val result = executor.execute<HealthDto> { client.get("http://localhost/x") }
        assertIs<AppError.Server>(assertIs<AppResult.Failure>(result).error)
    }

    @Test
    fun unauthorized401_isDistinctFromForbidden() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.Unauthorized,
                    """
                    {
                      "success": false,
                      "message": "unauthorized",
                      "code": 401,
                      "data": null,
                      "errors": []
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/me") }
        assertIs<AppError.Authentication.Unauthorized>(assertIs<AppResult.Failure>(result).error)
    }

    @Test
    fun malformedJson_returnsSerializationError() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                rawResponse(
                    io.ktor.http.HttpStatusCode.OK,
                    "{not-json",
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/x") }
        assertIs<AppError.Serialization>(assertIs<AppResult.Failure>(result).error)
    }

    @Test
    fun unknownFields_areIgnored() = kotlinx.coroutines.test.runTest {
        val client = createTestClient(
            MockEngine {
                jsonResponse(
                    HttpStatusCode.OK,
                    """
                    {
                      "success": true,
                      "message": "ok",
                      "code": 1,
                      "data": { "status": "ok", "future_field": true },
                      "errors": [],
                      "meta": { "ignored": true }
                    }
                    """.trimIndent(),
                )
            },
        )

        val result = executor.execute<HealthDto> { client.get("http://localhost/health") }
        assertIs<AppResult.Success<HealthDto>>(result)
    }
}
