package com.vitran.shop.core.session.repository

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.session.SessionReader
import com.vitran.shop.core.session.data.CredentialStore
import com.vitran.shop.core.session.data.SessionCredentialPersistence
import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.domain.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Instant

internal class DefaultSessionRepository(
    private val credentialStore: CredentialStore,
    private val persistence: SessionCredentialPersistence,
    private val invalidationListeners: MutableList<SessionInvalidationListener>,
    private val roleCache: SessionRoleCache,
) : SessionRepository, SessionInvalidationListener {

    private val mutex = Mutex()
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Restoring)
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    override suspend fun restore() {
        mutex.withLock {
            _sessionState.value = SessionState.Restoring
            val stored = persistence.read()
            if (stored == null) {
                credentialStore.clear()
                _sessionState.value = SessionState.Anonymous
            } else {
                credentialStore.set(stored)
                _sessionState.value = SessionState.Authenticated
            }
        }
    }

    override suspend fun establishSession(credentials: SessionCredentials) {
        mutex.withLock {
            credentialStore.set(credentials)
            persistence.write(credentials)
            _sessionState.value = SessionState.Authenticated
        }
    }

    override suspend fun establishSession(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresAt: Instant,
    ) {
        establishSession(
            SessionCredentials(
                accessToken = accessToken,
                refreshToken = refreshToken,
                accessTokenExpiresAt = accessTokenExpiresAt,
            ),
        )
    }

    override suspend fun updateAccessToken(accessToken: String, expiresAt: Instant) {
        mutex.withLock {
            val current = credentialStore.get() ?: return
            val updated = current.copy(
                accessToken = accessToken,
                accessTokenExpiresAt = expiresAt,
            )
            credentialStore.set(updated)
            persistence.write(updated)
        }
    }

    override suspend fun logoutLocal() {
        clearCredentialsAndNotify()
    }

    override suspend fun invalidateSession() {
        clearCredentialsAndNotify()
    }

    override suspend fun onSessionInvalidated() {
        mutex.withLock {
            roleCache.clear()
            _sessionState.value = SessionState.Anonymous
        }
    }

    override suspend fun currentRefreshToken(): String? = credentialStore.get()?.refreshToken

    override suspend fun readCredentials(): SessionCredentials? = credentialStore.get()

    private suspend fun clearCredentialsAndNotify() {
        mutex.withLock {
            credentialStore.clear()
            persistence.clear()
            roleCache.clear()
            _sessionState.value = SessionState.Anonymous
            invalidationListeners
                .filterNot { it === this }
                .forEach { it.onSessionInvalidated() }
        }
    }
}

internal class DefaultSessionReader(
    private val credentialStore: CredentialStore,
    private val roleCache: SessionRoleCache,
) : SessionReader {
    override val isAuthenticated: Boolean
        get() = credentialStore.peek() != null

    override val roles: Set<UserRole>
        get() = roleCache.roles

    override fun accessTokenOrNull(): String? = credentialStore.peek()?.accessToken
}
