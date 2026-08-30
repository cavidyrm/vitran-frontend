package com.vitran.shop.core.session.auth

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.TestSecureSessionStorage
import com.vitran.shop.core.session.data.CredentialStore
import com.vitran.shop.core.session.data.SessionCredentialPersistence
import com.vitran.shop.core.session.data.remote.TokenRefreshRemoteDataSource
import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.DefaultSessionRepository
import com.vitran.shop.core.session.repository.SessionRoleCache
import com.vitran.shop.core.session.time.FakeAppClock
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenRefreshCoordinatorTest {

    private val clock = FakeAppClock(Instant.parse("2026-01-01T12:00:00Z"))
    private val storage = TestSecureSessionStorage()
    private val persistence = SessionCredentialPersistence(storage)
    private val credentialStore = CredentialStore()
    private val listeners = mutableListOf<com.vitran.shop.core.session.repository.SessionInvalidationListener>()
    private val sessionRepository = DefaultSessionRepository(
        credentialStore = credentialStore,
        persistence = persistence,
        invalidationListeners = listeners,
        roleCache = SessionRoleCache(),
    )

    @Test
    fun concurrentRefresh_executesSingleRemoteCall() = runTest {
        val refreshRemote = CountingRefreshRemote(
            result = AppResult.Success(freshCredentials("access-new", "refresh-new")),
        )
        val coordinator = DefaultTokenRefreshCoordinator(sessionRepository, refreshRemote, clock)
        seedExpiredSession()

        val outcomes = (1..10).map {
            async { coordinator.refreshIfNeeded(force = false) }
        }.awaitAll()

        assertEquals(1, refreshRemote.callCount)
        assertTrue(outcomes.all { it is RefreshOutcome.Success })
        assertEquals("access-new", sessionRepository.readCredentials()?.accessToken)
        assertEquals("refresh-new", sessionRepository.readCredentials()?.refreshToken)
    }

    @Test
    fun terminalRefreshFailure_clearsSession() = runTest {
        val refreshRemote = CountingRefreshRemote(
            result = AppResult.Failure(AppError.Authentication.Unauthorized(httpStatus = 401)),
        )
        val coordinator = DefaultTokenRefreshCoordinator(sessionRepository, refreshRemote, clock)
        seedExpiredSession()

        val outcome = coordinator.refreshIfNeeded(force = true)

        assertEquals(RefreshOutcome.TerminalFailure, outcome)
        assertEquals(SessionState.Anonymous, sessionRepository.sessionState.value)
        assertNull(sessionRepository.readCredentials())
    }

    @Test
    fun transientRefreshFailure_preservesCredentials() = runTest {
        val refreshRemote = CountingRefreshRemote(
            result = AppResult.Failure(AppError.Network.Timeout()),
        )
        val coordinator = DefaultTokenRefreshCoordinator(sessionRepository, refreshRemote, clock)
        seedExpiredSession()

        val outcome = coordinator.refreshIfNeeded(force = true)

        assertIs<RefreshOutcome.TransientFailure>(outcome)
        assertEquals(SessionState.Authenticated, sessionRepository.sessionState.value)
        assertEquals("access-old", sessionRepository.readCredentials()?.accessToken)
    }

    @Test
    fun requiredAuthMode_withoutCredentials_failsFast() = runTest {
        val coordinator = DefaultTokenRefreshCoordinator(
            sessionRepository,
            CountingRefreshRemote(AppResult.Success(freshCredentials("a", "r"))),
            clock,
        )

        val result = coordinator.getValidAccessToken(AuthMode.Required)

        assertIs<AppResult.Failure>(result)
        assertIs<AppError.Authentication.SessionExpired>(result.error)
    }

    @Test
    fun concurrentRefresh_stormOfManyCallers_stillSingleFlight() = runTest {
        val refreshRemote = CountingRefreshRemote(
            result = AppResult.Success(freshCredentials("access-storm", "refresh-storm")),
            delayMs = 50,
        )
        val coordinator = DefaultTokenRefreshCoordinator(sessionRepository, refreshRemote, clock)
        seedExpiredSession()

        val outcomes = (1..50).map {
            async { coordinator.refreshIfNeeded(force = false) }
        }.awaitAll()

        assertEquals(1, refreshRemote.callCount)
        assertTrue(outcomes.all { it is RefreshOutcome.Success })
        assertEquals("access-storm", sessionRepository.readCredentials()?.accessToken)
        assertEquals("refresh-storm", sessionRepository.readCredentials()?.refreshToken)
    }

    @Test
    fun terminalRefreshFailure_underConcurrency_invalidatesOnce() = runTest {
        val refreshRemote = CountingRefreshRemote(
            result = AppResult.Failure(AppError.Authentication.Unauthorized(httpStatus = 401)),
            delayMs = 30,
        )
        val coordinator = DefaultTokenRefreshCoordinator(sessionRepository, refreshRemote, clock)
        seedExpiredSession()

        val outcomes = (1..20).map {
            async { coordinator.refreshIfNeeded(force = true) }
        }.awaitAll()

        assertEquals(1, refreshRemote.callCount)
        assertTrue(outcomes.all { it == RefreshOutcome.TerminalFailure })
        assertEquals(SessionState.Anonymous, sessionRepository.sessionState.value)
        assertNull(sessionRepository.readCredentials())
    }

    private suspend fun seedExpiredSession() {
        val credentials = SessionCredentials(
            accessToken = "access-old",
            refreshToken = "refresh-old",
            accessTokenExpiresAt = Instant.parse("2026-01-01T11:00:00Z"),
        )
        sessionRepository.establishSession(credentials)
    }

    private fun freshCredentials(access: String, refresh: String) = SessionCredentials(
        accessToken = access,
        refreshToken = refresh,
        accessTokenExpiresAt = Instant.parse("2026-01-01T13:00:00Z"),
    )

    private class CountingRefreshRemote(
        private val result: AppResult<SessionCredentials>,
        private val delayMs: Long = 25,
    ) : TokenRefreshRemoteDataSource {
        var callCount = 0

        override suspend fun refresh(refreshToken: String): AppResult<SessionCredentials> {
            callCount++
            delay(delayMs)
            return result
        }
    }
}
