package com.vitran.shop.core.network.config

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NetworkTimeouts(
    val connectTimeout: Duration = 10.seconds,
    val requestTimeout: Duration = 30.seconds,
    val socketTimeout: Duration = 30.seconds,
)

data class NetworkDiagnosticsConfig(
    val enableHttpLogging: Boolean = false,
    val logBodies: Boolean = false,
)

data class NetworkConfig(
    val apiEnvironment: ApiEnvironment,
    val timeouts: NetworkTimeouts = NetworkTimeouts(),
    val diagnostics: NetworkDiagnosticsConfig = NetworkDiagnosticsConfig(),
    val maxRetryCount: Int = 2,
)

fun NetworkConfig(apiEnvironment: ApiEnvironment, enableHttpLogging: Boolean): NetworkConfig =
    NetworkConfig(
        apiEnvironment = apiEnvironment,
        diagnostics = NetworkDiagnosticsConfig(
            enableHttpLogging = enableHttpLogging,
            logBodies = enableHttpLogging,
        ),
    )
