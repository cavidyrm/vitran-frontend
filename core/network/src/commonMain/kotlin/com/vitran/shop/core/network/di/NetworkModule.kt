package com.vitran.shop.core.network.di

import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.client.createUnauthenticatedHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.executor.FileDownloadExecutor
import com.vitran.shop.core.network.health.HealthApi
import com.vitran.shop.core.network.logging.NetworkLogger
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.network.session.KtorTokenRefreshRemoteDataSource
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import com.vitran.shop.core.session.data.remote.TokenRefreshRemoteDataSource
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
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

    single<HttpClient>(named(NetworkQualifiers.UnauthenticatedHttpClient)) {
        createUnauthenticatedHttpClient(
            config = get(),
            json = get(),
            networkLogger = get(),
        )
    }

    single<HttpClient> {
        createHttpClient(
            config = get(),
            json = get(),
            sessionAuthCoordinator = get<SessionAuthCoordinator>(),
            networkLogger = get(),
        )
    }

    single { ApiRequestExecutor(json = get(), logger = get()) }

    single { FileDownloadExecutor(json = get(), logger = get()) }

    single { HealthApi(client = get(), environment = get(), executor = get()) }

    single<TokenRefreshRemoteDataSource> {
        KtorTokenRefreshRemoteDataSource(
            client = get(named(NetworkQualifiers.UnauthenticatedHttpClient)),
            environment = get(),
            executor = get(),
        )
    }
}
