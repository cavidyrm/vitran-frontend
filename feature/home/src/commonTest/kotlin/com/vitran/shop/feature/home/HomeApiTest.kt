package com.vitran.shop.feature.home

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.home.data.mapper.toDomain
import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.data.remote.dto.HomeDataDto
import com.vitran.shop.feature.home.data.remote.dto.HomeScreenDataDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionDataDto
import com.vitran.shop.feature.location.domain.model.CityId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    private val homeScreenEnvelope = """
        {
          "success": true,
          "message": "ok",
          "code": 1,
          "data": {
            "screen": {
              "sections": [
                {
                  "slug": "picked-for-you",
                  "title": "Picked for you",
                  "type": "product_grid",
                  "layout": "horizontal_scroll",
                  "source": "product_matches",
                  "more": { "path": "/api/v1/home/sections/picked-for-you" }
                },
                {
                  "slug": "future-rail",
                  "title": "Future",
                  "type": "unknown_type",
                  "layout": "unknown_layout"
                }
              ]
            }
          },
          "errors": []
        }
    """.trimIndent()

    private val pickedForYouEnvelope = """
        {
          "success": true,
          "message": "بخش صفحه اصلی با موفقیت دریافت شد",
          "code": 1,
          "data": {
            "section": {
              "slug": "picked-for-you",
              "title": "Picked for you",
              "type": "product_grid",
              "layout": "horizontal_scroll",
              "source": "product_matches",
              "per_page": 20,
              "has_more": false,
              "results": [
                {
                  "id": 501,
                  "title": "Jacket",
                  "price": 890000,
                  "compare_at_price": 1200000
                }
              ]
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

    @Test
    fun getHomeScreen_keepsUnknownSectionTypes() = runTest {
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine { request ->
                    assertTrue(request.url.encodedPath.endsWith("/home/screen"))
                    assertEquals("1", request.url.parameters["city_id"])
                    jsonResponse(HttpStatusCode.OK, homeScreenEnvelope)
                },
                token = "test-token",
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getHomeScreen(CityId(1))

        assertIs<AppResult.Success<HomeScreenDataDto>>(result)
        val screen = result.value.toDomain(CityId(1))
        assertEquals(2, screen.sections.size)
        assertEquals("picked-for-you", screen.sections[0].slug)
        assertEquals("/api/v1/home/sections/picked-for-you", screen.sections[0].morePath)
        assertEquals("unknown_type", screen.sections[1].type)
        assertEquals("unknown_layout", screen.sections[1].layout)
    }

    @Test
    fun getPickedForYouSection_mapsCompareAtPrice() = runTest {
        var authorization: String? = null
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertTrue(request.url.encodedPath.endsWith("/home/sections/picked-for-you"))
                    assertEquals("20", request.url.parameters["limit"])
                    assertEquals("42", request.url.parameters["cursor"])
                    jsonResponse(HttpStatusCode.OK, pickedForYouEnvelope)
                },
                token = "test-token",
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getPickedForYouSection(limit = 20, cursor = "42")

        assertEquals("Bearer test-token", authorization)
        assertIs<AppResult.Success<HomeSectionDataDto>>(result)
        val page = result.value.section.toDomain()
        assertEquals("picked-for-you", page.slug)
        assertEquals(1, page.products.size)
        assertEquals(890000L, page.products[0].priceAmount)
        assertEquals(1200000L, page.products[0].compareAtPriceAmount)
    }
}
