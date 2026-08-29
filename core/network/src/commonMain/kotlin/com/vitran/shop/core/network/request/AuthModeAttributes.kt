package com.vitran.shop.core.network.request

import com.vitran.shop.core.domain.auth.AuthMode
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.util.AttributeKey

val AuthModeKey: AttributeKey<AuthMode> = AttributeKey("AuthMode")

var HttpRequestBuilder.authMode: AuthMode
    get() = attributes.getOrNull(AuthModeKey) ?: AuthMode.None
    set(value) {
        attributes.put(AuthModeKey, value)
    }

fun HttpRequestBuilder.authMode(mode: AuthMode) {
    authMode = mode
}
