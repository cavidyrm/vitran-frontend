package com.vitran.shop.core.network.serialization

import kotlinx.serialization.json.Json

fun createNetworkJson(): Json =
    Json {
        ignoreUnknownKeys = true
        isLenient = false
        encodeDefaults = true
        explicitNulls = false
    }
