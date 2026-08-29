package com.vitran.shop.core.platform.di

import com.vitran.shop.core.platform.storage.InMemorySecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.dsl.module

val jsPlatformModule = module {
    single<SecureSessionStorage> { InMemorySecureSessionStorage() }
}
