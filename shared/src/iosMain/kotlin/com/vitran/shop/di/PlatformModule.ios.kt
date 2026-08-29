package com.vitran.shop.di

import com.vitran.shop.core.platform.serialization.createPlatformJson
import com.vitran.shop.core.platform.storage.IosSecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { createPlatformJson() }
    single<SecureSessionStorage> { IosSecureSessionStorage(get()) }
}
