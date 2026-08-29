package com.vitran.shop.feature.auth

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.jsonResponse
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.auth.data.remote.AuthApi
import com.vitran.shop.feature.auth.data.repository.DefaultAuthRepository
import com.vitran.shop.feature.auth.domain.model.LoginResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultAuthRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createAuthTestExecutor()
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun login_success_establishesSession() = runTest {
        val sessionRepository = RecordingSessionRepository()
        val repository = DefaultAuthRepository(
            authApi = AuthApi(
                client = createAuthTestClient(
                    MockEngine {
                        jsonResponse(
                            HttpStatusCode.OK,
                            """
                            {
                              "success": true,
                              "message": "ok",
                              "code": 1,
                              "data": {
                                "tokens": {
                                  "access_token": "access-token",
                                  "refresh_token": "refresh-token",
                                  "expires_at": "2026-01-01T13:00:00Z"
                                }
                              },
                              "errors": []
                            }
                            """.trimIndent(),
                        )
                    },
                ),
                environment = environment,
                executor = executor,
            ),
            sessionRepository = sessionRepository,
            json = json,
        )

        val result = repository.login("09123456789", "secret")

        assertIs<AppResult.Success<LoginResult>>(result)
        assertIs<LoginResult.Authenticated>(result.value)
        assertEquals("access-token", sessionRepository.credentials?.accessToken)
    }

    @Test
    fun login_verificationRequired_mapsChallenge() = runTest {
        val repository = DefaultAuthRepository(
            AuthApi(
                client = createAuthTestClient(
                    MockEngine {
                        jsonResponse(
                            HttpStatusCode.Forbidden,
                            """
                            {
                              "success": false,
                              "message": "verification required",
                              "code": 403,
                              "data": {
                                "temp_token": "temp-123",
                                "otp_code": "123456"
                              },
                              "errors": []
                            }
                            """.trimIndent(),
                        )
                    },
                ),
                environment = environment,
                executor = executor,
            ),
            RecordingSessionRepository(),
            json,
        )

        val result = repository.login("09123456789", "secret")

        assertIs<AppResult.Success<LoginResult>>(result)
        val challenge = (result.value as LoginResult.VerificationRequired).challenge
        assertEquals("temp-123", challenge.tempToken)
        assertEquals("123456", challenge.developmentOtp)
    }

    @Test
    fun logout_alwaysClearsLocalSession() = runTest {
        val sessionRepository = RecordingSessionRepository(
            credentials = SessionCredentials(
                accessToken = "a",
                refreshToken = "refresh-local",
                accessTokenExpiresAt = Instant.parse("2026-01-01T13:00:00Z"),
            ),
        )
        val repository = DefaultAuthRepository(
            authApi = AuthApi(
                client = createAuthTestClient(
                    MockEngine {
                        jsonResponse(HttpStatusCode.GatewayTimeout, """{"success":false,"message":"timeout","code":504,"data":null,"errors":[]}""")
                    },
                ),
                environment = environment,
                executor = executor,
            ),
            sessionRepository = sessionRepository,
            json = json,
        )

        val result = repository.logout()

        assertIs<AppResult.Success<Unit>>(result)
        assertTrue(sessionRepository.logoutCalled)
    }

    private class RecordingSessionRepository(
        var credentials: SessionCredentials? = null,
    ) : SessionRepository {
        private val _state = MutableStateFlow(
            if (credentials != null) SessionState.Authenticated else SessionState.Anonymous,
        )
        override val sessionState: StateFlow<SessionState> = _state
        var logoutCalled = false

        override suspend fun restore() = Unit
        override suspend fun establishSession(credentials: SessionCredentials) {
            this.credentials = credentials
            _state.value = SessionState.Authenticated
        }
        override suspend fun establishSession(
            accessToken: String,
            refreshToken: String,
            accessTokenExpiresAt: Instant,
        ) {
            establishSession(SessionCredentials(accessToken, refreshToken, accessTokenExpiresAt))
        }
        override suspend fun updateAccessToken(accessToken: String, expiresAt: Instant) = Unit
        override suspend fun logoutLocal() {
            logoutCalled = true
            credentials = null
            _state.value = SessionState.Anonymous
        }
        override suspend fun invalidateSession() = logoutLocal()
        override suspend fun currentRefreshToken(): String? = credentials?.refreshToken
        override suspend fun readCredentials(): SessionCredentials? = credentials
    }
}
