package com.vitran.shop.feature.location

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.config.NetworkDiagnosticsConfig
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

internal fun MockRequestHandleScope.jsonResponse(
    status: HttpStatusCode,
    body: String,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json"),
)

internal fun createLocationTestClient(mockEngine: MockEngine): HttpClient =
    createHttpClient(
        config = NetworkConfig(
            apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
            diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
            maxRetryCount = 0,
        ),
        json = createNetworkJson(),
        sessionAuthCoordinator = object : SessionAuthCoordinator {
            override suspend fun resolveAccessToken(authMode: AuthMode) = AppResult.Success(null)

            override suspend fun handleUnauthorizedResponse(
                authMode: AuthMode,
                retryOnce: suspend () -> HttpResponse,
            ) = AppResult.Failure(AppError.Authentication.Unauthorized())
        },
        networkLogger = NoOpNetworkLogger,
        engine = mockEngine,
    )

internal fun createLocationTestExecutor(): ApiRequestExecutor =
    ApiRequestExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger)

internal val citiesListEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "cities": [
          { "id": 1, "slug": "tehran", "name": "تهران" },
          { "id": 2, "slug": "isfahan", "name": "اصفهان" }
        ]
      },
      "errors": []
    }
""".trimIndent()

internal val cityDetailEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": { "city": { "id": 1, "slug": "tehran", "name": "تهران" } },
      "errors": []
    }
""".trimIndent()
