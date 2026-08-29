package com.vitran.shop.core.network.request

import com.vitran.shop.core.domain.auth.AuthMode
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.util.AttributeKey

val AuthModeKey: AttributeKey<AuthMode> = AttributeKey("AuthMode")

/** When set, session auth plugin skips proactive refresh and 401 handling (e.g. refresh endpoint). */
val SkipSessionAuthKey: AttributeKey<Unit> = AttributeKey("SkipSessionAuth")

var HttpRequestBuilder.authMode: AuthMode
    get() = attributes.getOrNull(AuthModeKey) ?: AuthMode.None
    set(value) {
        attributes.put(AuthModeKey, value)
    }

fun HttpRequestBuilder.authMode(mode: AuthMode) {
    authMode = mode
}

fun HttpRequestBuilder.markSkipSessionAuth() {
    attributes.put(SkipSessionAuthKey, Unit)
}

fun HttpRequestBuilder.shouldSkipSessionAuth(): Boolean =
    attributes.contains(SkipSessionAuthKey)
