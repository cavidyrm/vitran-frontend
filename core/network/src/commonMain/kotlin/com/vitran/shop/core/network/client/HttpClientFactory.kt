package com.vitran.shop.core.network.client

import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.logging.NetworkLogger
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

/**
 * HttpClient for auth bootstrap calls (e.g. token refresh) that must not depend on
 * [SessionAuthCoordinator] — otherwise Koin circularly resolves the main client.
 */
fun createUnauthenticatedHttpClient(
    config: NetworkConfig,
    json: Json,
    networkLogger: NetworkLogger = com.vitran.shop.core.network.logging.NoOpNetworkLogger,
    engine: HttpClientEngine? = null,
): HttpClient = buildHttpClient(config, json, networkLogger, sessionAuthCoordinator = null, engine)

fun createHttpClient(
    config: NetworkConfig,
    json: Json,
    sessionAuthCoordinator: SessionAuthCoordinator,
    networkLogger: NetworkLogger = com.vitran.shop.core.network.logging.NoOpNetworkLogger,
    engine: HttpClientEngine? = null,
): HttpClient = buildHttpClient(config, json, networkLogger, sessionAuthCoordinator, engine)

private fun buildHttpClient(
    config: NetworkConfig,
    json: Json,
    networkLogger: NetworkLogger,
    sessionAuthCoordinator: SessionAuthCoordinator?,
    engine: HttpClientEngine?,
): HttpClient {
    val clientConfig: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false

        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            connectTimeoutMillis = config.timeouts.connectTimeout.inWholeMilliseconds
            requestTimeoutMillis = config.timeouts.requestTimeout.inWholeMilliseconds
            socketTimeoutMillis = config.timeouts.socketTimeout.inWholeMilliseconds
        }

        if (sessionAuthCoordinator != null) {
            installSessionAuth(sessionAuthCoordinator)
        }

        install(HttpRequestRetry) {
            maxRetries = config.maxRetryCount
            retryOnExceptionIf { request, cause ->
                val method = request.method
                val isSafeMethod = method == HttpMethod.Get || method == HttpMethod.Head
                isSafeMethod && (
                    cause is HttpRequestTimeoutException ||
                        cause is ConnectTimeoutException ||
                        cause is SocketTimeoutException ||
                        cause is IOException
                    )
            }
            retryIf { _, response ->
                val method = response.call.request.method
                val isSafeMethod = method == HttpMethod.Get || method == HttpMethod.Head
                isSafeMethod && (response.status.value in 500..599 || response.status.value == 429)
            }
            exponentialDelay(base = 200.0, maxDelayMs = 2_000)
        }

        if (config.diagnostics.enableHttpLogging) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        networkLogger.debug(sanitizeLogMessage(message))
                    }
                }
                // Never dump binary multipart bodies (product images). Headers-only when
                // logBodies is false; when true, sanitizeLogMessage still redacts secrets
                // and strips multipart binary sections.
                level = if (config.diagnostics.logBodies) LogLevel.BODY else LogLevel.HEADERS
                sanitizeHeader { header ->
                    header.lowercase() in SENSITIVE_HEADER_NAMES
                }
            }
        }

        defaultRequest {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
    }

    return if (engine != null) {
        HttpClient(engine, clientConfig)
    } else {
        HttpClient(clientConfig)
    }
}

private val SENSITIVE_HEADER_NAMES = setOf(
    "authorization",
    "x-api-key",
    "api-key",
)
