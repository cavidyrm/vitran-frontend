package com.vitran.shop.feature.engagement.follow

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.EmptySuccessEnvelope
import com.vitran.shop.feature.engagement.RecordingAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.createEngagementTestClient
import com.vitran.shop.feature.engagement.createEngagementTestExecutor
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.engagementEnvironment
import com.vitran.shop.feature.engagement.follow.data.repository.DefaultFollowRepository
import com.vitran.shop.feature.engagement.follow.domain.usecase.SetShopFollowedUseCase
import com.vitran.shop.feature.engagement.httpErrorEnvelope
import com.vitran.shop.feature.engagement.jsonResponse
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FollowStatus
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FollowMutationTest {

    private val shopId = ShopId(1)

    @Test
    fun follow_postsRequiredAuth_andIsIdempotent() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val api = engagementApi(
            MockEngine { request ->
                methods += request.method
                assertEquals("/api/v1/me/follows/shops/1", request.url.encodedPath)
                assertEquals("Bearer token", request.headers[HttpHeaders.Authorization])
                jsonResponse(HttpStatusCode.OK, EmptySuccessEnvelope)
            },
        )
        val repository = DefaultFollowRepository(api)

        assertIs<AppResult.Success<Unit>>(repository.setFollowed(shopId, followed = true))
        assertIs<AppResult.Success<Unit>>(repository.setFollowed(shopId, followed = true))
        assertEquals(listOf(HttpMethod.Post, HttpMethod.Post), methods)
    }

    @Test
    fun unfollow_deletesRequiredAuth() = runTest {
        val api = engagementApi(
            MockEngine { request ->
                assertEquals(HttpMethod.Delete, request.method)
                assertEquals("/api/v1/me/follows/shops/1", request.url.encodedPath)
                jsonResponse(HttpStatusCode.OK, EmptySuccessEnvelope)
            },
        )

        val result = DefaultFollowRepository(api).setFollowed(shopId, followed = false)

        assertIs<AppResult.Success<Unit>>(result)
    }

    @Test
    fun setFollowed_optimisticThenRollback_onFailure() = runTest {
        val store = EngagementStateStore(mutableListOf())
        val tracker = RecordingAnalyticsTracker()
        val api = engagementApi(
            MockEngine {
                jsonResponse(HttpStatusCode.Conflict, httpErrorEnvelope(409))
            },
        )
        val useCase = SetShopFollowedUseCase(
            followRepository = DefaultFollowRepository(api),
            stateStore = store,
            analyticsTracker = tracker,
        )

        val result = useCase(shopId, followed = true)

        assertIs<AppResult.Failure>(result)
        assertIs<AppError.Conflict>(result.error)
        assertEquals(FollowStatus.Unknown, store.followStatus(shopId))
        assertTrue(tracker.userEvents.isEmpty())
    }

    @Test
    fun setFollowed_success_emitsFollowShopAnalytics_notUnfollow() = runTest {
        val store = EngagementStateStore(mutableListOf())
        val tracker = RecordingAnalyticsTracker()
        val api = engagementApi(
            MockEngine { jsonResponse(HttpStatusCode.OK, EmptySuccessEnvelope) },
        )
        val useCase = SetShopFollowedUseCase(
            followRepository = DefaultFollowRepository(api),
            stateStore = store,
            analyticsTracker = tracker,
        )

        assertIs<AppResult.Success<Unit>>(useCase(shopId, followed = true))
        assertEquals(FollowStatus.Followed, store.followStatus(shopId))
        assertEquals(1, tracker.userEvents.filterIsInstance<UserPersonalizationEvent.FollowShop>().size)

        assertIs<AppResult.Success<Unit>>(useCase(shopId, followed = false))
        assertEquals(FollowStatus.NotFollowed, store.followStatus(shopId))
        assertEquals(1, tracker.userEvents.filterIsInstance<UserPersonalizationEvent.FollowShop>().size)
    }

    private fun engagementApi(engine: MockEngine): EngagementApi =
        EngagementApi(
            client = createEngagementTestClient(engine, token = "token"),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
}
