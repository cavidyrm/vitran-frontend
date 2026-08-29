package com.vitran.shop.di

import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

private var koinStarted = false

/**
 * Minimal Koin bootstrap for Phase 1.
 * Registers only [ApiEnvironment] — no repositories, ViewModels, or session impl yet.
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

/** Reserved for Phase 2 Ktor client wiring. */
val networkModule = module { }

/** Reserved for Phase 3 session implementation. */
val sessionModule = module { }
