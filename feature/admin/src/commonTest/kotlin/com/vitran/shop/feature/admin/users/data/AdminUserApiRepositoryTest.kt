package com.vitran.shop.feature.admin.users.data

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.admin.createAdminTestClient
import com.vitran.shop.feature.admin.createAdminTestExecutor
import com.vitran.shop.feature.admin.jsonResponse
import com.vitran.shop.feature.admin.users.data.remote.AdminUserApi
import com.vitran.shop.feature.admin.users.data.repository.DefaultAdminUserRepository
import com.vitran.shop.feature.admin.users.domain.model.AdminUserQuery
import com.vitran.shop.feature.admin.users.domain.model.UpdateAdminUserCommand
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AdminUserApiRepositoryTest {
    private val environment = ApiEnvironment(origin = "http://localhost:8080")

    @Test
    fun getUsers_sendsRequiredAuthAndPageFilters_thenMapsPage() = runTest {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/api/v1/admin/users", request.url.encodedPath)
            assertEquals("Bearer ADMIN_ACCESS", request.headers[HttpHeaders.Authorization])
            assertEquals("admin", request.url.parameters["role"])
            assertEquals("0912", request.url.parameters["phone"])
            assertEquals("false", request.url.parameters["is_active"])
            assertEquals("2", request.url.parameters["page"])
            assertEquals("10", request.url.parameters["per_page"])
            jsonResponse(HttpStatusCode.OK, usersEnvelope)
        }

        val result = createRepository(engine).getUsers(
            AdminUserQuery(
                role = "admin",
                phone = "0912",
                isActive = false,
                page = 2,
                perPage = 10,
            ),
        )

        val page = assertIs<AppResult.Success<*>>(result).value
            as com.vitran.shop.core.domain.pagination.PageResult<*>
        assertEquals(2, page.page)
        assertEquals(1, page.items.size)
        assertEquals(25L, page.total)
        assertEquals(true, page.hasMore)
        val user = page.items.single()
            as com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
        assertEquals(42L, user.id)
        assertEquals(setOf(UserRole.Admin, UserRole.Unknown("auditor")), user.roles)
        assertEquals(false, user.isActive)
    }

    @Test
    fun getUsers_decodesSlimEnvelopeWithoutPageOrTotals() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, slimUsersEnvelope) }

        val result = createRepository(engine).getUsers(AdminUserQuery(page = 1, perPage = 20))

        val page = assertIs<AppResult.Success<*>>(result).value
            as com.vitran.shop.core.domain.pagination.PageResult<*>
        assertEquals(1, page.page)
        assertEquals(1, page.lastPage)
        assertEquals(2L, page.total)
        assertEquals(false, page.hasMore)
        assertEquals(2, page.items.size)
        val user = page.items.first()
            as com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
        assertEquals(14L, user.id)
        assertEquals(setOf(UserRole.User), user.roles)
    }

    @Test
    fun getAndUpdateUser_useIdPath_mapTimestamps_andSerializeExactPatchBody() = runTest {
        var requestCount = 0
        val engine = MockEngine { request ->
            requestCount += 1
            assertEquals("/api/v1/admin/users/42", request.url.encodedPath)
            assertEquals("Bearer ADMIN_ACCESS", request.headers[HttpHeaders.Authorization])
            when (request.method) {
                HttpMethod.Get -> jsonResponse(HttpStatusCode.OK, userEnvelope)
                HttpMethod.Patch -> {
                    assertEquals(
                        """{"is_active":false,"roles":["user","super_admin"]}""",
                        (request.body as TextContent).text,
                    )
                    jsonResponse(HttpStatusCode.OK, updatedUserEnvelope)
                }
                else -> error("Unexpected method: ${request.method}")
            }
        }
        val repository = createRepository(engine)

        val loaded = assertIs<AppResult.Success<*>>(repository.getUser(42)).value
            as com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
        assertEquals(Instant.parse("2026-08-01T10:00:00Z"), loaded.createdAt)

        val updated = assertIs<AppResult.Success<*>>(
            repository.updateUser(
                UpdateAdminUserCommand(
                    userId = 42,
                    isActive = false,
                    roles = listOf("user", "super_admin"),
                ),
            ),
        ).value as com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
        assertEquals(setOf(UserRole.User, UserRole.SuperAdmin), updated.roles)
        assertEquals(false, updated.isActive)
        assertEquals(2, requestCount)
    }

    private fun createRepository(engine: MockEngine): DefaultAdminUserRepository {
        val api = AdminUserApi(
            client = createAdminTestClient(engine),
            environment = environment,
            executor = createAdminTestExecutor(),
        )
        return DefaultAdminUserRepository(api)
    }

    private companion object {
        val usersEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "users": {
                  "page": 2,
                  "per_page": 10,
                  "last_page": 3,
                  "from": 11,
                  "to": 20,
                  "total": 25,
                  "has_more": true,
                  "results": [{
                    "id": 42,
                    "phone": "09123456789",
                    "roles": ["admin", "auditor"],
                    "verified": true,
                    "is_active": false
                  }]
                }
              },
              "errors": []
            }
        """.trimIndent()

        val userEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "user": {
                  "id": 42,
                  "phone": "09123456789",
                  "roles": ["admin", "super_admin"],
                  "verified": true,
                  "is_active": true,
                  "created_at": "2026-08-01T10:00:00Z",
                  "updated_at": "2026-08-20T12:30:00Z"
                }
              },
              "errors": []
            }
        """.trimIndent()

        val updatedUserEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "user": {
                  "id": 42,
                  "phone": "09123456789",
                  "roles": ["user", "super_admin"],
                  "verified": true,
                  "is_active": false,
                  "created_at": "2026-08-01T10:00:00Z",
                  "updated_at": "2026-08-30T09:00:00Z"
                }
              },
              "errors": []
            }
        """.trimIndent()

        val slimUsersEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "users": {
                  "per_page": 20,
                  "has_more": false,
                  "results": [
                    {
                      "id": 14,
                      "phone": "9147786264",
                      "wishlist_public": false,
                      "roles": ["user"],
                      "shop_types": null,
                      "verified": true,
                      "created_at": "2026-09-08T19:47:13.021384Z",
                      "updated_at": "2026-09-08T19:47:13.021384Z",
                      "is_active": true
                    },
                    {
                      "id": 13,
                      "phone": "9330762801",
                      "wishlist_public": false,
                      "roles": ["user"],
                      "shop_types": null,
                      "verified": true,
                      "created_at": "2026-09-07T20:08:56.011038Z",
                      "updated_at": "2026-09-07T20:08:56.011038Z",
                      "is_active": true
                    }
                  ]
                }
              },
              "errors": []
            }
        """.trimIndent()
    }
}
