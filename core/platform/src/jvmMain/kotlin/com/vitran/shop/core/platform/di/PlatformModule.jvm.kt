package com.vitran.shop.core.platform.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.JvmDatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.file.JvmFileImagePicker
import com.vitran.shop.core.platform.file.JvmFileSaver
import com.vitran.shop.core.platform.serialization.createPlatformJson
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.core.platform.share.JvmExternalUrlLauncher
import com.vitran.shop.core.platform.share.JvmShareManager
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.core.platform.storage.JvmSecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.dsl.module

val jvmPlatformModule = module {
    single { createPlatformJson() }
    single<SecureSessionStorage> { JvmSecureSessionStorage(json = get()) }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { JvmDatabaseFactory() }
    single<ExternalUrlLauncher> { JvmExternalUrlLauncher() }
    single<ShareManager> { JvmShareManager(get()) }
    single<ImagePicker> { JvmFileImagePicker() }
    single<FileSaver> { JvmFileSaver() }
}
