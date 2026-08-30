package com.vitran.shop.core.platform.di

import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.core.platform.share.JvmExternalUrlLauncher
import com.vitran.shop.core.platform.share.JvmShareManager
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.core.platform.storage.InMemorySecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.dsl.module

/**
 * Desktop / Web fallback — refresh tokens are not persisted securely.
 * See docs/session-authentication.md for production status.
 */
val jvmPlatformModule = module {
    single<SecureSessionStorage> { InMemorySecureSessionStorage() }
    single<ExternalUrlLauncher> { JvmExternalUrlLauncher() }
    single<ShareManager> { JvmShareManager(get()) }
}
