package com.vitran.shop.feature.engagement

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
import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.ShopAnalyticsEvent
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

internal val engagementEnvironment = ApiEnvironment(origin = "http://localhost:8080")

internal fun MockRequestHandleScope.jsonResponse(
    status: HttpStatusCode,
    body: String,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json"),
)

internal fun createEngagementTestClient(
    mockEngine: MockEngine,
    token: String? = null,
): HttpClient =
    createHttpClient(
        config = NetworkConfig(
            apiEnvironment = engagementEnvironment,
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

internal fun createEngagementTestExecutor(): ApiRequestExecutor =
    ApiRequestExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger)

internal const val EmptySuccessEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {},
      "errors": []
    }
"""

internal const val FavoriteShopsEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "favorite_shops": {
          "per_page": 20,
          "has_more": true,
          "next_cursor": "42",
          "results": [
            {
              "favorited_at": "2026-06-09T12:00:00Z",
              "shop": { "id": 1, "slug": "my-shop", "title": "My Shop" }
            }
          ]
        }
      },
      "errors": []
    }
"""

internal const val WishlistEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "favorite_products": {
          "per_page": 20,
          "has_more": true,
          "next_cursor": "42",
          "results": [
            {
              "favorited_at": "2026-06-09T12:00:00Z",
              "product": { "id": 1, "title": "Blue Widget", "price": 150000 }
            }
          ]
        }
      },
      "errors": []
    }
"""

internal const val WishlistShareSettingsEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "share_slug": "wl-a1b2c3d4e5f67890",
        "public": false
      },
      "errors": []
    }
"""

internal const val PublicWishlistEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "wishlist": {
          "per_page": 20,
          "has_more": true,
          "next_cursor": "42",
          "results": [
            {
              "saved_at": "2026-06-09T12:00:00Z",
              "product": { "id": 1, "title": "Blue Widget" }
            }
          ]
        }
      },
      "errors": []
    }
"""

internal const val ReviewsEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "reviews": {
          "per_page": 20,
          "has_more": false,
          "results": [
            {
              "id": 1,
              "product_id": 1,
              "user_id": 2,
              "rating": 5,
              "comment": "Great product"
            }
          ]
        }
      },
      "errors": []
    }
"""

internal const val SubmittedReviewEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "review": {
          "id": 1,
          "product_id": 1,
          "rating": 5,
          "comment": "Excellent quality"
        }
      },
      "errors": []
    }
"""

internal const val PublicCommentsEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "comments": {
          "per_page": 20,
          "has_more": false,
          "results": [
            { "id": 1, "title": "Great shop", "confirmed": true }
          ]
        }
      },
      "errors": []
    }
"""

internal const val SubmittedCommentEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "comment": {
          "id": 9,
          "shop_id": 1,
          "user_id": 2,
          "title": "Great shop",
          "description": "Fast delivery and friendly staff.",
          "confirmed": false,
          "created_at": "2026-06-09T12:00:00Z"
        }
      },
      "errors": []
    }
"""

internal const val ContactWhatsAppEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "contact": {
          "routed_via": "whatsapp",
          "whatsapp_link": "https://wa.me/989123456789"
        },
        "intent": {
          "id": 1,
          "product_id": 1,
          "shop_id": 1,
          "routed_via": "whatsapp"
        }
      },
      "errors": []
    }
"""

internal fun contactUnsupportedEnvelope(routedVia: String) = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "contact": { "routed_via": "$routedVia" },
        "intent": {
          "id": 1,
          "product_id": 1,
          "shop_id": 1,
          "routed_via": "$routedVia"
        }
      },
      "errors": []
    }
"""

internal fun httpErrorEnvelope(status: Int, message: String = "error") = """
    {
      "success": false,
      "message": "$message",
      "code": $status,
      "data": null,
      "errors": []
    }
"""

internal const val UserEventEnvelope = """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "event": { "id": 1, "event_type": "view_product", "product_id": 1 }
      },
      "errors": []
    }
"""

internal class FakeSessionRepository(
    initiallyAuthenticated: Boolean = false,
) : SessionRepository {
    private val _state = MutableStateFlow(
        if (initiallyAuthenticated) SessionState.Authenticated else SessionState.Anonymous,
    )
    override val sessionState: StateFlow<SessionState> = _state

    fun setAuthenticated(value: Boolean) {
        _state.value = if (value) SessionState.Authenticated else SessionState.Anonymous
    }

    override suspend fun restore() = Unit
    override suspend fun establishSession(credentials: SessionCredentials) {
        _state.value = SessionState.Authenticated
    }
    override suspend fun establishSession(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresAt: Instant,
    ) {
        _state.value = SessionState.Authenticated
    }
    override suspend fun updateAccessToken(accessToken: String, expiresAt: Instant) = Unit
    override suspend fun logoutLocal() {
        _state.value = SessionState.Anonymous
    }
    override suspend fun invalidateSession() {
        _state.value = SessionState.Anonymous
    }
    override suspend fun currentRefreshToken(): String? = null
    override suspend fun readCredentials(): SessionCredentials? = null
}

internal class RecordingAnalyticsTracker : MarketplaceAnalyticsTracker {
    val userEvents = mutableListOf<UserPersonalizationEvent>()
    val shopEvents = mutableListOf<Pair<ShopId, ShopAnalyticsEvent>>()
    var throwOnTrack: Boolean = false

    override fun track(event: UserPersonalizationEvent) {
        if (throwOnTrack) error("analytics should be isolated")
        userEvents += event
    }

    override fun track(shopId: ShopId, event: ShopAnalyticsEvent) {
        if (throwOnTrack) error("analytics should be isolated")
        shopEvents += shopId to event
    }
}
