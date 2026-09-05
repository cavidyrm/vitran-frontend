package com.vitran.shop.core.platform.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.JsDatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.serialization.createPlatformJson
import com.vitran.shop.core.platform.storage.LocalStorageWebStorageBackend
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import com.vitran.shop.core.platform.storage.WebSecureSessionStorage
import org.koin.dsl.module

/** JS browser: same localStorage session persistence as Wasm. */
val jsPlatformModule = module {
    single { createPlatformJson() }
    single<SecureSessionStorage> {
        WebSecureSessionStorage(json = get(), backend = LocalStorageWebStorageBackend())
    }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { JsDatabaseFactory() }
}
