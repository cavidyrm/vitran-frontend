package com.vitran.shop.feature.seller.shop.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.UpdateShopCollectionRequestDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionDetailQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionListQuery
import com.vitran.shop.feature.seller.createSellerRepository
import com.vitran.shop.feature.seller.createShopWithTokenBody
import com.vitran.shop.feature.seller.createShopWithoutTokenBody
import com.vitran.shop.feature.seller.fulfillmentBody
import com.vitran.shop.feature.seller.apiKeyBody
import com.vitran.shop.feature.seller.hasAuthBearer
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.sellerGetPendingBody
import com.vitran.shop.feature.seller.sellerListBody
import com.vitran.shop.feature.seller.slugAvailableBody
import com.vitran.shop.feature.seller.slugConflictBody
import com.vitran.shop.feature.seller.slugTakenBody
import com.vitran.shop.feature.seller.titleAvailableBody
import com.vitran.shop.feature.seller.titleTakenBody
import com.vitran.shop.feature.seller.updateShopBody
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopRequestDto
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.error.isSlugAlreadyTaken
import com.vitran.shop.feature.seller.shop.data.mapper.toRequestDto
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.ShopRule
import com.vitran.shop.feature.seller.shop.domain.model.ShopShippingMethod
import com.vitran.shop.feature.seller.shop.domain.model.ShopWorkingHours
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SellerShopApiRepositoryTest {

    @Test
    fun slugCheck_available() = runTest {
        val engine =
            MockEngine { request ->
                assertTrue(request.url.encodedPath.contains("check-slug"))
                assertEquals("my-shop", request.url.parameters["slug"])
                jsonResponse(HttpStatusCode.OK, slugAvailableBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.checkSlugAvailability(ShopSlug("my-shop"))
        assertIs<AppResult.Success<*>>(result)
        assertTrue((result as AppResult.Success).value.isAvailable)
    }

    @Test
    fun slugCheck_taken() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, slugTakenBody) }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.checkSlugAvailability(ShopSlug("my-shop"))
        assertIs<AppResult.Success<*>>(result)
        assertFalse((result as AppResult.Success).value.isAvailable)
    }

    @Test
    fun slugCheck_excludeId() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals("12", request.url.parameters["exclude_id"])
                jsonResponse(HttpStatusCode.OK, slugAvailableBody)
            }
        val (repo, _) = createSellerRepository(engine)
        repo.checkSlugAvailability(ShopSlug("my-shop"), excludeId = ShopId(12))
    }

    @Test
    fun titleCheck_available() = runTest {
        val engine =
            MockEngine { request ->
                assertTrue(request.url.encodedPath.contains("check-title"))
                assertEquals("My Shop", request.url.parameters["title"])
                jsonResponse(HttpStatusCode.OK, titleAvailableBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.checkTitleAvailability("My Shop")
        assertIs<AppResult.Success<*>>(result)
        assertTrue((result as AppResult.Success).value.isAvailable)
    }

    @Test
    fun titleCheck_taken() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, titleTakenBody) }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.checkTitleAvailability("My Shop")
        assertIs<AppResult.Success<*>>(result)
        assertFalse((result as AppResult.Success).value.isAvailable)
    }

    @Test
    fun createShop_omitsSlug_andSerializesCategorySlugsAsStrings() {
        val json = createNetworkJson()
        val dto =
            CreateShopRequestDto(
                title = "My Shop",
                slug = null,
                type = "retailer",
                cityId = 1,
                categorySlugs = listOf("aa-1-2-3-4"),
            )
        val encoded = json.encodeToString(CreateShopRequestDto.serializer(), dto)
        assertFalse(encoded.contains("\"slug\""))
        assertFalse(encoded.contains("\"avatar_url\""))
        assertTrue(encoded.contains("\"category_slugs\":[\"aa-1-2-3-4\"]"))
        assertFalse(encoded.contains("seo_title"))
        assertFalse(encoded.contains("bale"))
    }

    @Test
    fun createShop_serializesProfileFields_andOmitsEmptyOptionals() {
        val json = createNetworkJson()
        val encoded =
            json.encodeToString(
                CreateShopRequestDto.serializer(),
                CreateShopCommand(
                    title = "My Shop",
                    type = "retailer",
                    cityId = CityId(1),
                    seoTitle = "My Shop | Vitran",
                    noindex = false,
                    rules = listOf(ShopRule(title = "Returns", body = "7 days")),
                    shippingMethods =
                        listOf(
                            ShopShippingMethod(
                                code = "post",
                                title = "Post",
                                cost = 250000,
                                minDays = 2,
                                maxDays = 5,
                                active = true,
                            ),
                        ),
                    workingHours =
                        listOf(
                            ShopWorkingHours(
                                day = "sat",
                                opensAt = "09:00",
                                closesAt = "18:00",
                                closed = false,
                            ),
                        ),
                    fulfillmentType = "api",
                    webhookUrl = "https://example.com/hook",
                    bale = "https://ble.ir/myshop",
                    rubika = "myshop",
                    tiktok = "@myshop",
                ).toRequestDto(),
            )
        assertTrue(encoded.contains("\"seo_title\":\"My Shop | Vitran\""))
        assertTrue(encoded.contains("\"noindex\":false"))
        assertTrue(encoded.contains("\"fulfillment_type\":\"api\""))
        assertTrue(encoded.contains("\"webhook_url\":\"https://example.com/hook\""))
        assertTrue(encoded.contains("\"bale\":\"https://ble.ir/myshop\""))
        assertTrue(encoded.contains("\"rubika\":\"myshop\""))
        assertTrue(encoded.contains("\"tiktok\":\"@myshop\""))
        assertTrue(encoded.contains("\"rules\""))
        assertTrue(encoded.contains("\"shipping_methods\""))
        assertTrue(encoded.contains("\"working_hours\""))
    }

    @Test
    fun updateShop_sendsClearFlags_andEmptyLists() {
        val json = createNetworkJson()
        val encoded =
            json.encodeToString(
                com.vitran.shop.feature.seller.shop.data.remote.dto.UpdateShopRequestDto.serializer(),
                UpdateShopCommand(
                    shopId = ShopId(1),
                    seoTitle = "",
                    rules = emptyList(),
                    clearAvatarUrl = true,
                    clearWebhookUrl = true,
                ).toRequestDto(),
            )
        assertTrue(encoded.contains("\"seo_title\":\"\""))
        assertTrue(encoded.contains("\"rules\":[]"))
        assertTrue(encoded.contains("\"clear_avatar_url\":true"))
        assertTrue(encoded.contains("\"clear_webhook_url\":true"))
        assertFalse(encoded.contains("shipping_methods"))
    }

    @Test
    fun fulfillmentMode_mapsDocumentedValues() {
        assertEquals(FulfillmentMode.Api, FulfillmentMode.fromRaw("api"))
        assertEquals(FulfillmentMode.Marketplace, FulfillmentMode.fromRaw("marketplace"))
        assertEquals(FulfillmentMode.Local, FulfillmentMode.fromRaw("local"))
        assertEquals("api", FulfillmentMode.Api.wireValue())
    }

    @Test
    fun createShop_serializesAvatarUrl() {
        val json = createNetworkJson()
        val dto =
            CreateShopRequestDto(
                title = "My Shop",
                avatarUrl = "https://cdn.example/shop.png",
                type = "retailer",
                cityId = 1,
            )
        val encoded = json.encodeToString(CreateShopRequestDto.serializer(), dto)
        assertTrue(encoded.contains("\"avatar_url\":\"https://cdn.example/shop.png\""))
    }

    @Test
    fun createShop_success_pendingApproval() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.Created, createShopWithoutTokenBody) }
        val (repo, _) = createSellerRepository(engine)
        val result =
            repo.createShop(
                CreateShopCommand(title = "Second", type = "retailer", cityId = CityId(1)),
            )
        assertIs<AppResult.Success<*>>(result)
        val shop = (result as AppResult.Success).value.shop
        assertEquals(ShopPublicationState.PendingApproval, shop.publicationState)
        assertNull(result.value.sessionAccessUpdate)
    }

    @Test
    fun createShop_mapsAvatarUrl() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.Created, createShopWithTokenBody) }
        val (repo, _) = createSellerRepository(engine)
        val result =
            repo.createShop(
                CreateShopCommand(
                    title = "My Shop",
                    avatarUrl = "https://cdn.example/shop.png",
                    type = "retailer",
                    cityId = CityId(1),
                ),
            )
        assertIs<AppResult.Success<*>>(result)
        assertEquals(
            "https://cdn.example/shop.png",
            (result as AppResult.Success).value.shop.avatarUrl,
        )
    }

    @Test
    fun createShop_slugConflict() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.Conflict, slugConflictBody) }
        val (repo, _) = createSellerRepository(engine)
        val result =
            repo.createShop(
                CreateShopCommand(title = "X", slug = ShopSlug("taken"), type = "retailer", cityId = CityId(1)),
            )
        assertIs<AppResult.Failure>(result)
        assertTrue((result as AppResult.Failure).error.isSlugAlreadyTaken())
    }

    @Test
    fun listMyShops_cursorAndActiveFilter() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals("true", request.url.parameters["active"])
                assertEquals("20", request.url.parameters["per_page"])
                jsonResponse(HttpStatusCode.OK, sellerListBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result =
            repo.getMyShops(
                SellerShopListQuery(activeFilter = SellerShopFilter.Active),
            )
        assertIs<AppResult.Success<*>>(result)
        assertEquals(1, (result as AppResult.Success).value.items.size)
    }

    @Test
    fun getMyShop_pendingLoads() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, sellerGetPendingBody) }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.getMyShop(ShopId(1))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(ShopPublicationState.PendingApproval, (result as AppResult.Success).value.publicationState)
    }

    @Test
    fun getMyShop_readsPascalCaseCategorySlugs() = runTest {
        val body = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "shop": {
                  "id": 1,
                  "slug": "galry-nvr",
                  "active": false,
                  "confirmed": false,
                  "CategorySlugs": ["ap-1", "fb-2"]
                }
              },
              "errors": []
            }
        """.trimIndent()
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, body) }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.getMyShop(ShopId(1))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(listOf("ap-1", "fb-2"), (result as AppResult.Success).value.categorySlugs)
    }

    @Test
    fun updateShop_reapproval() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, updateShopBody) }
        val (repo, store) = createSellerRepository(engine)
        val result =
            repo.updateShop(UpdateShopCommand(shopId = ShopId(1), title = "Updated"))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(ShopPublicationState.PendingApproval, (result as AppResult.Success).value.publicationState)
        assertNotNull(store.getDetails(ShopId(1)))
    }

    @Test
    fun fulfillmentOptions_mapsUnknownSafely() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, fulfillmentBody) }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.getFulfillmentOptions(ShopId(1))
        assertIs<AppResult.Success<*>>(result)
        val modes = (result as AppResult.Success).value
        assertTrue(modes.contains(FulfillmentMode.Manual))
        assertTrue(modes.contains(FulfillmentMode.Redirect))
        assertTrue(modes.any { it is FulfillmentMode.Unknown && it.rawValue == "future_mode" })
    }

    @Test
    fun regenerateApiKey_returnsSensitiveKey_notStoredInStateStore() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, apiKeyBody) }
        val store = SellerShopStateStore(mutableListOf())
        val (repo, _) = createSellerRepository(engine, stateStore = store)
        val result = repo.regenerateApiKey(ShopId(1))
        assertIs<AppResult.Success<*>>(result)
        assertEquals("vt_live_xxxxxxxx", (result as AppResult.Success).value.reveal())
        assertTrue(store.summaries.value.isEmpty())
        assertTrue(store.detailsById.value.isEmpty())
    }

    @Test
    fun createWithToken_thenListUsesNewAccess() = runTest {
        var accessToken = "OLD_ACCESS"
        val engine =
            MockEngine { request ->
                when {
                    request.method == HttpMethod.Post &&
                        request.url.encodedPath.endsWith("/seller/shops") -> {
                        assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                        jsonResponse(HttpStatusCode.Created, createShopWithTokenBody)
                    }
                    request.url.encodedPath.endsWith("/seller/shops") -> {
                        assertTrue(request.hasAuthBearer("NEW_ACCESS"))
                        jsonResponse(HttpStatusCode.OK, sellerListBody)
                    }
                    else -> jsonResponse(HttpStatusCode.NotFound, "{}")
                }
            }
        val (repo, _) = createSellerRepository(engine, tokenProvider = { accessToken })
        val created =
            repo.createShop(
                CreateShopCommand(title = "My Shop", type = "retailer", cityId = CityId(1)),
            )
        assertIs<AppResult.Success<*>>(created)
        accessToken = "NEW_ACCESS"
        val listed = repo.getMyShops(SellerShopListQuery())
        assertIs<AppResult.Success<*>>(listed)
    }

    @Test
    fun logoutClearsSellerState() = runTest {
        val listeners = mutableListOf<com.vitran.shop.core.session.repository.SessionInvalidationListener>()
        val store = SellerShopStateStore(listeners)
        store.upsertSummary(
            com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary(
                id = ShopId(1),
                title = "A",
                active = false,
                confirmed = false,
            ),
        )
        listeners.single().onSessionInvalidated()
        assertTrue(store.summaries.value.isEmpty())
    }

    @Test
    fun createCollection_sendsProductIds_andMapsRow() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.url.encodedPath.endsWith("/seller/shops/1/collections"))
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                jsonResponse(HttpStatusCode.Created, collectionCreatedBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result =
            repo.createCollection(
                CreateShopCollectionCommand(
                    shopId = ShopId(1),
                    title = "Summer",
                    description = "Warm-weather picks",
                    productIds = listOf(ProductId(4), ProductId(5)),
                ),
            )
        assertIs<AppResult.Success<*>>(result)
        val collection = (result as AppResult.Success).value
        assertEquals("Summer", collection.title)
        assertEquals(2, collection.productCount)
    }

    @Test
    fun listMyCollections_usesOwnerPath() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals("/api/v1/seller/shops/1/collections", request.url.encodedPath)
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                jsonResponse(HttpStatusCode.OK, collectionListBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.getMyCollections(ShopCollectionListQuery(shopId = ShopId(1)))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(1, (result as AppResult.Success).value.items.size)
    }

    @Test
    fun getMyCollection_mapsDraftProducts() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals("/api/v1/seller/shops/1/collections/1", request.url.encodedPath)
                jsonResponse(HttpStatusCode.OK, collectionDetailBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result =
            repo.getMyCollection(
                ShopCollectionDetailQuery(shopId = ShopId(1), collectionId = ShopCollectionId(1)),
            )
        assertIs<AppResult.Success<*>>(result)
        assertEquals("blue-widget", (result as AppResult.Success).value.products.items.first().slug)
    }

    @Test
    fun updateCollection_omitsUnsetFields_andEncodesEmptyMembership() {
        val json = createNetworkJson()
        val encoded =
            json.encodeToString(
                UpdateShopCollectionRequestDto.serializer(),
                UpdateShopCollectionCommand(
                    shopId = ShopId(1),
                    collectionId = ShopCollectionId(1),
                    title = "Summer sale",
                    productIds = emptyList(),
                ).toRequestDto(),
            )
        assertTrue(encoded.contains("\"title\":\"Summer sale\""))
        assertTrue(encoded.contains("\"product_ids\":[]"))
        assertFalse(encoded.contains("description"))
    }

    @Test
    fun deleteCollection_hitsOwnerPath() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Delete, request.method)
                assertTrue(request.url.encodedPath.endsWith("/seller/shops/1/collections/1"))
                jsonResponse(HttpStatusCode.OK, collectionDeletedBody)
            }
        val (repo, _) = createSellerRepository(engine)
        val result = repo.deleteCollection(ShopId(1), ShopCollectionId(1))
        assertIs<AppResult.Success<Unit>>(result)
    }
}

private const val collectionCreatedBody = """
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
    }
  },
  "errors": []
}
"""

private const val collectionListBody = """
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
          "product_count": 2
        }
      ]
    }
  },
  "errors": []
}
"""

private const val collectionDetailBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "collection": {
      "id": 1,
      "shop_id": 1,
      "title": "Summer",
      "product_count": 1
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

private const val collectionDeletedBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {},
  "errors": []
}
"""
