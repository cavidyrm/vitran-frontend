package com.vitran.shop.core.session.repository

import com.vitran.shop.core.platform.storage.StoredSessionCredentials
import com.vitran.shop.core.session.TestSecureSessionStorage
import com.vitran.shop.core.session.data.CredentialStore
import com.vitran.shop.core.session.data.SessionCredentialPersistence
import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.domain.SessionState
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionRepositoryTest {

    private val storage = TestSecureSessionStorage()
    private val persistence = SessionCredentialPersistence(storage)
    private val credentialStore = CredentialStore()
    private val listeners = mutableListOf<SessionInvalidationListener>()
    private val repository = DefaultSessionRepository(
        credentialStore = credentialStore,
        persistence = persistence,
        invalidationListeners = listeners,
        roleCache = SessionRoleCache(),
    )

    @Test
    fun restore_withoutStoredCredentials_emitsAnonymous() = runTest {
        repository.restore()

        assertEquals(SessionState.Anonymous, repository.sessionState.value)
        assertNull(repository.readCredentials())
    }

    @Test
    fun restore_withStoredCredentials_emitsAuthenticated() = runTest {
        storage.writeCredentials(
            StoredSessionCredentials(
                accessToken = "access",
                refreshToken = "refresh",
                accessTokenExpiresAt = "2026-01-01T13:00:00Z",
            ),
        )

        repository.restore()

        assertEquals(SessionState.Authenticated, repository.sessionState.value)
        assertEquals("access", repository.readCredentials()?.accessToken)
    }

    @Test
    fun updateAccessToken_preservesRefreshToken() = runTest {
        repository.establishSession(
            SessionCredentials(
                accessToken = "access-old",
                refreshToken = "refresh-keep",
                accessTokenExpiresAt = Instant.parse("2026-01-01T12:00:00Z"),
            ),
        )

        repository.updateAccessToken("access-new", Instant.parse("2026-01-01T13:00:00Z"))

        assertEquals("access-new", repository.readCredentials()?.accessToken)
        assertEquals("refresh-keep", repository.readCredentials()?.refreshToken)
    }
}
