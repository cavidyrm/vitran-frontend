package com.vitran.shop.core.session.repository

import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.domain.SessionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

interface SessionRepository {
    val sessionState: StateFlow<SessionState>

    suspend fun restore()

    suspend fun establishSession(credentials: SessionCredentials)

    suspend fun establishSession(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresAt: Instant,
    )

    suspend fun updateAccessToken(accessToken: String, expiresAt: Instant)

    /** Clears credentials and emits [SessionState.Anonymous]. */
    suspend fun logoutLocal()

    /** Terminal auth failure — same local clear semantics as logout without server call. */
    suspend fun invalidateSession()

    suspend fun currentRefreshToken(): String?

    suspend fun readCredentials(): SessionCredentials?
}
