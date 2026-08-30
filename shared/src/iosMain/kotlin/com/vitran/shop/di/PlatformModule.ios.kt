package com.vitran.shop.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.IosDatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.HostedFileSaver
import com.vitran.shop.core.platform.file.HostedImagePicker
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.serialization.createPlatformJson
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.core.platform.share.IosExternalUrlLauncher
import com.vitran.shop.core.platform.share.IosShareManager
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.core.platform.storage.IosSecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { createPlatformJson() }
    single<SecureSessionStorage> { IosSecureSessionStorage(get()) }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { IosDatabaseFactory() }
    single<ExternalUrlLauncher> { IosExternalUrlLauncher() }
    single<ShareManager> { IosShareManager(get()) }
    single { HostedImagePicker() }
    single<ImagePicker> { get<HostedImagePicker>() }
    single { HostedFileSaver() }
    single<FileSaver> { get<HostedFileSaver>() }
}
