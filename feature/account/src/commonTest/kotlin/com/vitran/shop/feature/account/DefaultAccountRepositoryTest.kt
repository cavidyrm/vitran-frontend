package com.vitran.shop.feature.account

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.repository.SessionRoleCache
import com.vitran.shop.feature.account.data.remote.AccountApi
import com.vitran.shop.feature.account.data.remote.dto.UpdateProfileRequestDto
import com.vitran.shop.feature.account.data.repository.DefaultAccountRepository
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.joinFullName
import com.vitran.shop.feature.account.domain.model.splitFullName
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultAccountRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createAccountTestExecutor()

    @Test
    fun refreshCurrentUser_mapsProfileCityAndAvatar() = runTest {
        val repository = repository(
            MockEngine {
                jsonResponse(HttpStatusCode.OK, currentUserBody)
            },
        )

        val result = repository.refreshCurrentUser()

        assertIs<AppResult.Success<*>>(result)
        val state = repository.currentUserState.value
        assertIs<CurrentUserState.Available>(state)
        assertTrue(state.user.roles.any { it is UserRole.Unknown && it.rawValue == "future_role" })
        assertEquals("Javid", state.user.fullName)
        assertEquals("https://cdn.example/avatar.png", state.user.avatarUrl)
        assertEquals(1L, state.user.cityId)
        assertEquals("Tehran", state.user.city?.name)
        assertEquals("Tehran", state.user.city?.province)
        assertEquals("V2", state.user.referralCode)
        assertEquals(emptyList(), state.user.shopTypes)
    }

    @Test
    fun updateProfile_sendsEditableFields_andUpdatesCache() = runTest {
        var bodyText = ""
        val repository = repository(
            MockEngine { request ->
                if (request.url.encodedPath.endsWith("/auth/profile")) {
                    bodyText = (request.body as TextContent).text
                    jsonResponse(HttpStatusCode.OK, updatedUserBody)
                } else {
                    jsonResponse(
                        HttpStatusCode.NotFound,
                        """{"success":false,"message":"missing","code":404,"data":null,"errors":[]}""",
                    )
                }
            },
        )

        val result = repository.updateProfile(
            UpdateProfileCommand(
                username = "updated",
                email = "new@example.com",
                fullName = "Javid Updated",
                avatarUrl = "https://cdn.example/avatar.png",
                cityId = 1,
            ),
        )

        assertIs<AppResult.Success<*>>(result)
        assertTrue(bodyText.contains("\"username\":\"updated\""))
        assertTrue(bodyText.contains("\"full_name\":\"Javid Updated\""))
        assertTrue(bodyText.contains("\"avatar_url\":\"https://cdn.example/avatar.png\""))
        assertTrue(bodyText.contains("\"city_id\":1"))
        assertFalse(bodyText.contains("clear_city_id"))
        val cached = repository.currentUserState.value as CurrentUserState.Available
        assertEquals("updated", cached.user.username)
        assertEquals("new@example.com", cached.user.email)
        assertEquals(1L, cached.user.cityId)
    }

    @Test
    fun updateProfile_clearCity_omitsCityIdAndSendsFlag() = runTest {
        var bodyText = ""
        val repository = repository(
            MockEngine { request ->
                bodyText = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.OK, updatedUserBodyClearedCity)
            },
        )

        val result = repository.updateProfile(
            UpdateProfileCommand(
                username = "javid",
                email = "user@example.com",
                fullName = "Javid",
                clearCityId = true,
            ),
        )

        assertIs<AppResult.Success<*>>(result)
        assertTrue(bodyText.contains("\"clear_city_id\":true"))
        assertFalse(bodyText.contains("city_id"))
        val cached = repository.currentUserState.value as CurrentUserState.Available
        assertNull(cached.user.cityId)
        assertNull(cached.user.city)
    }

    @Test
    fun updateProfileRequest_omitsNullsIncludingClearFlag() {
        val json = createNetworkJson()
        val encoded = json.encodeToString(
            UpdateProfileRequestDto.serializer(),
            UpdateProfileRequestDto(username = "javid", email = "user@example.com"),
        )
        assertEquals("""{"username":"javid","email":"user@example.com"}""", encoded)
    }

    @Test
    fun splitAndJoinFullName() {
        assertEquals("Javid" to "", splitFullName("Javid"))
        assertEquals("علی" to "محمدی", splitFullName("علی محمدی"))
        assertEquals("علی محمدی", joinFullName("علی", "محمدی"))
        assertNull(joinFullName("  ", ""))
    }

    private fun repository(engine: MockEngine) = DefaultAccountRepository(
        accountApi = AccountApi(
            client = createAccountTestClient(engine),
            environment = environment,
            executor = executor,
        ),
        roleCache = SessionRoleCache(),
        invalidationListeners = mutableListOf(),
    )
}

private val currentUserBody = """
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
      "full_name": "Javid",
      "avatar_url": "https://cdn.example/avatar.png",
      "city_id": 1,
      "city": {
        "id": 1,
        "slug": "tehran",
        "name": "Tehran",
        "province": "Tehran"
      },
      "referral_code": "V2",
      "wishlist_share_slug": "wl-a1b2c3d4e5f67890",
      "wishlist_public": false,
      "product_match_notify": true,
      "roles": ["user", "future_role"],
      "shop_types": [],
      "verified": true,
      "is_active": true,
      "created_at": "2026-01-01T12:00:00Z",
      "updated_at": "2026-01-01T12:00:00Z"
    }
  },
  "errors": []
}
""".trimIndent()

private val updatedUserBody = """
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
      "full_name": "Javid Updated",
      "avatar_url": "https://cdn.example/avatar.png",
      "city_id": 1,
      "city": { "id": 1, "slug": "tehran", "name": "Tehran", "province": "Tehran" },
      "roles": ["user"],
      "verified": true,
      "is_active": true,
      "created_at": "2026-01-01T12:00:00Z",
      "updated_at": "2026-01-02T12:00:00Z"
    }
  },
  "errors": []
}
""".trimIndent()

private val updatedUserBodyClearedCity = """
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
      "full_name": "Javid",
      "roles": ["user"],
      "verified": true,
      "is_active": true,
      "created_at": "2026-01-01T12:00:00Z",
      "updated_at": "2026-01-02T12:00:00Z"
    }
  },
  "errors": []
}
""".trimIndent()
