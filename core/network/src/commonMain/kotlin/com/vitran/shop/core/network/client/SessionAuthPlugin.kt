package com.vitran.shop.core.network.client

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.request.AuthModeKey
import com.vitran.shop.core.network.request.shouldSkipSessionAuth
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod

fun createSessionAuthPlugin(coordinator: SessionAuthCoordinator) = createClientPlugin("SessionAuth") {
    on(Send) { request ->
        if (request.shouldSkipSessionAuth()) {
            return@on proceed(request)
        }

        val authMode = request.attributes.getOrNull(AuthModeKey) ?: AuthMode.None

        suspend fun attachAccessToken() {
            if (authMode == AuthMode.None) return
            when (val tokenResult = coordinator.resolveAccessToken(authMode)) {
                is AppResult.Success -> {
                    tokenResult.value?.let { token ->
                        request.header(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                is AppResult.Failure -> throw LocalAuthException(tokenResult.error)
            }
        }

        attachAccessToken()

        val initialCall = proceed(request)
        if (initialCall.response.status.value != 401 || authMode == AuthMode.None) {
            return@on initialCall
        }

        if (authMode == AuthMode.Optional && request.method !in SAFE_ANONYMOUS_RETRY_METHODS) {
            return@on initialCall
        }

        return@on when (
            val retried = coordinator.handleUnauthorizedResponse(authMode) {
                attachAccessToken()
                proceed(request).response
            }
        ) {
            is AppResult.Success -> retried.value.call
            is AppResult.Failure -> throw LocalAuthException(retried.error)
        }
    }
}

private val SAFE_ANONYMOUS_RETRY_METHODS = setOf(HttpMethod.Get, HttpMethod.Head)

class LocalAuthException(val error: AppError) : Exception(error.message)

fun HttpClientConfig<*>.installSessionAuth(coordinator: SessionAuthCoordinator) {
    install(createSessionAuthPlugin(coordinator))
}
