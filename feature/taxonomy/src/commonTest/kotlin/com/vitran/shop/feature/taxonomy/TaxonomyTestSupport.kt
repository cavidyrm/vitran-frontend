package com.vitran.shop.feature.taxonomy

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

internal fun createTaxonomyTestClient(mockEngine: MockEngine): HttpClient =
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

internal fun createTaxonomyTestExecutor(): ApiRequestExecutor =
    ApiRequestExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger)

internal val categoryTreeEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "categories": [
          {
            "slug": "aa-1-1-1-1",
            "title": "Apparel & Accessories",
            "name": "پوشاک و اکسسوری",
            "is_leaf": false,
            "children": [
              {
                "slug": "aa-1-2-3-4",
                "title": "Clothing",
                "name": "پوشاک",
                "is_leaf": false,
                "children": [
                  {
                    "slug": "aa-1-2-3-5",
                    "title": "T-Shirts",
                    "name": "تی‌شرت",
                    "is_leaf": true
                  }
                ]
              }
            ]
          },
          {
            "slug": "aa-9-9-9-9",
            "title": "Empty Branch",
            "name": null,
            "is_leaf": false,
            "children": []
          }
        ]
      },
      "errors": []
    }
""".trimIndent()

internal val categoryDetailEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "category": {
          "slug": "aa-1-2-3-4",
          "title": "T-Shirts",
          "name": null,
          "full_name": "Apparel & Accessories > Clothing > Shirts > T-Shirts",
          "is_leaf": true,
          "icon_url": "https://cdn.example.com/categories/uuid.jpg",
          "children": []
        }
      },
      "errors": []
    }
""".trimIndent()
