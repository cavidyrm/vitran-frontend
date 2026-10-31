package com.vitran.shop.feature.home

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.home.data.HomeClientContext
import com.vitran.shop.feature.home.data.mapper.toDomain
import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.domain.model.HomeBannerAction
import com.vitran.shop.feature.home.domain.model.HomeRailItem
import com.vitran.shop.feature.home.domain.model.HomeRailLayout
import com.vitran.shop.feature.home.domain.model.HomeRailType
import com.vitran.shop.feature.home.domain.model.shouldRender
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

    private val clientContext = HomeClientContext(platform = "ios", appVersion = "1.4.0")

    private val homeScreenEnvelope = """
        {
          "success": true,
          "message": "چیدمان صفحه اصلی با موفقیت دریافت شد",
          "code": 1,
          "data": {
            "screen": {
              "revision": 1735689600,
              "city_id": 1,
              "sections": [
                {
                  "id": 1,
                  "key": "categories",
                  "type": "categories",
                  "layout": "chips",
                  "title": "دسته‌بندی‌ها",
                  "requires_auth": false,
                  "hide_when_empty": false,
                  "empty": false,
                  "items": {
                    "per_page": 8,
                    "has_more": false,
                    "results": [
                      { "slug": "aa-1", "title": "Apparel", "name": "پوشاک", "level": 0, "is_leaf": false }
                    ]
                  }
                },
                {
                  "id": 6,
                  "key": "popular",
                  "type": "products",
                  "layout": "grid",
                  "title": "پربازدید",
                  "requires_auth": false,
                  "hide_when_empty": false,
                  "empty": false,
                  "items": {
                    "per_page": 12,
                    "has_more": true,
                    "next_cursor": "412",
                    "total": 87,
                    "results": [
                      {
                        "id": 501,
                        "title": "Jacket",
                        "price": 890000,
                        "compare_at_price": 1200000,
                        "main_image": { "id": 9, "url": "https://cdn.example/p/9.jpg", "is_main": true }
                      }
                    ]
                  },
                  "more": { "path": "/home/sections/popular", "cursor_param": "cursor" }
                },
                {
                  "id": 7,
                  "key": "summer-campaign",
                  "type": "banners",
                  "layout": "hero",
                  "title": "حراج تابستانه",
                  "hide_when_empty": true,
                  "empty": false,
                  "items": {
                    "per_page": 5,
                    "has_more": false,
                    "results": [
                      {
                        "id": 14,
                        "image_url": "https://cdn.example/banners/new-feature.jpg",
                        "action": { "type": "open_future_thing", "value": "abc" }
                      }
                    ]
                  }
                },
                {
                  "key": "spring-banner",
                  "type": "products",
                  "layout": "carousel_3d",
                  "title": "…"
                }
              ]
            }
          },
          "errors": []
        }
    """.trimIndent()

    private val sectionPageEnvelope = """
        {
          "success": true,
          "message": "بخش صفحه اصلی با موفقیت دریافت شد",
          "code": 1,
          "data": {
            "section": {
              "key": "popular",
              "type": "products",
              "layout": "grid",
              "title": "پربازدید",
              "requires_auth": false,
              "hide_when_empty": false,
              "empty": false,
              "items": {
                "per_page": 12,
                "has_more": false,
                "results": [ { "id": 388, "title": "Backpack", "price": 450000 } ]
              },
              "more": { "path": "/home/sections/popular", "cursor_param": "cursor" }
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
    fun getHomeScreen_decodesRailsAndSkipsUnknownLayout() = runTest {
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine { request ->
                    assertTrue(request.url.encodedPath.endsWith("/home/screen"))
                    assertEquals("1", request.url.parameters["city_id"])
                    assertEquals("ios", request.url.parameters["platform"])
                    assertEquals("1.4.0", request.url.parameters["app_version"])
                    jsonResponse(HttpStatusCode.OK, homeScreenEnvelope)
                },
                token = null,
            ),
            environment = environment,
            executor = executor,
            clientContext = clientContext,
        )

        val result = api.getHomeScreen(CityId(1))

        assertIs<AppResult.Success<HomeScreenDataDto>>(result)
        val screen = result.value.toDomain(CityId(1))
        assertEquals(1735689600L, screen.revision)
        assertEquals(4, screen.sections.size)

        val categories = screen.sections[0]
        assertEquals("categories", categories.key)
        assertEquals(HomeRailType.Categories, categories.type)
        assertEquals(HomeRailLayout.Chips, categories.layout)
        assertTrue(categories.shouldRender())
        val category = categories.items.results.single() as HomeRailItem.Category
        assertEquals("پوشاک", category.title)
        assertEquals("aa-1", category.slug)

        val popular = screen.sections[1]
        assertEquals("popular", popular.key)
        assertEquals("/home/sections/popular", popular.morePath)
        assertEquals("412", popular.items.nextCursor)
        assertEquals(87, popular.items.total)
        assertTrue(popular.shouldRender())
        val product = popular.items.results.single() as HomeRailItem.Product
        assertEquals(890000L, product.priceAmount)
        assertEquals(1200000L, product.compareAtPriceAmount)
        assertEquals("https://cdn.example/p/9.jpg", product.imageUrl)

        val banner = screen.sections[2]
        assertTrue(banner.shouldRender())
        val bannerItem = banner.items.results.single() as HomeRailItem.Banner
        assertEquals(HomeBannerAction.None, bannerItem.action)

        val unknown = screen.sections[3]
        assertEquals(HomeRailType.Products, unknown.type)
        assertEquals(HomeRailLayout.Unknown, unknown.layout)
        assertTrue(!unknown.shouldRender())
    }

    @Test
    fun getHomeSection_sendsCursorPlatformAndVersion() = runTest {
        var authorization: String? = null
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertTrue(request.url.encodedPath.endsWith("/home/sections/popular"))
                    assertEquals("12", request.url.parameters["limit"])
                    assertEquals("412", request.url.parameters["cursor"])
                    assertEquals("ios", request.url.parameters["platform"])
                    assertEquals("1.4.0", request.url.parameters["app_version"])
                    assertEquals("1", request.url.parameters["city_id"])
                    jsonResponse(HttpStatusCode.OK, sectionPageEnvelope)
                },
                token = "test-token",
            ),
            environment = environment,
            executor = executor,
            clientContext = clientContext,
        )

        val result = api.getHomeSection(
            path = "/home/sections/popular",
            limit = 12,
            cursor = "412",
            cursorParam = "cursor",
            cityId = CityId(1),
        )

        assertEquals("Bearer test-token", authorization)
        assertIs<AppResult.Success<HomeSectionDataDto>>(result)
        val page = result.value.section.toDomain()
        assertEquals("popular", page.key)
        assertEquals(false, page.items.hasMore)
        val product = page.items.results.single() as HomeRailItem.Product
        assertEquals(388L, product.id)
        assertEquals(450000L, product.priceAmount)
    }

    @Test
    fun getHomeScreen_decodesGuestExampleTrackingAndOpaqueCursor() = runTest {
        val api = HomeApi(
            client = createHomeTestClient(
                MockEngine {
                    jsonResponse(HttpStatusCode.OK, guestScreenEnvelope)
                },
                token = null,
            ),
            environment = environment,
            executor = executor,
            clientContext = clientContext,
        )

        val result = api.getHomeScreen(CityId(1))

        assertIs<AppResult.Success<HomeScreenDataDto>>(result)
        val screen = result.value.toDomain(CityId(1))
        assertEquals(3791378435928646L, screen.revision)
        assertEquals(3, screen.sections.size)

        val banners = screen.sections[0]
        assertEquals("summer-campaign", banners.key)
        assertEquals(HomeRailType.Banners, banners.type)
        assertEquals("banners", banners.tracking?.source)
        assertEquals("summer-campaign", banners.tracking?.sectionKey)
        assertEquals(1, banners.tracking?.position)
        assertTrue(banners.shouldRender())

        val popular = screen.sections[2]
        assertEquals("popular", popular.key)
        assertEquals(HomeRailLayout.Grid, popular.layout)
        assertEquals("pop:12", popular.items.nextCursor)
        assertEquals(true, popular.items.hasMore)
        assertEquals("popular_products", popular.tracking?.source)
        val product = popular.items.results.single() as HomeRailItem.Product
        assertEquals(501L, product.id)
        assertEquals("aa-1-2", product.categorySlug)
        assertEquals(3L, product.shopId)
    }

    private val guestScreenEnvelope = """
        {
          "success": true,
          "message": "چیدمان صفحه اصلی با موفقیت دریافت شد",
          "code": 1,
          "data": {
            "screen": {
              "revision": 3791378435928646,
              "city_id": 1,
              "sections": [
                {
                  "id": 7,
                  "key": "summer-campaign",
                  "type": "banners",
                  "layout": "hero",
                  "title": "حراج تابستانه",
                  "requires_auth": false,
                  "hide_when_empty": true,
                  "empty": false,
                  "items": {
                    "per_page": 5,
                    "has_more": false,
                    "results": [
                      {
                        "id": 12,
                        "title": "تا ۴۰٪ تخفیف",
                        "image_url": "https://cdn.vitran.ir/banners/summer.jpg",
                        "action": { "type": "open_section", "value": "popular" }
                      }
                    ]
                  },
                  "tracking": {
                    "section_key": "summer-campaign",
                    "source": "banners",
                    "position": 1
                  }
                },
                {
                  "id": 1,
                  "key": "categories",
                  "type": "categories",
                  "layout": "chips",
                  "title": "دسته‌بندی‌ها",
                  "requires_auth": false,
                  "hide_when_empty": false,
                  "empty": false,
                  "items": {
                    "per_page": 8,
                    "has_more": false,
                    "results": [ { "slug": "aa-1", "name": "پوشاک", "level": 0, "is_leaf": false } ]
                  },
                  "tracking": { "section_key": "categories", "source": "categories", "position": 2 }
                },
                {
                  "id": 6,
                  "key": "popular",
                  "type": "products",
                  "layout": "grid",
                  "title": "پربازدید",
                  "requires_auth": false,
                  "hide_when_empty": false,
                  "empty": false,
                  "items": {
                    "per_page": 12,
                    "has_more": true,
                    "next_cursor": "pop:12",
                    "total": 87,
                    "results": [
                      {
                        "id": 501,
                        "shop_id": 3,
                        "title": "کاپشن زمستانه",
                        "slug": "winter-jacket",
                        "category_slug": "aa-1-2",
                        "price": 890000,
                        "status": "active",
                        "main_image": { "id": 9, "url": "https://cdn.vitran.ir/p/9.jpg", "is_main": true }
                      }
                    ]
                  },
                  "more": { "path": "/home/sections/popular", "cursor_param": "cursor" },
                  "tracking": { "section_key": "popular", "source": "popular_products", "position": 3 }
                }
              ]
            }
          },
          "errors": []
        }
    """.trimIndent()
}
