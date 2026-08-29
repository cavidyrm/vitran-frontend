package com.vitran.shop.feature.account

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.jsonResponse
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.session.repository.SessionRoleCache
import com.vitran.shop.feature.account.data.remote.AccountApi
import com.vitran.shop.feature.account.data.repository.DefaultAccountRepository
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultAccountRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createAccountTestExecutor()

    @Test
    fun refreshCurrentUser_mapsUnknownRole() = runTest {
        val roleCache = SessionRoleCache()
        val repository = DefaultAccountRepository(
            accountApi = AccountApi(
                client = createAccountTestClient(
                    MockEngine {
                        jsonResponse(
                            HttpStatusCode.OK,
                            """
                            {
                              "success": true,
                              "message": "ok",
                              "code": 1,
                              "data": {
                                "user": {
                                  "id": 1,
                                  "phone": "9123456789",
                                  "username": "javid",
                                  "email": "user@example.com",
                                  "roles": ["customer", "future_role"],
                                  "verified": true,
                                  "is_active": true,
                                  "created_at": "2026-01-01T12:00:00Z",
                                  "updated_at": "2026-01-01T12:00:00Z"
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
            roleCache = roleCache,
            invalidationListeners = mutableListOf(),
        )

        val result = repository.refreshCurrentUser()

        assertIs<AppResult.Success<*>>(result)
        val state = repository.currentUserState.value
        assertIs<CurrentUserState.Available>(state)
        assertTrue(state.user.roles.any { it is UserRole.Unknown && it.rawValue == "future_role" })
        assertTrue(roleCache.roles.any { it is UserRole.Unknown })
    }

    @Test
    fun updateProfile_updatesCacheImmediately() = runTest {
        val repository = DefaultAccountRepository(
            accountApi = AccountApi(
                client = createAccountTestClient(
                    MockEngine { request ->
                        if (request.url.encodedPath.endsWith("/auth/profile")) {
                            jsonResponse(
                                HttpStatusCode.OK,
                                """
                                {
                                  "success": true,
                                  "message": "ok",
                                  "code": 1,
                                  "data": {
                                    "user": {
                                      "id": 1,
                                      "phone": "9123456789",
                                      "username": "updated",
                                      "email": "new@example.com",
                                      "roles": ["customer"],
                                      "verified": true,
                                      "is_active": true,
                                      "created_at": "2026-01-01T12:00:00Z",
                                      "updated_at": "2026-01-02T12:00:00Z"
                                    }
                                  },
                                  "errors": []
                                }
                                """.trimIndent(),
                            )
                        } else {
                            jsonResponse(HttpStatusCode.NotFound, """{"success":false,"message":"missing","code":404,"data":null,"errors":[]}""")
                        }
                    },
                ),
                environment = environment,
                executor = executor,
            ),
            roleCache = SessionRoleCache(),
            invalidationListeners = mutableListOf(),
        )

        val result = repository.updateProfile(
            com.vitran.shop.feature.account.domain.model.UpdateProfileCommand(
                username = "updated",
                email = "new@example.com",
            ),
        )

        assertIs<AppResult.Success<*>>(result)
        val cached = repository.currentUserState.value as CurrentUserState.Available
        assertEquals("updated", cached.user.username)
        assertEquals("new@example.com", cached.user.email)
    }
}
