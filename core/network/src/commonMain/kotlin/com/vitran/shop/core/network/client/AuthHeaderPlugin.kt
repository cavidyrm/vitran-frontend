package com.vitran.shop.core.network.client

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.network.request.AuthModeKey
import com.vitran.shop.core.network.request.shouldSkipSessionAuth
import com.vitran.shop.core.session.SessionReader
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

/**
 * Legacy bearer attachment for requests not yet processed by [createSessionAuthPlugin].
 * Session auth plugin handles Required/Optional with refresh; this remains a fallback read.
 */
fun createAuthHeaderPlugin(sessionReader: SessionReader) = createClientPlugin("AuthHeader") {
    onRequest { request, _ ->
        if (request.shouldSkipSessionAuth()) return@onRequest
        val authMode = request.attributes.getOrNull(AuthModeKey) ?: AuthMode.None
        if (authMode == AuthMode.None) return@onRequest

        val token = sessionReader.accessTokenOrNull()
        if (token != null) {
            request.header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
