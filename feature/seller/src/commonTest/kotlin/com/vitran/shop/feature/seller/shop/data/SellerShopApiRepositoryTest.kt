package com.vitran.shop.feature.seller.shop.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
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
import com.vitran.shop.feature.seller.updateShopBody
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopRequestDto
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.error.isSlugAlreadyTaken
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
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
    fun createShop_omitsSlug_andSerializesCategorySlugsAsLongs() {
        val json = createNetworkJson()
        val dto =
            CreateShopRequestDto(
                title = "My Shop",
                slug = null,
                type = "retailer",
                cityId = 1,
                categorySlugs = listOf(1L, 2L),
            )
        val encoded = json.encodeToString(CreateShopRequestDto.serializer(), dto)
        assertFalse(encoded.contains("\"slug\""))
        assertTrue(encoded.contains("\"category_slugs\":[1,2]"))
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
}
