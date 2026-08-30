package com.vitran.shop.di

import com.vitran.shop.core.platform.di.jsPlatformModule
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.platform.BrowserFileSaver
import com.vitran.shop.platform.BrowserImagePicker
import com.vitran.shop.platform.JsExternalUrlLauncher
import com.vitran.shop.platform.JsShareManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    includes(jsPlatformModule)
    single<ExternalUrlLauncher> { JsExternalUrlLauncher() }
    single<ShareManager> { JsShareManager() }
    single<ImagePicker> { BrowserImagePicker() }
    single<FileSaver> { BrowserFileSaver() }
}
