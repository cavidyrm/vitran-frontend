package com.vitran.shop.core.network

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.config.NetworkDiagnosticsConfig
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.executor.FileDownloadExecutor
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.SessionReader
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json

internal fun createTestJson(): Json = createNetworkJson()

internal fun createTestExecutor(json: Json = createTestJson()): ApiRequestExecutor =
    ApiRequestExecutor(json = json, logger = NoOpNetworkLogger)

internal fun createTestDownloadExecutor(json: Json = createTestJson()): FileDownloadExecutor =
    FileDownloadExecutor(json = json, logger = NoOpNetworkLogger)

internal class FakeSessionAuthCoordinator(
    private val sessionReader: SessionReader = FakeSessionReader(),
) : SessionAuthCoordinator {
    override suspend fun resolveAccessToken(authMode: AuthMode): AppResult<String?> =
        when (authMode) {
            AuthMode.None -> AppResult.Success(null)
            else -> AppResult.Success(sessionReader.accessTokenOrNull())
        }

    override suspend fun handleUnauthorizedResponse(
        authMode: AuthMode,
        retryOnce: suspend () -> HttpResponse,
    ): AppResult<HttpResponse> = AppResult.Failure(AppError.Authentication.Unauthorized())
}

internal class FakeSessionReader : SessionReader {
    override val isAuthenticated: Boolean = false
    override val roles: Set<com.vitran.shop.core.domain.auth.UserRole> = emptySet()
    override fun accessTokenOrNull(): String? = null
}

internal fun createTestClient(
    mockEngine: MockEngine,
    sessionReader: SessionReader = FakeSessionReader(),
    sessionAuthCoordinator: SessionAuthCoordinator = FakeSessionAuthCoordinator(sessionReader),
    config: NetworkConfig = NetworkConfig(
        apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
        diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
        maxRetryCount = 2,
    ),
    json: Json = createTestJson(),
): HttpClient =
    createHttpClient(
        config = config,
        json = json,
        sessionAuthCoordinator = sessionAuthCoordinator,
        networkLogger = NoOpNetworkLogger,
        engine = mockEngine,
    )

internal fun MockRequestHandleScope.jsonResponse(
    status: HttpStatusCode,
    body: String,
): io.ktor.client.request.HttpResponseData =
    respond(
        content = body,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

internal fun MockRequestHandleScope.rawResponse(
    status: HttpStatusCode,
    body: String,
): io.ktor.client.request.HttpResponseData =
    respond(
        content = ByteReadChannel(body),
        status = status,
    )
