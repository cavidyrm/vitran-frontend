package com.vitran.shop.feature.engagement.state

import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.engagement.ReviewsEnvelope
import com.vitran.shop.feature.engagement.WishlistShareSettingsEnvelope
import com.vitran.shop.feature.engagement.createEngagementTestClient
import com.vitran.shop.feature.engagement.createEngagementTestExecutor
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.engagementEnvironment
import com.vitran.shop.feature.engagement.jsonResponse
import com.vitran.shop.feature.engagement.review.data.remote.ProductReviewApi
import com.vitran.shop.feature.engagement.review.data.repository.DefaultProductReviewRepository
import com.vitran.shop.feature.engagement.wishlist.data.repository.DefaultWishlistRepository
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EngagementStateAndSessionTest {

    @Test
    fun logout_clearsFollowFavoriteWishlistAndShareCache() = runTest {
        var shareGets = 0
        val listeners = mutableListOf<SessionInvalidationListener>()
        val api = EngagementApi(
            client = createEngagementTestClient(
                MockEngine {
                    shareGets += 1
                    jsonResponse(HttpStatusCode.OK, WishlistShareSettingsEnvelope)
                },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val store = EngagementStateStore(listeners)
        val wishlistRepository = DefaultWishlistRepository(api, listeners)
        val shopId = ShopId(7)
        val productId = ProductId(9)

        store.setFollowStatus(shopId, FollowStatus.Followed)
        store.setFavoriteShopStatus(shopId, FavoriteShopStatus.Favorited)
        store.setSaveStatus(productId, SaveStatus.Saved)
        assertIs<AppResult.Success<*>>(wishlistRepository.getShareSettings())
        store.setShareSettings(wishlistRepository.getShareSettings().getOrNull())
        assertEquals(2, shareGets)

        listeners.forEach { it.onSessionInvalidated() }

        assertTrue(store.followStateByShopId.value.isEmpty())
        assertTrue(store.favoriteShopStateByShopId.value.isEmpty())
        assertTrue(store.wishlistStateByProductId.value.isEmpty())
        assertNull(store.shareSettings.value)
        assertEquals(FollowStatus.Unknown, store.followStatus(shopId))
        assertEquals(SaveStatus.Unknown, store.saveStatus(productId))

        assertIs<AppResult.Success<*>>(wishlistRepository.getShareSettings())
        assertEquals(3, shareGets)
    }

    @Test
    fun publicReviews_areNotSessionState() = runTest {
        var reviewGets = 0
        val listeners = mutableListOf<SessionInvalidationListener>()
        val store = EngagementStateStore(listeners)
        store.setSaveStatus(ProductId(1), SaveStatus.Saved)
        val reviewRepository = DefaultProductReviewRepository(
            ProductReviewApi(
                client = createEngagementTestClient(
                    MockEngine {
                        reviewGets += 1
                        jsonResponse(HttpStatusCode.OK, ReviewsEnvelope)
                    },
                ),
                environment = engagementEnvironment,
                executor = createEngagementTestExecutor(),
            ),
        )

        val before = reviewRepository.getReviews(ProductId(1), CursorPagination())
        listeners.forEach { it.onSessionInvalidated() }
        val after = reviewRepository.getReviews(ProductId(1), CursorPagination())

        assertIs<AppResult.Success<*>>(before)
        assertIs<AppResult.Success<*>>(after)
        assertEquals(2, reviewGets)
        assertEquals(before.getOrNull()?.items?.size, after.getOrNull()?.items?.size)
        assertTrue(store.wishlistStateByProductId.value.isEmpty())
    }
}
