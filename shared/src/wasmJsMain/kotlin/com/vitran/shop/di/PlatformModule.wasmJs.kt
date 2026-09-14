package com.vitran.shop.di

import com.vitran.shop.core.platform.di.wasmJsPlatformModule
import com.vitran.shop.core.platform.file.FileKitImagePicker
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.HostedFileSaver
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.platform.WasmExternalUrlLauncher
import com.vitran.shop.platform.WasmShareManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    includes(wasmJsPlatformModule)
    single<ExternalUrlLauncher> { WasmExternalUrlLauncher() }
    single<ShareManager> { WasmShareManager() }
    single<ImagePicker> { FileKitImagePicker() }
    single { HostedFileSaver() }
    single<FileSaver> { get<HostedFileSaver>() }
}
