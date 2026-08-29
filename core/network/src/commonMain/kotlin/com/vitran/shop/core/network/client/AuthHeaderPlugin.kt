package com.vitran.shop.core.network.client

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.network.request.AuthModeKey
import com.vitran.shop.core.session.SessionReader
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

/**
 * Attaches Bearer tokens based on request [AuthMode] and [SessionReader].
 * Phase 3 replaces [SessionReader] with a real implementation.
 */
fun createAuthHeaderPlugin(sessionReader: SessionReader) = createClientPlugin("AuthHeader") {
    onRequest { request, _ ->
        val authMode = request.attributes.getOrNull(AuthModeKey) ?: AuthMode.None
        if (authMode == AuthMode.None) return@onRequest

        val token = sessionReader.accessTokenOrNull()
        if (token != null) {
            request.header(HttpHeaders.Authorization, "Bearer $token")
        }
        // Required mode without token: Phase 3 decides fail-fast vs anonymous fallback.
    }
}
