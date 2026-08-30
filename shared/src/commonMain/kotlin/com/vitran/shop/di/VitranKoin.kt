package com.vitran.shop.di

import com.vitran.shop.core.database.di.databaseModule
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.di.networkModule
import com.vitran.shop.core.session.di.sessionModule
import com.vitran.shop.feature.account.di.accountModule
import com.vitran.shop.feature.admin.di.adminModule
import com.vitran.shop.feature.auth.di.authModule
import com.vitran.shop.feature.content.di.contentModule
import com.vitran.shop.feature.engagement.di.engagementModule
import com.vitran.shop.feature.home.di.homeModule
import com.vitran.shop.feature.location.di.locationModule
import com.vitran.shop.feature.marketplace.di.marketplaceModule
import com.vitran.shop.feature.seller.di.sellerModule
import com.vitran.shop.feature.taxonomy.di.taxonomyModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

private var koinStarted = false

fun startVitranKoin(
    apiEnvironment: ApiEnvironment = defaultApiEnvironment(),
    extraModules: List<Module> = emptyList(),
) {
    if (koinStarted) return
    startKoin {
        modules(
            appModule(apiEnvironment),
            platformModule(),
            *extraModules.toTypedArray(),
            // After platform + extras so DatabaseFactory is bound (Android supplies it via extraModules).
            databaseModule,
            sessionModule,
            networkModule,
            authModule,
            accountModule,
            locationModule,
            taxonomyModule,
            marketplaceModule,
            homeModule,
            engagementModule,
            sellerModule,
            contentModule,
            adminModule,
            appCoordinatorModule,
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
