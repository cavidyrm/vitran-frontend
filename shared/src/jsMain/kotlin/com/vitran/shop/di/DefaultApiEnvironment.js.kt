package com.vitran.shop.di

import com.vitran.shop.core.network.config.ApiEnvironment
import kotlin.js.ExperimentalWasmJsInterop
import web.location.location

@OptIn(ExperimentalWasmJsInterop::class)
actual fun defaultApiEnvironment(): ApiEnvironment =
    ApiEnvironment(origin = location.origin)
