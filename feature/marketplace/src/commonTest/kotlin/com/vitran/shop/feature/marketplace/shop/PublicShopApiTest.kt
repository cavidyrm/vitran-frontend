package com.vitran.shop.feature.marketplace.shop

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.marketplace.createMarketplaceTestClient
import com.vitran.shop.feature.marketplace.createMarketplaceTestExecutor
import com.vitran.shop.feature.marketplace.jsonResponse
import com.vitran.shop.feature.marketplace.shop.data.remote.PublicShopApi
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopDataDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopsDataDto
import com.vitran.shop.feature.marketplace.shop.data.mapper.toDetail
import com.vitran.shop.feature.marketplace.shop.data.mapper.toShopCollectionPage
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionDetailQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionListQuery
import com.vitran.shop.feature.marketplace.shopDetailEnvelope
import com.vitran.shop.feature.marketplace.shopListEnvelope
import com.vitran.shop.feature.marketplace.shopBrowseEnvelope
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class PublicShopApiTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createMarketplaceTestExecutor()

    @Test
    fun getShops_noAuthorization_andEncodesFilters() = runTest {
        var authorization: String? = "x"
        val api = PublicShopApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertEquals("tehran", request.url.parameters["city_slug"])
                    assertEquals("aa-1-2-3-4", request.url.parameters["category_slug"])
                    jsonResponse(HttpStatusCode.OK, shopListEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getShops(
            ShopListQuery(
                city = com.vitran.shop.feature.marketplace.common.domain.filter.CityFilter.BySlug(
                    com.vitran.shop.feature.location.domain.model.CitySlug("tehran"),
                ),
                categorySlug = CategorySlug("aa-1-2-3-4"),
            ),
        )

        assertIs<AppResult.Success<ShopsDataDto>>(result)
        assertNull(authorization)
    }

    @Test
    fun browseShops_preservesServerOrder() = runTest {
        val api = PublicShopApi(
            client = createMarketplaceTestClient(
                MockEngine {
                    jsonResponse(HttpStatusCode.OK, shopBrowseEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.browseShops(ShopBrowseQuery())

        assertIs<AppResult.Success<*>>(result)
        @Suppress("UNCHECKED_CAST")
        val shops = (result as AppResult.Success<com.vitran.shop.feature.marketplace.shop.data.remote.dto.BrowseShopsDataDto>).value.shops
        assertEquals("business-store", shops.results.first().slug)
    }

    @Test
    fun getShopBySlug_encodesSlugPath() = runTest {
        val api = PublicShopApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/shops/slug/my-shop", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, shopDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getShopBySlug(ShopSlug("my-shop"))

        assertIs<AppResult.Success<ShopDataDto>>(result)
    }

    @Test
    fun getShopById_mapsDetail() = runTest {
        val api = PublicShopApi(
            client = createMarketplaceTestClient(
                MockEngine {
                    jsonResponse(HttpStatusCode.OK, shopDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getShopById(ShopId(1))

        assertIs<AppResult.Success<ShopDataDto>>(result)
        assertEquals("my-shop", result.value.shop.slug)
    }

    @Test
    fun getShopCollections_cursorListWithoutAuth() = runTest {
        var authorization: String? = "x"
        val api = PublicShopApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertEquals("/api/v1/shops/1/collections", request.url.encodedPath)
                    assertEquals("20", request.url.parameters["per_page"])
                    jsonResponse(HttpStatusCode.OK, shopCollectionsEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getShopCollections(ShopCollectionListQuery(shopId = ShopId(1)))

        assertIs<AppResult.Success<*>>(result)
        val page = (result as AppResult.Success).value.collections.toShopCollectionPage()
        assertEquals(1, page.items.size)
        assertEquals("Summer", page.items.first().title)
        assertEquals(2, page.items.first().productCount)
        assertNull(authorization)
    }

    @Test
    fun getShopCollection_mapsThinProducts() = runTest {
        val api = PublicShopApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/shops/1/collections/1", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, shopCollectionDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getShopCollection(
            ShopCollectionDetailQuery(shopId = ShopId(1), collectionId = ShopCollectionId(1)),
        )

        assertIs<AppResult.Success<*>>(result)
        val detail = (result as AppResult.Success).value.toDetail()
        assertEquals("Summer", detail.collection.title)
        assertEquals("blue-widget", detail.products.items.first().slug)
        assertEquals(150000, detail.products.items.first().priceAmount)
    }
}

private const val shopCollectionsEnvelope = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "collections": {
      "per_page": 20,
      "has_more": false,
      "results": [
        {
          "id": 1,
          "shop_id": 1,
          "title": "Summer",
          "description": "Warm-weather picks",
          "product_count": 2,
          "created_at": "2026-06-09T12:00:00Z",
          "updated_at": "2026-06-09T12:00:00Z"
        }
      ]
    }
  },
  "errors": []
}
"""

private const val shopCollectionDetailEnvelope = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "collection": {
      "id": 1,
      "shop_id": 1,
      "title": "Summer",
      "description": "Warm-weather picks",
      "product_count": 2,
      "created_at": "2026-06-09T12:00:00Z",
      "updated_at": "2026-06-09T12:00:00Z"
    },
    "products": {
      "per_page": 20,
      "has_more": false,
      "results": [
        { "id": 4, "title": "Blue Widget", "slug": "blue-widget", "price": 150000 }
      ]
    }
  },
  "errors": []
}
"""
