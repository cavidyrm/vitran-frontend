package com.vitran.shop.di

import android.content.Context
import com.vitran.shop.core.database.AndroidDatabaseFactory
import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.platform.crash.CrashReporter
import com.vitran.shop.core.platform.crash.NoOpCrashReporter
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.HostedFileSaver
import com.vitran.shop.core.platform.file.HostedImagePicker
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.serialization.createPlatformJson
import com.vitran.shop.core.platform.share.AndroidExternalUrlLauncher
import com.vitran.shop.core.platform.share.AndroidShareManager
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.core.platform.storage.AndroidSecureSessionStorage
import com.vitran.shop.core.platform.storage.SecureSessionStorage
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidPlatformModule(context: Context): Module = module {
    single { createPlatformJson() }
    single { context.applicationContext }
    single<SecureSessionStorage> { AndroidSecureSessionStorage(get(), get()) }
    single<CrashReporter> { NoOpCrashReporter() }
    single<DatabaseFactory> { AndroidDatabaseFactory(get()) }
    single<ExternalUrlLauncher> { AndroidExternalUrlLauncher(get()) }
    single<ShareManager> { AndroidShareManager(get()) }
    single { HostedImagePicker() }
    single<ImagePicker> { get<HostedImagePicker>() }
    single { HostedFileSaver() }
    single<FileSaver> { get<HostedFileSaver>() }
}
