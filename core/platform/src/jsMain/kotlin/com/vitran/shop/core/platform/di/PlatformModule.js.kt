package com.vitran.shop.core.platform.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.JsDatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.storage.InMemorySecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.dsl.module

/** JS browser: same intentional in-memory session policy as Wasm. */
val jsPlatformModule = module {
    single<SecureSessionStorage> { InMemorySecureSessionStorage() }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { JsDatabaseFactory() }
}
