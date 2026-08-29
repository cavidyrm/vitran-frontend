package com.vitran.shop.core.network.di

import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.health.HealthApi
import com.vitran.shop.core.network.logging.NetworkLogger
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.SessionReader
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single<Json> { createNetworkJson() }

    single {
        val environment: ApiEnvironment = get()
        NetworkConfig(
            apiEnvironment = environment,
            diagnostics = com.vitran.shop.core.network.config.NetworkDiagnosticsConfig(
                enableHttpLogging = environment == ApiEnvironments.Local,
                logBodies = environment == ApiEnvironments.Local,
            ),
        )
    }

    single<NetworkLogger> { NoOpNetworkLogger }

    single<HttpClient> {
        createHttpClient(
            config = get(),
            json = get(),
            sessionReader = get<SessionReader>(),
            networkLogger = get(),
        )
    }

    single { ApiRequestExecutor(json = get(), logger = get()) }

    single { HealthApi(client = get(), environment = get(), executor = get()) }
}
