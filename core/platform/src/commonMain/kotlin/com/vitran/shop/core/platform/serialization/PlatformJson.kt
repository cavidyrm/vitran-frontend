package com.vitran.shop.core.platform.serialization

import kotlinx.serialization.json.Json

fun createPlatformJson(): Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
