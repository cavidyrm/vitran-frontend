package com.vitran.shop.di

import com.vitran.shop.core.platform.di.wasmJsPlatformModule
import org.koin.core.module.Module

actual fun platformModule(): Module = wasmJsPlatformModule
