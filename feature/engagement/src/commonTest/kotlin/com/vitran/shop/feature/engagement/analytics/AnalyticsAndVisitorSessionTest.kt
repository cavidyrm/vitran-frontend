package com.vitran.shop.feature.engagement.analytics

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.engagement.EmptySuccessEnvelope
import com.vitran.shop.feature.engagement.UserEventEnvelope
import com.vitran.shop.feature.engagement.analytics.data.DefaultMarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.data.remote.ShopAnalyticsApi
import com.vitran.shop.feature.engagement.analytics.data.remote.UserEventApi
import com.vitran.shop.feature.engagement.analytics.data.toRequest
import com.vitran.shop.feature.engagement.analytics.domain.model.ShopAnalyticsEvent
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.createEngagementTestClient
import com.vitran.shop.feature.engagement.createEngagementTestExecutor
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.engagementEnvironment
import com.vitran.shop.feature.engagement.httpErrorEnvelope
import com.vitran.shop.feature.engagement.jsonResponse
import com.vitran.shop.feature.engagement.session.DefaultVisitorSessionProvider
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.SaveStatus
import com.vitran.shop.feature.engagement.wishlist.data.repository.DefaultWishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsAndVisitorSessionTest {

    @Test
    fun userDto_usesEventType_shopDto_usesEvent() {
        val json = createNetworkJson()
        val user = UserPersonalizationEvent.ViewProduct(ProductId(1), ShopId(1)).toRequest("sid")
        val shop = ShopAnalyticsEvent.ProductView(ProductId(1)).toRequest("sid")
        val userEncoded = json.encodeToString(
            com.vitran.shop.feature.engagement.analytics.data.remote.dto.UserEventRequestDto.serializer(),
            user,
        )
        val shopEncoded = json.encodeToString(
            com.vitran.shop.feature.engagement.analytics.data.remote.dto.ShopAnalyticsEventRequestDto.serializer(),
            shop,
        )
        assertTrue(userEncoded.contains("event_type"))
        assertTrue(userEncoded.contains("view_product"))
        assertTrue(shopEncoded.contains("\"event\""))
        assertTrue(shopEncoded.contains("product_view"))
        assertTrue(!shopEncoded.contains("event_type"))
    }

    @Test
    fun userEvents_optionalAuth_shopEvents_noneAuth() = runTest {
        var userAuth: String? = "unset"
        var shopAuth: String? = "unset"
        val visitor = DefaultVisitorSessionProvider("sid")
        val userApi = UserEventApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    userAuth = request.headers[HttpHeaders.Authorization]
                    assertEquals("/api/v1/events", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.Created, UserEventEnvelope)
                },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val shopApi = ShopAnalyticsApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    shopAuth = request.headers[HttpHeaders.Authorization]
                    assertEquals("/api/v1/shops/1/analytics/events", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.Created, EmptySuccessEnvelope)
                },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val tracker = DefaultMarketplaceAnalyticsTracker(
            userEventApi = userApi,
            shopAnalyticsApi = shopApi,
            visitorSessionProvider = visitor,
            logger = NoOpNetworkLogger,
            scope = this,
        )

        tracker.track(UserPersonalizationEvent.ViewProduct(ProductId(1), ShopId(1)))
        tracker.track(ShopId(1), ShopAnalyticsEvent.ShopView)
        testScheduler.advanceUntilIdle()

        assertEquals("Bearer token", userAuth)
        assertNull(shopAuth)
    }

    @Test
    fun analyticsFailure_doesNotFailWishlist() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val store = EngagementStateStore(mutableListOf())
        val api = EngagementApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    when {
                        request.url.encodedPath.contains("/favorites/products") ->
                            jsonResponse(HttpStatusCode.OK, EmptySuccessEnvelope)
                        else -> jsonResponse(HttpStatusCode.InternalServerError, httpErrorEnvelope(500))
                    }
                },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val tracker = DefaultMarketplaceAnalyticsTracker(
            userEventApi = UserEventApi(
                client = createEngagementTestClient(
                    MockEngine { jsonResponse(HttpStatusCode.InternalServerError, httpErrorEnvelope(500)) },
                    token = "token",
                ),
                environment = engagementEnvironment,
                executor = createEngagementTestExecutor(),
            ),
            shopAnalyticsApi = ShopAnalyticsApi(
                client = createEngagementTestClient(
                    MockEngine { jsonResponse(HttpStatusCode.InternalServerError, httpErrorEnvelope(500)) },
                ),
                environment = engagementEnvironment,
                executor = createEngagementTestExecutor(),
            ),
            visitorSessionProvider = DefaultVisitorSessionProvider("sid"),
            logger = NoOpNetworkLogger,
            scope = kotlinx.coroutines.CoroutineScope(dispatcher),
        )
        val useCase = SetProductSavedUseCase(
            wishlistRepository = DefaultWishlistRepository(api, mutableListOf()),
            stateStore = store,
            analyticsTracker = tracker,
        )

        val result = useCase(ProductId(1), saved = true, shopId = ShopId(1))

        assertIs<AppResult.Success<Unit>>(result)
        assertEquals(SaveStatus.Saved, store.saveStatus(ProductId(1)))
    }

    @Test
    fun visitorSession_isStableAndUnique() {
        val first = DefaultVisitorSessionProvider()
        val second = DefaultVisitorSessionProvider()
        assertEquals(first.sessionId(), first.sessionId())
        assertNotEquals(first.sessionId(), second.sessionId())
        assertTrue(first.sessionId().isNotBlank())
    }
}
