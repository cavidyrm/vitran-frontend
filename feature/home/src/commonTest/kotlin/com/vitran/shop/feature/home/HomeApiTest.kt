package com.vitran.shop.feature.home

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.data.remote.dto.HomeDataDto
import com.vitran.shop.feature.location.domain.model.CityId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class HomeApiTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createHomeTestExecutor()

    private val homeEnvelope = """
        {
          "success": true,
          "message": "ok",
          "code": 1,
          "data": {
            "home": {
              "featured": [],
              "popular": [],
              "categories": [],
              "following": [],
              "personal": []
            }
          },
          "errors": []
        }
    """.trimIndent()

    @Test
    fun getHome_anonymous_hasNoAuthorizationHeader() = runTest {
        var authorization: String? = "token"
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertEquals("1", request.url.parameters["city_id"])
                    jsonResponse(HttpStatusCode.OK, homeEnvelope)
                },
                token = null,
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getHome(CityId(1))

        assertIs<AppResult.Success<HomeDataDto>>(result)
        assertNull(authorization)
    }

    @Test
    fun getHome_authenticated_attachesAuthorizationHeader() = runTest {
        var authorization: String? = null
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    jsonResponse(HttpStatusCode.OK, homeEnvelope)
                },
                token = "test-token",
            ),
            environment = environment,
            executor = executor,
        )

        api.getHome()

        assertEquals("Bearer test-token", authorization)
    }
}
