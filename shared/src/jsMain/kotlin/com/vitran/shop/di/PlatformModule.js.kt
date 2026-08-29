package com.vitran.shop.di

import com.vitran.shop.core.platform.di.jsPlatformModule
import org.koin.core.module.Module

actual fun platformModule(): Module = jsPlatformModule
