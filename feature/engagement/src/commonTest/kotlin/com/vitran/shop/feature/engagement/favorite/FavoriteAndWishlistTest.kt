package com.vitran.shop.feature.engagement.favorite

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.engagement.EmptySuccessEnvelope
import com.vitran.shop.feature.engagement.FavoriteShopsEnvelope
import com.vitran.shop.feature.engagement.PublicWishlistEnvelope
import com.vitran.shop.feature.engagement.RecordingAnalyticsTracker
import com.vitran.shop.feature.engagement.WishlistEnvelope
import com.vitran.shop.feature.engagement.WishlistShareSettingsEnvelope
import com.vitran.shop.feature.engagement.createEngagementTestClient
import com.vitran.shop.feature.engagement.createEngagementTestExecutor
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.engagementEnvironment
import com.vitran.shop.feature.engagement.favorite.data.repository.DefaultShopFavoriteRepository
import com.vitran.shop.feature.engagement.favorite.domain.usecase.SetShopFavoriteUseCase
import com.vitran.shop.feature.engagement.httpErrorEnvelope
import com.vitran.shop.feature.engagement.jsonResponse
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FavoriteShopStatus
import com.vitran.shop.feature.engagement.state.SaveStatus
import com.vitran.shop.feature.engagement.wishlist.data.repository.DefaultWishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.error.PublicWishlistResult
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.UpdateWishlistSharingUseCase
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FavoriteAndWishlistTest {

    private val shopId = ShopId(1)
    private val productId = ProductId(1)

    @Test
    fun favoriteList_mapsCursorPage() = runTest {
        val api = engagementApi(
            MockEngine { request ->
                assertEquals("/api/v1/me/favorites/shops", request.url.encodedPath)
                assertEquals("Bearer token", request.headers[HttpHeaders.Authorization])
                jsonResponse(HttpStatusCode.OK, FavoriteShopsEnvelope)
            },
        )

        val page = requireNotNull(
            DefaultShopFavoriteRepository(api).getFavoriteShops(CursorPagination()).getOrNull(),
        )
        val items = page.items
        assertEquals(1, items.size)
        assertEquals("my-shop", items.first().shop.slug.value)
        assertTrue(page.hasMore)
        assertEquals("42", page.nextCursor)
    }

    @Test
    fun addFavorite_409_staysConflict() = runTest {
        val store = EngagementStateStore(mutableListOf())
        val api = engagementApi(
            MockEngine {
                jsonResponse(HttpStatusCode.Conflict, httpErrorEnvelope(409, "already favorited"))
            },
        )
        val useCase = SetShopFavoriteUseCase(DefaultShopFavoriteRepository(api), store)

        val result = useCase(shopId, favorite = true)

        assertIs<AppResult.Failure>(result)
        assertIs<AppError.Conflict>(result.error)
        assertEquals(FavoriteShopStatus.Unknown, store.favoriteShopStatus(shopId))
    }

    @Test
    fun favoriteAddRemove_updatesStore() = runTest {
        val store = EngagementStateStore(mutableListOf())
        val api = engagementApi(
            MockEngine { jsonResponse(HttpStatusCode.OK, EmptySuccessEnvelope) },
        )
        val useCase = SetShopFavoriteUseCase(DefaultShopFavoriteRepository(api), store)

        assertIs<AppResult.Success<Unit>>(useCase(shopId, favorite = true))
        assertEquals(FavoriteShopStatus.Favorited, store.favoriteShopStatus(shopId))
        assertIs<AppResult.Success<Unit>>(useCase(shopId, favorite = false))
        assertEquals(FavoriteShopStatus.NotFavorited, store.favoriteShopStatus(shopId))
    }

    @Test
    fun wishlistList_mapsPriceAndSavedAt() = runTest {
        val api = engagementApi(
            MockEngine { request ->
                assertEquals("/api/v1/me/favorites/products", request.url.encodedPath)
                jsonResponse(HttpStatusCode.OK, WishlistEnvelope)
            },
        )

        val page = DefaultWishlistRepository(api, mutableListOf()).getWishlist(CursorPagination())
        val item = requireNotNull(page.getOrNull()).items.single()
        assertEquals(1L, item.product.id.value)
        assertEquals(150000L, item.product.priceAmount)
        assertEquals("Blue Widget", item.product.title)
    }

    @Test
    fun setSaved_rollbackOnFailure() = runTest {
        val store = EngagementStateStore(mutableListOf())
        store.setSaveStatus(productId, SaveStatus.NotSaved)
        val api = engagementApi(
            MockEngine {
                jsonResponse(HttpStatusCode.InternalServerError, httpErrorEnvelope(500))
            },
        )
        val useCase = SetProductSavedUseCase(
            wishlistRepository = DefaultWishlistRepository(api, mutableListOf()),
            stateStore = store,
            analyticsTracker = RecordingAnalyticsTracker(),
        )

        val result = useCase(productId, saved = true)

        assertIs<AppResult.Failure>(result)
        assertEquals(SaveStatus.NotSaved, store.saveStatus(productId))
    }

    @Test
    fun shareSettings_getAndPut() = runTest {
        var putCalls = 0
        val api = engagementApi(
            MockEngine { request ->
                when (request.method) {
                    HttpMethod.Get -> jsonResponse(HttpStatusCode.OK, WishlistShareSettingsEnvelope)
                    HttpMethod.Put -> {
                        putCalls += 1
                        jsonResponse(
                            HttpStatusCode.OK,
                            WishlistShareSettingsEnvelope.replace("\"public\": false", "\"public\": true"),
                        )
                    }
                    else -> error(request.method)
                }
            },
        )
        val listeners = mutableListOf<SessionInvalidationListener>()
        val repository = DefaultWishlistRepository(api, listeners)
        val store = EngagementStateStore(listeners)
        val update = UpdateWishlistSharingUseCase(repository, store)

        val settings = requireNotNull(repository.getShareSettings().getOrNull())
        assertEquals("wl-a1b2c3d4e5f67890", settings.shareSlug.value)
        assertEquals(false, settings.isPublic)

        val updated = requireNotNull(update(isPublic = true).getOrNull())
        assertEquals(true, updated.isPublic)
        assertEquals(1, putCalls)
        assertEquals(true, store.shareSettings.value?.isPublic)
    }

    @Test
    fun publicWishlist_403_isPrivate_notForbiddenSession() = runTest {
        val api = engagementApi(
            MockEngine {
                jsonResponse(HttpStatusCode.Forbidden, httpErrorEnvelope(403))
            },
            token = null,
        )

        val result = DefaultWishlistRepository(api, mutableListOf()).getPublicWishlist(
            WishlistShareSlug("wl-a1b2c3d4e5f67890"),
            CursorPagination(),
        )

        assertEquals(PublicWishlistResult.Private, result)
    }

    @Test
    fun publicWishlist_404_isNotFound() = runTest {
        val api = engagementApi(
            MockEngine { jsonResponse(HttpStatusCode.NotFound, httpErrorEnvelope(404)) },
            token = null,
        )

        val result = DefaultWishlistRepository(api, mutableListOf()).getPublicWishlist(
            WishlistShareSlug("missing"),
            CursorPagination(),
        )

        assertEquals(PublicWishlistResult.NotFound, result)
    }

    @Test
    fun publicWishlist_mapsSavedAtAndTitleOnly() = runTest {
        val api = engagementApi(
            MockEngine { request ->
                assertEquals("/api/v1/wishlists/share/wl-a1b2c3d4e5f67890", request.url.encodedPath)
                assertNull(request.headers[HttpHeaders.Authorization])
                jsonResponse(HttpStatusCode.OK, PublicWishlistEnvelope)
            },
            token = null,
        )

        val result = DefaultWishlistRepository(api, mutableListOf()).getPublicWishlist(
            WishlistShareSlug("wl-a1b2c3d4e5f67890"),
            CursorPagination(),
        )

        assertIs<PublicWishlistResult.Content>(result)
        assertEquals("Blue Widget", result.page.items.single().product.title)
    }

    private fun engagementApi(engine: MockEngine, token: String? = "token"): EngagementApi =
        EngagementApi(
            client = createEngagementTestClient(engine, token = token),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
}
