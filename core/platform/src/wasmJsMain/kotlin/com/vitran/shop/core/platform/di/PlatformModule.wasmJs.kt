package com.vitran.shop.core.platform.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.WasmDatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.storage.InMemorySecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.dsl.module

/**
 * Wasm: bearer credentials stay in-memory intentionally (no localStorage).
 * See docs/production-blockers.md P12-001.
 */
val wasmJsPlatformModule = module {
    single<SecureSessionStorage> { InMemorySecureSessionStorage() }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { WasmDatabaseFactory() }
}
