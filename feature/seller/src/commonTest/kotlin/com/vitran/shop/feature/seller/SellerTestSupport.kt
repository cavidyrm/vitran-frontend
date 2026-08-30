package com.vitran.shop.feature.seller

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
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.seller.shop.data.remote.SellerShopApi
import com.vitran.shop.feature.seller.shop.data.repository.DefaultSellerShopRepository
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

internal fun MockRequestHandleScope.jsonResponse(
    status: HttpStatusCode,
    body: String,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json"),
)

internal fun createSellerTestClient(
    mockEngine: MockEngine,
    tokenProvider: () -> String? = { "OLD_ACCESS" },
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
                AppResult.Success(tokenProvider())

            override suspend fun handleUnauthorizedResponse(
                authMode: AuthMode,
                retryOnce: suspend () -> HttpResponse,
            ) = AppResult.Failure(AppError.Authentication.Unauthorized())
        },
        networkLogger = NoOpNetworkLogger,
        engine = mockEngine,
    )

internal fun createSellerTestExecutor(): ApiRequestExecutor =
    ApiRequestExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger)

internal fun createSellerRepository(
    mockEngine: MockEngine,
    tokenProvider: () -> String? = { "OLD_ACCESS" },
    stateStore: SellerShopStateStore = SellerShopStateStore(mutableListOf()),
): Pair<SellerShopRepository, SellerShopStateStore> {
    val client = createSellerTestClient(mockEngine, tokenProvider)
    val api = SellerShopApi(client, ApiEnvironment(origin = "http://localhost:8080"), createSellerTestExecutor())
    return DefaultSellerShopRepository(api, stateStore) to stateStore
}

internal class FakeSessionRepository(
    access: String = "OLD_ACCESS",
    refresh: String = "REFRESH_1",
    expiresAt: Instant = Instant.parse("2026-06-09T13:00:00Z"),
) : SessionRepository {
    var credentials: SessionCredentials? =
        SessionCredentials(
            accessToken = access,
            refreshToken = refresh,
            accessTokenExpiresAt = expiresAt,
        )
    private val _state = MutableStateFlow<SessionState>(SessionState.Authenticated)
    override val sessionState: StateFlow<SessionState> = _state

    override suspend fun restore() = Unit
    override suspend fun establishSession(credentials: SessionCredentials) {
        this.credentials = credentials
        _state.value = SessionState.Authenticated
    }

    override suspend fun establishSession(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresAt: Instant,
    ) {
        credentials =
            SessionCredentials(accessToken, refreshToken, accessTokenExpiresAt)
        _state.value = SessionState.Authenticated
    }

    override suspend fun updateAccessToken(accessToken: String, expiresAt: Instant) {
        val current = credentials ?: return
        credentials = current.copy(accessToken = accessToken, accessTokenExpiresAt = expiresAt)
    }

    override suspend fun logoutLocal() {
        credentials = null
        _state.value = SessionState.Anonymous
    }

    override suspend fun invalidateSession() = logoutLocal()

    override suspend fun currentRefreshToken(): String? = credentials?.refreshToken

    override suspend fun readCredentials(): SessionCredentials? = credentials
}

internal class FakeAccountRepository(
    var refreshResult: AppResult<User> = AppResult.Failure(AppError.Network.Timeout()),
) : AccountRepository {
    var refreshCalls = 0
    private val _state = MutableStateFlow<CurrentUserState>(CurrentUserState.Unknown)
    override val currentUserState: StateFlow<CurrentUserState> = _state

    override suspend fun refreshCurrentUser(): AppResult<User> {
        refreshCalls += 1
        return refreshResult
    }

    override suspend fun updateProfile(command: UpdateProfileCommand): AppResult<User> =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun clear() = Unit
}

internal fun HttpRequestData.hasAuthBearer(token: String): Boolean =
    headers[HttpHeaders.Authorization] == "Bearer $token"

internal val createShopWithTokenBody = """
{
  "success": true,
  "message": "created",
  "code": 1,
  "data": {
    "tokens": {
      "access_token": "NEW_ACCESS",
      "expires_at": "2026-06-09T14:00:00Z"
    },
    "shop": {
      "id": 1,
      "owner_id": 2,
      "city_id": 1,
      "title": "My Shop",
      "slug": "my-shop",
      "description": "Best products",
      "type": "retailer",
      "share_url": "https://vitran.ir/my-shop",
      "qr_code_url": "http://localhost/qr.png",
      "active": false,
      "confirmed": false,
      "category_slugs": [1],
      "created_at": "2026-06-09T12:00:00Z",
      "updated_at": "2026-06-09T12:00:00Z"
    }
  },
  "errors": []
}
""".trimIndent()

internal val createShopWithoutTokenBody = """
{
  "success": true,
  "message": "created",
  "code": 1,
  "data": {
    "shop": {
      "id": 2,
      "owner_id": 2,
      "city_id": 1,
      "title": "Second Shop",
      "slug": "second-shop",
      "type": "retailer",
      "active": false,
      "confirmed": false,
      "category_slugs": [],
      "created_at": "2026-06-09T12:00:00Z",
      "updated_at": "2026-06-09T12:00:00Z"
    }
  },
  "errors": []
}
""".trimIndent()

internal val slugAvailableBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": { "slug_check": { "slug": "my-shop", "available": true } },
  "errors": []
}
""".trimIndent()

internal val slugTakenBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": { "slug_check": { "slug": "my-shop", "available": false } },
  "errors": []
}
""".trimIndent()

internal val sellerListBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "shops": {
      "per_page": 20,
      "has_more": false,
      "results": [
        { "id": 1, "title": "My Shop", "active": false, "confirmed": false }
      ]
    }
  },
  "errors": []
}
""".trimIndent()

internal val sellerGetPendingBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "shop": { "id": 1, "slug": "my-shop", "active": false, "confirmed": false }
  },
  "errors": []
}
""".trimIndent()

internal val updateShopBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "shop": {
      "id": 1,
      "owner_id": 2,
      "title": "Updated",
      "slug": "my-shop",
      "type": "retailer",
      "active": false,
      "confirmed": false,
      "category_slugs": [1, 2],
      "updated_at": "2026-06-09T14:00:00Z"
    }
  },
  "errors": []
}
""".trimIndent()

internal val fulfillmentBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": { "fulfillment_options": ["manual", "redirect", "future_mode"] },
  "errors": []
}
""".trimIndent()

internal val apiKeyBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": { "api_key": "vt_live_xxxxxxxx" },
  "errors": []
}
""".trimIndent()

internal val slugConflictBody = """
{
  "success": false,
  "message": "shop slug already taken",
  "code": 409,
  "data": null,
  "errors": [{ "reason": "slug", "messages": ["taken"] }]
}
""".trimIndent()
