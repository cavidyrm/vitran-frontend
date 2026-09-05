package com.vitran.shop.core.platform.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.WasmDatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.serialization.createPlatformJson
import com.vitran.shop.core.platform.storage.LocalStorageWebStorageBackend
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import com.vitran.shop.core.platform.storage.WebSecureSessionStorage
import org.koin.dsl.module

/**
 * Wasm: persist bearers in localStorage so refresh restores the session.
 * XSS residual until Cookie/BFF — see docs/production-blockers.md P12-001.
 */
val wasmJsPlatformModule = module {
    single { createPlatformJson() }
    single<SecureSessionStorage> {
        WebSecureSessionStorage(json = get(), backend = LocalStorageWebStorageBackend())
    }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { WasmDatabaseFactory() }
}
