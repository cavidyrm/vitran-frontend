package com.vitran.shop.feature.marketplace

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

internal fun createMarketplaceTestClient(
    mockEngine: MockEngine,
    token: String? = null,
): HttpClient =
    createHttpClient(
        config = NetworkConfig(
            apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
            diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
            maxRetryCount = 0,
        ),
        json = createNetworkJson(),
        sessionAuthCoordinator = object : SessionAuthCoordinator {
            override suspend fun resolveAccessToken(authMode: AuthMode) =
                AppResult.Success(token)

            override suspend fun handleUnauthorizedResponse(
                authMode: AuthMode,
                retryOnce: suspend () -> HttpResponse,
            ) = AppResult.Failure(AppError.Authentication.Unauthorized())
        },
        networkLogger = NoOpNetworkLogger,
        engine = mockEngine,
    )

internal fun createMarketplaceTestExecutor(): ApiRequestExecutor =
    ApiRequestExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger)

internal val productListEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "products": {
          "per_page": 20,
          "has_more": true,
          "next_cursor": "42",
          "results": [
            {
              "id": 1,
              "shop_id": 1,
              "category_slug": "aa-1-2-3-4",
              "title": "Blue Widget",
              "price": 150000,
              "active": true,
              "confirmed": true,
              "images": []
            }
          ]
        }
      },
      "errors": []
    }
""".trimIndent()

internal val productDetailEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "product": {
          "id": 1,
          "shop_id": 1,
          "category_slug": 1,
          "title": "Blue Widget",
          "description": "High quality widget",
          "price": 150000,
          "active": true,
          "confirmed": true,
          "images": [
            { "id": 1, "url": "http://localhost/img.jpg", "sort_order": 0 }
          ],
          "created_at": "2026-06-09T12:00:00Z",
          "updated_at": "2026-06-09T15:00:00Z"
        }
      },
      "errors": []
    }
""".trimIndent()

internal val shopListEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "shops": {
          "per_page": 20,
          "has_more": false,
          "results": [
            {
              "id": 1,
              "title": "My Shop",
              "slug": "my-shop",
              "active": true,
              "confirmed": true
            }
          ]
        }
      },
      "errors": []
    }
""".trimIndent()

internal val shopDetailEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "shop": {
          "id": 1,
          "owner_id": 2,
          "city_id": 1,
          "title": "My Shop",
          "slug": "my-shop",
          "type": "retailer",
          "share_url": "https://vitran.ir/my-shop",
          "active": true,
          "confirmed": true,
          "category_slugs": [1],
          "created_at": "2026-06-09T12:00:00Z",
          "updated_at": "2026-06-09T13:00:00Z"
        }
      },
      "errors": []
    }
""".trimIndent()

internal val shopBrowseEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "shops": {
          "per_page": 20,
          "has_more": false,
          "results": [
            {
              "id": 2,
              "slug": "business-store",
              "plan": { "slug": "business", "title": "Business" }
            }
          ]
        }
      },
      "errors": []
    }
""".trimIndent()
