package com.vitran.shop.core.network

import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.config.NetworkDiagnosticsConfig
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.EmptySessionReader
import com.vitran.shop.core.session.SessionReader
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json

internal fun createTestJson(): Json = createNetworkJson()

internal fun createTestExecutor(json: Json = createTestJson()): ApiRequestExecutor =
    ApiRequestExecutor(json = json, logger = NoOpNetworkLogger)

internal fun createTestClient(
    mockEngine: MockEngine,
    sessionReader: SessionReader = EmptySessionReader(),
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
        sessionReader = sessionReader,
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
