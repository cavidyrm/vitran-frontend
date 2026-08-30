package com.vitran.shop.di

import com.vitran.shop.core.platform.file.HostedImagePicker
import com.vitran.shop.core.platform.file.ImagePicker
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { HostedImagePicker() }
    single<ImagePicker> { get<HostedImagePicker>() }
}
