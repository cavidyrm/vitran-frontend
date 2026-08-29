package com.vitran.shop.di

import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments
import com.vitran.shop.core.network.di.networkModule
import com.vitran.shop.core.session.di.sessionModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

private var koinStarted = false

/**
 * Koin bootstrap for VitranShop.
 * Registers API environment, networking infrastructure, and session stub.
 */
fun startVitranKoin(
    apiEnvironment: ApiEnvironment = ApiEnvironments.Local,
) {
    if (koinStarted) return
    startKoin {
        modules(
            appModule(apiEnvironment),
            networkModule,
            sessionModule,
        )
    }
    koinStarted = true
}

fun stopVitranKoin() {
    if (!koinStarted) return
    stopKoin()
    koinStarted = false
}

private fun appModule(apiEnvironment: ApiEnvironment) = module {
    single { apiEnvironment }
}
